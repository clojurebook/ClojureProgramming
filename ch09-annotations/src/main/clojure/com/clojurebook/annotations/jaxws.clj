(ns com.clojurebook.annotations.jaxws
  (:import (javax.jws WebService WebMethod)
           javax.xml.ws.Endpoint))

(definterface
  ^{WebService {:targetNamespace "com.clojurebook.annotations.jaxrs"}}
  EchoService
  (^{WebMethod true} echo [^String message]))

(deftype ^{WebService {:endpointInterface
                       "com.clojurebook.annotations.jaxrs.EchoService"}}
          EchoServiceImpl []
  EchoService
  (echo [this message] message))

; to run, load this file in your REPL and invoke:
;
; => (Endpoint/publish "http://localhost:8080/echo" (EchoServiceImpl.))
;
; The service's WSDL will be available at http://localhost:8080/echo?wsdl

