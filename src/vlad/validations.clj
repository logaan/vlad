(ns orm-untangled.validations
  (:use [orm-untangled.validation-types])
  (:require [clojure.string :as str]))

; = Rails validation methods
; == Special cases
; validates_each
; validates_associated
;
; == Normal validators
; validates_acceptance_of
; validates_confirmation_of
; validates_exclusion_of
; validates_format_of
; validates_inclusion_of
; * validates_length_of
; validates_numericality_of
; * validates_presence_of
; * validates_size_of
; validates_uniqueness_of

(defn present [name & selector]
  (simple
    :selector  selector
    :predicate str/blank?
    :message   (format "%s is required." name)))

(defn length_over [size name & selector]
  (simple
    :selector  selector
    :predicate #(> size (count %))
    :message   (format "%s must be more than %d characters long." name size)))

(defn length_under [size name & selector]
  (simple
    :selector  selector
    :predicate #(< size (count %))
    :message   (format "%s must be less than %d characters long." name size)))

(defn length_in [lower upper name & selector]
  (join
    (length_over  lower name selector)
    (length_under upper name selector)))

