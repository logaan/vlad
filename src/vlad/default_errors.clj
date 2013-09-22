;; Validations return a data structure that gives all information about an
;; error. You may find this information does not suit the tastes of your users.
(ns vlad.default-errors
  (:require [clojure.string :as s]))

(defn assign-name
  "`translate` expects each field to have a human readable name. `assign-name`
  takes a collection of errors and a map of selectors to names and will return
  the errors with names inserted."
  [errors selectors-to-names]
  (map #(assoc % :name (selectors-to-names (:selector %))) errors))

(defmulti english-translation
  "Takes an error and returns a human readable version of it."
  :type)

(defmethod english-translation :vlad.validations/present
  [{:keys [name]}]
  (format "%s is required." name))

(defmethod english-translation :vlad.validations/length-over
  [{:keys [name size]}]
  (format "%s must be over %s characters long." name size))

(defmethod english-translation :vlad.validations/length-under
  [{:keys [name size]}]
  (format "%s must be under %s characters long." name size))

(defmethod english-translation :vlad.validations/one-of
  [{:keys [name set]}]
  (format "%s must be one of %s." name (s/join ", " set)))

(defmethod english-translation :vlad.validations/not-of
  [{:keys [name set]}]
  (format "%s must not be one of %s." name (s/join ", " set)))

(defmethod english-translation :vlad.validations/equals-value
  [{:keys [name value]}]
  (format "%s must be \"%s\"." name value))

(defmethod english-translation :vlad.validations/equals-field
  [{:keys [first-name second-name]}]
  (format "%s must be the same as %s." first-name second-name))

(defmethod english-translation :vlad.validations/matches
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
  [errors translation]
  (reduce (fn [output-map {:keys [selector] :as error}]
            (let [existing-errors (get output-map selector [])
                  new-errors      (conj existing-errors (translation error))]
              (assoc output-map selector new-errors)))
          {} errors))

