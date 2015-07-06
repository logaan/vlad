(ns vlad.test.runner
   (:require [cljs.test :refer-macros [run-all-tests]]
            vlad.test.validation-types
            vlad.test.validations
            vlad.test.readme
            vlad.test.default-errors))


; Props these bloggers who enabled the autotest suite:
; - https://nvbn.github.io/2015/06/08/cljs-test/
; - http://noprompt.github.io/clojurescript/testing/ruby/2014/01/25/autotesting-clojurescript.html

(enable-console-print!)

(defn ^:export run []
  (run-all-tests #"vlad.test.*"))

