(ns vlad.test.default_errors
  (:use vlad.default_errors
        clojure.test))

(deftest translations
  (are [errors translations] (= translations (translate-errors errors))

  [{:type :vlad.validations/present :selector [:password] :name "Password"}]
  {[:password] "Password is required."}

  [{:type :vlad.validations/length-over :selector [:password] :name "Password" :size 8}]
  {[:password] "Password must be over 8 characters long."}

  [{:type :vlad.validations/length-under :selector [:password] :name "Password" :size 8}]
  {[:password] "Password must be under 8 characters long."}))

