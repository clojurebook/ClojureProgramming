;-----
'(a b :name 12.5)       ;; list

['a 'b :name 12.5]      ;; vector

{:name "Chas" :age 31}  ;; map

#{1 2 3}                ;; set

{Math/PI "~3.14"
 [:composite "key"] 42
 nil "nothing"}         ;; another map

#{{:first-name "chas" :last-name "emerick"}
  {:first-name "brian" :last-name "carper"}
  {:first-name "christophe" :last-name "grand"}}  ;; a set of maps


;-----
(def v [1 2 3])
;= #'user/v
(conj v 4)
;= [1 2 3 4]
(conj v 4 5)
;= [1 2 3 4 5]
(seq v)
;= (1 2 3)


;-----
(def m {:a 5 :b 6})
;= #'user/m
(conj m [:c 7])
;= {:a 5, :c 7, :b 6}
(seq m)              
;= ([:a 5] [:b 6])


;-----
(def s #{1 2 3})
;= #'user/s
(conj s 10)
;= #{1 2 3 10}
(conj s 3 4)
;= #{1 2 3 4}
(seq s)
;= (1 2 3)


;-----
(def lst '(1 2 3))
;= #'user/lst
(conj lst 0)
;= (0 1 2 3)
(conj lst 0 -1)
;= (-1 0 1 2 3)
(seq lst)
;= (1 2 3)


;-----
(into v [4 5])
;= [1 2 3 4 5]
(into m [[:c 7] [:d 8]])
;= {:a 5, :c 7, :b 6, :d 8}
(into #{1 2} [2 3 4 5 3 3 2])
;= #{1 2 3 4 5}
(into [1] {:a 1 :b 2})
;= [1 [:a 1] [:b 2]]


;-----
(conj '(1 2 3) 4)
;= (4 1 2 3)
(into '(1 2 3) [:a :b :c])
;= (:c :b :a 1 2 3)


;-----
(defn swap-pairs
  [sequential]
  (into (empty sequential)
        (interleave
          (take-nth 2 (drop 1 sequential))
          (take-nth 2 sequential))))

(swap-pairs (apply list (range 10)))
;= (8 9 6 7 4 5 2 3 0 1)
(swap-pairs (apply vector (range 10)))
;= [1 0 3 2 5 4 7 6 9 8]


;-----
(defn map-map
  [f m]
  (into (empty m)
        (for [[k v] m]
          [k (f v)])))


;-----
(map-map inc (hash-map :z 5 :c 6 :a 0))
;= {:z 6, :a 1, :c 7}
(map-map inc (sorted-map :z 5 :c 6 :a 0))
;= {:a 1, :c 7, :z 6}


;-----
(count [1 2 3])
;= 3
(count {:a 1 :b 2 :c 3})
;= 3
(count #{1 2 3})
;= 3
(count '(1 2 3))
;= 3


;-----
(seq "Clojure")
;= (\C \l \o \j \u \r \e)
(seq {:a 5 :b 6})
;= ([:a 5] [:b 6])
(seq (java.util.ArrayList. (range 5)))
;= (0 1 2 3 4)
(seq (into-array ["Clojure" "Programming"]))
;= ("Clojure" "Programming")
(seq [])
;= nil
(seq nil)                                     
;= nil


;-----
(map str "Clojure")
;= ("C" "l" "o" "j" "u" "r" "e")
(set "Programming")
;= #{\a \g \i \m \n \o \P \r}


;-----
(first "Clojure")
;= \C
(rest "Clojure")
;= (\l \o \j \u \r \e)
(next "Clojure")
;= (\l \o \j \u \r \e)


;-----
(rest [1])
;= ()
(next [1])
;= nil
(rest nil)
;= ()
(next nil)
;= nil


;-----
(= (next x)
   (seq (rest x)))


;-----
(doseq [x (range 3)]
  (println x))
; 0
; 1
; 2


;-----
(let [r (range 3)
      rst (rest r)]
  (prn (map str rst))
  (prn (map #(+ 100 %) r))
  (prn (conj r -1) (conj rst 42)))
; ("1" "2")
; (100 101 102)
; (-1 0 1 2) (42 1 2)


;-----
(let [s (range 1e6)]
  (time (count s)))
; "Elapsed time: 147.661 msecs"
;= 1000000
(let [s (apply list (range 1e6))]
  (time (count s)))
; "Elapsed time: 0.03 msecs"
;= 1000000


;-----
(cons 0 (range 1 5))
;= (0 1 2 3 4)


;-----
(cons :a [:b :c :d])
;= (:a :b :c :d)


;-----
(cons 0 (cons 1 (cons 2 (cons 3 (range 4 10)))))
;= (0 1 2 3 4 5 6 7 8 9)
(list* 0 1 2 3 (range 4 10))
;= (0 1 2 3 4 5 6 7 8 9)


;-----
(lazy-seq [1 2 3])
;= (1 2 3)


;-----
(defn random-ints
  "Returns a lazy seq of random integers in the range [0,limit)."
  [limit]
  (lazy-seq                                    
    (cons (rand-int limit)
          (random-ints limit))))

(take 10 (random-ints 50))        
;= (32 37 8 2 22 41 19 27 34 27)


;-----
(defn random-ints
  [limit]
  (lazy-seq                     
    (println "realizing random number")
    (cons (rand-int limit)         
          (random-ints limit))))

(def rands (take 10 (random-ints 50)))
;= #'user/rands
(first rands)
; realizing random number
;= 39
(nth rands 3)
; realizing random number
; realizing random number
; realizing random number
;= 44
(count rands)
; realizing random number
; realizing random number
; realizing random number
; realizing random number
; realizing random number
; realizing random number
;= 10
(count rands)
;= 10


;-----
(repeatedly 10 (partial rand-int 50))
;= (47 19 26 14 18 37 44 13 41 38)


;-----
(def x (next (random-ints 50)))
; realizing random number
; realizing random number


;-----
(def x (rest (random-ints 50)))
; realizing random number


;-----
(let [[x & rest] (random-ints 50)])
; realizing random number
; realizing random number
;= nil


;-----
(dorun (take 5 (random-ints 50)))
; realizing random number
; realizing random number
; realizing random number
; realizing random number
; realizing random number
;= nil


;-----
(doc iterate)
; -------------------------
; clojure.core/iterate
; ([f x])
;   Returns a lazy sequence of x, (f x), (f (f x)) etc.
;   f must be free of side-effects

(doc reverse)
; -------------------------
; clojure.core/reverse
; ([coll])
;   Returns a seq of the items in coll in reverse order. Not lazy.


;-----
(apply str (remove (set "aeiouy")
             "vowels are useless! or maybe not..."))
;= "vwls r slss! r mb nt..."


;-----
(split-with neg? (range -5 5))
;= [(-5 -4 -3 -2 -1) (0 1 2 3 4)]


;-----
(let [[t d] (split-with #(< % 12) (range 1e8))]
  [(count d) (count t)])
;= #<OutOfMemoryError java.lang.OutOfMemoryError: Java heap space>


;-----
(let [[t d] (split-with #(< % 12) (range 1e8))]
  [(count t) (count d)])
;= [12 99999988]


;-----
(def m {:a 1, :b 2, :c 3})
;= #'user/m
(get m :b)
;= 2
(get m :d)
;= nil
(get m :d "not-found")
;= "not-found"
(assoc m :d 4)
;= {:a 1, :b 2, :c 3, :d 4}
(dissoc m :b)
;= {:a 1, :c 3}


;-----
(assoc m
  :x 4
  :y 5
  :z 6)
;= {:z 6, :y 5, :x 4, :a 1, :c 3, :b 2}
(dissoc m :a :c)
;= {:b 2}


;-----
(def v [1 2 3])
;= #'user/v
(get v 1)
;= 2
(get v 10)
;= nil
(get v 10 "not-found")
;= "not-found"
(assoc v
  1 4
  0 -12
  2 :p)
;= [-12 4 :p]


;-----
(assoc v 3 10)
;= [1 2 3 10]


;-----
(get #{1 2 3} 2)
;= 2
(get #{1 2 3} 4)
;= nil
(get #{1 2 3} 4 "not-found")
;= "not-found"


;-----
(when (get #{1 2 3} 2)
  (println "it contains `2`!"))
; it contains `2`!


;-----
(contains? [1 2 3] 0)
;= true
(contains? {:a 5 :b 6} :b)
;= true
(contains? {:a 5 :b 6} 42)
;= false
(contains? #{1 2 3} 1)
;= true


;-----
(contains? [1 2 3] 3)
;= false
(contains? [1 2 3] 2)
;= true
(contains? [1 2 3] 0)
;= true


;-----
(get "Clojure" 3)
;= \j
(contains? (java.util.HashMap.) "not-there")
;= false
(get (into-array [1 2 3]) 0)
;= 1


;-----
(get {:ethel nil} :lucy)
;= nil
(get {:ethel nil} :ethel)
;= nil


;-----
(find {:ethel nil} :lucy)
;= nil
(find {:ethel nil} :ethel)
;= [:ethel nil]


;-----
(if-let [e (find {:a 5 :b 6} :a)]
  (format "found %s => %s" (key e) (val e)) 
  "not found")
;= "found :a => 5"
(if-let [[k v] (find {:a 5 :b 6} :a)]
  (format "found %s => %s" k v) 
  "not found")
;= "found :a => 5"


;-----
(nth [:a :b :c] 2)
;= :c
(get [:a :b :c] 2)
;= :c
(nth [:a :b :c] 3)
;= java.lang.IndexOutOfBoundsException
(get [:a :b :c] 3)
;= nil
(nth [:a :b :c] -1)
;= java.lang.IndexOutOfBoundsException
(get [:a :b :c] -1)
;= nil


;-----
(nth [:a :b :c] -1 :not-found)
;= :not-found
(get [:a :b :c] -1 :not-found)
;= :not-found


;-----
(get 42 0)
;= nil
(nth 42 0)
;= java.lang.UnsupportedOperationException: nth not supported on this type: Long


;-----
(conj '() 1)
;= (1)
(conj '(2 1) 3)
;= (3 2 1)
(peek '(3 2 1))
;= 3
(pop '(3 2 1))
;= (2 1)
(pop '(1))
;= ()


;-----
(conj [] 1)
;= [1]
(conj [1 2] 3)
;= [1 2 3]
(peek [1 2 3])
;= 3
(pop [1 2 3])
;= [1 2]
(pop [1])
;= []


;-----
(get #{1 2 3} 2)
;= 2
(get #{1 2 3} 4)
;= nil
(get #{1 2 3} 4 "not-found")
;= "not-found"


;-----
(disj #{1 2 3} 3 1)
;= #{2}


;-----
(def sm (sorted-map :z 5 :x 9 :y 0 :b 2 :a 3 :c 4))
;= #'user/sm
sm
;= {:a 3, :b 2, :c 4, :x 9, :y 0, :z 5}
(rseq sm)
;= ([:z 5] [:y 0] [:x 9] [:c 4] [:b 2] [:a 3])
(subseq sm <= :c)
;= ([:a 3] [:b 2] [:c 4])
(subseq sm > :b <= :y)
;= ([:c 4] [:x 9] [:y 0])
(rsubseq sm > :b <= :y)
;= ([:y 0] [:x 9] [:c 4])


;-----
(compare 2 2)
;= 0
(compare "ab" "abc")
;= -1
(compare ["a" "b" "c"] ["a" "b"])
;= 1
(compare ["a" 2] ["a" 2 0])
;= -1


;-----
(sort < (repeatedly 10 #(rand-int 100)))
;= (12 16 22 23 41 42 61 63 83 87)
(sort-by first > (map-indexed vector "Clojure"))
;= ([6 \e] [5 \r] [4 \u] [3 \j] [2 \o] [1 \l] [0 \C])


;-----
((comparator <) 1 4)
;= -1
((comparator <) 4 1)
;= 1
((comparator <) 4 4)
;= 0


;-----
(sorted-map-by compare :z 5 :x 9 :y 0 :b 2 :a 3 :c 4)
;= {:a 3, :b 2, :c 4, :x 9, :y 0, :z 5}
(sorted-map-by (comp - compare) :z 5 :x 9 :y 0 :b 2 :a 3 :c 4)
;= {:z 5, :y 0, :x 9, :c 4, :b 2, :a 3}


;-----
(defn magnitude
  [x]
  (-> x Math/log10 Math/floor))
;= #'user/magnitude
(magnitude 100)
;= 2.0
(magnitude 100000)
;= 5.0


;-----
(defn compare-magnitude
  [a b]
  (neg? (- (magnitude a) (magnitude b))))

((comparator compare-magnitude) 10 10000)
;= -1
((comparator compare-magnitude) 100 10)
;= 1
((comparator compare-magnitude) 10 75)
;= 0


;-----
(sorted-set-by compare-magnitude 10 1000 500)
;= #{10 500 1000}
(conj *1 600)
;= #{10 500 1000}
(disj *1 750)
;= #{10 1000}
(contains? *1 1239)
;= true


;-----
(defn compare-magnitude
  [a b]
  (let [diff (- (magnitude a) (magnitude b))]
    (if (zero? diff)
      (compare a b)
      diff)))

(sorted-set-by compare-magnitude 10 1000 500)
;= #{10 500 1000}
(conj *1 600)
;= #{10 500 600 1000}
(disj *1 750)
;= #{10 500 600 1000}


;-----
(sorted-set-by compare-magnitude 10 1000 500 670 1239)
;= #{10 500 670 1000 1239}
(def ss *1)
;= #'user/ss
(subseq ss > 500)
;= (670 1000 1239)
(subseq ss > 500 <= 1000)
;= (670 1000)
(rsubseq ss > 500 <= 1000)
;= (1000 670)


;-----
(defn interpolate
 "Takes a collection of points (as [x y] tuples), returning a function
  which is a linear interpolation between those points."
 [points]
  (let [results (into (sorted-map) (map vec points))]
    (fn [x]
      (let [[xa ya] (first (rsubseq results <= x))
            [xb yb] (first (subseq results > x))]
        (if (and xa xb)
          (/ (+ (* ya (- xb x)) (* yb (- x xa)))
             (- xb xa))
          (or ya yb))))))


;-----
(def f (interpolate [[0 0] [10 10] [15 5]]))
;= #'user/f
(map f [2 10 12])
;= (2 10 8)


;-----
(get [:a :b :c] 2)
;= :c
(get {:a 5 :b 6} :b)
;= 6
(get {:a 5 :b 6} :c 7)
;= 7
(get #{1 2 3} 3)
;= 3


;-----
([:a :b :c] 2)
;= :c
({:a 5 :b 6} :b)
;= 6
({:a 5 :b 6} :c 7)
;= 7
(#{1 2 3} 3)
;= 3


;-----
([:a :b :c] -1)
;= #<IndexOutOfBoundsException java.lang.IndexOutOfBoundsException>


;-----
(get {:a 5 :b 6} :b)
;= 6
(get {:a 5 :b 6} :c 7)
;= 7
(get #{:a :b :c} :d)
;= nil


;-----
(:b {:a 5 :b 6})
;= 6
(:c {:a 5 :b 6} 7)
;= 7
(:d #{:a :b :c})
;= nil


;-----
(defn get-foo
  [map]
  (:foo map))
;= #'user/get-foo
(get-foo nil)
;= nil
(defn get-bar
  [map]
  (map :bar))      
;= #'user/get-bar
(get-bar nil)
;= #<NullPointerException java.lang.NullPointerException>


;-----
(map :name [{:age 21 :name "David"}
            {:gender :f :name "Suzanne"}
            {:name "Sara" :location "NYC"}])
;= ("David" "Suzanne" "Sara")


;-----
(some #{1 3 7} [0 2 4 5 6])
;= nil
(some #{1 3 7} [0 2 3 4 5 6])
;= 3


;-----
(filter :age [{:age 21 :name "David"}
              {:gender :f :name "Suzanne"}
              {:name "Sara" :location "NYC"}])
;= ({:age 21, :name "David"})

(filter (comp (partial <= 25) :age) [{:age 21 :name "David"}
                                     {:gender :f :name "Suzanne" :age 20}
                                     {:name "Sara" :location "NYC" :age 34}])
;= ({:age 34, :name "Sara", :location "NYC"})


;-----
(remove #{5 7} (cons false (range 10)))        
;= (false 0 1 2 3 4 6 8 9)
(remove #{5 7 false} (cons false (range 10)))
;= (false 0 1 2 3 4 6 8 9)


;-----
(remove (partial contains? #{5 7 false}) (cons false (range 10)))
;= (0 1 2 3 4 6 8 9)


;-----
'(1 2 3)
;= (1 2 3)


;-----
'(1 2 (+ 1 2))
;= (1 2 (+ 1 2))


;-----
(list 1 2 (+ 1 2))
;= (1 2 3)


;-----
(vector 1 2 3)
;= [1 2 3]
(vec (range 5))
;= [0 1 2 3 4]


;-----
(defn euclidian-division
  [x y]
  [(quot x y) (rem x y)])

(euclidian-division 42 8)
;= [5 2]


;-----
(let [[q r] (euclidian-division 53 7)]
  (str "53/7 = " q " * 7 + " r))   
;= "53/7 = 7 * 7 + 4"


;-----
(def point-3d [42 26 -7])

(def travel-legs [["LYS" "FRA"] ["FRA" "PHL"] ["PHL" "RDU"]])


;-----
#{1 2 3}
;= #{1 2 3}
#{1 2 3 3}
;= #<IllegalArgumentException java.lang.IllegalArgumentException:
;=   Duplicate key: 3>


;-----
(hash-set :a :b :c :d)
;= #{:a :c :b :d}


;-----
(set [1 6 1 8 3 7 7])
;= #{1 3 6 7 8}


;-----
(apply str (remove (set "aeiouy") "vowels are useless"))
;= "vwls r slss"

(defn numeric? [s] (every? (set "0123456789") s))
;= #'user/numeric?
(numeric? "123")
;= true
(numeric? "42b")
;= false


;-----
{:a 5 :b 6}
;= {:a 5, :b 6}
{:a 5 :a 5}
;= #<IllegalArgumentException java.lang.IllegalArgumentException:
;=   Duplicate key: :a>


;-----
(hash-map :a 5 :b 6)
;= {:a 5, :b 6}
(apply hash-map [:a 5 :b 6])
;= {:a 5, :b 6}


;-----
(keys m)
;= (:a :b :c)
(vals m)
;= (1 2 3)


;-----
(map key m)
;= (:a :c :b)
(map val m)
;= (1 3 2)


;-----
(def playlist
  [{:title "Elephant", :artist "The White Stripes", :year 2003}
   {:title "Helioself", :artist "Papas Fritas", :year 1997}
   {:title "Stories from the City, Stories from the Sea",
    :artist "PJ Harvey", :year 2000}
   {:title "Buildings and Grounds", :artist "Papas Fritas", :year 2000}
   {:title "Zen Rodeo", :artist "Mardi Gras BB", :year 2002}])


;-----
(map :title playlist)
;= ("Elephant" "Helioself" "Stories from the City, Stories from the Sea"
;=  "Buildings and Grounds" "Zen Rodeo")


;-----
(defn summarize [{:keys [title artist year]}]
  (str title " / " artist " / " year))


;-----
(group-by #(rem % 3) (range 10))
;= {0 [0 3 6 9], 1 [1 4 7], 2 [2 5 8]}


;-----
(group-by :artist playlist)
;= {"Papas Fritas" [{:title "Helioself", :artist "Papas Fritas", :year 1997}
;=                  {:title "Buildings and Grounds", :artist "Papas Fritas"}]
;=  ...}


;-----
(into {} (for [[k v] (group-by key-fn coll)]
           [k (summarize v)]))


;-----
(defn reduce-by
  [key-fn f init coll]
  (reduce (fn [summaries x]
            (let [k (key-fn x)]
              (assoc summaries k (f (summaries k init) x))))
    {} coll))


;-----
(def orders
  [{:product "Clock", :customer "Wile Coyote", :qty 6, :total 300}
   {:product "Dynamite", :customer "Wile Coyote", :qty 20, :total 5000}
   {:product "Shotgun", :customer "Elmer Fudd", :qty 2, :total 800}
   {:product "Shells", :customer "Elmer Fudd", :qty 4, :total 100}
   {:product "Hole", :customer "Wile Coyote", :qty 1, :total 1000}
   {:product "Anvil", :customer "Elmer Fudd", :qty 2, :total 300}
   {:product "Anvil", :customer "Wile Coyote", :qty 6, :total 900}])


;-----
(reduce-by :customer #(+ %1 (:total %2)) 0 orders)
;= {"Elmer Fudd" 1200, "Wile Coyote" 7200}


;-----
(reduce-by :product #(conj %1 (:customer %2)) #{} orders)
;= {"Anvil" #{"Wile Coyote" "Elmer Fudd"}, 
;=  "Hole" #{"Wile Coyote"}, 
;=  "Shells" #{"Elmer Fudd"}, 
;=  "Shotgun" #{"Elmer Fudd"}, 
;=  "Dynamite" #{"Wile Coyote"}, 
;=  "Clock" #{"Wile Coyote"}}


;-----
(fn [order] 
  [(:customer order) (:product order)])

#(vector (:customer %) (:product %))

(fn [{:keys [customer product]}] 
  [customer product])

(juxt :customer :product)


;-----
(reduce-by (juxt :customer :product) 
  #(+ %1 (:total %2)) 0 orders)
;= {["Wile Coyote" "Anvil"] 900, 
;=  ["Elmer Fudd" "Anvil"] 300, 
;=  ["Wile Coyote" "Hole"] 1000, 
;=  ["Elmer Fudd" "Shells"] 100, 
;=  ["Elmer Fudd" "Shotgun"] 800, 
;=  ["Wile Coyote" "Dynamite"] 5000, 
;=  ["Wile Coyote" "Clock"] 300}


;-----
(defn reduce-by-in
  [keys-fn f init coll]
  (reduce (fn [summaries x]
            (let [ks (keys-fn x)]
              (assoc-in summaries ks 
                (f (get-in summaries ks init) x))))
    {} coll))


;-----
(reduce-by-in (juxt :customer :product) 
  #(+ %1 (:total %2)) 0 orders)
;= {"Elmer Fudd" {"Anvil" 300, 
;=                "Shells" 100, 
;=                "Shotgun" 800}, 
;=  "Wile Coyote" {"Anvil" 900, 
;=                 "Hole" 1000, 
;=                 "Dynamite" 5000, 
;=                 "Clock" 300}}


;-----
(def flat-breakup 
  {["Wile Coyote" "Anvil"] 900, 
   ["Elmer Fudd" "Anvil"] 300, 
   ["Wile Coyote" "Hole"] 1000, 
   ["Elmer Fudd" "Shells"] 100, 
   ["Elmer Fudd" "Shotgun"] 800, 
   ["Wile Coyote" "Dynamite"] 5000, 
   ["Wile Coyote" "Clock"] 300})


;-----
(reduce #(apply assoc-in %1 %2) {} flat-breakup)
;= {"Elmer Fudd" {"Shells" 100, 
;=                "Anvil" 300, 
;=                "Shotgun" 800}, 
;=  "Wile Coyote" {"Hole" 1000, 
;=                 "Dynamite" 5000, 
;=                 "Clock" 300, 
;=                 "Anvil" 900}}


;-----
(+ 1 2)
;= 3


;-----
(def v (vec (range 1e6)))
;= #'user/v
(count v)                 
;= 1000000
(def v2 (conj v 1e6))
;= #'user/v2
(count v2)
;= 1000001
(count v)
;= 1000000


;-----
(def a (list 1 2 3))


;-----
(def b (conj a 0))
;= #'user/b
b
;= (0 1 2 3)


;-----
(def c (rest a))
;= #'user/c
c
;= (2 3)


;-----
(def a {:a 5 :b 6 :c 7 :d 8})


;-----
(def b (assoc a :c 0))
;= #'user/b
b
;= {:a 5, :c 0, :b 6, :d 8}


;-----
(def c (dissoc a :d))
;= #'user/c
c
;= {:a 5, :c 7, :b 6}


;-----
(def version1 {:name "Chas" :info {:age 31}})
;= #'user/version1
(def version2 (update-in version1 [:info :age] + 3))
;= #'user/version2
version1
;= {:info {:age 31}, :name "Chas"}
version2
;= {:info {:age 34}, :name "Chas"}


;-----
(def x (transient []))
;= #'user/x
(def y (conj! x 1))
;= #'user/y
(count y)
;= 1
(count x)
;= 1


;-----
(into #{} (range 5))
;= #{0 1 2 3 4}


;-----
(defn naive-into
  [coll source]
  (reduce conj coll source))

(= (into #{} (range 500))
   (naive-into #{} (range 500)))
;= true


;-----
(time (do (into #{} (range 1e6))
        nil))
; "Elapsed time: 1756.696 msecs"
(time (do (naive-into #{} (range 1e6))
        nil))
; "Elapsed time: 3394.684 msecs"


;-----
(defn faster-into
  [coll source]
  (persistent! (reduce conj! (transient coll) source)))


;-----
(time (do (faster-into #{} (range 1e6))
        nil))
; "Elapsed time: 1639.156 msecs"


;-----
(defn transient-capable?
  "Returns true if a transient can be obtained for the given collection.
   i.e. tests if `(transient coll)` will succeed."
  [coll]
  (instance? clojure.lang.IEditableCollection coll))


;-----
(def v [1 2])
;= #'user/v
(def tv (transient v))
;= #'user/tv
(conj v 3)
;= [1 2 3]


;-----
(persistent! tv)
;= [1 2]
(get tv 0)
;= #<IllegalAccessError java.lang.IllegalAccessError:
;=   Transient used after persistent! call>


;-----
(nth (transient [1 2]) 1)
;= 2
(get (transient {:a 1 :b 2}) :a)
;= 1
((transient {:a 1 :b 2}) :a)
;= 1
((transient [1 2]) 1)
;= 2
(find (transient {:a 1 :b 2}) :a)
;= #<CompilerException java.lang.ClassCastException:
;=   clojure.lang.PersistentArrayMap$TransientArrayMap
;=   cannot be cast to java.util.Map (NO_SOURCE_FILE:0)>


;-----
(let [tm (transient {})]
  (doseq [x (range 100)]
    (assoc! tm x 0))
  (persistent! tm))
;= {0 0, 1 0, 2 0, 3 0, 4 0, 5 0, 6 0, 7 0}


;-----
(let [t (transient {})]
  @(future (get t :a)))
;= #<IllegalAccessError java.lang.IllegalAccessError:
;=   Transient used by non-owner thread>


;-----
(persistent! (transient [(transient {})]))
;= [#<TransientArrayMap clojure.lang.PersistentArrayMap$TransientArrayMap@b57b39f>]


;-----
(= (transient [1 2]) (transient [1 2]))
;= false


;-----
(def a ^{:created (System/currentTimeMillis)}
        [1 2 3])
;= #'user/a
(meta a)
;= {:created 1322065198169}


;-----
(meta ^:private [1 2 3])
;= {:private true}
(meta ^:private ^:dynamic [1 2 3])
;= {:dynamic true, :private true}


;-----
(def b (with-meta a (assoc (meta a)
                      :modified (System/currentTimeMillis))))
;= #'user/b
(meta b)
;= {:modified 1322065210115, :created 1322065198169}
(def b (vary-meta a assoc :modified (System/currentTimeMillis)))
;= #'user/b
(meta b)
;= {:modified 1322065229972, :created 1322065198169}


;-----
(= a b)
;= true
a
;= [1 2 3]
b
;= [1 2 3]
(= ^{:a 5} 'any-value
   ^{:b 5} 'any-value)
;= true


;-----
(meta (conj a 500))
;= {:created 1319481540825}


;-----
(defn empty-board
  "Creates a rectangular empty board of the specified width
   and height."
  [w h]
  (vec (repeat w (vec (repeat h nil)))))


;-----
(defn populate
  "Turns :on each of the cells specified as [y, x] coordinates."
  [board living-cells]
  (reduce (fn [board coordinates]
            (assoc-in board coordinates :on))
          board
          living-cells))

(def glider (populate (empty-board 6 6) #{[2 0] [2 1] [2 2] [1 2] [0 1]}))

(pprint glider)
; [[nil :on nil nil nil nil]
;  [nil nil :on nil nil nil]
;  [:on :on :on nil nil nil]
;  [nil nil nil nil nil nil]
;  [nil nil nil nil nil nil]
;  [nil nil nil nil nil nil]]


;-----
(defn neighbours
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)] 
    [(+ dx x) (+ dy y)]))

(defn count-neighbours
  [board loc]
  (count (filter #(get-in board %) (neighbours loc))))

(defn indexed-step 
  "Yields the next state of the board, using indices to determine neighbors,
   liveness, etc."
  [board]
  (let [w (count board)
        h (count (first board))]
    (loop [new-board board x 0 y 0]
      (cond
        (>= x w) new-board
        (>= y h) (recur new-board (inc x) 0)
        :else
          (let [new-liveness
                 (case (count-neighbours board [x y])
                   2 (get-in board [x y])
                   3 :on
                   nil)]
            (recur (assoc-in new-board [x y] new-liveness) x (inc y)))))))


;-----
(-> (iterate indexed-step glider) (nth 8) pprint)
; [[nil nil nil nil nil nil]
;  [nil nil nil nil nil nil]
;  [nil nil nil :on nil nil]
;  [nil nil nil nil :on nil]
;  [nil nil :on :on :on nil]
;  [nil nil nil nil nil nil]]


;-----
(defn indexed-step2
  [board]
  (let [w (count board)
        h (count (first board))]
    (reduce 
      (fn [new-board x]
        (reduce 
          (fn [new-board y]
            (let [new-liveness
                   (case (count-neighbours board [x y])
                     2 (get-in board [x y])
                     3 :on
                     nil)]
              (assoc-in new-board [x y] new-liveness)))
          new-board (range h)))
      board (range w))))


;-----
(defn indexed-step3
  [board]
  (let [w (count board)
        h (count (first board))]
    (reduce 
      (fn [new-board [x y]]
        (let [new-liveness
               (case (count-neighbours board [x y])
                 2 (get-in board [x y])
                 3 :on
                 nil)]
           (assoc-in new-board [x y] new-liveness)))
      board (for [x (range h) y (range w)] [x y]))))


;-----
(partition 3 1 (range 5))
;= ((0 1 2) (1 2 3) (2 3 4))


;-----
(partition 3 1 (concat [nil] (range 5) [nil]))
;= ((nil 0 1) (0 1 2) (1 2 3) (2 3 4) (3 4 nil))


;-----
(defn window
  "Returns a lazy sequence of 3-item windows centered around each item of coll."
  [coll]
  (partition 3 1 (concat [nil] coll [nil])))


;-----
(defn cell-block 
  "Creates a sequences of 3x3 windows from a triple of 3 sequences."
  [[left mid right]]
  (window (map vector 
            (or left (repeat nil)) mid (or right (repeat nil)))))


;-----
(defn window
  "Returns a lazy sequence of 3-item windows centered
   around each item of coll, padded as necessary with
   pad or nil."
  ([coll] (window nil coll))
  ([pad coll]
   (partition 3 1 (concat [pad] coll [pad]))))

(defn cell-block 
  "Creates a sequences of 3x3 windows from a triple of 3 sequences."
  [[left mid right]]
  (window (map vector left mid right))) 


;-----
(defn liveness
  "Returns the liveness (nil or :on) of the center cell for
   the next step."  
  [block]
  (let [[_ [_ center _] _] block]
    (case (- (count (filter #{:on} (apply concat block)))
             (if (= :on center) 1 0))
      2 center
      3 :on
      nil)))


;-----
(defn- step-row
  "Yields the next state of the center row."
  [rows-triple]
  (vec (map liveness (cell-block rows-triple)))) 

(defn index-free-step
  "Yields the next state of the board."
  [board]
  (vec (map step-row (window (repeat nil) board))))


;-----
(= (nth (iterate indexed-step glider) 8)
   (nth (iterate index-free-step glider) 8))
;= true


;-----
(defn step 
 "Yields the next state of the world"
 [cells]
 (set (for [[loc n] (frequencies (mapcat neighbours cells))
            :when (or (= n 3) (and (= n 2) (cells loc)))]
        loc)))


;-----
(->> (iterate step #{[2 0] [2 1] [2 2] [1 2] [0 1]})
  (drop 8)
  first
  (populate (empty-board 6 6))
  pprint)
; [[nil nil nil nil nil nil]
;  [nil nil nil nil nil nil]
;  [nil nil nil :on nil nil]
;  [nil nil nil nil :on nil]
;  [nil nil :on :on :on nil]
;  [nil nil nil nil nil nil]]


;-----
(defn stepper 
  "Returns a step function for Life-like cell automata.
   neighbours takes a location and return a sequential collection
   of locations. survive? and birth? are predicates on the number
   of living neighbours."
  [neighbours birth? survive?] 
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbours cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))


;-----
(defn hex-neighbours
  [[x y]]
  (for [dx [-1 0 1] dy (if (zero? dx) [-2 2] [-1 1])] 
    [(+ dx x) (+ dy y)]))

(def hex-step (stepper hex-neighbours #{2} #{3 4}))

;= ; this configuration is an oscillator of period 4
(hex-step #{[0 0] [1 1] [1 3] [0 4]})
;= #{[1 -1] [2 2] [1 5]}
(hex-step *1)
;= #{[1 1] [2 4] [1 3] [2 0]}
(hex-step *1)
;= #{[1 -1] [0 2] [1 5]}
(hex-step *1)
;= #{[0 0] [1 1] [1 3] [0 4]}


;-----
(stepper #(filter (fn [[i j]] (and (< -1 i w) (< -1 j h))) 
            (neighbours %)) #{2 3} #{3})


;-----
(defn maze 
  "Returns a random maze carved out of walls; walls is a set of
   2-item sets #{a b} where a and b are locations.
   The returned maze is a set of the remaining walls."
  [walls]
  (let [paths (reduce (fn [index [a b]]
                        (merge-with into index {a [b] b [a]}))
                {} (map seq walls))
        start-loc (rand-nth (keys paths))]
    (loop [walls walls
           unvisited (disj (set (keys paths)) start-loc)]
      (if-let [loc (when-let [s (seq unvisited)] (rand-nth s))]
        (let [walk (iterate (comp rand-nth paths) loc)
              steps (zipmap (take-while unvisited walk) (next walk))]
          (recur (reduce disj walls (map set steps))
            (reduce disj unvisited (keys steps))))
        walls))))


;-----
(defn grid
  [w h]
  (set (concat
         (for [i (range (dec w)) j (range h)] #{[i j] [(inc i) j]})
         (for [i (range w) j (range (dec h))] #{[i j] [i (inc j)]}))))

(defn draw
  [w h maze]
  (doto (javax.swing.JFrame. "Maze")
    (.setContentPane 
      (doto (proxy [javax.swing.JPanel] []
              (paintComponent [^java.awt.Graphics g]
                (let [g (doto ^java.awt.Graphics2D (.create g)
                          (.scale 10 10)
                          (.translate 1.5 1.5)
                          (.setStroke (java.awt.BasicStroke. 0.4)))]
                  (.drawRect g -1 -1 w h)
                  (doseq [[[xa ya] [xb yb]] (map sort maze)]
                    (let [[xc yc] (if (= xa xb) 
                                    [(dec xa) ya]
                                    [xa (dec ya)])]
                      (.drawLine g xa ya xc yc))))))
        (.setPreferredSize (java.awt.Dimension. 
                       	     (* 10 (inc w)) (* 10 (inc h))))))
    .pack
    (.setVisible true)))


;-----
(defn wmaze 
  "The original Wilson's algorithm."
  [walls]
  (let [paths (reduce (fn [index [a b]] 
                        (merge-with into index {a [b] b [a]}))
                {} (map seq walls))
        start-loc (rand-nth (keys paths))]
    (loop [walls walls unvisited (disj (set (keys paths)) start-loc)]
      (if-let [loc (when-let [s (seq unvisited)] (rand-nth s))]
        (let [walk (iterate (comp rand-nth paths) loc)
              steps (zipmap (take-while unvisited walk) (next walk))
              walk (take-while identity (iterate steps loc))
              steps (zipmap walk (next walk))]
          (recur (reduce disj walls (map set steps))
            (reduce disj unvisited (keys steps))))
        walls))))


;-----
(defn hex-grid
  [w h]
  (let [vertices (set (for [y (range h) x (range (if (odd? y) 1 0) (* 2 w) 2)]
                        [x y]))
        deltas [[2 0] [1 1] [-1 1]]]
    (set (for [v vertices d deltas f [+ -]
               :let [w (vertices (map f v d))]
               :when w] #{v w}))))

(defn- hex-outer-walls
  [w h]
  (let [vertices (set (for [y (range h) x (range (if (odd? y) 1 0) (* 2 w) 2)]
                        [x y]))
        deltas [[2 0] [1 1] [-1 1]]]
    (set (for [v vertices d deltas f [+ -]
               :let [w (map f v d)]
               :when (not (vertices w))] #{v (vec w)}))))

(defn hex-draw
  [w h maze]
  (doto (javax.swing.JFrame. "Maze")
    (.setContentPane 
      (doto (proxy [javax.swing.JPanel] []
              (paintComponent [^java.awt.Graphics g]
                (let [maze (into maze (hex-outer-walls w h))
                      g (doto ^java.awt.Graphics2D (.create g)
                          (.scale 10 10)
                          (.translate 1.5 1.5)
                          (.setStroke (java.awt.BasicStroke. 0.4
                                        java.awt.BasicStroke/CAP_ROUND
                                        java.awt.BasicStroke/JOIN_MITER)))
                      draw-line (fn [[[xa ya] [xb yb]]]
                                  (.draw g 
                                    (java.awt.geom.Line2D$Double. 
                                      xa (* 2 ya) xb (* 2 yb))))]
                  (doseq [[[xa ya] [xb yb]] (map sort maze)]
                    (draw-line
                      (cond 
                        (= ya yb) [[(inc xa) (+ ya 0.4)] [(inc xa) (- ya 0.4)]]
                        (< ya yb) [[(inc xa) (+ ya 0.4)] [xa (+ ya 0.6)]]
                        :else [[(inc xa) (- ya 0.4)] [xa (- ya 0.6)]]))))))
        (.setPreferredSize (java.awt.Dimension. 
                       	     (* 20 (inc w)) (* 20 (+ 0.5 h))))))
    .pack
    (.setVisible true)))

(hex-draw 40 40 (maze (hex-grid 40 40)))


;-----
(require '[clojure.zip :as z])

(def v [[1 2 [3 4]] [5 6]])
;= #'user/v
(-> v z/vector-zip z/node)
;= [[1 2 [3 4]] [5 6]]
(-> v z/vector-zip z/down z/node)
;= [1 2 [3 4]]
(-> v z/vector-zip z/down z/right z/node)
;= [5 6]


;-----
(-> v z/vector-zip z/down z/right (z/replace 56) z/node) 
;= 56
(-> v z/vector-zip z/down z/right (z/replace 56) z/root)
;= [[1 2 [3 4]] 56]
(-> v z/vector-zip z/down z/right z/remove z/node)
;= 4
(-> v z/vector-zip z/down z/right z/remove z/root)
;= [[1 2 [3 4]]]
(-> v z/vector-zip z/down z/down z/right (z/edit * 42) z/root)
;= [[1 84 [3 4]] [5 6]]


;-----
(defn html-zip [root]
  (z/zipper
    vector?
    (fn [[tagname & xs]] 
      (if (map? (first xs)) (next xs) xs))
    (fn [[tagname & xs] children]
      (into (if (map? (first xs)) [tagname (first xs)] [tagname])
        children))
    root))


;-----
(defn wrap 
  "Wraps the current node in the specified tag and attributes."
  ([loc tag]
    (z/edit loc #(vector tag %)))
  ([loc tag attrs]
    (z/edit loc #(vector tag attrs %))))

(def h [:body [:h1 "Clojure"] 
              [:p "What a wonderful language!"]])
;= #'user/h
(-> h html-zip z/down z/right z/down (wrap :b) z/root)
;= [:body [:h1 "Clojure"] [:p [:b "What a wonderful language!"]]]


;-----
(def labyrinth (maze (grid 10 10)))


;-----
(def labyrinth (let [g (grid 10 10)] (reduce disj g (maze g))))


;-----
(def theseus (rand-nth (distinct (apply concat labyrinth))))
(def minotaur (rand-nth (distinct (apply concat labyrinth))))


;-----
(defn ariadne-zip
  [labyrinth loc]
  (let [paths (reduce (fn [index [a b]] 
                        (merge-with into index {a [b] b [a]}))
                {} (map seq labyrinth))
        children (fn [[from to]] 
                   (seq (for [loc (paths to) 
                              :when (not= loc from)] 
                          [to loc])))]
    (z/zipper (constantly true)
      children
      nil
      [nil loc])))


;-----
(->> theseus 
     (ariadne-zip labyrinth) 
     (iterate z/next)
     (filter #(= minotaur (second (z/node %))))
     first z/path
     (map second))
([3 9] [4 9] [4 8] [4 7] [4 6] [5 7] [5 6] [5 5] [5 4] 
 [5 8] [6 8] [6 7] [6 6] [6 5] [7 6] [8 6] [9 6] [9 5]
 [9 4] [9 3] [9 2] [9 1] [9 0] [8 2] [8 1] [8 0] [7 0]
 [6 0] [7 1] [7 2] [6 2] [6 1] [5 1] [4 1] [4 0] [5 0]
 [3 0] [4 2] [5 2] [3 2] [3 3] [4 3] [4 4] [4 5] [3 5])


;-----
(defn draw
  [w h maze path]
  (doto (javax.swing.JFrame. "Maze")
   (.setContentPane 
     (doto (proxy [javax.swing.JPanel] []
             (paintComponent [^java.awt.Graphics g]
               (let [g (doto ^java.awt.Graphics2D (.create g)
                         (.scale 10 10)
                         (.translate 1.5 1.5)
                         (.setStroke (java.awt.BasicStroke. 0.4)))]
                 (.drawRect g -1 -1 w h)
                 (doseq [[[xa ya] [xb yb]] (map sort maze)]
                   (let [[xc yc] (if (= xa xb) 
                                   [(dec xa) ya]
                                   [xa (dec ya)])]
                     (.drawLine g xa ya xc yc)))
                 (.translate g -0.5 -0.5)
                 (.setColor g java.awt.Color/RED)
                 (doseq [[[xa ya] [xb yb]] path]
                   (.drawLine g xa ya xb yb)))))
       (.setPreferredSize (java.awt.Dimension. 
                      	     (* 10 (inc w)) (* 10 (inc h))))))
   .pack
   (.setVisible true)))


;-----
(let [w 40, h 40
      grid (grid w h)
      walls (maze grid)
      labyrinth (reduce disj grid walls)
      places (distinct (apply concat labyrinth))
      theseus (rand-nth places)
      minotaur (rand-nth places)
      full-path #(conj (z/path %) (z/node %)) ; erratum 
      path (->> theseus
             (ariadne-zip labyrinth) 
             (iterate z/next)
             (filter #(= minotaur (second (z/node %)))) ; erratum: replaced first by second
             first full-path rest)] ; erratum: replaced z/path by full-path
  (draw w h walls path))


