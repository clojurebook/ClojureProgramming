(ns com.clojurebook.url-shortener.beanstalk
  (:use [compojure.core :only (HEAD defroutes)])
  (:require [com.clojurebook.url-shortener :as shortener]
	        [compojure.core :as compojure]))

(compojure/defroutes app
  ; This HEAD route is here because Amazon's Elastic Beanstalk determines if
  ; your application is up by whether it responds successfully to a
  ; HEAD request at /
  (compojure/HEAD "/" [] "")
  shortener/app)
