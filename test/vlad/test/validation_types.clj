(ns vlad.test.validation_types
  (:use vlad.validation_types
        clojure.test))

(deftest identities
  (are [errors validator] (= errors (validate validator nil))
    [] valid
    [] (join valid valid)
    [] (chain valid valid)))

(deftest predicates
  (let [fail       (predicate [:foo] (fn [d] true) "fail")
        other-fail (predicate [:foo] (fn [d] true) "other-fail")
        pass       (predicate [:foo] (fn [d] false) "pass")
        data       {:foo true}]
  (are [errors validator] (= errors (validate validator data))
    ["fail"]               fail
    []                     pass

    ["fail" "fail"]        (join fail fail)
    ["fail" "fail" "fail"] (join (join fail fail) fail)

    ["fail"]               (chain fail other-fail)
    ["fail"]               (chain (chain fail other-fail) other-fail)

    ["fail" "other-fail"]  (join (chain pass fail) other-fail)
    ["fail"]               (chain (join fail other-fail) other-fail))))

(deftest functions
  (let [fail (fn [data] ["fail"])
        pass (fn [data] [])]
  (are [errors validator] (= errors (validate validator nil))
    ["fail"]               fail
    []                     pass)))

