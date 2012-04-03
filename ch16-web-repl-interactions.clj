;-----
{:remote-addr "127.0.0.1",
 :scheme :http,
 :request-method :get,
 :query-string "q=Acme",
 :content-type nil,               
 :uri "/accounts",
 :server-name "company.com",
 :content-length nil,             
 :server-port 8080,
 :body #<ByteArrayInputStream java.io.ByteArrayInputStream@604fd0e9>,
 :headers
 {"user-agent" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6) Firefox/8.0.1",
  "accept-charset" "ISO-8859-1,utf-8;q=0.7,*;q=0.7",
  "accept" "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
  "accept-encoding" "gzip, deflate",
  "accept-language" "en-us,en;q=0.5",
  "connection" "keep-alive"}}


;-----
{:status 200
 :headers {"Content-Type" "text/html"}
 :body "<html>...</html>"}


;-----
{:status 200
 :headers {"Content-Type" "image/png"}
 :body (java.io.File. "/path/to/file.png")}


;-----
{:status 201 :headers {}}


;-----
[ring "1.0.0"]


;-----
(use '[ring.adapter.jetty :only (run-jetty)])     
;= nil
(defn app
  [{:keys [uri]}]
  {:body (format "You requested %s" uri)})
;= #'user/app
(def server (run-jetty #'app {:port 8080 :join? false}))
;= #'user/server


;-----
(defn app
  [{:keys [uri query-string]}]
  {:body (format "You requested %s with query %s" uri query-string)})
;= #'user/app


;-----
(use '[ring.middleware.params :only (wrap-params)])
;= nil
(defn app*
  [{:keys [uri params]}]
  {:body (format "You requested %s with query %s" uri params)})
;= #'user/app*
(def app (wrap-params app*))
;= #'user/app


;-----
[compojure "1.0.0"]
[ring "1.0.0"]


;-----
(def ^:private counter (atom 0))

(def ^:private mappings (ref {}))


;-----
(defn url-for
  [id]
  (@mappings id))

(defn shorten!
 "Stores the given URL under a new unique identifier, or the given identifier
  if provided.  Returns the identifier as a string.
  Modifies the global mapping accordingly." 
 ([url]
  (let [id (swap! counter inc)
        id (Long/toString id 36)]
    (or (shorten! url id)
        (recur url))))
 ([url id]
   (dosync
     (when-not (@mappings id)
       (alter mappings assoc id url)
       id))))


;-----
(shorten! "http://clojurebook.com")
;= "1"
(shorten! "http://clojure.org" "clj")
;= "clj"
(shorten! "http://id-already-exists.com" "clj")
;= nil
@mappings
;= {"clj" "http://clojure.org", "1" "http://clojurebook.com"}


;-----
(defn retain
  [& [url id :as args]]
  (if-let [id (apply shorten! args)]
    {:status 201
     :headers {"Location" id}
     :body (list "URL " url " assigned the short identifier " id)}
    {:status 409 :body (format "Short URL %s is already taken" id)}))


;-----
(require 'ring.util.response)

(defn redirect
  [id]
  (if-let [url (url-for id)]
    (ring.util.response/redirect url)                              
    {:status 404 :body (str "No such short URL: " id)}))


;-----
(use '[compojure.core :only (GET PUT POST defroutes)])
(require 'compojure.route)

(defroutes app*
  (GET "/" request "Welcome!")
  (PUT "/:id" [id url] (retain url id))
  (POST "/" [url] (retain url))
  (GET "/:id" [id] (redirect id))
  (GET "/list/" [] (interpose "\n" (keys @mappings)))
  (compojure.route/not-found "Sorry, there's nothing here."))


;-----
(PUT "/:id" [id url] (retain url id))


;-----
((PUT "/:id"
      [id url]
      (list "You requested that " url " be assigned id " id))
  {:uri "/some-id" :params {:url "http://clojurebook.com"} :request-method :put})
;= {:status 200, :headers {"Content-Type" "text/html"},
;=  :body ("You requested that " "http://clojurebook.com" " be assigned id " "some-id")}


;-----
((PUT ["/*/*/:id/:id"]
      [* id]
      (str * id))
  {:uri "/abc/xyz/foo/bar" :request-method :put})
;= {:status 200, :headers {"Content-Type" "text/html"},
;=  :body "[\"abc\" \"xyz\"][\"foo\" \"bar\"]"}


;-----
((PUT ["/:id" :id #"\d+"]
   [id url]
   (list "You requested that " url " be assigned id " id))
 {:uri "/some-id" :params {:url "http://clojurebook.com"} :request-method :put})
;= nil
((PUT ["/:id" :id #"\d+"]
   [id url]
   (list "You requested that " url " be assigned id " id))
  {:uri "/590" :params {:url "http://clojurebook.com"} :request-method :put})
;= {:status 200, :headers {"Content-Type" "text/html"},
;=  :body "You requested that http://clojurebook.com be assigned id 590"}


;-----
((PUT "/:id" req (str "You requested: " (:uri req)))
  {:uri "/foo" :request-method :put})
;= {:status 200, :headers {"Content-Type" "text/html"}, :body "You requested: /foo"}
((PUT "/:id" {:keys [uri]} (str "You requested: " uri))
  {:uri "/foo" :request-method :put})
;= {:status 200, :headers {"Content-Type" "text/html"}, :body "You requested: /foo"}


;-----
(require 'compojure.handler)

(def app (compojure.handler/api app*))


;-----
(use '[ring.adapter.jetty :only (run-jetty)])     
;= nil
(def server (run-jetty #'app {:port 8080 :join? false}))
;= #'user/server



;-----
(defroutes app+admin
  (GET "/admin/" request ...)
  (POST "/admin/some-admin-action" request ...)
  app*)



;-----
(require '[net.cgrand.enlive-html :as h])
;= nil
(h/sniptest "<h1>Lorem Ipsum</h1>")
;= "<h1>Lorem Ipsum</h1>"


;-----
(h/sniptest "<h1>Lorem Ipsum</h1>"
  [:h1] (h/content "Hello Reader!"))
;= "<h1>Hello Reader!</h1>"


;-----
(h/html-snippet "<p>x, <a id=\"home\" href=\"/\">y</a>, <a href=\"..\">z</a></p>")
;= ({:tag :p,
;=   :attrs nil,
;=   :content
;=   ("x, "
;=    {:tag :a, :attrs {:href "/", :id "home"}, :content ("y")}
;=    ", "
;=    {:tag :a, :attrs {:href ".."}, :content ("z")})})


;-----
(h/sniptest "<p>x, <a id=\"home\" href=\"/\">y</a>, <a href=\"..\">z</a></p>"
  [:a#home] (h/set-attr :href "http://clojurebook.com")
  [[:a (h/attr= :href "..")]] (h/content "go up"))
;= "<p>x, <a href=\"http://clojurebook.com\" id=\"home\">y</a>, <a href=\"..\">go up</a></p>"


;-----
(h/sniptest "<p class=\"\"><a href=\"\" class=\"\"></a></p>"
  [[:p (h/attr? :class)]] (h/content "XXX"))
;= "<p class=\"\">XXX</p>"

(h/sniptest "<p class=\"\"><a href=\"\" class=\"\"></a></p>"
  [:p (h/attr? :class)] (h/content "XXX"))
;= "<p class=\"\"><a class=\"\" href=\"\">XXX</a></p>"


;-----
(defn some-attr= 
 "Selector step, matches elements where at least one attribute
  has the specified value."
 [value]
 (h/pred (fn [node]
           (some #{value} (vals (:attrs node))))))


;-----
(h/sniptest "<ul><li id=\"foo\">A<li>B<li name=\"foo\">C</li></ul>"
  [(some-attr= "foo")] (h/set-attr :found "yes"))
;= "<ul>
;=    <li found=\"yes\" id=\"foo\">A</li>
;=    <li>B</li>
;=    <li found=\"yes\" name=\"foo\">C</li>
;= </ul>"


;-----
(defn display
  [msg]
  (h/sniptest "<div><span class=\"msg\"></span></div>"
    [:.msg] (when msg (h/content msg))))
;= #'user/display
(display "Welcome back!")                           
;= "<div><span class=\"msg\">Welcome back!</span></div>"
(display nil)                                 
;= "<div></div>"


;-----
(defn display
  [msg]
  (h/sniptest "<div><span class=\"msg\"></span></div>"
    [:.msg] (if msg
              (h/content msg)
              (h/add-class "hidden"))))
;= #'user/display
(display nil)
;= "<div><span class=\"msg hidden\"></span></div>"


;-----
(defn countdown
  [n]
  (h/sniptest "<ul><li></li></ul>"
    [:li] (h/clone-for [i (range n 0 -1)]
            (h/content (str i)))))
;= #'user/countdown
(countdown 0)
;= "<ul></ul>"
(countdown 3)
;= "<ul><li>3</li><li>2</li><li>1</li></ul>"


;-----
(defn countdown
  [n]
  (h/sniptest "<ul><li id=\"foo\"></li></ul>"
    [:#foo] (h/do->
              (h/remove-attr :id)
              (h/clone-for [i (range n 0 -1)]
                (h/content (str i))))))
;= #'user/countdown
(countdown 3)
;= "<ul><li>3</li><li>2</li><li>1</li></ul>"



;-----
(h/defsnippet footer "footer.html" [:.footer]
  [message]
  [:.footer] (h/content message))


;-----
(footer "hello")
;= ({:tag :div, :attrs {:class "footer"}, :content ("hello")})



;-----
(h/deftemplate friends-list "friends.html"
  [username friends]
  [:.username] (h/content username)
  [:ul.friends :li] (h/clone-for [f friends]
                      (h/content f)))

(friends-list "Chas" ["Christophe" "Brian"])
;= ("<html>" "<body>" "<h1>" "Hello, " "<span class=\"username\">"
;=  "Chas" "</span>" "</h1>" "\n" "<p>These are your friends:</p>"
;=  "\n" "<ul class=\"friends\">" "<li>" "Christophe" "</li>" "<li>"
;=  "Brian" "</li>" "</ul>" "\n" "</body>" "</html>")


;-----
(h/deftemplate friends-list "friends.html"
  [username friends friend-class]
  [:.username] (h/content username)
  [:ul.friends :li] (h/clone-for [f friends]
                      (h/do-> (h/content f)
                              (h/add-class friend-class))))

(friends-list "Chas" ["Christophe" "Brian"] "programmer")
;= ("<html>" "<body>" "<h1>" "Hello, " "<span class=\"username\">" "Chas"
;=  "</span>" "</h1>" "\n" "<p>These are your friends:</p>" "\n"
;=  "<ul class=\"friends\">" "<" "li" " " "class" "=\"" "programmer" "\""
;=  ">" "Christophe" "</" "li" ">" "<" "li" " " "class" "=\"" "programmer"
;=  "\"" ">" "Brian" "</" "li" ">" "</ul>" "\n" "</body>" "</html>")


;-----
(h/deftemplate friends-list "friends.html"
  [username friends friend-class]
  [:.username] (h/content username)
  [:ul.friends :li] (h/clone-for [f friends]
                      (h/do-> (h/content f)
                              (h/add-class friend-class)))
  [:body] (h/append (footer (str "Goodbye, " username))))

(friends-list "Chas" ["Christophe" "Brian"] "programmer")
;= ("<html>" "<body>" "<h1>" "Hello, " "<span class=\"username\">" "Chas"
;= "</span>" "</h1>" "\n" "<p>These are your friends:</p>" "\n"
;= "<ul class=\"friends\">" "<" "li" " " "class" "=\"" "programmer" "\""
;= ">" "Christophe" "</" "li" ">" "<" "li" " " "class" "=\"" "programmer"
;= "\"" ">" "Brian" "</" "li" ">" "</ul>" "\n" "<div class=\"footer\">"
;= "Goodbye, Chas" "</div>" "</body>" "</html>")


