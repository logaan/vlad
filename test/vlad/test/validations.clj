(ns vlad.test.validations
  (:use [midje.sweet :only [tabular fact just contains]]
        [vlad validations validation-types]))

(tabular
  (fact (validate ?validator {:name "Chris" :confirm_name "Brad" :number-of-teeth 32}) => ?errors)
  ?validator ?errors

  (attr [:name] present)
  []                   

  (attr [:number-of-teeth] present)
  [{:type :vlad.validations/present
    :selector [:number-of-teeth]}] 

  (attr [:age] present)
  [{:type :vlad.validations/present
    :selector [:age]}] 

  (attr [:name] (length-over 4))
  []                   

  (attr [:name] (length-over 9))
  [{:type :vlad.validations/length-over
    :size 9
    :selector [:name]}] 

  (attr [:name] (length-under 9))
  []                   

  (attr [:name] (length-under 4))
  [{:type :vlad.validations/length-under
    :size 4
    :selector [:name]}] 

  (attr [:name] (length-in 4 9))
  []                   

  (attr [:name] (length-in 9 4))
  [{:type :vlad.validations/length-over
    :size 9
    :selector [:name]}
   {:type :vlad.validations/length-under
    :size 4
    :selector [:name]}]

  (attr [:name] (one-of #{"Chris" "Fred"}))
  []

  (attr [:name] (one-of #{"Thelma" "Luise"}))
  [{:type :vlad.validations/one-of
    :set #{"Thelma" "Luise"}
    :selector [:name]}]

  (attr [:name] (not-of #{"Thelma" "Luise"}))
  []

  (attr [:name] (not-of #{"Chris" "Fred"}))
  [{:type :vlad.validations/not-of
    :set #{"Chris" "Fred"}
    :selector [:name]}]

  (attr [:name] (equals-value "Chris"))
  []

  (attr [:name] (equals-value "Maddy"))
  [{:type :vlad.validations/equals-value
    :value "Maddy"
    :selector [:name]}] 

  (equals-field [:name] [:name])
  []

  (equals-field [:name] [:confirm_name])
  [{:type :vlad.validations/equals-field
    :first-selector [:name]
    :second-selector [:confirm_name]}]
  
  (attr [:name] (matches #"..ris"))
  []
  
  (attr [:name] (matches #"andy"))
  (just [(contains {:type :vlad.validations/matches
                              :selector [:name]})]))

