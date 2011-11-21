(defproject kirja "0.1.0-SNAPSHOT"
  :description "Literate programming with markdown"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.markdownj/markdownj "0.3.0-1.0.2b4"]]
  :dev-dependencies [[midje "1.3-alpha5"]]
  :run-aliases {:weave kirja.weave/weave
                :tangle kirja.tangle/tangle})