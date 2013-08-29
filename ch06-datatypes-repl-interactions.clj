;-----
(defprotocol ProtocolName                          
  "documentation"
  (a-method [this arg1 arg2] "method docstring")   
  (another-method [x] [x arg] "docstring"))


;-----
(defprotocol AProtocol
  (methodName [this x y & more]))


;-----
(defprotocol Matrix
  "Protocol for working with 2d datastructures."
  (lookup [matrix i j])
  (update [matrix i j value])
  (rows [matrix])
  (cols [matrix])
  (dims [matrix]))


;-----
(extend-protocol Matrix
  clojure.lang.IPersistentVector
  (lookup [vov i j]
    (get-in vov [i j]))
  (update [vov i j value]
    (assoc-in vov [i j] value))
  (rows [vov]
    (seq vov))
  (cols [vov]
    (apply map vector vov))
  (dims [vov] 
    [(count vov) (count (first vov))]))


;-----
(extend-type AType
  AProtocol
  (method-from-AProtocol [this x]
    (;...implementation for AType
     ))   
  AnotherProtocol   
  (method1-from-AnotherProtocol [this x]
    (;...implementation for AType
     ))   
  (method2-from-AnotherProtocol [this x y]
    (;...implementation for AType
     )))

(extend-protocol AProtocol
  AType
  (method1-from-AProtocol [this x]
    (;...implementation for AType
     ))
  AnotherType   
  (method1-from-AProtocol [this x]
    (;...implementation for AnotherType
     ))   
  (method2-from-AProtocol [this x y]
    (;...implementation for AnotherType
     )))


;-----
(extend-protocol Matrix 
  nil 
  (lookup [x i j])
  (update [x i j value])
  (rows [x] [])
  (cols [x] [])
  (dims [x] [0 0]))

(lookup nil 5 5)
;= nil
(dims nil)
;= [0 0]


;-----
(defn vov 
  "Create a vector of h w-item vectors."
  [h w]
  (vec (repeat h (vec (repeat w nil)))))


;-----
(def matrix (vov 3 4))
;= #'user/matrix
matrix
;= [[nil nil nil nil]
;=  [nil nil nil nil]
;=  [nil nil nil nil]]
(update matrix 1 2 :x)
;= [[nil nil nil nil]
;=  [nil nil :x nil]
;=  [nil nil nil nil]]
(lookup *1 1 2)
;= :x
(rows (update matrix 1 2 :x))
;= ([nil nil nil nil]
;=  [nil nil :x nil]
;=  [nil nil nil nil])
(cols (update matrix 1 2 :x))
;= ([nil nil nil]
;=  [nil nil nil]
;=  [nil :x nil]
;=  [nil nil nil])


;-----
(extend-protocol Matrix
  (Class/forName "[[D") 
  (lookup [matrix i j]
    (aget matrix i j))
  (update [matrix i j value]
    (let [clone (aclone matrix)]
      (aset clone i
        (doto (aclone (aget clone i))
          (aset j value)))
      clone))
  (rows [matrix]
    (map vec matrix))
  (cols [matrix]
    (apply map vector matrix))
  (dims [matrix]
    (let [rs (count matrix)]
      (if (zero? rs)
        [0 0]
        [rs (count (aget matrix 0))]))))


;-----
(def matrix (make-array Double/TYPE 2 3))
;= #'user/matrix
(rows matrix)
;= ([0.0 0.0 0.0]
;=  [0.0 0.0 0.0])
(rows (update matrix 1 1 3.4))
;= ([0.0 0.0 0.0]
;=  [0.0 3.4 0.0])
(lookup (update matrix 1 1 3.4) 1 1)
;= 3.4
(cols (update matrix 1 1 3.4))
;= ([0.0 0.0]
;=  [0.0 3.4]
;=  [0.0 0.0])
(dims matrix)
;= [2 3]


;-----
(defrecord Point [x y])


;-----
(deftype Point [x y])


;-----
(.x (Point. 3 4))
;= 3


;-----
(defrecord NamedPoint [^String name ^long x ^long y])


;-----
(NamedPoint/getBasis)
;= [name x y]


