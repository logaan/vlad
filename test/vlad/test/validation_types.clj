(ns vlad.test.validation-types
  (:use vlad.validation-types
        midje.sweet
        clojure.test))

(defn fail       [data] ["fail"])
(defn other-fail [data] ["other-fail"])
(defn pass       [data] [])

{:type :vlad.validations/present
    :selector [:number-of-teeth]}

(tabular
  (fact (validate ?validator {:foo true}) => ?errors)
  ?errors     ?validator
  []          (in [:foo] valid)
  [{:type :fail :selector [:foo]}] (in [:foo] (fn [_] [{:type :fail}]))
  [{:type :fail :selector [:foo :bar]}] (in [:foo] (fn [_] [{:type :fail :selector [:bar]}]))
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

