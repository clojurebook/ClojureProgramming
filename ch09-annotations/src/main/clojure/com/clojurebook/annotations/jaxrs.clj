(ns com.clojurebook.annotations.jaxrs
  (:import (javax.ws.rs Path PathParam Produces GET)))

(definterface Greeting
  (greet [^String visitor-name]))

(deftype ^{Path "/greet/{visitorname}"} GreetingResource []
  Greeting
  (^{GET true
     Produces ["text/plain"]}
    greet
    [this ^{PathParam "visitorname"} visitor-name]
    (format "Hello %s!" visitor-name)))

; to run, compile this namespace; then, in your REPL, invoke:
;
;   => (com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory/create
;        "http://localhost:8080/"
;        {"com.sun.jersey.config.property.packages" "com.clojurebook.annotations.jaxrs"})
;
; The service's WADL will be available at http://localhost:8080/application.wadl,
; and URLs like http://localhost:8080/greet/James will say hello.