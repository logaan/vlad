;; Here can be found ways to create, compose and execute validations.
(ns vlad.validation_types)

(defprotocol Validation
  "The core of vlad is the `Validation` protocol. It simply requires that your
  validation type knows how to run against some data. Implementations of
  `validate` should return a vector of errors as strings."
  (validate [self data]))

(defn valid
  "`valid` is a validation that does nothing. It can be safely composed with
  other validations. It is used as the identity value for reducers/monoid
  functions"
  [data] [])

(defn child-errors
  "Composed validations form a binary tree structure. This structure is
  recursively descended when validating against some data. `child-errors`
  validates both branches against the data and concatenates their errors."
  [{:keys [left right]} data]
  (let [errors (map #(validate % data) [left right])]
    (reduce concat errors)))

;; Two validations can be composed in a `Join`. When `validate` is called their
;; error messages will be combined into one vector. You can nest joined
;; validations and they will be recursively traversed.
(defrecord Join [left right]
  Validation
  (validate [self data]
    (child-errors self data)))

(defn join
  "Example:

    (join
      (present \"Name\" :name)
      (present \"Age\" :age))"
  ([] valid)
  ([left] left)
  ([left right] (Join. left right))
  ([left right & rest] (reduce (Join. left right) rest)))

;; `Chain` can also be using for composing validations. However it will fail
;; fast, only returning the first validation if it fails. If the first
;; validation failed with multiple errors (ie: you're chaining a `Join`) then
;; all of those errors will still be returned
(defrecord Chain [left right]
  Validation
  (validate [self data]
    (let [errors (child-errors self data)
          error  (first (filter not-empty errors))]
      (if (nil? error) [] [error]))))

(defn chain
  "Example:

    (chain
      (present \"Password\" :password)
      (length_over 7 \"Password\" :password))"
  ([] valid)
  ([left] left)
  ([left right] (Chain. left right))
  ([left right & rest] (reduce (Chain. left right) rest)))

;; Predicates are simple functions that take some data and return a boolean
;; value. They're ideal for use as validators and so `Predicate` exists to make
;; wrapping them up easy. All that's needed to turn a predicate into a
;; validator is a selector (for drilling down into data) and an error message.
(defrecord Predicate [selector predicate message]
  Validation
  (validate [{:keys [selector predicate message]} data]
    (if (predicate (get-in data (flatten selector)))
      [message] [])))

(defn predicate
  "Example:

    (predicate :name str/blank? \"Name is required.\")"
  [selector predicate message]
  (Predicate. selector predicate message))

;; The most powerful form of validator is simply a function that takes some
;; data and returns an array of errors. It can be composed with all other
;; validators as normal.
(extend-type clojure.lang.IFn
  Validation
  (validate [self data]
    (self data)))

