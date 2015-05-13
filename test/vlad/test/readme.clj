(ns vlad.test.readme
  (:require [vlad.core :refer :all]
            [clojure.test :refer :all]))

; Basics
(def validation
  (attr [:age] present))

(def invalid-data
  {:name "Logan Campbell"})

(deftest basics
  (is (= (validate validation invalid-data)
         [{:type :vlad.core/present
           :selector [:age]}])))

; Composition
(def common
  (join (attr [:name] present)
        (attr [:email] present)))

(def password
  (chain (attr [:password] present)
         (attr [:password] (join (length-in 6 128)
                                 (matches #"[a-zA-Z]")
                                 (matches #"[0-9]")))
         (equals-field [:password] [:confirmation])))

(def signup
  (join common password))

(def edit
  common)

(deftest composition
  (is (= (validate signup {:name "Logan Campbell"})
      '({:selector [:email],    :type :vlad.core/present}
        {:selector [:password], :type :vlad.core/present}))))

; Translation
(def english-field-names
  {[:name]         "Full Name"
   [:email]        "Email Address"
   [:password]     "Password"
   [:confirmation] "Password Confirmation"})

(deftest translation
  (is (= (-> (validate signup {:password "!"})
             (assign-name english-field-names)
             (translate-errors english-translation))

         {[:password] ["Password must be over 6 characters long."
                       "Password must match the pattern [a-zA-Z]."
                       "Password must match the pattern [0-9]."],
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
  (format "请输入%s" name))

; Other validation translations go here.

(deftest translation-chinese
  (is (= (-> (validate edit {:name "Rich"})
             (assign-name chinese-field-names)
             (translate-errors chinese-translation))

         {[:email] ["请输入邮箱"]})))
