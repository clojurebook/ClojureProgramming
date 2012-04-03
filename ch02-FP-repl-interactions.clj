;-----
true  false  5  14.2  \T  "hello"  nil


;-----
(= 5 5)

(= 5 (+ 2 3))

(= "boot" (str "bo" "ot"))

(= nil nil)

(let [a 5]
  (do-something-with-a-number a)
  (= a 5))


;-----
public class StatefulInteger extends Number {
    private int state;

    public StatefulInteger (int initialState) {
        this.state = initialState;
    }

    public void setInt (int newState) {
        this.state = newState;
    }

    public int intValue () {
        return state;
    }

    public int hashCode () {
        return state;
    }

    public boolean equals (Object obj) {
        return obj instanceof StatefulInteger &&
            state == ((StatefulInteger)obj).state;
    }
    
    // remaining xxxValue() methods from java.lang.Number...
}


;-----
(def five (StatefulInteger. 5))
;= #'user/five
(def six (StatefulInteger. 6))        
;= #'user/six
(.intValue five)
;= 5
(= five six)
;= false
(.setInt five 6)
;= nil
(= five six)
;= true


;-----
(defn print-number
  [n]
  (println (.intValue n))            
  (.setInt n 42))                  
;= #'user/print-number
(print-number six)
; 6
;= nil
(= five six)
;= false
(= five (StatefulInteger. 42))                  
;= true



;-----
(def h {[1 2] 3})
;= #'user/h
(h [1 2])
;= 3
(conj (first (keys h)) 3)
;= [1 2 3]
(h [1 2])
;= 3
h
;= {[1 2] 3}


;-----
(defn call-twice [f x]
  (f x)
  (f x))

(call-twice println 123)
; 123
; 123



