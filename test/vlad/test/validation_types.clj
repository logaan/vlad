(ns vlad.test.validation-types
  (:require [vlad.core :refer :all]
            [midje.sweet :refer [fact tabular]]
            [clojure.test :refer :all]))

(defn fail       [data] ["fail"])
(defn other-fail [data] ["other-fail"])
(defn pass       [data] [])

{:type :vlad.core/present
    :selector [:number-of-teeth]}

(tabular
  (fact (validate ?validator {:foo true}) => ?errors)
  ?errors     ?validator
  []          (attr [:foo] valid)
  [{:type :fail :selector [:foo]}] (attr [:foo] (fn [_] [{:type :fail}]))
  [{:type :fail :selector [:foo :bar]}] (attr [:foo] (fn [_] [{:type :fail :selector [:bar]}]))
  )

(tabular
  (fact (validate ?validator nil) => [])
    ?validator
    valid
    (join valid valid)
    (chain valid valid))

(tabular
  (fact (validate ?validator {:foo true}) => ?errors)
    ?errors                ?validator
    ["fail"]               fail
    []                     pass

    ["fail" "fail"]        (join fail fail)
    ["fail" "fail" "fail"] (join (join fail fail) fail)

    ["fail"]               (chain fail other-fail)
    ["fail"]               (chain (chain fail other-fail) other-fail)

    ["fail" "other-fail"]  (join (chain pass fail) other-fail)
    ["fail" "other-fail"]  (chain (join fail other-fail) other-fail))

