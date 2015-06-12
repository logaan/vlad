(require 'cljs.build.api)

(cljs.build.api/watch
  "test"
  {:main 'vlad.core
   :output-dir "cljs/out"
   :output-to "cljs/out/test.js"})
