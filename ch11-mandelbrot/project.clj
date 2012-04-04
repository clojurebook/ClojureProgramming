(defproject com.clojurebook.mandelbrot "1.0.0-SNAPSHOT"
  :description "A Mandelbrot Set implementation in Clojure that
demonstrates the usage and impact of primitive type declarations on the
runtime of numerically-intensive algorithms. From chapter 11 of 'Clojure
Programming' by Emerick, Carper, and Grand."
  :url "http://github.com/clojurebook/ClojureProgramming"
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :main ^:skip-aot com.clojurebook.mandelbrot
  :run-aliases {:fast com.clojurebook.mandelbrot/-fast-main})
