(ns vlad.test.validations
  (:require [vlad.core :as v]
            [#?(:cljs cljs.test :clj clojure.test) :as t]))

(def data
  {:name "Chris" :confirm_name "Brad" :number-of-teeth 32})

(t/deftest validations
  (t/are
    [?validator ?errors]
    (= ?errors (v/validate ?validator data))

    (v/attr [:name] (v/present))
    []

    (v/attr [:number-of-teeth] (v/present {:warning true}))
    [{:type :vlad.core/present
      :selector [:number-of-teeth]
      :warning true}]

    (v/attr [:age] (v/present))
    [{:type :vlad.core/present
      :selector [:age]}]

    (v/attr [:name] (v/length-over 4))
    []

    (v/attr [:name] (v/length-over 5 {:severity :warning}))
    [{:type :vlad.core/length-over
      :size 5
      :selector [:name]
      :severity :warning}]

    (v/attr [:name] (v/length-under 6))
    []

    (v/attr [:name] (v/length-under 5 {:severity :major}))
    [{:type :vlad.core/length-under
      :size 5
      :selector [:name]
      :severity :major}]

    (v/attr [:name] (v/length-in 4 9))
    []

    (v/attr [:name] (v/length-in 9 4 {:foo :bar}))
    [{:type :vlad.core/length-over
      :size 9
      :selector [:name]
      :foo :bar}
     {:type :vlad.core/length-under
      :size 4
      :selector [:name]
      :foo :bar}]

    (v/attr [:name] (v/one-of #{"Chris" "Fred"}))
    []

    (v/attr [:name] (v/one-of #{"Thelma" "Luise"}))
    [{:type :vlad.core/one-of
      :set #{"Thelma" "Luise"}
      :selector [:name]}]

    (v/attr [:name] (v/not-of #{"Thelma" "Luise"}))
    []

    (v/attr [:name] (v/not-of #{"Chris" "Fred"}))
    [{:type :vlad.core/not-of
      :set #{"Chris" "Fred"}
      :selector [:name]}]

    (v/attr [:name] (v/equals-value "Chris"))
    []

    (v/attr [:name] (v/equals-value "Maddy"))
    [{:type :vlad.core/equals-value
      :value "Maddy"
      :selector [:name]}]

    (v/equals-field [:name] [:name])
    []

    (v/equals-field [:name] [:confirm_name])
    [{:type :vlad.core/equals-field
      :first-selector [:name]
      :second-selector [:confirm_name]}]

    (v/attr [:name] (v/matches #"..ris"))
    [])

  (let [error (first (v/validate (v/attr [:name] (v/matches #"andy" {:type ::andy-match})) data))]
    (t/is (= ::andy-match (:type error)))
    (t/is (= [:name] (:selector error)))))

