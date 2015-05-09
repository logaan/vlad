(require 'cljs.build.api)

(cljs.build.api/watch
  "src"
  {:main 'vlad.core
   :output-to "out/main.js"})
