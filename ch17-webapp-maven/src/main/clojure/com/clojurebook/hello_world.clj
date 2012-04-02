(ns com.clojurebook.hello-world
  (:use
    [ring.util.servlet :only (defservice)]
    [compojure.core :only (GET)])
  (:gen-class
    :extends javax.servlet.http.HttpServlet))

(defservice
  (GET "*" {:keys [uri]}
       (format "<html>
                URL requested: %s
                <p>
                  <a href=\"/wright_pond.jpg\">
                    Image served by app server via web.xml <servlet-mapping>
                  </a>
                </p>
                </html>"
               uri)))