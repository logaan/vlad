(ns vlad.validations
  (:use [vlad.validation-types])
  (:require [clojure.string :as str]))


; Checks that a string is not blank.
; 
; Examples:
; 
;   (validate present \"Vlad\")
(def present
  (predicate #(if (string? %) (str/blank? %) true)  {:type ::present}))

(defn length-over 
  "Checks that the `count` of the value is over `size`."
  [size]
  (predicate #(> size (count %)) {:type ::length-over :size size}))

(defn length-under 
  "Checks that the `count` of the value is under `size`."
  [size]
  (predicate #(< size (count %)) {:type ::length-under :size size}))

(defn length-in 
  "Checks that the `count` of the value is over `lower` and under `upper`. No
  checking is done that `lower` is lower than `upper`. This validator may
  return multiple errors"
  [lower upper]
  (join
    (length-over  lower)
    (length-under upper)))

(defn one-of
  "Checks that the value is found within `set`"
  [set]
  (predicate #(not (contains? set %)) {:type ::one-of :set set}))

(defn not-of
  "Checks that the value is not found within `set`"
  [set]
  (predicate #(contains? set %) {:type ::not-of :set set}))

(defn equals-value
  "Checks that the value is equal to the `value` that you
  provide."
  [value]
  (predicate #(not (= value %)) {:type ::equals-value :value value}))

(defn equals-field
  "Checks that the values found at each of your selectors are equal to each
  other"
  [first-selector second-selector]
  (fn [data]
    (let [first-value  (get-in data first-selector)
          second-value (get-in data second-selector)]
      (if (= first-value second-value)
          []
          [{:type ::equals-field
            :first-selector first-selector
            :second-selector second-selector}]))))

(defn matches
  "Checks that the value is a regex match for `pattern`.  This uses clojure's
  `re-matches` function which may not behave as you expect.  Your pattern will
  have to match the whole string to count as a match."
  [pattern]
  (predicate #(nil? (re-matches pattern %))
             {:type ::matches :pattern pattern}))

