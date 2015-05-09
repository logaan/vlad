(require 'cljs.repl)
(require 'cljs.repl.nashorn)

(cljs.repl/repl (cljs.repl.nashorn/repl-env))
