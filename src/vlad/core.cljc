(ns vlad.core
  (:require [clojure.string :as str]))

(defprotocol Validation
  "The core of vlad is the `Validation` protocol. It simply requires that your
  validation type knows how to run against some data. Implementations of
  `validate` should return a sequence of errors. An error may be anything but
  is generally of the form `{:type ::something :some-extra-data 123}`."
  (validate [self data]))

(defn valid? [validations data]
  (empty? (validate validations data)))

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
  
  (validate (attr [:name] (present)) {:name \"Vlad\"})"
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
      (attr :name (present))
      (attr :age (present)))"
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

    (attr :password
      (chain
        (present)
        (length-over 7))"
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
(extend-type #?(:cljs function :clj clojure.lang.IFn)
  Validation
  (validate [self data]
    (self data)))


; Checks that a string is not blank.
; 
; Examples:
; 
;   (validate (present) \"Vlad\")
(defn present
  ([]
   (present {}))
  ([error-data]
   (predicate #(if (string? %) (str/blank? %) true)
              (merge {:type ::present} error-data))))

(defn length-over 
  "Checks that the `count` of the value is over `size`."
  ([size]
   (length-over size {}))
  ([size error-data]
   (predicate #(> size (count %))
              (merge {:type ::length-over :size size} error-data))))

(defn length-under 
  "Checks that the `count` of the value is under `size`."
  ([size]
   (length-under size {}))
  ([size error-data]
   (predicate #(< size (count %))
              (merge {:type ::length-under :size size} error-data))))

(defn length-in 
  "Checks that the `count` of the value is over `lower` and under `upper`. No
  checking is done that `lower` is lower than `upper`. This validator may
  return multiple errors"
  ([lower upper]
   (length-in lower upper {}))
  ([lower upper error-data]
   (join
    (length-over  lower error-data)
    (length-under upper error-data))))

(defn one-of
  "Checks that the value is found within `set`"
  ([set]
   (one-of set {}))
  ([set error-data]
   (predicate #(not (contains? set %))
              (merge {:type ::one-of :set set} error-data))))

(defn not-of
  "Checks that the value is not found within `set`"
  ([set]
   (not-of set {}))
  ([set error-data]
   (predicate #(contains? set %)
              (merge {:type ::not-of :set set} error-data))))

(defn equals-value
  "Checks that the value is equal to the `value` that you provide."
  ([value]
   (equals-value value {}))
  ([value error-data]
   (predicate #(not (= value %))
              (merge {:type ::equals-value :value value}
                     error-data))))

(defn equals-field
  "Checks that the values found at each of your selectors are equal to each
  other"
  ([first-selector second-selector]
   (equals-field first-selector second-selector {}))
  ([first-selector second-selector error-data]
   (fn [data]
    (let [first-value  (get-in data first-selector)
          second-value (get-in data second-selector)]
      (if (= first-value second-value)
          []
          [(merge {:type ::equals-field
                   :first-selector first-selector
                   :second-selector second-selector}
                  error-data)])))))

(defn matches
  "Checks that the value is a regex match for `pattern`.  This uses clojure's
  `re-matches` function which may not behave as you expect.  Your pattern will
  have to match the whole string to count as a match."
  ([pattern]
   (matches pattern {}))
  ([pattern error-data]
   (predicate #(nil? (re-matches pattern %))
             (merge {:type ::matches :pattern pattern} error-data))))

(defn assign-names
  "`translate` expects each field to have a human readable name. `assign-names`
  takes a collection of errors and a map of selectors to names and will return
  the errors with names inserted."
  [errors selectors-to-names]
  (map #(assoc % :name (selectors-to-names (:selector %))) errors))

(defn- guess-field-name [selector]
  (str/capitalize (str/replace (name (last selector)) #"-" " ")))

(defn guess-field-names
  "Will convert selectors to strings by swapping hyphens for spaces and
  capitalising."
   [errors]
   (map #(assoc % :name (guess-field-name (:selector %))) errors))

(defmulti english-translation
  "Takes an error and returns a human readable version of it."
  :type)

(defmethod english-translation :vlad.core/present
  [{:keys [name]}]
  (str name " is required."))

(defmethod english-translation :vlad.core/length-over
  [{:keys [name size]}]
  (str name " must be over " size " characters long."))

(defmethod english-translation :vlad.core/length-under
  [{:keys [name size]}]
  (str name " must be under " size " characters long."))

(defmethod english-translation :vlad.core/one-of
  [{:keys [name set]}]
  (str name " must be one of " (str/join ", " set) "."))

(defmethod english-translation :vlad.core/not-of
  [{:keys [name set]}]
  (str name " must not be one of " (str/join ", " set) "."))

(defmethod english-translation :vlad.core/equals-value
  [{:keys [name value]}]
  (str name " must be \"" value "\"."))

(defmethod english-translation :vlad.core/equals-field
  [{:keys [first-name second-name]}]
  (str first-name " must be the same as " second-name "."))

(defmethod english-translation :vlad.core/matches
  [{:keys [name pattern]}]
  (str name " must match the pattern " (.toString pattern) "."))

(defn translate-errors
  "Translates a sequence of errors into a map of plain english error messages.
   Selectors are used as keys.

   Example:

    (translate-errors [{
      :type :vlad.core/length-under
      :selector [:password]
      :name \"Password\"
      :size 8}])
    ; => {[:password] \"Password must be under 8 characters long.\"}"
  [errors translation]
  (reduce (fn [output-map {:keys [selector] :as error}]
            (let [existing-errors (get output-map selector [])
                  new-errors      (conj existing-errors (translation error))]
              (assoc output-map selector new-errors)))
          {} errors))

(defn field-errors [validations data]
  (-> (validate validations data)
      (guess-field-names)
      (translate-errors english-translation)))

(defn flat-errors [validations data]
  (-> (field-errors validations data) vals flatten))
