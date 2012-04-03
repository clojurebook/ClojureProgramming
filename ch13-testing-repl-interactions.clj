;-----
(defn get-address
  [username]
  ;; access database
  )


;-----
(with-redefs [address-lookup (constantly "123 Main St.")]
  (println (address-lookup)))
; 123 Main St.


;-----
(use 'clojure.test)

(is (= 5 (+ 4 2)) "I never was very good at math...")
; FAIL in clojure.lang.PersistentList$EmptyList@1 (NO_SOURCE_FILE:1)
; I was never very good at math...
; expected: (= 5 (+ 4 2))
;   actual: (not (= 5 6))
;= false

(is (re-find #"foo" "foobar"))
;= "foo"


;-----
(is (thrown? ArithmeticException (/ 1 0)))
;= #<ArithmeticException java.lang.ArithmeticException: Divide by zero>
(is (thrown? ArithmeticException (/ 1 1)))
; FAIL in clojure.lang.PersistentList$EmptyList@1 (NO_SOURCE_FILE:1)
; expected: (thrown? ArithmeticException (/ 1 1))
;   actual: nil
;= nil


;-----
(is (thrown-with-msg? ArithmeticException #"zero" (/ 1 0)))
;= #<ArithmeticException java.lang.ArithmeticException: Divide by zero>
(is (thrown-with-msg? ArithmeticException #"zero" (inc Long/MAX_VALUE)))
; FAIL in clojure.lang.PersistentList$EmptyList@1 (NO_SOURCE_FILE:1)
; expected: (thrown-with-msg? ArithmeticException #"zero" (inc Long/MAX_VALUE))
;   actual: #<ArithmeticException java.lang.ArithmeticException: integer overflow>
;= #<ArithmeticException java.lang.ArithmeticException: integer overflow>


;-----
(testing "Strings"
  (testing "regex"
    (is (re-find #"foo" "foobar"))
    (is (re-find #"foo" "bar")))
  (testing ".contains"
    (is (.contains "foobar" "foo"))))
; FAIL in clojure.lang.PersistentList$EmptyList@1 (NO_SOURCE_FILE:1)
; Strings regex
; expected: (re-find #"foo" "bar")
;   actual: (not (re-find #"foo" "bar"))


;-----
(deftest test-foo
  (is (= 1 1)))
;= #'user/test-foo
(test-foo)
;= nil


;-----
(:test (meta #'test-foo))
;= #<user$fn__366 user$fn__366@4e842e74>


;-----
(with-test
  (defn hello [name]
    (str "Hello, " name))
  (is (= (hello "Brian") "Hello, Brian"))
  (is (= (hello nil) "Hello, nil")))
;= #'user/hello


;-----
(hello "Judy")
;= "Hello, Judy"


;-----
((:test (meta #'hello)))
; FAIL in clojure.lang.PersistentList$EmptyList@1 (NO_SOURCE_FILE:5)
; expected: (= (hello nil) "Hello, nil")
;   actual: (not (= "Hello, " "Hello, nil"))
;= false


;-----
(run-tests)
; Testing user
; 
; FAIL in (hello) (NO_SOURCE_FILE:5)
; expected: (= (hello nil) "Hello, nil")
;   actual: (not (= "Hello, " "Hello, nil"))
; 
; Ran 2 tests containing 3 assertions.
; 1 failures, 0 errors.
;= {:type :summary, :pass 2, :test 2, :error 0, :fail 1}


;-----
(ns-unmap *ns* 'hello)
;= nil
(run-tests)
; Testing user
; 
; Ran 1 tests containing 1 assertions.
; 0 failures, 0 errors.
;= {:type :summary, :pass 1, :test 1, :error 0, :fail 0}


;-----
(with-test
  (defn hello [name]
    (str "Hello, " name))
  (is (= (hello "Brian") "Hello, Brian"))
  (is (= (hello nil) "Hello, nil")))
;= #'user/hello
(alter-meta! #'hello dissoc :test)
;= {:ns #<Namespace user>, :name hello, :arglists ([name]),
;=  :line 2, :file "NO_SOURCE_PATH"}
(run-tests *ns*)
; Testing user
; 
; Ran 1 tests containing 1 assertions.
; 0 failures, 0 errors.
;= {:type :summary, :pass 1, :test 1, :error 0, :fail 0}
(hello "Rebecca")
;= "Hello, Rebecca"


;-----
(deftest a
  (is (== 0 (- 3 2))))
;= #'user/a
(deftest b (a))
;= #'user/b
(deftest c (b))
;= #'user/c
(c)
; FAIL in (c b a) (NO_SOURCE_FILE:2)
; expected: (== 0 (- 3 2))
;   actual: (not (== 0 1))


;-----
(run-tests)
; Testing user
; 
; FAIL in (b a) (NO_SOURCE_FILE:2)
; expected: (== 0 (- 3 2))
;   actual: (not (== 0 1))
; 
; FAIL in (c b a) (NO_SOURCE_FILE:2)
; expected: (== 0 (- 3 2))
;   actual: (not (== 0 1))
; 
; FAIL in (a) (NO_SOURCE_FILE:2)
; expected: (== 0 (- 3 2))
;   actual: (not (== 0 1))
; 
; Ran 6 tests containing 3 assertions.
; 3 failures, 0 errors.
;= {:type :summary, :pass 0, :test 6, :error 0, :fail 3}


;-----
(defn test-ns-hook [] (c))
;= #'user/test-ns-hook
(run-tests)
; Testing user
; 
; FAIL in (c b a) (NO_SOURCE_FILE:2)
; expected: (== 0 (- 3 2))
;   actual: (not (== 0 1))
; 
; Ran 3 tests containing 1 assertions.
; 1 failures, 0 errors.
;= {:type :summary, :pass 0, :test 3, :error 0, :fail 1}


;-----
(ns-unmap *ns* 'test-ns-hook)
;= nil
(defn a
  []
  (is (== 0 (- 3 2))))
;= #'user/a
(defn b [] (a))
;= #'user/b
(deftest c (b))
;= #'user/c
(run-tests)
; Testing user
; 
; FAIL in (c) (NO_SOURCE_FILE:3)
; expected: (== 0 (- 3 2))
;   actual: (not (== 0 1))
; 
; Ran 1 tests containing 1 assertions.
; 1 failures, 0 errors.
;= {:type :summary, :pass 0, :test 1, :error 0, :fail 1}


;-----
(defn some-fixture
  [f]
  (try
    ;; set up database connections, load test data,
    ;; mock out functions using `with-redefs` or `binding`, etc.
    (f)
    (finally
      ;; clean up database connections, files, etc.
      )))


;-----
(defprotocol Bark
  (bark [this]))

(defrecord Chihuahua [weight price]
  Bark
  (bark [this] "Yip!"))

(defrecord PetStore [dog])

(defn configured-petstore
  []
  (-> "petstore-config.clj"
    slurp
    read-string
    map->PetStore))


;-----
(def ^:private dummy-petstore (PetStore. (Chihuahua. 12 "$84.50")))

(deftest test-configured-petstore
  (is (= (configured-petstore) dummy-petstore)))


;-----
(run-tests)
; Testing user
; 
; ERROR in (test-configured-petstore) (FileInputStream.java:-2)
; expected: (= (configured-petstore) dummy-petstore)
;   actual: java.io.FileNotFoundException: petstore-config.clj (No such file or directory)
;  at java.io.FileInputStream.open (FileInputStream.java:-2)
;     ...
; 
; Ran 1 tests containing 1 assertions.
; 0 failures, 1 errors.
;= {:type :summary, :pass 0, :test 1, :error 1, :fail 0}


;-----
(defn petstore-config-fixture
  [f]
  (let [file (java.io.File. "petstore-config.clj")]
    (try
      (spit file (with-out-str (pr dummy-petstore)))
      (f)
      (finally
        (.delete file)))))


;-----
(use-fixtures :once petstore-config-fixture)


;-----
(run-tests)
; Testing user
;
; Ran 1 tests containing 1 assertions.
; 0 failures, 0 errors.
;= {:type :summary, :pass 1, :test 1, :error 0, :fail 0}


;-----
[:html 
 [:head [:title "Propaganda"]]
 [:body [:p "Visit us at "
         [:a {:href "http://clojureprogramming.com"} 
          "our website"]
         "."]]]


;-----
<html>
  <head><title>Propaganda</title></head>
  <body>
    <p>Visit us at <a href="http://clojurebook.com">our website</a>.</p>
  </body>
</html>


;-----
(deftest test-addition
  (are [x y z] (= x (+ y z))      
    10 7 3
    20 10 10
    100 89 11))


;-----
(do
  (clojure.test/is (= 10 (+ 7 3)))
  (clojure.test/is (= 20 (+ 10 10)))
  (clojure.test/is (= 100 (+ 89 11))))


;-----
(defmacro are* [f & body]
  `(are [x# y#] (~'= (~f x#) y#)
     ~@body))


;-----
(deftest test-tostring
  (are* str
    10 "10"
    :foo ":foo"
    "identity" "identity"))


;-----
(require 'clojure.string)

(declare html attrs)

(deftest test-html
  (are* html    
    [:html]
    "<html></html>"

    [:a [:b]]
    "<a><b></b></a>"

    [:a {:href "/"} "Home"]
    "<a href=\"/\">Home</a>"

    [:div "foo" [:span "bar"] "baz"]
    "<div>foo<span>bar</span>baz</div>"))

(deftest test-attrs
  (are* (comp clojure.string/trim attrs)
    nil ""

    {:foo "bar"}
    "foo=\"bar\""

    (sorted-map :a "b" :c "d")
    "a=\"b\" c=\"d\""))


;-----
(defn attrs
  [attr-map]
  (->> attr-map
    (mapcat (fn [[k v]] [k " =\"" v "\""]))
    (apply str)))

(defn html
  [x]
  (if-not (sequential? x)
    (str x)
    (let [[tag & body] x
          [attr-map body] (if (map? (first body))
                            [(first body) (rest body)]
                            [nil body])]
      (str "<" (name tag) (attrs attr-map) ">"
           (apply str (map html body))
           "</" (name tag) ">"))))


;-----
(run-tests)
; Testing user
; 
; FAIL in (test-html) (NO_SOURCE_FILE:6)
; expected: (= (html [:a {:href "/"} "Home"]) "<a href=\"/\">Home</a>")
;   actual: (not (= "<a:href =\"/\">Home</a>" "<a href=\"/\">Home</a>"))
; 
; FAIL in (test-attrs) (NO_SOURCE_FILE:20)
; expected: (= ((comp clojure.string/trim attrs) {:foo "bar"}) "foo=\"bar\"")
;   actual: (not (= ":foo =\"bar\"" "foo=\"bar\""))
; 
; FAIL in (test-attrs) (NO_SOURCE_FILE:20)
; expected: (= ((comp clojure.string/trim attrs)
;               (sorted-map :a "b" :c "d"))
;             "a=\"b\" c=\"d\"")
;   actual: (not (= ":a =\"b\":c =\"d\"" "a=\"b\" c=\"d\""))
; 
; Ran 2 tests containing 7 assertions.
; 3 failures, 0 errors.
;= {:type :summary, :pass 4, :test 2, :error 0, :fail 3}


;-----
(defn attrs
  [attrs]
  (->> attrs
    (mapcat (fn [[k v]] [(name k) "=\"" v "\""]))
    (apply str)))


;-----
(test-attrs)
; FAIL in (test-attrs) (NO_SOURCE_FILE:20)
; expected: (= ((comp clojure.string/trim attrs)
;               (sorted-map :a "b" :c "d"))
;             "a=\"b\" c=\"d\"")
;   actual: (not (= ":a =\"b\":c =\"d\"" "a=\"b\" c=\"d\""))


;-----
(defn attrs
  [attrs]
  (->> attrs
    (mapcat (fn [[k v]] [\space (name k) "=\"" v "\""]))
    (apply str)))


;-----
(test-attrs)
;= nil
(run-tests)
; Testing user
;
; Ran 2 tests containing 7 assertions.
; 0 failures, 0 errors.
;= {:type :summary, :pass 7, :test 2, :error 0, :fail 0}


;-----
(html [:html 
         [:head [:title "Propaganda"]]
         [:body [:p "Visit us at "
                 [:a {:href "http://clojureprogramming.com"} 
                  "our website"]
                 "."]]])
;= "<html>
;=    <head><title>Propaganda</title></head>
;=    <body>
;=      <p>Visit us at <a href=\"http://clojureprogramming.com\">our website</a>.</p>
;=    </body>
;= </html>"


;-----
(html (list* :ul (for [author ["Chas Emerick" "Christophe Grand" "Brian Carper"]]
                   [:li author])))
;= "<ul><li>Chas Emerick</li><li>Christophe Grand</li><li>Brian Carper</li></ul>"


;-----
{:tag :a, :attrs {:href "http://clojure.org"}, :content ["Clojure"]}


;-----
(html {:tag :a, :attrs {:href "http://clojure.org"}, :content ["Clojure"]})
;= "{:content [\"Clojure\"], :attrs {:href \"http://clojure.org\"}, :tag :a}"


;-----
(defn attrs
  [attrs]
  (assert (or (map? attr-map)
              (nil? attr-map)) "attr-map must be nil, or a map")
  (->> attrs
    (mapcat (fn [[k v]] [\space (name k) "=\"" v "\""]))
    (apply str)))

(attrs "hi")
;= #<AssertionError java.lang.AssertionError:
;=   Assert failed: attr-map must be nil, or a map
;=   (or (map? attr-map) (nil? attr-map))>


;-----
(set! *assert* false)
;= false
(defn attrs
  [attr-map]
  (assert (or (map? attr-map)
              (nil? attr-map)) "attr-map must be nil, or a map")
  (->> attr-map
    (mapcat (fn [[k v]] [\space (name k) "=\"" v "\""]))
    (apply str)))
;= #'user/attrs
(attrs "hi")
;= #<UnsupportedOperationException java.lang.UnsupportedOperationException:
;=   nth not supported on this type: Character>
(set! *assert* true)
;= true


;-----
(defn attrs
  [attr-map]
  {:pre [(or (map? attr-map)
             (nil? attr-map))]}
  (->> attr-map
    (mapcat (fn [[k v]] [\space (name k) "=\"" v "\""]))
    (apply str)))

(defn html
  [x]
  {:pre [(if (sequential? x)
           (some #(-> x first %) [keyword? symbol? string?])
           (not (map? x)))]
   :post [(string? %)]}
  (if-not (sequential? x)
    (str x)
    (let [[tag & body] x
          [attr-map body] (if (map? (first body))
                            [(first body) (rest body)]
                            [nil body])]
      (str "<" (name tag) (attrs attr-map) ">"
           (apply str (map html body))
           "</" (name tag) ">"))))


;-----
(html {:tag :a, :attrs {:href "http://clojure.org"}, :content ["Clojure"]})
;= #<AssertionError java.lang.AssertionError:
;=   Assert failed: (if (sequential? x)
;=                    (some (fn* [p1__843#] (-> x first p1__843#))
;=                          [keyword? symbol? string?])
;=                    (not (map? x)))>


