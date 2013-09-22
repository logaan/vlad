(ns vlad.test.validations
  (:use [midje.sweet :only [tabular fact just contains]]
        [vlad validations validation-types]))

(tabular
  (fact (validate ?validator {:name "Chris" :confirm_name "Brad"}) => ?errors)
  ?validator ?errors

  (present [:name])
  []                   

  (present [:age])
  [{:type :vlad.validations/present
    :selector [:age]}] 

  (length-over 4 [:name])
  []                   

  (length-over 9 [:name])
  [{:type :vlad.validations/length-over
    :size 9
    :selector [:name]}] 

  (length-under 9 [:name])
  []                   

  (length-under 4 [:name])
  [{:type :vlad.validations/length-under
    :size 4
    :selector [:name]}] 

  (length-in 4 9 [:name])
  []                   

  (length-in 9 4 [:name])
  [{:type :vlad.validations/length-over
    :size 9
    :selector [:name]}
   {:type :vlad.validations/length-under
    :size 4
    :selector [:name]}]

  (one-of #{"Chris" "Fred"} [:name])
  []

  (one-of #{"Thelma" "Luise"} [:name])
  [{:type :vlad.validations/one-of
    :set #{"Thelma" "Luise"}
    :selector [:name]}]

  (not-of #{"Thelma" "Luise"} [:name])
  []

  (not-of #{"Chris" "Fred"} [:name])
  [{:type :vlad.validations/not-of
    :set #{"Chris" "Fred"}
    :selector [:name]}]

  (equals-value "Chris" [:name])
  []

  (equals-value "Maddy" [:name])
  [{:type :vlad.validations/equals-value
    :value "Maddy"
    :selector [:name]}] 

  (equals-field [:name] [:name])
  []

  (equals-field [:name] [:confirm_name])
  [{:type :vlad.validations/equals-field
    :first-selector [:name]
    :second-selector [:confirm_name]}]
  
  (matches #"..ris" [:name])
  []
  
  (matches #"andy" [:name])
  (just [(contains {:type :vlad.validations/matches
                              :selector [:name]})]))

