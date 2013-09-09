;-----
(defn average
  [numbers]
  (/ (apply + numbers) (count numbers)))


;-----
(defn average                             
  [numbers]                               
  (/ (apply + numbers) (count numbers)))  
;= #'user/average
(average [60 80 100 400])                 
;= 160


;-----
(println (average [60 80 100 400]))
; 160
;= nil                              


;-----
(read-string "42")
;= 42
(read-string "(+ 1 2)")
;= (+ 1 2)


;-----
(pr-str [1 2 3])
;= "[1 2 3]"
(read-string "[1 2 3]")
;= [1 2 3]


;-----
"hello there"
;= "hello there"


;-----
"multiline strings
are very handy"
;= "multiline strings\nare very handy"


;-----
(class \c)
;= java.lang.Character


;-----
\u00ff
;= \ÿ
\o41
;= \!


;-----
(def person {:name "Sandra Cruz"
             :city "Portland, ME"})
;= #'user/person
(:city person)
;= "Portland, ME"


;-----
(def pizza {:name "Ramunto's"
            :location "Claremont, NH"
            ::location "43.3734,-72.3365"})
;= #'user/pizza
pizza
;= {:name "Ramunto's", :location "Claremont, NH", :user/location "43.3734,-72.3365"}
(:user/location pizza)
;= "43.3734,-72.3365"


;-----
(name :user/location)
;= "location"
(namespace :user/location)
;= "user"
(namespace :location)
;= nil


;-----
(average [60 80 100 400])
;= 160


