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
  (:import [vlad.validation_types Simple Join Chain])
  (:require [clojure.string :as str]))

(defn present
  "Checks that the string found at `selector` is not blank.
  
  ex. `(validate (present \"Name\" :name) {:name \"Vlad\"})`"
  [name & selector]
  (Simple. selector str/blank?
           (format "%s is required." name)))

(defn length_over 
  "Checks that the `count` of the value found at `selector` is over `size`."
  [size name & selector]
  (Simple. selector #(> size (count %))
           (format "%s must be more than %d characters long." name size)))

(defn length_under 
  "Checks that the `count` of the value found at `selector` is under `size`."
  [size name & selector]
  (Simple. selector #(< size (count %))
           (format "%s must be less than %d characters long." name size)))

(defn length_in 
  "Checks that the `count` of the value found at `selector` is over `lower` and
  under `upper`. No checking is done that `lower` is lower than `upper`. This
  validator may return multiple errors"
  [lower upper name & selector]
  (Join.
    (length_over  lower name selector)
    (length_under upper name selector)))
