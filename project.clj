(defproject vlad "1.2.0"
  :description "Vlad is an attempt at providing convenient and simple
               validations. Vlad is purely functional and makes no assumptions
               about your data. It can be used for validating html form data
               just as well as it can be used to validate your csv about cats."
  :url "https://github.com/logaan/vlad"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [potemkin "0.3.3"]]
  :profiles {:doc {}
             :dev {:source-paths  ["dev"]
                   :plugins  [[lein-midje "3.1.1"]]
                   :dependencies [[midje "1.5.1"]]}})

