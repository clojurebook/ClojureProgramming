(defproject com.clojurebook.concurrency.webcrawler "1.0.0-SNAPSHOT"
  :description "A naive agent-based webcrawler, explored in chapter 4 of
'Clojure Programming' by Emerick, Carper, and Grand."
  :url "http://github.com/clojurebook/ClojureProgramming"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [enlive "1.0.0"]]
  :profiles {:1.4 {:dependencies [[org.clojure/clojure "1.4.0-beta6"]]}}
  :main ^:skip-aot com.clojurebook.concurrency.webcrawler)
