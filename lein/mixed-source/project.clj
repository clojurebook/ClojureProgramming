(defproject com.clojurebook/lein-mixed-source "1.0.0"
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :aot :all)

(require '(leiningen compile javac))

(add-hook #'leiningen.compile/compile
  (fn [compile project & args]
    (apply compile project args)
    (leiningen.javac/javac (assoc project :java-source-path "src"))))