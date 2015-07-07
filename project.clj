(defproject vlad "3.0.0"
  :description "Vlad is an attempt at providing convenient and simple
               validations. Vlad is purely functional and makes no assumptions
               about your data. It can be used for validating html form data
               just as well as it can be used to validate your csv about cats."
  :url "https://github.com/logaan/vlad"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "0.0-3308"]]
  :profiles {:doc {}
            :test {:dependencies [[org.clojure/tools.namespace "0.2.11"]]}}
  :test-matcher #"vlad\.test\..*"

  :plugins  [[lein-cljsbuild "1.0.6"]]
  :cljsbuild {:builds {:test {:source-paths ["src" "test"]
                              :notify-command ["phantomjs" "resources/test/test.js"]
                              :compiler {:output-to "resources/test/compiled.js"
                                         :optimizations :whitespace
                                         :pretty-print true}}}})
