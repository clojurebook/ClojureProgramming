;-----
(def d (delay (println "Running...")
              :done!))
;= #'user/d
(deref d)
; Running...
;= :done!


;-----
(def a-fn (fn []    
            (println "Running...")
            :done!))
;= #'user/a-fn
(a-fn)
; Running...
;= :done!


;-----
@d
;= :done!


;-----
(defn get-document
  [id]
  ; ... do some work to retrieve the identified document's metadata ...
  {:url "http://www.mozilla.org/about/manifesto.en.html"
   :title "The Mozilla Manifesto"
   :mime "text/html"
   :content (delay (slurp "http://www.mozilla.org/about/manifesto.en.html"))})
;= #'user/get-document
(def d (get-document "some-id"))
;= #'user/d
d
;= {:url "http://www.mozilla.org/about/manifesto.en.html",
;=  :title "The Mozilla Manifesto",
;=  :mime "text/html",
;=  :content #<Delay@2efb541d: :pending>}


;-----
(realized? (:content d))
;= false
@(:content d)
;= "<!DOCTYPE html><html>..."
(realized? (:content d))
;= true


;-----
(def long-calculation (future (apply + (range 1e8))))
;= #'user/long-calculation


;-----
@long-calculation
;= 4999999950000000


;-----
@(future (Thread/sleep 5000) :done!)
;= :done!


;-----
(deref (future (Thread/sleep 5000) :done!)
       1000
       :impatient!)
;= :impatient!


;-----
(defn get-document
  [id]
  ; ... do some work to retrieve the identified document's metadata ...
  {:url "http://www.mozilla.org/about/manifesto.en.html"
   :title "The Mozilla Manifesto"
   :mime "text/html"
   :content (future (slurp "http://www.mozilla.org/about/manifesto.en.html"))})


;-----
(def p (promise))
;= #'user/p


;-----
(realized? p)
;= false
(deliver p 42)
;= #<core$promise$reify__1707@3f0ba812: 42>
(realized? p)
;= true
@p
;= 42


;-----
(def a (promise))
(def b (promise))
(def c (promise))


;-----
(future
  (deliver c (+ @a @b))
  (println "Delivery complete!"))


;-----
(deliver a 15)
;= #<core$promise$reify__5727@56278e83: 15>
(deliver b 16)
; Delivery complete!
;= #<core$promise$reify__5727@47ef7de4: 16>
@c
;= 31


;-----
(def a (promise))
(def b (promise))
(future (deliver a @b))
(future (deliver b @a))
(realized? a)
;= false
(realized? b)
;= false
(deliver a 42)
;= #<core$promise$reify__5727@6156f1b0: 42>
@a
;= 42
@b
;= 42


;-----
(defn call-service
  [arg1 arg2 callback-fn]
  ; ...perform service call, eventually invoking callback-fn with results...
  (future (callback-fn (+ arg1 arg2) (- arg1 arg2))))


;-----
(defn sync-fn 
  [async-fn]
  (fn [& args]
    (let [result (promise)]
      (apply async-fn (conj (vec args) #(deliver result %&)))
      @result)))

((sync-fn call-service) 8 7)
;= (15 1)


;-----
(defn phone-numbers
  [string]
  (re-seq #"(\d{3})[\.-]?(\d{3})[\.-]?(\d{4})" string))
;= #'user/phone-numbers
(phone-numbers " Sunil: 617.555.2937, Betty: 508.555.2218")
;= (["617.555.2937" "617" "555" "2937"] ["508.555.2218" "508" "555" "2218"])


;-----
(def files (repeat 100
                   (apply str
                     (concat (repeat 1000000 \space)
                             "Sunil: 617.555.2937, Betty: 508.555.2218"))))


;-----
(time (dorun (map phone-numbers files)))
; "Elapsed time: 2460.848 msecs"


;-----
(time (dorun (pmap phone-numbers files)))
; "Elapsed time: 1277.973 msecs"


;-----
(def files (repeat 100000
                   (apply str
                     (concat (repeat 1000 \space)
                             "Sunil: 617.555.2937, Betty: 508.555.2218"))))

(time (dorun (map phone-numbers files)))
; "Elapsed time: 2649.807 msecs"
(time (dorun (pmap phone-numbers files)))
; "Elapsed time: 2772.794 msecs"


;-----
(time (->> files
        (partition-all 250)
        (pmap (fn [chunk] (doall (map phone-numbers chunk))))
        (apply concat)                                      
        dorun))
; "Elapsed time: 1465.138 msecs"


;-----
class Person {
    public String name;
    public int age;
    public boolean wearsGlasses;

    public Person (String name, int age, boolean wearsGlasses) {
      this.name = name;
      this.age = age;
      this.wearsGlasses = wearsGlasses;
    }
}

Person sarah = new Person("Sarah", 25, false);



;-----
(def sarah {:name "Sarah" :age 25 :wears-glasses? false})
;= #'user/sarah


;-----
@(atom 12)
;= 12
@(agent {:c 42})
;= {:c 42}
(map deref [(agent {:c 42}) (atom 12) (ref "http://clojure.org") (var +)])
;= ({:c 42} 12 "http://clojure.org" #<core$_PLUS_ clojure.core$_PLUS_@65297549>)


;-----
(defmacro futures
  [n & exprs]
  (vec (for [_ (range n)
             expr exprs]
         `(future ~expr))))


;-----
(defmacro wait-futures
  [& args]
  `(doseq [f# (futures ~@args)]
     @f#))


;-----
(def sarah (atom {:name "Sarah" :age 25 :wears-glasses? false}))
;= #'user/sarah
(swap! sarah update-in [:age] + 3)
;= {:age 28, :wears-glasses? false, :name "Sarah"}


;-----
(swap! sarah (comp #(update-in % [:age] inc)          
                   #(assoc % :wears-glasses? true)))
;= {:age 29, :wears-glasses? true, :name "Sarah"}


;-----
(def xs (atom #{1 2 3}))
;= #'user/xs
(wait-futures 1 (swap! xs (fn [v]
                            (Thread/sleep 250)
                            (println "trying 4")
                            (conj v 4)))
                (swap! xs (fn [v]
                            (Thread/sleep 500)
                            (println "trying 5")
                            (conj v 5))))
;= nil
; trying 4
; trying 5
; trying 5
@xs
;= #{1 2 3 4 5}


;-----
(def x (atom 2000))
;= #'user/x
(swap! x #(Thread/sleep %))
;= nil                    


;-----
(compare-and-set! xs :wrong "new value")
;= false
(compare-and-set! xs @xs "new value")    
;= true 
@xs
;= "new value"


;-----
(def xs (atom #{1 2}))
;= #'user/xs
(compare-and-set! xs #{1 2} "new value")
;= false


;-----
(reset! xs :y)
;= :y
@xs
;= :y


;-----
(defn echo-watch
  [key identity old new]
  (println key old "=>" new))
;= #'user/echo-watch
(def sarah (atom {:name "Sarah" :age 25}))
;= #'user/sarah
(add-watch sarah :echo echo-watch)        
;= #<Atom@418bbf55: {:name "Sarah", :age 25}>
(swap! sarah update-in [:age] inc)        
; :echo {:name Sarah, :age 25} => {:name Sarah, :age 26}
;= {:name "Sarah", :age 26}
(add-watch sarah :echo2 echo-watch)
;= #<Atom@418bbf55: {:name "Sarah", :age 26}>
(swap! sarah update-in [:age] inc)
; :echo {:name Sarah, :age 26} => {:name Sarah, :age 27}
; :echo2 {:name Sarah, :age 26} => {:name Sarah, :age 27}
;= {:name "Sarah", :age 27}


;-----
(remove-watch sarah :echo2)
;= #<Atom@418bbf55: {:name "Sarah", :age 27}>
(swap! sarah update-in [:age] inc) 
; :echo {:name Sarah, :age 27} => {:name Sarah, :age 28}
;= {:name "Sarah", :age 28}


;-----
(reset! sarah @sarah)
; :echo {:name Sarah, :age 28} => {:name Sarah, :age 28}
;= {:name "Sarah", :age 28}


;-----
(def history (atom ()))

(defn log->list
  [dest-atom key source old new]
  (when (not= old new)
    (swap! dest-atom conj new)))

(def sarah (atom {:name "Sarah", :age 25}))
;= #'user/sarah
(add-watch sarah :record (partial log->list history))
;= #<Atom@5143f787: {:age 25, :name "Sarah"}>
(swap! sarah update-in [:age] inc)
;= {:age 26, :name "Sarah"}
(swap! sarah update-in [:age] inc)
;= {:age 27, :name "Sarah"}
(swap! sarah identity)
;= {:age 27, :name "Sarah"}
(swap! sarah assoc :wears-glasses? true)
;= {:age 27, :wears-glasses? true, :name "Sarah"}
(swap! sarah update-in [:age] inc)
;= {:age 28, :wears-glasses? true, :name "Sarah"}
(pprint @history)
;= ;= nil
;= ; ({:age 28, :wears-glasses? true, :name "Sarah"}
;= ;  {:age 27, :wears-glasses? true, :name "Sarah"}
;= ;  {:age 27, :name "Sarah"}
;= ;  {:age 26, :name "Sarah"})


;-----
(defn log->db
  [db-id identity old new]
  (when (not= old new)
    (let [db-connection (get-connection db-id)]
      ...)))

(add-watch sarah "jdbc:postgresql://hostname/some_database" log->db)


;-----
(def n (atom 1 :validator pos?))
;= #'user/n
(swap! n + 500)
;= 501
(swap! n - 1000)
;= #<IllegalStateException java.lang.IllegalStateException: Invalid reference state>


;-----
(def sarah (atom {:name "Sarah" :age 25}))
;= #'user/sarah
(set-validator! sarah :age)
;= nil
(swap! sarah dissoc :age)
;= #<IllegalStateException java.lang.IllegalStateException: Invalid reference state>


;-----
(set-validator! sarah #(or (:age %)
                         (throw (IllegalStateException. "People must have `:age`s!"))))
;= nil
(swap! sarah dissoc :age)
;= #<IllegalStateException java.lang.IllegalStateException: People must have `:age`s!>


;-----
(defn character
  [name & {:as opts}]
  (ref (merge {:name name :items #{} :health 500}
              opts)))


;-----
(def smaug (character "Smaug" :health 500 :strength 400 :items (set (range 50))))
(def bilbo (character "Bilbo" :health 100 :strength 100))
(def gandalf (character "Gandalf" :health 75 :mana 750))


;-----
(defn loot
  [from to]
  (dosync                                           
    (when-let [item (first (:items @from))]
      (alter to update-in [:items] conj item)      
      (alter from update-in [:items] disj item))))


;-----
(wait-futures 1
              (while (loot smaug bilbo))
              (while (loot smaug gandalf)))
;= nil
@smaug
;= {:name "Smaug", :items #{}, :health 500}
@bilbo
;= {:name "Bilbo", :items #{0 44 36 13 ... 16}, :health 500}
@gandalf
;= {:name "Gandalf", :items #{32 4 26 ... 15}, :health 500}


;-----
(map (comp count :items deref) [bilbo gandalf])
;= (21 29)                
(filter (:items @bilbo) (:items @gandalf))
;= ()


;-----
(= (/ (/ 120 3) 4) (/ (/ 120 4) 3))
;= true


;-----
(= ((comp #(/ % 3) #(/ % 4)) 120) ((comp #(/ % 4) #(/ % 3)) 120))
;= true


;-----
(def x (ref 0))
;= #'user/x


;-----
(time (wait-futures 5
                    (dotimes [_ 1000]
                      (dosync (alter x + (apply + (range 1000)))))
                    (dotimes [_ 1000]
                      (dosync (alter x - (apply + (range 1000)))))))
; "Elapsed time: 1466.621 msecs"


;-----
(time (wait-futures 5
                    (dotimes [_ 1000]
                      (dosync (commute x + (apply + (range 1000)))))
                    (dotimes [_ 1000]
                      (dosync (commute x - (apply + (range 1000)))))))
; "Elapsed time: 818.41 msecs"


;-----
(defn flawed-loot
  [from to]
  (dosync                                           
    (when-let [item (first (:items @from))]         
      (commute to update-in [:items] conj item)      
      (commute from update-in [:items] disj item))))


;-----
(def smaug (character "Smaug" :health 500 :strength 400 :items (set (range 50))))
(def bilbo (character "Bilbo" :health 100 :strength 100))
(def gandalf (character "Gandalf" :health 75 :mana 750))

(wait-futures 1
              (while (flawed-loot smaug bilbo))
              (while (flawed-loot smaug gandalf)))
;= nil
(map (comp count :items deref) [bilbo gandalf])  
;= (5 48)              
(filter (:items @bilbo) (:items @gandalf))       
;= (18 32 1)


;-----
(defn fixed-loot
  [from to]
  (dosync                                           
    (when-let [item (first (:items @from))]         
      (commute to update-in [:items] conj item)      
      (alter from update-in [:items] disj item))))

(def smaug (character "Smaug" :health 500 :strength 400 :items (set (range 50))))
(def bilbo (character "Bilbo" :health 100 :strength 100))
(def gandalf (character "Gandalf" :health 75 :mana 750))

(wait-futures 1
              (while (fixed-loot smaug bilbo))
              (while (fixed-loot smaug gandalf)))
;= nil
(map (comp count :items deref) [bilbo gandalf])  
;= (24 26)              
(filter (:items @bilbo) (:items @gandalf))       
;= ()


;-----
(defn attack
  [aggressor target]
  (dosync
    (let [damage (* (rand 0.1) (:strength @aggressor))]
      (commute target update-in [:health] #(max 0 (- % damage))))))

(defn heal
  [healer target]
  (dosync
    (let [aid (* (rand 0.1) (:mana @healer))]
      (when (pos? aid)
        (commute healer update-in [:mana] - (max 5 (/ aid 5)))
        (commute target update-in [:health] + aid)))))


;-----
(def alive? (comp pos? :health))

(defn play
  [character action other]
  (while (and (alive? @character)
              (alive? @other)
              (action character other))
    (Thread/sleep (rand-int 50))))


;-----
(wait-futures 1
              (play bilbo attack smaug)
              (play smaug attack bilbo))
;= nil
(map (comp :health deref) [smaug bilbo])
;= (488.80755445030337 -12.0394908759935)    


;-----
(dosync
  (alter smaug assoc :health 500)
  (alter bilbo assoc :health 100))

(wait-futures 1
              (play bilbo attack smaug)
              (play smaug attack bilbo)
              (play gandalf heal bilbo))
;= nil
(map (comp #(select-keys % [:name :health :mana]) deref) [smaug bilbo gandalf])
;= ({:health 0, :name "Smaug"}
;=  {:health 853.6622368542827, :name "Bilbo"}
;=  {:mana -2.575955687302212, :health 75, :name "Gandalf"})


;-----
(dosync (ref-set bilbo {:name "Bilbo"}))
;= {:name "Bilbo"}


;-----
(dosync (alter bilbo (constantly {:name "Bilbo"})))
; {:name "Bilbo"}


;-----
(defn- enforce-max-health
  [{:keys [name health]}]
  (fn [character-data]
    (or (<= (:health character-data) health)
      (throw (IllegalStateException. (str name " is already at max health!"))))))

(defn character
  [name & {:as opts}]
  (let [cdata (merge {:name name :items #{} :health 500}
                     opts)
        cdata (assoc cdata :max-health (:health cdata))
        validators (list* (enforce-max-health name (:health cdata))
                          (:validators cdata))]
    (ref (dissoc cdata :validators)
      :validator #(every? (fn [v] (v %)) validators))))


;-----
(def bilbo (character "Bilbo" :health 100 :strength 100))
;= #'user/bilbo
(heal gandalf bilbo)
;= #<IllegalStateException java.lang.IllegalStateException: Bilbo is already at max health!>


;-----
(dosync (alter bilbo assoc-in [:health] 95))                                 
;= {:max-health 100, :strength 100, :name "Bilbo", :items #{}, :health 95, :xp 0}
(heal gandalf bilbo)
;= #<IllegalStateException java.lang.IllegalStateException: Bilbo is already at max health!>


;-----
(defn heal
  [healer target]
  (dosync
    (let [aid (min (* (rand 0.1) (:mana @healer))
                   (- (:max-health @target) (:health @target)))]
      (when (pos? aid)
        (commute healer update-in [:mana] - (max 5 (/ aid 5)))
        (alter target update-in [:health] + aid)))))              


;-----
(dosync (alter bilbo assoc-in [:health] 95))
;= {:max-health 100, :strength 100, :name "Bilbo", :items #{}, :health 95}
(heal gandalf bilbo)
;= {:max-health 100, :strength 100, :name "Bilbo", :items #{}, :health 100}
(heal gandalf bilbo)
;= nil


;-----
(defn unsafe
  []
  (io! (println "writing to database...")))
;= #'user/unsafe
(dosync (unsafe))
;= #<IllegalStateException java.lang.IllegalStateException: I/O in transaction>


;-----
(def x (ref (java.util.ArrayList.)))
;= #'user/x
(wait-futures 2 (dosync (dotimes [v 5]
                          (Thread/sleep (rand-int 50))
                          (alter x #(doto % (.add v))))))
;= nil
@x
;= #<ArrayList [0, 0, 1, 0, 2, 3, 4, 0, 1, 2, 3, 4]>


;-----
(def x (ref 0))
;= #'user/x
(dosync
  @(future (dosync (ref-set x 0)))               
  (ref-set x 1))
;= #<RuntimeException java.lang.RuntimeException:
;=   Transaction failed after reaching retry limit>
@x
;= 0


;-----
(ref-max-history (ref "abc" :min-history 3 :max-history 30))
;= 30


;-----
(def a (ref 0))
(future (dotimes [_ 500] (dosync (Thread/sleep 200) (alter a inc))))
;= #<core$future_call$reify__5684@10957096: :pending>
@(future (dosync (Thread/sleep 1000) @a))
;= 28
(ref-history-count a)
;= 5


;-----
(def a (ref 0))
(future (dotimes [_ 500] (dosync (Thread/sleep 20) (alter a inc))))
;= #<core$future_call$reify__5684@10957096: :pending>
@(future (dosync (Thread/sleep 1000) @a))
;= 500
(ref-history-count a)
;= 10


;-----
(def a (ref 0 :max-history 100))
(future (dotimes [_ 500] (dosync (Thread/sleep 20) (alter a inc))))
;= #<core$future_call$reify__5684@10957096: :pending>
@(future (dosync (Thread/sleep 1000) @a))
;= 500
(ref-history-count a)
;= 10


;-----
(def a (ref 0 :min-history 50 :max-history 100))
(future (dotimes [_ 500] (dosync (Thread/sleep 20) (alter a inc))))
@(future (dosync (Thread/sleep 1000) @a))
;= 33


;-----
(def daylight (ref 1))

(defn attack
  [aggressor target]
  (dosync
    (let [damage (* (rand 0.1) (:strength @aggressor) @daylight)]
      (commute target update-in [:health] #(max 0 (- % damage))))))


;-----
map
;= #<core$map clojure.core$map@501d5ebc>
#'map
;= #'clojure.core/map
@#'map
;= #<core$map clojure.core$map@501d5ebc>


;-----
(def ^:private everything 42)


;-----
(def ^{:private true} everything 42)


;-----
(def ^:private everything 42)
;= #'user/everything
(ns other-namespace)
;= nil
(refer 'user)
;= nil
everything
;= #<CompilerException java.lang.RuntimeException:
;=   Unable to resolve symbol: everything in this context, compiling:(NO_SOURCE_PATH:0)>
@#'user/everything
;= 42


;-----
(meta #'a)
;= {:ns #<Namespace user>, :name a, :doc "A sample value.", :line 1, :file "NO_SOURCE_PATH"}


;-----
(def ^:const everything 42)


;-----
(def max-value 255)
;= #'user/max-value
(defn valid-value?
  [v]
  (<= v max-value))
;= #'user/valid-value?
(valid-value? 218)
;= true
(valid-value? 299)
;= false
(def max-value 500)
;= #'user/max-value
(valid-value? 299)
;= true


;-----
(def ^:const max-value 255)
;= #'user/max-value
(defn valid-value?
  [v]
  (<= v max-value))
;= #'user/valid-value?
(def max-value 500)
;= #'user/max-value
(valid-value? 299)
;= false


;-----
(let [a 1
      b 2]
  (println (+ a b))
  (let [b 3
        + -]
    (println (+ a b))))
;= 3
;= -2


;-----
(def ^:dynamic *max-value* 255)
;= #'user/*max-value*
(defn valid-value?
  [v]
  (<= v *max-value*))
;= #'user/valid-value?
(binding [*max-value* 500]
  (valid-value? 299))
;= true


;-----
(binding [*max-value* 500]
  (println (valid-value? 299))
  (doto (Thread. #(println "in other thread:" (valid-value? 299)))
    .start
    .join))
;= true
;= in other thread: false


;-----
(def ^:dynamic *var* :root)
;= #'user/*var*
(defn get-*var* [] *var*)
;= #'user/get-*var*
(binding [*var* :a]
  (binding [*var* :b]
    (binding [*var* :c]
      (get-*var*))))
;= :c                               


;-----
(binding [*var* :a]
  (binding [*var* :b]
    (binding [*var* :c]
      (binding [*var* :d]
        (get-*var*)))))
;= :d


;-----
(defn http-get
  [url-string]
  (let [conn (-> url-string java.net.URL. .openConnection)
        response-code (.getResponseCode conn)]
    (if (== 404 response-code)
      [response-code]
      [response-code (-> conn .getInputStream slurp)])))

(http-get "http://google.com/bad-url")
;= [404]
(http-get "http://google.com/")
;= [200 "<!doctype html><html><head>..."]


;-----
(def ^:dynamic *response-code* nil)

(defn http-get
  [url-string]
  (let [conn (-> url-string java.net.URL. .openConnection)
        response-code (.getResponseCode conn)]
    (when (thread-bound? #'*response-code*)
      (set! *response-code* response-code))
    (when (not= 404 response-code) (-> conn .getInputStream slurp))))

(http-get "http://google.com")
;= "<!doctype html><html><head>..."
*response-code*
;= nil
(binding [*response-code* nil]
  (let [content (http-get "http://google.com/bad-url")]
    (println "Response code was:" *response-code*)
    ; ... do something with `content` if it is not nil ...
    ))
;= Response code was: 404
;= nil


;-----
(binding [*max-value* 500]
  (println (valid-value? 299))
  @(future (valid-value? 299)))
; true
;= true


;-----
(binding [*max-value* 500]
  (map valid-value? [299]))
;= (false)


;-----
(map #(binding [*max-value* 500]
        (valid-value? %))
     [299])
;= (true)


;-----
def foo
  x = 123
  y = 456
  x = x + y
end


;-----
(defn never-do-this []
  (def x 123)
  (def y 456)
  (def x (+ x y)
  x))


;-----
(def x 80)
;= #'user/x
(defn never-do-this []
  (def x 123)
  (def y 456)
  (def x (+ x y))
  x)
;= #'user/never-do-this
(never-do-this)
;= 579
x
;= 579


;-----
(def x 0)
;= #'user/x
(alter-var-root #'x inc)
;= 1


;-----
(def j)
;= #'user/j
j
;= #<Unbound Unbound: #'user/j>


;-----
(declare complex-helper-fn other-helper-fn)

(defn public-api-function
  [arg1 arg2]
  ...
  (other-helper-fn arg1 arg2 (complex-helper-fn arg1 arg2))

(defn- complex-helper-fn
  [arg1 arg2]
  ...)

(defn- other-helper-fn                                         
  [arg1 arg2 arg3]
  ...)


;-----
(def a (agent 500))
;= #'user/a
(send a range 1000)
;= #<Agent@53d2f8be: 500>                            
@a
;= (500 501 502 503 504 ... 999)


;-----
(def a (agent 0))
;= #'user/a
(send a inc)
;= #<Agent@65f7bb1f: 1>


;-----
(def a (agent 5000))
(def b (agent 10000))

(send-off a #(Thread/sleep %))
;= #<Agent@da7d7b5: 5000>
(send-off b #(Thread/sleep %))
;= #<Agent@c0cd75b: 10000>
@a
;= 5000
(await a b)
;= nil
@a
;= nil


;-----
(def a (agent nil))
;= #'user/a
(send a (fn [_] (throw (Exception. "something is wrong"))))
;= #<Agent@3cf71b00: nil>
a
;= #<Agent@3cf71b00 FAILED: nil>
(send a identity)
;= #<Exception java.lang.Exception: something is wrong>


;-----
(restart-agent a 42)
;= 42
(send a inc)
;= #<Agent@5f2308c9: 43>
(reduce send a (for [x (range 3)]
                 (fn [_] (throw (Exception. (str "error #" x))))))
;= #<Agent@5f2308c9: 43>
(agent-error a)                               
;= #<Exception java.lang.Exception: error #0>
(restart-agent a 42)
;= 42
(agent-error a)
;= #<Exception java.lang.Exception: error #1>
(restart-agent a 42 :clear-actions true)
;= 42
(agent-error a)
;= nil


;-----
(def a (agent nil :error-mode :continue))
;= #'user/a
(send a (fn [_] (throw (Exception. "something is wrong"))))
;= #<Agent@44a5b703: nil>
(send a identity)
;= #<Agent@44a5b703: nil>


;-----
(def a (agent nil
              :error-mode :continue
              :error-handler (fn [the-agent exception]
                               (.println System/out (.getMessage exception)))))
;= #'user/a
(send a (fn [_] (throw (Exception. "something is wrong"))))
;= #<Agent@bb07c59: nil>
; something is wrong
(send a identity)
:= #<Agent@bb07c59: nil>


;-----
(set-error-handler! a (fn [the-agent exception]
                        (when (= "FATAL" (.getMessage exception))
                          (set-error-mode! the-agent :fail))))
;= nil
(send a (fn [_] (throw (Exception. "FATAL"))))
;= #<Agent@6fe546fd: nil>
(send a identity)
;= #<Exception java.lang.Exception: FATAL>


;-----
(require '[clojure.java.io :as io])

(def console (agent *out*))
(def character-log (agent (io/writer "character-states.log" :append true)))


;-----
(defn write
  [^java.io.Writer w & content]
  (doseq [x (interpose " " content)]
    (.write w (str x)))
  (doto w
    (.write "\n")
    .flush))


;-----
(defn log-reference
  [reference & writer-agents]
  (add-watch reference :log
             (fn [_ reference old new]
               (doseq [writer-agent writer-agents]
                 (send-off writer-agent write new)))))


;-----
(def smaug (character "Smaug" :health 500 :strength 400))
(def bilbo (character "Bilbo" :health 100 :strength 100))
(def gandalf (character "Gandalf" :health 75 :mana 1000))

(log-reference bilbo console character-log)
(log-reference smaug console character-log)
  
(wait-futures 1
              (play bilbo attack smaug)
              (play smaug attack bilbo)
              (play gandalf heal bilbo))

; {:max-health 500, :strength 400, :name "Smaug", :items #{}, :health 490.0526187036565}
; {:max-health 100, :strength 100, :name "Bilbo", :items #{}, :health 61.501239189435296}
; {:max-health 100, :strength 100, :name "Bilbo", :items #{}, :health 100.0}
; {:max-health 100, :strength 100, :name "Bilbo", :items #{}, :health 67.34251519995752}
; {:max-health 100, :strength 100, :name "Bilbo", :items #{}, :health 100.0}
; {:max-health 500, :strength 400, :name "Smaug", :items #{}, :health 480.9901413567355}
; ...


;-----
(defn attack
  [aggressor target]
  (dosync
    (let [damage (* (rand 0.1) (:strength @aggressor) (ensure daylight))]
      (send-off console write
        (:name @aggressor) "hits" (:name @target) "for" damage)
      (commute target update-in [:health] #(max 0 (- % damage))))))

(defn heal
  [healer target]
  (dosync
    (let [aid (min (* (rand 0.1) (:mana @healer))
                   (- (:max-health @target) (:health @target)))]
      (when (pos? aid)
        (send-off console write
          (:name @healer) "heals" (:name @target) "for" aid)
        (commute healer update-in [:mana] - (max 5 (/ aid 5)))
        (alter target update-in [:health] + aid)))))

(dosync                                                                         
  (alter smaug assoc :health 500)
  (alter bilbo assoc :health 100))
; {:max-health 100, :strength 100, :name "Bilbo", :items #{}, :health 100}
; {:max-health 500, :strength 400, :name "Smaug", :items #{}, :health 500}

(wait-futures 1
              (play bilbo attack smaug)
              (play smaug attack bilbo)
              (play gandalf heal bilbo))
; {:max-health 500, :strength 400, :name "Smaug", :items #{}, :health 497.41458153660614}
; Bilbo hits Smaug for 2.585418463393845
; {:max-health 100, :strength 100, :name "Bilbo", :items #{}, :health 66.62625211852506}
; Smaug hits Bilbo for 33.373747881474934
; {:max-health 500, :strength 400, :name "Smaug", :items #{}, :health 494.6674778679298}
; Bilbo hits Smaug for 2.747103668676348
; {:max-health 100, :strength 100, :name "Bilbo", :items #{}, :health 100.0}
; Gandalf heals Bilbo for 33.37374788147494
; ...


;-----
(require '[net.cgrand.enlive-html :as enlive])
(use '[clojure.string :only (lower-case)])
(import '(java.net URL MalformedURLException))

(defn- links-from
  [base-url html]
  (remove nil? (for [link (enlive/select html [:a])]
                 (when-let [href (-> link :attrs :href)]
                   (try
                     (URL. base-url href)
                     ; ignore bad URLs
                     (catch MalformedURLException e))))))

(defn- words-from
  [html]
  (let [chunks (-> html
                 (enlive/at [:script] nil)
                 (enlive/select [:body enlive/text-node]))]
    (->> chunks
      (mapcat (partial re-seq #"\w+"))
      (remove (partial re-matches #"\d+"))
      (map lower-case))))


;-----
(def url-queue (LinkedBlockingQueue.))
(def crawled-urls (atom #{}))
(def word-freqs (atom {}))


;-----
(declare get-url)

(def agents (set (repeatedly 25 #(agent {::t #'get-url :queue url-queue}))))


;-----
(declare run process handle-results)

(defn ^::blocking get-url
  [{:keys [^BlockingQueue queue] :as state}]
  (let [url (as-url (.take queue))]
    (try
      (if (@crawled-urls url)
        state
        {:url url
         :content (slurp url)
         ::t #'process})
      (catch Exception e
        ;; skip any URL we failed to load
        state)
      (finally (run *agent*)))))


;-----
(defn process                                                       
  [{:keys [url content]}]
  (try
    (let [html (enlive/html-resource (java.io.StringReader. content))]
      {::t #'handle-results
       :url url         
       :links (links-from url html)
       :words (reduce (fn [m word]
                        (update-in m [word] (fnil inc 0)))
                      {}
                      (words-from html))})
    (finally (run *agent*))))


;-----
(defn ^::blocking handle-results
  [{:keys [url links words]}]
  (try
    (swap! crawled-urls conj url)
    (doseq [url links]
      (.put url-queue url))
    (swap! word-freqs (partial merge-with +) words)
    
    {::t #'get-url :queue url-queue}
    (finally (run *agent*))))


;-----
(defn paused? [agent] (::paused (meta agent)))

(defn run
  ([] (doseq [a agents] (run a)))
  ([a]
    (when (agents a)
      (send a (fn [{transition ::t :as state}]
                (when-not (paused? *agent*)                             
                  (let [dispatch-fn (if (-> transition meta ::blocking)
                                      send-off
                                      send)]
                    (dispatch-fn *agent* transition)))
                state)))))


;-----
(defn pause
  ([] (doseq [a agents] (pause a)))
  ([a] (alter-meta! a assoc ::paused true)))

(defn restart
  ([] (doseq [a agents] (restart a)))
  ([a]
    (alter-meta! a dissoc ::paused)
    (run a)))


;-----
(defn test-crawler
  "Resets all state associated with the crawler, adds the given URL to the
   url-queue, and runs the crawler for 60 seconds, returning a vector
   containing the number of URLs crawled, and the number of URLs
   accumulated through crawling that have yet to be visited."
  [agent-count starting-url]
  (def agents (set (repeatedly agent-count #(agent {::t #'get-url :queue url-queue}))))
  (.clear url-queue)
  (swap! crawled-urls empty)
  (swap! word-freqs empty)
  (.add url-queue starting-url)
  (run)
  (Thread/sleep 60000)
  (pause)
  [(count @crawled-urls) (count url-queue)])


;-----
(test-crawler 1 "http://www.bbc.co.uk/news/")
;= [86 14598]


;-----
(test-crawler 25 "http://www.bbc.co.uk/news/")
;= [670 81775]


;-----
(->> (sort-by val @word-freqs)
  reverse
  (take 10))
;= (["the" 23083] ["to" 14308] ["of" 11243] ["bbc" 10969] ["in" 9473]
;=  ["a" 9214] ["and" 8595] ["for" 5203] ["is" 4844] ["on" 4364])
(->> (sort-by val @word-freqs)
  (take 10))
;= (["relieved" 1] ["karim" 1] ["gnome" 1] ["brummell" 1] ["mccredie" 1]
;=  ["ensinar" 1] ["estrictas" 1] ["arap" 1] ["forcibly" 1] ["kitchin" 1])


;-----
(alter-meta! #'process assoc ::blocking true)
;= {:arglists ([{:keys [url content]}]), :ns #<Namespace user>, :name process, :user/blocking true}


;-----
(test-crawler 25 "http://www.bbc.co.uk/news/")
;= [573 80576]


;-----
(.start (Thread. #(println "Running...")))
;= Running...
;= nil


;-----
(defn add
  [some-list value]
  (locking some-list
    (.add some-list value)))


