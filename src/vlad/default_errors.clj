;; Validations return a data structure that gives all information about an
;; error. You may find this information does not suit the tastes of your users.
(ns vlad.default_errors)

(defmulti translate :type)

(defmethod translate :vlad.validations/present
  [{:keys [name]}]
  (format "%s is required." name))

(defmethod translate :vlad.validations/length-over
  [{:keys [name size]}]
  (format "%s must be over %s characters long." name size))

(defmethod translate :vlad.validations/length-under
  [{:keys [name size]}]
  (format "%s must be under %s characters long." name size))

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