;-----
(class #"(p|h)ail")
;= java.util.regex.Pattern


;-----
(re-seq #"(...) (...)" "foo bar")
;= (["foo bar" "foo" "bar"])


;-----
(re-seq #"(\d+)-(\d+)" "1-3")     ;; would be "(\\d+)-(\\d+)" in Java
;= (["1-3" "1" "3"])


;-----
(read-string "(+ 1 2 #_(* 2 2) 8)")
;= (+ 1 2 8)


;-----
(when true
  (comment (println "hello")))
;= nil


;-----
(+ 1 2 (comment (* 2 2)) 8)
;= #<NullPointerException java.lang.NullPointerException>


;-----
(defn silly-adder
  [x y]
  (+ x y))


;-----
(defn silly-adder
  [x, y]
  (+, x, y))


;-----
(= [1 2 3] [1, 2, 3])
;= true


;-----
(create-user {:name new-username, :email email})


;-----
'(a b :name 12.5)       ;; list

['a 'b :name 12.5]      ;; vector

{:name "Chas" :age 31}  ;; map

#{1 2 3}                ;; set


;-----
(def a 10)
;= #'user/a
(defn diff
  [a b]
  (- a b))
;= #'user/diff
(diff 5 5)
;= 0
a
;= 10


;-----
(def x 1)
;= #'user/x


;-----
x
;= 1


;-----
(def x "hello")
;= #'user/x
x
;= "hello"


;-----
*ns*
;= #<Namespace user>
(ns foo)
;= nil
*ns*
;= #<Namespace foo>
user/x
;= "hello"
x
;= #<CompilerException java.lang.RuntimeException:
;=   Unable to resolve symbol: x in this context, compiling:(NO_SOURCE_PATH:0)>


;-----
String
;= java.lang.String
Integer
;= java.lang.Integer
java.util.List
;= java.util.List
java.net.Socket
;= java.net.Socket


;-----
filter
;= #<core$filter clojure.core$filter@7444f787>


;-----
(defn average                            
  [numbers]                               
  (/ (apply + numbers) (count numbers)))


;-----
(average [60 80 100 400])
;= 160


;-----
(quote x)
;= x
(symbol? (quote x))
;= true


;-----
'x
;= x


;-----
'(+ x x)
;= (+ x x)
(list? '(+ x x))
;= true


;-----
(list '+ 'x 'x)
;= (+ x x)


;-----
''x
;= (quote x)


;-----
'@x
;= (clojure.core/deref x)
'#(+ % %)
;= (fn* [p1__3162792#] (+ p1__3162792# p1__3162792#))
'`(a b ~c)
;= (seq (concat (list (quote user/a)) 
;=              (list (quote user/b)) 
;=              (list c)))


;-----
(do
  (println "hi")
  (apply * [4 5 6]))
; hi
;= 120


;-----
(let [a (inc (rand-int 6))
      b (inc (rand-int 6))]
  (println (format "You rolled a %s and a %s" a b))
  (+ a b))


;-----
(let [a (inc (rand-int 6))
      b (inc (rand-int 6))]
  (do
    (println (format "You rolled a %s and a %s" a b))
    (+ a b)))


;-----
(def p "foo")
;= #'user/p
p
;= "foo"



;-----
(defn hypot
  [x y]
  (let [x2 (* x x)
        y2 (* y y)]
    (Math/sqrt (+ x2 y2))))


;-----
(def v [42 "foo" 99.2 [5 12]])
;= #'user/v


;-----
(first v)
;= 42
(second v)   
;= "foo"
(last v)     
;= [5 12]
(nth v 2)
;= 99.2
(v 2)
;= 99.2
(.get v 2)
;= 99.2


;-----
(+ (first v) (v 2))
;= 141.2


;-----
(+ (first v) (first (last v)))
;= 47


;-----
(def v [42 "foo" 99.2 [5 12]])
;= #'user/v
(let [[x y z] v]
  (+ x z))
;= 141.2


;-----
(let [x (nth v 0)
      y (nth v 1)
      z (nth v 2)]
  (+ x z))
;= 141.2



;-----
[x  y     z]
[42 "foo" 99.2 [5 12]]


;-----
(let [[x _ _ [y z]] v]
  (+ x y z))
;= 59


;-----
[x  _     _    [y z ]]
[42 "foo" 99.2 [5 12]]


;-----
(let [[x & rest] v]
  rest)
;= ("foo" 99.2 [5 12])


;-----
(let [[x _ z :as original-vector] v]
  (conj original-vector (+ x z)))
;= [42 "foo" 99.2 [5 12] 141.2]


;-----
(def m {:a 5 :b 6
        :c [7 8 9]
        :d {:e 10 :f 11}
        "foo" 88
        42 false})
;= #'user/m
(let [{a :a b :b} m]
  (+ a b))
;= 11


;-----
{a  :a b  :b}
{:a 5  :b 6}


;-----
(let [{f "foo"} m]
  (+ f 12))
;= 100
(let [{v 42} m]
  (if v 1 0))
;= 0


;-----
(let [{x 3 y 8} [12 0 0 -18 44 6 0 0 1]]
  (+ x y))
;= -17


;-----
(let [{{e :e} :d} m]
 (* 2 e))
;= 20


;-----
(let [{[x _ y] :c} m]
  (+ x y))
;= 16
(def map-in-vector ["James" {:birthday (java.util.Date. 73 1 6)}])
;= #'user/map-in-vector
(let [[name {bd :birthday}] map-in-vector]
  (str name " was born on " bd))
;= "James was born on Thu Feb 06 00:00:00 EST 1973"


;-----
(let [{r1 :x r2 :y :as randoms}
      (zipmap [:x :y :z] (repeatedly (partial rand-int 10)))]
  (assoc randoms :sum (+ r1 r2)))
;= {:sum 17, :z 3, :y 8, :x 9}


;-----
(let [{k :unknown x :a
       :or {k 50}} m]
  (+ k x))
;= 55


;-----
(let [{k :unknown x :a} m
      k (or k 50)]
  (+ k x))
;= 55


;-----
(let [{opt1 :option} {:option false}
      opt1 (or opt1 true)
      {opt2 :option :or {opt2 true}} {:option false}]
  {:opt1 opt1 :opt2 opt2})
;= {:opt1 true, :opt2 false}


;-----
(def chas {:name "Chas" :age 31 :location "Massachusetts"})
;= #'user/chas
(let [{name :name age :age location :location} chas]
  (format "%s is %s years old and lives in %s." name age location))
;= "Chas is 31 years old and lives in Massachusetts."


;-----
(let [{:keys [name age location]} chas]
  (format "%s is %s years old and lives in %s." name age location))
;= "Chas is 31 years old and lives in Massachusetts."


;-----
(def brian {"name" "Brian" "age" 31 "location" "British Columbia"})
;= #'user/brian
(let [{:strs [name age location]} brian]
  (format "%s is %s years old and lives in %s." name age location))
;= "Brian is 31 years old and lives in British Columbia."

(def christophe {'name "Christophe" 'age 33 'location "Rhône-Alpes"})
;= #'user/christophe
(let [{:syms [name age location]} christophe]
  (format "%s is %s years old and lives in %s." name age location))
;= "Christophe is 31 years old and lives in Rhône-Alpes."


;-----
(def user-info ["robert8990" 2011 :name "Bob" :city "Boston"])
;= #'user/user-info


;-----
(let [[username account-year & extra-info] user-info
      {:keys [name city]} (apply hash-map extra-info)]
  (format "%s is in %s" name city))
;= "Bob is in Boston"


;-----
(let [[username account-year & {:keys [name city]}] user-info]
  (format "%s is in %s" name city))
;= "Bob is in Boston"


;-----
(fn [x]
  (+ 10 x))


;-----
((fn [x] (+ 10 x)) 8)
;= 18


;-----
(let [x 8]
  (+ 10 x))


;-----
((fn [x y z] (+ x y z))
 3 4 12)
;= 19


;-----
(let [x 3
      y 4
      z 12]
  (+ x y z))


;-----
(def strange-adder (fn adder-self-reference
                     ([x] (adder-self-reference x 1))
                     ([x y] (+ x y))))
;= #'user/strange-adder
(strange-adder 10)
;= 11
(strange-adder 10 50)
;= 60


;-----
(letfn [(odd? [n]
          (if (zero? n)
            false
            (even? (dec n))))
        (even? [n]
          (or (zero? n)
              (odd? (dec n))))]
  (odd? 11))
;= true


;-----
(def strange-adder (fn strange-adder
                     ([x] (strange-adder x 1))
                     ([x y] (+ x y))))

(defn strange-adder
  ([x] (strange-adder x 1))
  ([x y] (+ x y))))


;-----
(def redundant-adder (fn redundant-adder
                       [x y z]
                       (+ x y z)))

(defn redundant-adder
  [x y z]
  (+ x y z))


;-----
(defn concat-rest
  [x & rest]
  (apply str (butlast rest)))
;= #'user/concat-rest
(concat-rest 0 1 2 3 4)
;= "123"


;-----
(defn make-user
  [& [user-id]]
  {:user-id (or user-id
              (str (java.util.UUID/randomUUID)))})
;= #'user/make-user
(make-user)
;= {:user-id "ef165515-6d6f-49d6-bd32-25eeb024d0b4"}
(make-user "Bobby")
;= {:user-id "Bobby"}


;-----
(defn make-user
  [username & {:keys [email join-date]
               :or {join-date (java.util.Date.)}}]
  {:username username
   :join-date join-date
   :email email
   ;; 2.592e9 -> one month in ms
   :exp-date (java.util.Date. (long (+ 2.592e9 (.getTime join-date))))})
;= #'user/make-user
(make-user "Bobby")
;= {:username "Bobby", :join-date #<Date Mon Jan 09 16:56:16 EST 2012>,
;=  :email nil, :exp-date #<Date Wed Feb 08 16:56:16 EST 2012>}
(make-user "Bobby"
  :join-date (java.util.Date. 111 0 1)
  :email "bobby@example.com")
;= {:username "Bobby", :join-date #<Date Sun Jan 01 00:00:00 EST 2011>,
;=  :email "bobby@example.com", :exp-date #<Date Tue Jan 31 00:00:00 EST 2011>}


;-----
(defn foo
  [& {k ["m" 9]}]
  (inc k))
;= #'user/foo
(foo ["m" 9] 19)
;= 20


;-----
(fn [x y] (Math/pow x y))

#(Math/pow %1 %2)


;-----
(read-string "#(Math/pow %1 %2)")
;= (fn* [p1__285# p2__286#] (Math/pow p1__285# p2__286#))


;-----
(fn [x y]
  (println (str x \^ y))
  (Math/pow x y))


;-----
#(do (println (str %1 \^ %2))
     (Math/pow %1 %2))


;-----
(fn [x & rest]
  (- x (apply + rest)))

#(- % (apply + %&))


;-----
(fn [x]
  (fn [y]
    (+ x y)))


;-----
#(#(+ % %))
;= #<IllegalStateException java.lang.IllegalStateException:
;=   Nested #()s are not allowed>


;-----
(if "hi" \t)
;= \t
(if 42 \t)
;= \t
(if nil "unevaluated" \f)
;= \f
(if false "unevaluated" \f)
;= \f
(if (not true) \t)
;= nil


;-----
(true? "string")
;= false
(if "string" \t \f)
;= \t


;-----
(loop [x 5]
  (if (neg? x)
    x
    (recur (dec x))))
;= -1


;-----
(defn countdown
  [x]
  (if (zero? x)
    :blastoff!
    (do (println x)
        (recur (dec x)))))
;= #'user/countdown
(countdown 5)
; 5
; 4
; 3
; 2
; 1
;= :blastoff!


;-----
(def x 5)
;= #'user/x
x
;= 5


;-----
(var x)
;= #'user/x


;-----
#'x
;= #'user/x


;-----
(defn average                            
  [numbers]                               
  (/ (apply + numbers) (count numbers)))


;-----
(def average (fn average                            
               [numbers]                               
               (/ (apply + numbers) (count numbers))))


;-----
(eval :foo)
;= :foo
(eval [1 2 3])
;= [1 2 3]
(eval "text")
;= "text"


;-----
(eval '(average [60 80 100 400]))
;= 160


;-----
(eval (read-string "(average [60 80 100 400])"))
;= 160


;-----
(defn embedded-repl
  "A naive Clojure REPL implementation.  Enter `:quit`
   to exit."
  []                      
  (print (str (ns-name *ns*) ">>> "))
  (flush)
  (let [expr (read)      
        value (eval expr)]
    (when (not= :quit value)
      (println value)
      (recur))))

(embedded-repl)
; user>>> (defn average2                            
;           [numbers]                               
;           (/ (apply + numbers) (count numbers)))
; #'user/average2
; user>>> (average2 [3 7 5])
; 5
; user>>> :quit
;= nil


