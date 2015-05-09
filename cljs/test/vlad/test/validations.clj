(ns vlad.test.validations
  (:require [vlad.core :refer :all]
            [midje.sweet :refer [tabular fact just contains]]))

(tabular
  (fact (validate ?validator {:name "Chris" :confirm_name "Brad" :number-of-teeth 32}) => ?errors)
  ?validator ?errors

  (attr [:name] present)
  []                   

  (attr [:number-of-teeth] present)
  [{:type :vlad.core/present
    :selector [:number-of-teeth]}] 

  (attr [:age] present)
  [{:type :vlad.core/present
    :selector [:age]}] 

  (attr [:name] (length-over 4))
  []                   

  (attr [:name] (length-over 9))
  [{:type :vlad.core/length-over
    :size 9
    :selector [:name]}] 

  (attr [:name] (length-under 9))
  []                   

  (attr [:name] (length-under 4))
  [{:type :vlad.core/length-under
    :size 4
    :selector [:name]}] 

  (attr [:name] (length-in 4 9))
  []                   

  (attr [:name] (length-in 9 4))
  [{:type :vlad.core/length-over
    :size 9
    :selector [:name]}
   {:type :vlad.core/length-under
    :size 4
    :selector [:name]}]

  (attr [:name] (one-of #{"Chris" "Fred"}))
  []

  (attr [:name] (one-of #{"Thelma" "Luise"}))
  [{:type :vlad.core/one-of
    :set #{"Thelma" "Luise"}
    :selector [:name]}]

  (attr [:name] (not-of #{"Thelma" "Luise"}))
  []

  (attr [:name] (not-of #{"Chris" "Fred"}))
  [{:type :vlad.core/not-of
    :set #{"Chris" "Fred"}
    :selector [:name]}]

  (attr [:name] (equals-value "Chris"))
  []

  (attr [:name] (equals-value "Maddy"))
  [{:type :vlad.core/equals-value
    :value "Maddy"
    :selector [:name]}] 

  (equals-field [:name] [:name])
  []

  (equals-field [:name] [:confirm_name])
  [{:type :vlad.core/equals-field
    :first-selector [:name]
    :second-selector [:confirm_name]}]
  
  (attr [:name] (matches #"..ris"))
  []
  
  (attr [:name] (matches #"andy"))
  (just [(contains {:type :vlad.core/matches
                              :selector [:name]})]))

