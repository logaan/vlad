(ns vlad.test.default-errors
  (:require [vlad.core :as v]
            [#?(:cljs cljs.test :clj clojure.test) :as t]))

(t/deftest default-errors
  (t/are
      [?errors ?translations]
      (= (v/translate-errors ?errors v/english-translation) ?translations)

    [{:type :vlad.core/present :selector [:password] :name "Password"}]
    {[:password] ["Password is required."]}

    [{:message "Custom message." :selector [:password]}]
    {[:password] ["Custom message."]}

    [{:type :vlad.core/length-over :selector [:password] :name "Password" :size 8}]
    {[:password] ["Password must be over 8 characters long."]}

    [{:type :vlad.core/one-of :selector [:title] :name "Title" :set #{"Mr"}}]
    {[:title] ["Title must be one of Mr."]}

    [{:type :vlad.core/not-of :selector [:username] :name "Username" :set #{"login"}}]
    {[:username] ["Username must not be one of login."]}

    [{:type :vlad.core/equals-value :selector [:over_18] :name "Over 18" :value "yes"}]
    {[:over_18] ["Over 18 must be \"yes\"."]}

    [{:type :vlad.core/equals-field
      :first-name "Password" :first-selector [:password]
      :second-name "Password confirmation" :second-selector [:password-confirmation]}]
    {nil ["Password must be the same as Password confirmation."]}

    [{:type :vlad.core/matches :name "Username" :selector [:username] :pattern #"\w+"}]
    {[:username] #?(:clj ["Username must match the pattern \\w+."]
                         :cljs ["Username must match the pattern /\\w+/."])})

  (t/is (= (v/assign-names (v/validate (v/attr [:foo] (v/present)) {})
                           {[:foo] "Foozle"})
           [{:name "Foozle" :type :vlad.core/present :selector [:foo]}]))

  (t/is (= (v/assign-names (v/validate (v/attr [:foo] (v/present {:name "Kitten"})) {})
                           {[:foo] "Foozle"})
           [{:name "Kitten" :type :vlad.core/present :selector [:foo]}]))

  (t/is (= [{:selector [:kittens], :name "Kittens"}
            {:selector [:long-word-example], :name "Long word example"}
            {:selector [:address :postcode], :name "Postcode"}]
           (v/guess-field-names [{:selector [:kittens]}
                                 {:selector [:long-word-example]}
                                 {:selector [:address :postcode]}])))

  (t/is (= {[:name] ["Name is required."]}
           (v/field-errors (v/attr [:name] (v/present)) {})))

  (t/is (= {[:name] ["Kittens is required."]}
           (v/field-errors (v/attr [:name] (v/present {:name "Kittens"})) {})))

  (t/is (= ["Name is required."]
           (v/flat-errors (v/attr [:name] (v/present)) {})))

  (t/is (= ["Name is required."]
           (v/flat-errors (v/attr [:name] (v/present)) {}))))

