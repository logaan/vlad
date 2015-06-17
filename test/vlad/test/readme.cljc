(ns vlad.test.readme
  (:require [vlad.core :as v]
            [#?(:cljs cljs.test :clj clojure.test) :as t]))

; Basics
(def validation
  (v/attr [:age] v/present))

(def invalid-data
  {:name "Logan Campbell"})

(t/deftest basics
  (t/is (= (v/validate validation invalid-data)
           [{:type :vlad.core/present
             :selector [:age]}])))

; Composition
(def common
  (v/join (v/attr [:name] v/present)
          (v/attr [:email] v/present)))

(def password
  (v/chain (v/attr [:password] v/present)
         (v/attr [:password] (v/join (v/length-in 6 128)
                                     (v/matches #"[a-zA-Z]")
                                     (v/matches #"[0-9]")))
         (v/equals-field [:password] [:confirmation])))

(def signup
  (v/join common password))

(def edit
  common)

(t/deftest composition
  (t/is (= (v/validate signup {:name "Logan Campbell"})
           '({:selector [:email],    :type :vlad.core/present}
             {:selector [:password], :type :vlad.core/present}))))

; Translation
(def english-field-names
  {[:name]         "Full Name"
   [:email]        "Email Address"
   [:password]     "Password"
   [:confirmation] "Password Confirmation"})

(t/deftest translation
  (t/is (= (-> (v/validate signup {:password "!"})
               (v/assign-name english-field-names)
               (v/translate-errors v/english-translation))

           {[:password] #?(:cljs ["Password must be over 6 characters long."
                                  "Password must match the pattern /[a-zA-Z]/."
                                  "Password must match the pattern /[0-9]/."]
                           :clj ["Password must be over 6 characters long."
                                 "Password must match the pattern [a-zA-Z]."
                                 "Password must match the pattern [0-9]."])
            ,
            [:email]    ["Email Address is required."],
            [:name]     ["Full Name is required."]})))

(def chinese-field-names
  {[:name]         "姓名"
   [:email]        "邮箱"
   [:password]     "密码"
   [:confirmation] "确认密码"})

(defmulti chinese-translation :type)

(defmethod chinese-translation :vlad.core/present
  [{:keys [name]}]
  (str "请输入" name))

; Other validation translations go here.

(t/deftest translation-chinese
  (t/is (= (-> (v/validate edit {:name "Rich"})
               (v/assign-name chinese-field-names)
               (v/translate-errors chinese-translation))

           {[:email] ["请输入邮箱"]})))