;-----
(max 5 6)
;= 6
(require 'clojure.string)
;= nil
(clojure.string/lower-case "Clojure")
;= "clojure"


;-----
(map clojure.string/lower-case ["Java" "Imperative" "Weeping"
                                "Clojure" "Learning" "Peace"])
;= ("java" "imperative" "weeping" "clojure" "learning" "peace")
(map * [1 2 3 4] [5 6 7 8])
;= (5 12 21 32)


;-----
(reduce max [0 -3 10 48])
;= 10


;-----
(max 0 -3)
;= 0
(max 0 10)
;= 10
(max 10 48)
;= 48


;-----
(max (max (max 0 -3) 10) 48)
;= 48


;-----
(reduce + 50 [1 2 3 4])
;= 60


;-----
(reduce
  (fn [m v]  
    (assoc m v (* v v)))
  {}
  [1 2 3 4])
;= {4 16, 3 9, 2 4, 1 1}


;-----
(reduce
  #(assoc % %2 (* %2 %2))
  {}
  [1 2 3 4])
;= {4 16, 3 9, 2 4, 1 1}


;-----
((complement pos?) 5)
;= false
((complement string?) "hello")
;= false
((complement string?) :hello)
;= true


;-----
(take 10 (repeatedly #(rand-int 10)))
;= (5 3 5 5 9 0 8 0 1 0)
(take 3 (repeatedly (fn []
                      (Thread/sleep 1000)
                      (System/currentTimeMillis))))
;= (1322663857960 1322663858961 1322663859961)



;-----
(apply hash-map [:a 5 :b 6])
;= {:a 5, :b 6}


;-----
(def args [2 -2 10])
;= #'user/args
(apply * 0.5 3 args)
;= -60.0



;-----
(def only-strings (partial filter string?))
;= #'user/only-strings
(only-strings ["a" 5 "b" 6])
;= ("a" "b")


;-----
(def database-lookup (partial get-data "jdbc:mysql://..."))


;-----
(#(filter string? %) ["a" 5 "b" 6])
;= ("a" "b")


;-----
(#(filter % ["a" 5 "b" 6]) string?)
;= ("a" "b")
(#(filter % ["a" 5 "b" 6]) number?)
;= (5 6)


;-----
(#(map *) [1 2 3] [4 5 6] [7 8 9])
;= #<ArityException clojure.lang.ArityException:
;=   Wrong number of args (3) passed to: user$eval812$fn>
(#(map * % %2 %3) [1 2 3] [4 5 6] [7 8 9])
;= (28 80 162)
(#(map * % %2 %3) [1 2 3] [4 5 6])
;= #<ArityException clojure.lang.ArityException:
;=   Wrong number of args (2) passed to: user$eval843$fn>
(#(apply map * %&) [1 2 3] [4 5 6] [7 8 9])
;= (28 80 162)
(#(apply map * %&) [1 2 3])           
;= (1 2 3)

((partial map *) [1 2 3] [4 5 6] [7 8 9])
;= (28 80 162)


;-----
(defn negated-sum-str
  [& numbers]
  (str (- (apply + numbers))))
;= #'user/negated-sum-str
(negated-sum-str 10 12 3.4)
;= "-25.4"


;-----
(def negated-sum-str (comp str - +))
;= #'user/negated-sum-str
(negated-sum-str 10 12 3.4)
;= "-25.4"


;-----
((comp + - str) 5 10)
;= #<ClassCastException java.lang.ClassCastException:
;=   java.lang.String cannot be cast to java.lang.Number>


;-----
(require '[clojure.string :as str])

(def camel->keyword (comp keyword
                          str/join
                          (partial interpose \-)
                          (partial map str/lower-case)
                          #(str/split % #"(?<=[a-z])(?=[A-Z])")))
;= #'user/camel->keyword
(camel->keyword "CamelCase")
;= :camel-case
(camel->keyword "lowerCamelCase")
;= :lower-camel-case


;-----
(defn camel->keyword
  [s]
  (->> (str/split s #"(?<=[a-z])(?=[A-Z])")
    (map str/lower-case)
    (interpose \-)
    str/join
    keyword))


;-----
(def camel-pairs->map (comp (partial apply hash-map)
                            (partial map-indexed (fn [i x]
                                                   (if (odd? i)
                                                     x
                                                     (camel->keyword x))))))
;= #'user/camel-pairs->map
(camel-pairs->map ["CamelCase" 5 "lowerCamelCase" 3])
;= {:camel-case 5, :lower-camel-case 3}


;-----
(defn adder
  [n]
  (fn [x] (+ n x)))
;= #'user/adder
((adder 5) 18)
;= 23


;-----
(defn doubler
  [f]
  (fn [& args]
    (* 2 (apply f args))))
;= #'user/doubler
(def double-+ (doubler +))
;= #'user/double-+
(double-+ 1 2 3)
;= 12


;-----
(defn print-logger
  [writer]
  #(binding [*out* writer]
     (println %)))


;-----
(def *out*-logger (print-logger *out*))
;= #'user/*out*-logger
(*out*-logger "hello")
; hello
;= nil


;-----
(def writer (java.io.StringWriter.))
;= #'user/writer
(def retained-logger (print-logger writer))  
;= #'user/retained-logger
(retained-logger "hello")
;= nil
(str writer)
;= "hello\n"


;-----
(require 'clojure.java.io)

(defn file-logger
  [file]
  #(with-open [f (clojure.java.io/writer file :append true)]
     ((print-logger f) %)))


;-----
(def log->file (file-logger "messages.log"))
;= #'user/log->file
(log->file "hello")
;= nil

% more messages.log 
hello


;-----
(defn multi-logger
  [& logger-fns]
  #(doseq [f logger-fns]
     (f %)))               


;-----
(def log (multi-logger
           (print-logger *out*)
           (file-logger "messages.log")))
;= #'user/log
(log "hello again")
; hello again                                      
;= nil

% more messages.log 
hello
hello again


;-----
(defn timestamped-logger
  [logger]
  #(logger (format "[%1$tY-%1$tm-%1$te %1$tH:%1$tM:%1$tS] %2$s" (java.util.Date.) %)))

(def log-timestamped (timestamped-logger
                       (multi-logger
                         (print-logger *out*)
                         (file-logger "messages.log"))))

(log-timestamped "goodbye, now")
; [2011-11-30 08:54:00] goodbye, now
;= nil

% more messages.log 
hello
hello again
[2011-11-30 08:54:00] goodbye, now


;-----
(defn perform-bank-transfer!
  [from-account to-account amount]
  ...)

(defn authorize-medical-treatment!
  [patient-id treatment-id]
  ...)

(defn launch-missiles!
  [munition-type target-coordinates]
  ...)


;-----
(require 'clojure.xml)

(defn twitter-followers
  [username]
  (->> (str "https://api.twitter.com/1/users/show.xml?screen_name=" username)
    clojure.xml/parse
    :content
    (filter (comp #{:followers_count} :tag))
    first
    :content
    first
    Integer/parseInt))

(twitter-followers "ClojureBook")
;= 106
(twitter-followers "ClojureBook")
;= 107


;-----
(+ 1 2)  (- 10 7)  (count [-1 0 1])


;-----
(defn prime?
  [n]
  (cond
    (== 1 n) false
    (== 2 n) true
    (even? n) false
    :else (->> (range 3 (inc (Math/sqrt n)) 2)
            (filter #(zero? (rem n %)))
            empty?)))

(time (prime? 1125899906842679))
; "Elapsed time: 2181.014 msecs"
;= true
(let [m-prime? (memoize prime?)]
  (time (m-prime? 1125899906842679))        
  (time (m-prime? 1125899906842679)))       
; "Elapsed time: 2085.029 msecs"
; "Elapsed time: 0.042 msecs"
;= true


;-----
(repeatedly 10 (partial rand-int 10))
;= (3 0 2 9 8 8 5 7 3 5)
(repeatedly 10 (partial (memoize rand-int) 10))
;= (4 4 4 4 4 4 4 4 4 4)


