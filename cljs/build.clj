(require 'cljs.build.api)

(cljs.build.api/watch
  "src"
  {:main 'vlad.core
   :output-dir "cljs/out"
   :output-to "cljs/out/main.js"})
