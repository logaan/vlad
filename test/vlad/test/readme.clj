(ns vlad.test.readme
  (:require [vlad :refer :all]
            [midje.sweet :refer [fact]]))

; Basics
(def validation
  (present [:age]))

(def data
  {:name "Logan Campbell"})

(fact (validate validation data)
      => [{:type :vlad.validations/present
           :selector [:age]}])

; Composition
(def common
  (join (present [:name])
        (present [:email])))

(def password
  (chain (present [:password])
         (join (length-in 6 128 [:password])
               (matches #"[a-zA-Z]" [:password])
               (matches #"[0-9]" [:password]))
         (equals-field [:password] [:confirmation])))

(def signup
  (join common password))

(def update
  common)

(fact (validate signup {:name "Logan Campbell"})
      => '({:selector [:email],    :type :vlad.validations/present}
           {:selector [:password], :type :vlad.validations/present}))

; Translation
(def english-field-names
  {[:name]         "Full Name"
   [:email]        "Email Address"
   [:password]     "Password"
   [:confirmation] "Password Confirmation"})

(fact (-> (validate signup {:password "!"})
          (assign-name english-field-names)
          (translate-errors english-translation))

      => {[:password] ["Password must be over 6 characters long."
                       "Password must match the pattern [a-zA-Z]."
                       "Password must match the pattern [0-9]."],
          [:email]    ["Email Address is required."],
          [:name]     ["Full Name is required."]})

(def chinese-field-names
  {[:name]         "姓名"
   [:email]        "邮箱"
   [:password]     "密码"
   [:confirmation] "确认密码"})

(defmulti chinese-translation :type)

(defmethod chinese-translation :vlad.validations/present
  [{:keys [name]}]
  (format "请输入%s" name))

; Other validation translations go here.

(fact (-> (validate update {:name "Rich"})
          (assign-name chinese-field-names)
          (translate-errors chinese-translation))

      => {[:email] ["请输入邮箱"]})