;-----
(map meta (NamedPoint/getBasis))
;= ({:tag String} {:tag long} {:tag long})


;-----
(def x "hello")
;= #'user/hello
(defrecord Point [x y])
;= user.Point
(Point. 5 5)
;= #user.Point{:x 5, :y 5}
(ns user2)
(refer 'user)
x
;= "hello"
Point
;= CompilerException java.lang.Exception:
;=   Unable to resolve symbol: Point
(import 'user.Point)
Point                             
;= user.Point


;-----
(defrecord Point [x y])
;= user.Point
(= (Point. 3 4) (Point. 3 4))
;= true
(= 3 3N)
;= true
(= (Point. 3N 4N) (Point. 3 4))
;= true


;-----
(:x (Point. 3 4))
;= 3
(:z (Point. 3 4) 0)
;= 0
(map :x [(Point. 3 4)
         (Point. 5 6)
         (Point. 7 8)])
;= (3 5 7)


;-----
(assoc (Point. 3 4) :z 5)
;= #user.Point{:x 3, :y 4, :z 5}
(let [p (assoc (Point. 3 4) :z 5)]
  (dissoc p :x))
;= {:y 4, :z 5}
(let [p (assoc (Point. 3 4) :z 5)]
  (dissoc p :z))
;= #user.Point{:x 3, :y 4}


;-----
(:z (assoc (Point. 3 4) :z 5))
;= 5
(.z (assoc (Point. 3 4) :z 5))
;= #<java.lang.IllegalArgumentException:
;=   No matching field found: z for class user.Point>


;-----
(-> (Point. 3 4)
  (with-meta {:foo :bar})
  meta)                   
;= {:foo :bar}


;-----
#user.Point{:x 3, :y 4, :z 5}


;-----
(pr-str (assoc (Point. 3 4) :z [:a :b]))
;= "#user.Point{:x 3, :y 4, :z [:a :b]}"
(= (read-string *1)
   (assoc (Point. 3 4) :z [:a :b]))
;= true


;-----
(Point. 3 4 {:foo :bar} {:z 5})
;= #user.Point{:x 3, :y 4, :z 5}
(meta *1)
;= {:foo :bar}


;-----
(-> (Point. 3 4)
  (with-meta {:foo :bar})
  (assoc :z 5))


;-----
(defn point [x y] 
  (Point. x y)) 


;-----
(->Point 3 4)
;= #user.Point{:x 3, :y 4}


;-----
(map->Point {:x 3, :y 4, :z 5})
;= #user.Point{:x 3, :y 4, :z 5}


;-----
(apply ->Point [5 6])
;= #user.Point{:x 5, :y 6}

(map (partial apply ->Point) [[5 6] [7 8] [9 10]])
;= (#user.Point{:x 5, :y 6}
;=  #user.Point{:x 7, :y 8}
;=  #user.Point{:x 9, :y 10})

(map map->Point [{:x 1 :y 2} {:x 5 :y 6 :z 44}])
;= (#user.Point{:x 1, :y 2}
;=  #user.Point{:x 5, :y 6, :z 44})


;-----
(Point/create {:x 3, :y 4, :z 5})
;= #user.Point{:x 3, :y 4, :z 5}


;-----
(defn log-point
  [x]
  {:pre [(pos? x)]}
  (Point. x (Math/log x)))

(log-point -42)
;= #<AssertionError java.lang.AssertionError: Assert failed: (pos? x)>
(log-point Math/E)
;= #user.Point{:x 2.718281828459045, :y 1.0}


;-----
(defn point [x y] 
  {:x x, :y y}) 


;-----
(defrecord Point [x y])
;= user.Point
(= (Point. 3 4) (Point. 3 4))
;= true
(= {:x 3 :y 4} (Point. 3 4))
;= false
(= (Point. 3 4) {:x 3 :y 4})
;= false


;-----
(deftype Point [x y])
;= user.Point
(.x (Point. 3 4))     
;= 3
(:x (Point. 3 4))     
;= nil


;-----
(deftype MyType [^:volatile-mutable fld])


;-----
(deftype SchrödingerCat [^:unsynchronized-mutable state]
  clojure.lang.IDeref 
  (deref [sc]
    (locking sc 
      (or state 
        (set! state (if (zero? (rand-int 2))
                      :dead
                      :alive))))))

(defn schrödinger-cat 
 "Creates a new Schrödinger's cat. Beware, the REPL may kill it!"
 []
 (SchrödingerCat. nil))

(def felix (schrödinger-cat))
;= #'user/felix
@felix
;= :dead
(schrödinger-cat)
;= #<SchrödingerCat@3248bc64: :dead>
(schrödinger-cat)
;= #<SchrödingerCat@3248bc64: :alive>


;-----
(delay (if (zero? (rand-int 2))
         :dead
         :alive))


;-----
(defrecord Point [x y]
  Matrix
  (lookup [pt i j]
    (when (zero? j)
      (case i
        0 x
        1 y))) 
  (update [pt i j value]
    (if (zero? j) 
      (condp = i 
        0 (Point. value y)
        1 (Point. x value))
      pt))
  (rows [pt] [[x] [y]])
  (cols [pt] [[x y]])
  (dims [pt] [2 1]))


;-----
(defrecord Point [x y])

(extend-protocol Matrix
  Point
  (lookup [pt i j]
    (when (zero? j)
      (case i
        0 (:x pt)
        1 (:y pt))))
  (update [pt i j value]
    (if (zero? j) 
      (condp = i 
        0 (Point. value (:y pt))
        1 (Point. (:x pt) value))
      pt))
  (rows [pt]
    [[(:x pt)] [(:y pt)]])
  (cols [pt]
    [[(:x pt) (:y pt)]])
  (dims [pt] [2 1]))


;-----
(defprotocol ClashWhenInlined
  (size [x]))
;= ClashWhenInlined
(defrecord R []
  ClashWhenInlined              
  (size [x]))
;= #<CompilerException java.lang.ClassFormatError:
;=   Duplicate method name&signature in class file user/R, compiling:(NO_SOURCE_PATH:1)>

(defrecord R [])
;= user.R
(extend-type R
  ClashWhenInlined
  (size [x]))
;= nil


;-----
(deftype MyType [a b c]
  java.lang.Runnable
  (run [this] ...)
  Object
  (equals [this that] ...)
  (hashCode [this] ...)
  Protocol1
  (method1 [this ...] ...)
  Protocol2
  (method2 [this ...] ...)
  (method3 [this ...] ...))


;-----
(deftype Point [x y]
  Matrix                                     
  (lookup [pt i j]                           
    (when (zero? j)
      (case i
        0 x
        1 y)))   
  (update [pt i j value]
    (if (zero? j) 
      (case i 
        0 (Point. value y)
        1 (Point. x value))
      pt))
  (rows [pt]
    [[x] [y]])
  (cols [pt]
    [[x y]])
  (dims [pt]
    [2 1])
  Object
  (equals [this other]
    (and (instance? (class this) other)
      (= x (.x other)) (= y (.y other))))
  (hashCode [this]
    (-> x hash (hash-combine y))))


;-----
(deftype Point [x y]
  Matrix                                     
  (lookup [pt i j]                           
    (when (zero? j) (case i 0 x 1 y)))   
  (update [pt i j value]
    (if (zero? j) 
      (case i 
        0 (Point. value y)
        1 (Point. x value))
      pt))
  (rows [pt]
    [[x] [y]])
  (cols [pt]
    [[x y]])
  (dims [pt]
    [2 1])
  Object
  (equals [this other]
    (and (satisfies? Matrix other) (= (cols this) (cols other)))))
  (hashCode [this]
    (-> x hash (hash-combine y))))


;-----
(reify
  Protocol-or-Interface-or-Object
  (method1 [this x]
    (implementation))
  Another-Protocol-or-Interface
  (method2 [this x y]
    (implementation))
  (method3 [this x]
    (implementation)))


;-----
(defn listener 
  "Creates an AWT/Swing `ActionListener` that delegates to the given function."
  [f]
  (reify
    java.awt.event.ActionListener
    (actionPerformed [this e]
      (f e))))


;-----
(.listFiles (java.io.File. ".")
  (reify
    java.io.FileFilter
    (accept [this f]
      (.isDirectory f))))


;-----
(defrecord Point [x y])

(extend Point
  Matrix
  {:lookup (fn [pt i j]
             (when (zero? j)
               (case i
                 0 (:x pt)
                 1 (:y pt))))
   :update (fn [pt i j value]
             (if (zero? j) 
               (condp = i 
                 0 (Point. value (:y pt))
                 1 (Point. (:x pt) value))
               pt))
   :rows (fn [pt]
          [[(:x pt)] [(:y pt)]])
   :cols (fn [pt]
           [[(:x pt) (:y pt)]])
   :dims (fn [pt] [2 1])})


;-----
(def abstract-matrix-impl
  {:cols (fn [pt]
           (let [[h w] (dims pt)]
             (map
               (fn [x] (map #(lookup pt x y) (range 0 w)))
               (range 0 h))))
   :rows (fn [pt]
           (apply map vector (cols pt)))})


;-----
(extend Point
  Matrix
  (assoc abstract-matrix-impl
    :lookup (fn [pt i j]
             (when (zero? j)
               (case i
                 0 (:x pt)
                 1 (:y pt))))
    :update (fn [pt i j value]
              (if (zero? j) 
                (condp = i 
                  0 (Point. value (:y pt))
                  1 (Point. (:x pt) value))
               pt))
    :dims (fn [pt] [2 1])))


;-----
(defprotocol Measurable
  "A protocol for retrieving the dimensions of widgets."
  (width [measurable] "Returns the width in px.")
  (height [measurable] "Returns the height in px."))


;-----
(defrecord Button [text])

(extend-type Button
  Measurable
  (width [btn]
    (* 8 (-> btn :text count)))
  (height [btn] 8))

(def bordered
  {:width #(* 2 (:border-width %))
   :height #(* 2 (:border-height %))})


;-----
Measurable
;= {:impls 
;=   {user.Button 
;=     {:height #<user$eval2056$fn__2057 user$eval2056$fn__2057@112f8578>,
;=      :width #<user$eval2056$fn__2059 user$eval2056$fn__2059@74b90ff7>}},
;=  :on-interface user.Measurable,
;=  :on user.Measurable, 
;=  :doc "A protocol for retrieving the 2D dimensions of widgets.", 
;=  :sigs 
;=    {:height 
;=      {:doc "Returns the height in px.", 
;=       :arglists ([measurable]), 
;=       :name height}, 
;=     :width 
;=       {:doc "Returns the width in px.",
;=        :arglists ([measurable]),
;=        :name width}},
;=  :var #'user/Measurable, 
;=  :method-map {:width :width, :height :height}, 
;=  :method-builders
;=    {#'user/height #<user$eval2012$fn__2013 user$eval2012$fn__2013@27aa7aac>,
;=     #'user/width #<user$eval2012$fn__2024 user$eval2012$fn__2024@4848268a>}}


;-----
(get-in Measurable [:impls Button])
;= {:height #<user$eval1251$fn__1252 user$eval1251$fn__1252@744589eb>,
;=  :width #<user$eval1251$fn__1254 user$eval1251$fn__1254@40735f45>}


;-----
(defn combine
 "Takes two functions f and g and returns a fn that takes a variable number
  of args, applies them to f and g and then returns the result of 
  (op rf rg) where rf and rg are the results of the calls to f and g."
 [op f g]
 (fn [& args]
   (op (apply f args) (apply g args))))


;-----
(defrecord BorderedButton [text border-width border-height])

(extend BorderedButton
  Measurable
  (merge-with (partial combine +)
    (get-in Measurable [:impls Button])
    bordered))


;-----
(let [btn (Button. "Hello World")]
  [(width btn) (height btn)])
;= [88 8]

(let [bbtn (BorderedButton. "Hello World" 6 4)]
  [(width bbtn) (height bbtn)])
;= [100 16]


;-----
(extenders Measurable)
;= (user.BorderedButton user.Button)


;-----
(extends? Measurable Button)
;= true


;-----
(satisfies? Measurable (Button. "hello"))
;= true
(satisfies? Measurable :other-value)
;= false


;-----
(deftype Foo [x y]
  Measurable
  (width [_] x)
  (height [_] y))
;= user.Foo
(satisfies? Measurable (Foo. 5 5))
;= true


;-----
(instance? user.Measurable (Foo. 5 5))
;= true


;-----
(defprotocol P
  (a [x]))
;= P
(extend-protocol P
  java.util.Collection
  (a [x] :collection!)
  java.util.List
  (a [x] :list!))
;= nil
(a [])
;= :list!


;-----
(defprotocol P
  (a [x]))

(extend-protocol P
  java.util.Map
  (a [x] :map!)
  java.io.Serializable
  (a [x] :serializable!))


;-----
(a {})
;= :serializable!


;-----
(defn scaffold
  "Given an interface, returns a 'hollow' body suitable for use with `deftype`."
  [interface]
  (doseq [[iface methods] (->> interface
                            .getMethods
                            (map #(vector (.getName (.getDeclaringClass %))
                                    (symbol (.getName %))
                                    (count (.getParameterTypes %))))
                            (group-by first))]
    (println (str "  " iface))
    (doseq [[_ name argcount] methods]
      (println
        (str "    "
          (list name (into '[this] (take argcount (repeatedly gensym)))))))))


;-----
(scaffold clojure.lang.IPersistentSet)
;  clojure.lang.IPersistentSet
;    (get [this G__5617])
;    (contains [this G__5618])
;    (disjoin [this G__5619])
;  clojure.lang.IPersistentCollection
;    (count [this])
;    (cons [this G__5620])
;    (empty [this])
;    (equiv [this G__5621])
;  clojure.lang.Seqable
;    (seq [this])       
;  clojure.lang.Counted
;    (count [this])


;-----
(declare empty-array-set)
(def ^:private ^:const max-size 4)

(deftype ArraySet [^objects items
                   ^int size
                   ^:unsynchronized-mutable ^int hashcode]
  clojure.lang.IPersistentSet
  (get [this x]
    (loop [i 0]
      (when (< i size)
        (if (= x (aget items i))
          (aget items i)
          (recur (inc i))))))
  (contains [this x]
    (boolean
      (loop [i 0]
        (when (< i size)
          (or (= x (aget items i)) (recur (inc i)))))))
  (disjoin [this x]
    (loop [i 0]
      (if (== i size)
        this
        (if (not= x (aget items i))
          (recur (inc i))
          (ArraySet. (doto (aclone items)
                       (aset i (aget items (dec size)))
                       (aset (dec size) nil))
                     (dec size)
                     -1)))))
 clojure.lang.IPersistentCollection
  (count [this] size)
  (cons [this x]
    (cond
      (.contains this x) this
      (== size max-size) (into #{x} this)
      :else (ArraySet. (doto (aclone items)
                         (aset size x))
                       (inc size)
                       -1)))
  (empty [this] empty-array-set)
  (equiv [this that] (.equals this that))
  clojure.lang.Seqable
  (seq [this] (seq (take size items)))
  Object
  (hashCode [this]
    (when (== -1 hashcode)
      (set! hashcode (int (areduce items idx ret 0
                            (unchecked-add-int ret (hash (aget items idx)))))))
    hashcode)
  (equals [this that]
    (or
      (identical? this that)
      (and (or (instance? java.util.Set that)
               (instance? clojure.lang.IPersistentSet that))
           (= (count this) (count that))
           (every? #(contains? this %) that)))))

(def ^:private empty-array-set (ArraySet. (object-array max-size) 0 -1))

(defn array-set
  "Creates an array-backed set containing the given values."
  [& vals]
  (into empty-array-set vals))


;-----
(array-set)
;= #{}
(conj (array-set) 1)
;= #{1}
(apply array-set "hello")
;= #{\h \e \l \o}
(get (apply array-set "hello") \w)
;= nil
(get (apply array-set "hello") \h)
;= \h
(contains? (apply array-set "hello") \h)
;= true
(= (array-set) #{})
;= true


;-----
((apply array-set "hello") \h)
; #<ClassCastException java.lang.ClassCastException:
;   user.ArraySet cannot be cast to clojure.lang.IFn>


;-----
(= #{} (array-set))
;= false


;-----
(scaffold java.util.Set)
;  java.util.Set
;    (add [this G__6140])
;    (equals [this G__6141])
;    (hashCode [this])
;    (clear [this])
;    (isEmpty [this])
;    (contains [this G__6142])
;    (addAll [this G__6143])
;    (size [this])
;    (toArray [this G__6144])
;    (toArray [this])
;    (iterator [this])
;    (remove [this G__6145])
;    (removeAll [this G__6146])
;    (containsAll [this G__6147])
;    (retainAll [this G__6148])


;-----
  java.util.Set
    (isEmpty [this])
    (size [this])
    (toArray [this G__6144])
    (toArray [this])
    (iterator [this])
    (containsAll [this G__6147])


;-----
(deftype ArraySet [^objects items
                   ^int size
                   ^:unsynchronized-mutable ^int hashcode]
  clojure.lang.IPersistentSet
  (get [this x]
    (loop [i 0]
      (when (< i size)
        (if (= x (aget items i))
          (aget items i)
          (recur (inc i))))))
  (contains [this x]
    (boolean
      (loop [i 0]
        (when (< i size)
          (or (= x (aget items i)) (recur (inc i)))))))
  (disjoin [this x]
    (loop [i 0]
      (if (== i size)
        this
        (if (not= x (aget items i))
          (recur (inc i))
          (ArraySet. (doto (aclone items)
                       (aset i (aget items (dec size)))
                       (aset (dec size) nil))
                     (dec size)
                     -1)))))
  clojure.lang.IPersistentCollection
  (count [this] size)
  (cons [this x]
    (cond
      (.contains this x) this
      (== size max-size) (into #{x} this)
      :else (ArraySet. (doto (aclone items)
                         (aset size x))
                       (inc size)
                       -1)))
  (empty [this] empty-array-set)
  (equiv [this that] (.equals this that))                    
  clojure.lang.Seqable
  (seq [this] (seq (take size items)))
  Object
  (hashCode [this]
    (when (== -1 hashcode)
      (set! hashcode (int (areduce items idx ret 0
                            (unchecked-add-int ret (hash (aget items idx)))))))
    hashcode)
  (equals [this that]
    (or
      (identical? this that)
      (and (instance? java.util.Set that)
           (= (count this) (count that))
           (every? #(contains? this %) that))))
  clojure.lang.IFn
  (invoke [this key] (.get this key))
  (applyTo [this args]
    (when (not= 1 (count args))
      (throw (clojure.lang.ArityException. (count args) "ArraySet")))
    (this (first args)))
  java.util.Set
  (isEmpty [this] (zero? size))
  (size [this] size)
  (toArray [this array]
    (.toArray ^java.util.Collection (sequence items) array))
  (toArray [this] (into-array (seq this)))
  (iterator [this] (.iterator ^java.util.Collection (sequence this)))
  (containsAll [this coll]
    (every? #(contains? this %) coll)))

(def ^:private empty-array-set (ArraySet. (object-array max-size) 0 -1))


;-----
(= #{3 1 2 0} (array-set 0 1 2 3))
;= true
((apply array-set "hello") \h)
;= \h


;-----
(defn microbenchmark
  [f & {:keys [size trials] :or {size 4 trials 1e6}}]
  (let [items (repeatedly size gensym)]
    (time (loop [s (apply f items)
                 n trials]
            (when (pos? n)
              (doseq [x items] (contains? s x))
              (let [x (rand-nth items)]
                (recur (-> s (disj x) (conj x)) (dec n))))))))

(doseq [n (range 1 5)
          f [#'array-set #'hash-set]]
    (print n (-> f meta :name) ": ")
    (microbenchmark @f :size n))
; size 1 array-set : "Elapsed time: 839.336 msecs"
; size 1 hash-set : "Elapsed time: 1105.059 msecs"
; size 2 array-set : "Elapsed time: 1201.81 msecs"
; size 2 hash-set : "Elapsed time: 1369.192 msecs"
; size 3 array-set : "Elapsed time: 1658.36 msecs"
; size 3 hash-set : "Elapsed time: 1740.955 msecs"
; size 4 array-set : "Elapsed time: 2197.424 msecs"
; size 4 hash-set : "Elapsed time: 2154.637 msecs"

