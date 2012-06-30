;; I aim to implement equivilents to all rails validation methods.
;;
;; ## Implemented validations
;;
;; * `validates_acceptance_of`
;; * `validates_confirmation_of`
;; * `validates_exclusion_of`
;; * `validates_inclusion_of`
;; * `validates_length_of`
;; * `validates_presence_of`
;; * `validates_size_of`
;;
;; ## Outstanding validations
;;
;; * `validates_format_of`
;; * `validates_numericality_of`
;; * `validates_uniqueness_of`
;;
(ns vlad.validations
  (:use [vlad.validation_types])
  (:require [clojure.string :as str]))

(defn present
  "Checks that the string found at `selector` is not blank.
  
  Example:
  
    (validate (present \"Name\" :name)
              {:name \"Vlad\"})"
  [name selector]
  (predicate selector str/blank?
             {:type ::present :name name :selector selector}))

(defn length-over 
  "Checks that the `count` of the value found at `selector` is over `size`."
  [size name selector]
  (predicate selector #(> size (count %))
             {:type ::length-over :size size :name name :selector selector}))

(defn length-under 
  "Checks that the `count` of the value found at `selector` is under `size`."
  [size name selector]
  (predicate selector #(< size (count %))
             {:type ::length-under :size size :name name :selector selector}))

(defn length-in 
  "Checks that the `count` of the value found at `selector` is over `lower` and
  under `upper`. No checking is done that `lower` is lower than `upper`. This
  validator may return multiple errors"
  [lower upper name selector]
  (join
    (length-over  lower name selector)
    (length-under upper name selector)))

(defn one-of
  "Checks that the value found at `selector` is found within `set`"
  [set name selector]
  (predicate selector #(not (contains? set %))
             {:type ::one-of :set set :name name :selector selector}))

(defn not-of
  "Checks that the value found at `selector` is not found within `set`"
  [set name selector]
  (predicate selector #(contains? set %)
             {:type ::not-of :set set :name name :selector selector}))

(defn equals-value
  "Checks that the value found at `selector` is equal to the `value` that you
  provide."
  [value name selector]
  (predicate selector #(not (= value %))
             {:type ::equals-value :value value :name name :selector selector}))

(defn equals-field
  "Checks that the values found at each of your selectors are equal to each
  other"
  [first-name first-selector second-name second-selector]
  (fn [data]
    (let [first-value  (get-in data first-selector)
          second-value (get-in data second-selector)]
      (if (= first-value second-value)
          []
          [{:type ::equals-field
            :first-name first-name   :first-selector first-selector
            :second-name second-name :second-selector second-selector}]))))

(defn matches
  "Checks that the value found at `selector` is a regex match for `pattern`.
  This uses clojure's `re-matches` function which may not behave as you expect.
  Your pattern will have to match the whole string found at `selector` to count
  as a match."
  [pattern name selector]
  (predicate selector #(nil? (re-matches pattern %))
             {:type ::matches :pattern pattern :name name :selector selector}))

