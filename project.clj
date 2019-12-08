(defproject vlad "3.3.3-SNAPSHOT"
  :description "Vlad is an attempt at providing convenient and simple
               validations. Vlad is purely functional and makes no assumptions
               about your data. It can be used for validating html form data
               just as well as it can be used to validate your csv about cats."
  :url "https://github.com/logaan/vlad"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1" :scope "provided"]
                 [org.clojure/clojurescript "1.10.520" :scope "provided"]]
  :profiles {:doc  {}
             :test {:dependencies   [[org.clojure/tools.namespace "0.2.11"]]
                    :resource-paths ["test-resources"]
                    :plugins        [[quickie "0.4.1"]
                                     [lein-cljsbuild "1.1.7"]]}}
  :test-matcher #"vlad\.test\..*"

  :cljsbuild {:builds {:test {:source-paths ["src" "test"]
                              :notify-command ["phantomjs" "test-resources/test/test.js"]
                              :compiler {:output-to "test-resources/test/compiled.js"
                                         :optimizations :whitespace
                                         :pretty-print true}}}})
