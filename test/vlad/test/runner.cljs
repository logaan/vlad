(ns vlad.test.runner
  (:require [cljs.test :as t]
            vlad.test.validation-types
            vlad.test.validations))

; Required to avoid 0 failures... appearing on the same line as Ran 3 tests..
(set! *print-newline* true)

(set! *print-fn*
      (fn [& args]
        (.apply (.-write js/document) js/document (into-array args))))

(println "<pre>")
(t/run-all-tests)
(println "</pre>")
