(ns vlad.test.validation-types
  (:require [vlad.core :refer [validate valid attr join chain valid]]
            [#?(:cljs cljs.test :clj clojure.test) :as t]))

(defn fail       [data] ["fail"])
(defn other-fail [data] ["other-fail"])
(defn pass       [data] [])

{:type :vlad.core/present
    :selector [:number-of-teeth]}

(t/deftest attr-fails
  (t/are [?errors     ?validator]
       (= (validate ?validator {:foo true}) ?errors)

       []
       (attr [:foo] valid)

       [{:type :fail :selector [:foo]}]
       (attr [:foo] (fn [_] [{:type :fail}]))

       [{:type :fail :selector [:foo :bar]}]
       (attr [:foo] (fn [_] [{:type :fail :selector [:bar]}]))))

(t/deftest join-chain-nil
  (t/are [?validator]
       (= (validate ?validator nil) [])
       valid
       (join valid valid)
       (chain valid valid)))

(t/deftest join-chain
  (t/are
    [?errors                ?validator]
    (= (validate ?validator {:foo true}) ?errors)
    ["fail"]               fail
    []                     pass

    ["fail" "fail"]        (join fail fail)
    ["fail" "fail" "fail"] (join (join fail fail) fail)

    ["fail"]               (chain fail other-fail)
    ["fail"]               (chain (chain fail other-fail) other-fail)

    ["fail" "other-fail"]  (join (chain pass fail) other-fail)
    ["fail" "other-fail"]  (chain (join fail other-fail) other-fail)))

