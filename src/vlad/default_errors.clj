;; Validations return a data structure that gives all information about an
;; error. You may find this information does not suit the tastes of your users.
(ns vlad.default_errors
  (:require [clojure.string :as s]))

(defn assign-name [errors selectors-to-names]
  (map #(assoc % :name (selectors-to-names (:selector %))) errors))

(defmulti translate
  "The translate function simply takes an error and returns a readable version
  of it."
  :type)

(defmethod translate :vlad.validations/present
  [{:keys [name]}]
  (format "%s is required." name))

(defmethod translate :vlad.validations/length-over
  [{:keys [name size]}]
  (format "%s must be over %s characters long." name size))

(defmethod translate :vlad.validations/length-under
  [{:keys [name size]}]
  (format "%s must be under %s characters long." name size))

(defmethod translate :vlad.validations/one-of
  [{:keys [name set]}]
  (format "%s must be one of %s." name (s/join ", " set)))

(defmethod translate :vlad.validations/not-of
  [{:keys [name set]}]
  (format "%s must not be one of %s." name (s/join ", " set)))

(defmethod translate :vlad.validations/equals-value
  [{:keys [name value]}]
  (format "%s must be \"%s\"." name value))

(defmethod translate :vlad.validations/equals-field
  [{:keys [first-name second-name]}]
  (format "%s must be the same as %s." first-name second-name))

(defmethod translate :vlad.validations/matches
  [{:keys [name pattern]}]
  (format "%s must match the pattern %s." name (.toString pattern)))

(defn translate-errors
  "Translates a sequence of errors into a map of plain english error messages.
   Selectors are used as keys.

   Example:

    (translate-errors [{
      :type :vlad.validations/length-under
      :selector [:password]
      :name \"Password\"
      :size 8}])
    ; => {[:password] \"Password must be under 8 characters long.\"}"
  [errors]
  (letfn [(translate-with-selector [error]
            {(:selector error) (translate error)})]
    (apply merge (map translate-with-selector errors))))
