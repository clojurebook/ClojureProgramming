(defproject com.clojurebook/sample-lein-web-project "1.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [compojure/compojure "1.0.0"]
                 [ring/ring-servlet "1.0.1"]]
  :plugins [[lein-ring "0.6.2"]]
  :ring {:handler com.clojurebook.hello-world/routes})
