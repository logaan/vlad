;; Here you will find ways to create, compose and execute validations.
(ns vlad.validation-types)

(defprotocol Validation
  "The core of vlad is the `Validation` protocol. It simply requires that your
  validation type knows how to run against some data. Implementations of
  `validate` should return a sequence of errors. An error may be anything but
  is generally of the form `{:type ::something :some-extra-data 123}`."
  (validate [self data]))

(defn valid
  "`valid` is a validation that does nothing. It can be safely composed with
  other validations. It is used as the identity value for reducers/monoid
  functions (Don't panic. You don't need to know what that means to use vlad)."
  [data] [])

(defrecord Attr [selector validation]
  Validation
  (validate [{:keys [selector validation]} data]
    (let [errors (validate validation (get-in data selector))]
      (map (fn [{child-selector :selector :as error}]
             (assoc error :selector
                       (concat selector (or child-selector []))))
           errors))))

(defn attr
  "Runs a validation on the data found at `selector`. If there are nested uses
  of `attr` any `:selector` attributes in errors will be updated to reflect the
  full `selector`.
  
  Example:
  
  (validate (attr [:name] present) {:name \"Vlad\"})"
  ([selector] (Attr. selector valid))
  ([selector validation] (Attr. selector validation)))

;; Two validations can be composed in a `Join`. When `validate` is called their
;; error messages will be combined into one sequence You can nest joined
;; validations and they will be recursively traversed.
(defrecord Join [left right]
  Validation
  (validate [{:keys [left right]} data]
    (mapcat #(validate % data) [left right])))

(defn join
  "Example:

    (join
      (present \"Name\" :name)
      (present \"Age\" :age))"
  ([] valid)
  ([left] left)
  ([left right] (Join. left right))
  ([left right & validations] (reduce #(Join. %1 %2) (Join. left right) validations)))

;; `Chain` can also be using for composing validations. However it will fail
;; fast, only returning the first validation if it fails. If the first
;; validation failed with multiple errors (ie: you're chaining a `Join`) then
;; all of those errors will still be returned
(defrecord Chain [left right]
  Validation
  (validate [{:keys [left right]} data]
    (let [left-errors (validate left  data)]
      (if (empty? left-errors) (validate right data) left-errors))))

(defn chain
  "Example:

    (chain
      (present \"Password\" :password)
      (length_over 7 \"Password\" :password))"
  ([] valid)
  ([left] left)
  ([left right] (Chain. left right))
  ([left right & validations] (reduce #(Chain. %1 %2) (Chain. left right) validations)) )

;; Predicates are simple functions that take some data and return a boolean
;; value. They're ideal for use as validators and so `Predicate` exists to make
;; wrapping them up easy. All that's needed to turn a predicate into a
;; validator is an error message.
(defrecord Predicate [predicate information]
  Validation
  (validate [{:keys [predicate information]} data]
    (if (predicate data) [information] [])))

(defn predicate
  "Examples:
    (predicate #(> size (count %))
               {:type ::length-over :size size})

    (predicate [:user :password]
               #(> size (count %))
               {:type ::length-over :size size})"
  ([pred information]
   (Predicate. pred information))
  ([selector pred information]
   (attr selector (predicate pred information))))

;; The most powerful form of validator is simply a function that takes some
;; data and returns an array of errors. It can be composed with all other
;; validators as normal.
(extend-type clojure.lang.IFn
  Validation
  (validate [self data]
    (self data)))

