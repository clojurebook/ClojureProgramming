(defproject game-of-life "1.0.0-SNAPSHOT"
  :description "A generic life-like automaton by Emerick, Carper, and Grand."
  :url "http://github.com/clojurebook/ClojureProgramming"
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :profiles {:1.4 {:dependencies [[org.clojure/clojure "1.4.0-beta6"]]}}
  :run-aliases {:rect com.clojurebook.collections.life/rect-demo})
