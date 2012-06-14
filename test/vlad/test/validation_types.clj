(ns vlad.test.validation_types
  (:use vlad.validation_types
        clojure.test))

(def fail       (predicate [:foo] (fn [d] true) "fail"))
(def other-fail (predicate [:foo] (fn [d] true) "other-fail"))
(def pass       (predicate [:foo] (fn [d] false) "pass"))
(def data       {:foo true})

(deftest simples
  (are [errors validator] (= errors (validate validator data))
    ["fail"]               fail
    []                     pass))

(deftest composed-simples
  (are [errors validator] (= errors (validate validator data))
    ["fail" "fail"]        (join fail fail)
    ["fail" "fail" "fail"] (join (join fail fail) fail)

    ["fail"]               (chain fail other-fail)
    ["fail"]               (chain (chain fail other-fail) other-fail)

    ["fail" "other-fail"]  (join (chain pass fail) other-fail)
    ["fail"]               (chain (join fail other-fail) other-fail)))

(def ffail (fn [data] ["fail"]))
(def fpass (fn [data] []))

(deftest functions
  (are [errors validator] (= errors (validate validator data))
    ["fail"]               ffail
    []                     fpass))
