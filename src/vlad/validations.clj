;; I aim to implement equivilents to all rails validation methods.
;;
;; ## Implemented validations
;;
;; * `validates_presence_of`
;; * `validates_length_of`
;; * `validates_size_of`
;;
;; ## Outstanding validations
;;
;; * `validates_acceptance_of`
;; * `validates_confirmation_of`
;; * `validates_exclusion_of`
;; * `validates_format_of`
;; * `validates_inclusion_of`
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
