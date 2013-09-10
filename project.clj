(defproject vlad "1.0.0"
  :description "Vlad is an attempt at providing convenient and simple
               validations. Vlad is purely functional and makes no assumptions
               about your data. It can be used for validating html form data as
               just as well as it can be used to validate your csv about cats."
  :url "https://github.com/logaan/vlad"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:source-paths  ["dev"]
                   :dependencies [[lein-marginalia "0.7.1"]
                                  [midje "1.5.1"]
                                  [com.stuartsierra/lazytest "1.2.3"]
                                  [clj-ns-browser "1.3.1"]]}})

