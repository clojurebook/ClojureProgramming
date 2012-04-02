(ns com.clojurebook.hello-world
  (:use
    [compojure.core :only (GET defroutes)]
    [compojure.route :only (resources)]))

(defroutes routes
  (resources "/")
  (GET "*" {:keys [uri]}
       (format "<html>
                URL requested: %s
                <p>
                  <a href=\"/wright_pond.jpg\">
                    Image served by compojure.route/resources
                  </a>
                </p>
                </html>"
               uri)))