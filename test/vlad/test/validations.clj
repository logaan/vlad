(ns vlad.test.validations
  (:use midje.sweet
        [vlad validations validation_types]))

(tabular
  (fact (validate ?validator {:name "Chris" :confirm_name "Brad"}) => ?errors)
    ?validator ?errors

    (present "Name" [:name])
       []                   

    (present "Age"  [:age])
       [{:type :vlad.validations/present
         :name "Age"
         :selector [:age]}] 

    (length-over 4 "Name" [:name])
       []                   

    (length-over 9 "Name" [:name])
       [{:type :vlad.validations/length-over
         :size 9
         :name "Name"
         :selector [:name]}] 

    (length-under 9 "Name" [:name])
       []                   

    (length-under 4 "Name" [:name])
       [{:type :vlad.validations/length-under
         :size 4
         :name "Name"
         :selector [:name]}] 

    (length-in 4 9 "Name" [:name])
       []                   

    (length-in 9 4 "Name" [:name])
       [{:type :vlad.validations/length-over
         :size 9
         :name "Name"
         :selector [:name]}
        {:type :vlad.validations/length-under
         :size 4
         :name "Name"
         :selector [:name]}]
  
    (equals-value "Chris" "Name" [:name])
      []

    (equals-value "Maddy" "Name" [:name])
       [{:type :vlad.validations/equals-value
         :value "Maddy"
         :name "Name"
         :selector [:name]}] 

    (equals-field "Name" [:name] "Name confirmation" [:name])
      []

    (equals-field "Name" [:name] "Name confirmation" [:confirm_name])
       [{:type :vlad.validations/equals-field
         :first-name "Name"
         :first-selector [:name]
         :second-name "Name confirmation"
         :second-selector [:confirm_name]}])

