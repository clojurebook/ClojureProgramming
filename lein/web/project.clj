(defproject com.clojurebook/url-shortener "1.0.0-SNAPSHOT"
  :description "A toy URL shortener HTTP service written using Ring and Compojure."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [compojure "1.0.1"]
		 		         [ring "1.0.1"]]
  :plugins [[lein-beanstalk "0.2.2"]]
  :ring {:handler com.clojurebook.url-shortener.beanstalk/app})
