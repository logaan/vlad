(ns vlad.test.validations
  (:use clojure.test
        [vlad validations validation_types]))

(deftest validations-work
  (let [data {:name "Chris"}]
    (are [validator errors ] (= errors (validate validator data))
      (present "Name" :name) []                   
      (present "Age"  :age)  ["Age is required."] 

      (length_over 4 "Name" :name) []                   
      (length_over 9 "Name" :name) ["Name must be more than 9 characters long."] 

      (length_under 9 "Name" :name) []                   
      (length_under 4 "Name" :name) ["Name must be less than 4 characters long."] 

      (length_in 4 9 "Name" :name) []                   
      (length_in 9 4 "Name" :name) ["Name must be more than 9 characters long."
                                    "Name must be less than 4 characters long."])))
