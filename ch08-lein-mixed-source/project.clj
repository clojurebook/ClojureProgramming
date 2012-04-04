(defproject com.clojurebook/lein-mixed-source "1.0.0"
  :description "An example of building a mixed-source project in
Leiningen, with the additional requirement that some Java sources depend
upon a type defined in the Clojure sources.  From chapter 8 of 'Clojure
Programming' by Emerick, Carper, and Grand."
  :url "http://github.com/clojurebook/ClojureProgramming"
  :dependencies [[org.clojure/clojure "1.3.0"]]
  :aot :all)

(require '(leiningen compile javac)
         'robert.hooke)

(robert.hooke/add-hook #'leiningen.compile/compile
  (fn [compile project & args]
    (let [compile-result (apply compile project args)]
      (leiningen.javac/javac (assoc project
                               ;; Leiningen 1 uses :java-source-path
                               :java-source-path "srcj"
                               ;; Lein 2 uses :java-source-paths
                               :java-source-paths ["srcj"]))
      compile-result)))
