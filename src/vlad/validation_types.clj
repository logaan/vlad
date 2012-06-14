;; Here can be found ways to create, compose and execute validations.
(ns vlad.validation_types)

;; The core of vlad is the `Validation` protocol. It simply requires that your
;; validation type knows how to run against some data. Implementations of
;; `validate` should return a vector of errors as strings.
(defprotocol Validation
  (validate [self data]))

;; Composed validations form a binary tree structure. This structure is
;; recursively descended when validating against some data. `child-errors`
;; validates both branches against the data and concatenates their errors.
(defn child-errors [{:keys [left right]} data]
  (let [errors (map #(validate % data) [left right])]
    (reduce concat errors)))

(defrecord Join [left right]
  Validation
  (validate [self data]
    (child-errors self data)))

(defrecord Chain [left right]
  Validation
  (validate [self data]
    (let [errors (child-errors self data)
          error  (first (filter not-empty errors))]
      (if (nil? error) [] [error]))))

(defrecord Simple [selector predicate message]
  Validation
  (validate [{:keys [selector predicate message]} data]
    (if (predicate (get-in data (flatten selector)))
      [message] [])))

(extend-type clojure.lang.IFn
  Validation
  (validate [self data]
    (self data)))

