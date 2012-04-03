
;-----
(defmacro foreach [[sym coll] & body]
  `(loop [coll# ~coll]
     (when-let [[~sym & xs#] (seq coll#)]
       ~@body
       (recur xs#))))
;= #'user/foreach
(foreach [x [1 2 3]]
  (println x))
; 1
; 2
; 3



;-----
(defmacro foo []
  `(if (= 0 (rand-int 2))
     (println "foo!)))     ;; oops, forgot a closing quote
;= #<Exception java.lang.Exception: EOF while reading string>



;-----
(defmacro print-keyword [x]
  `(println (keyword ~x)))
;= #'user/print-keyword
(print-keyword "foo")
; :foo
;= nil


;-----
(reverse-it (nltnirp "foo"))


;-----
(println "foo")


;-----
(require '(clojure [string :as str]
                   [walk :as walk]))

(defmacro reverse-it
  [form]
  (walk/postwalk #(if (symbol? %)
                    (symbol (str/reverse (name %)))
                    %)
                 form))


;-----
(reverse-it
  (qesod [gra (egnar 5)]
    (nltnirp (cni gra))))
; 1
; 2
; 3
; 4
; 5
;= nil


;-----
(macroexpand-1 '(reverse-it
                  (qesod [gra (egnar 5)]
                    (nltnirp (cni gra)))))
;= (doseq [arg (range 5)]
;=   (println (inc arg)))


;-----
(defn oops [arg] (frobnicate arg))
;= #<CompilerException java.lang.Exception:
;=   Unable to resolve symbol: frobnicate in this context (NO_SOURCE_FILE:1)>


;-----
(defmacro oops [arg] `(frobnicate ~arg))
;= #'user/oops


;-----
(oops 123)
;= #<CompilerException java.lang.IllegalStateException:
;=   Var user/frobnicate is unbound. (NO_SOURCE_FILE:0)>


;-----
(macroexpand-1 '(oops 123))
;= (user/frobnicate 123)


;-----
(macroexpand-1 '(reverse-it
                  (qesod [gra (egnar 5)]
                    (nltnirp (cni gra)))))
;= (doseq [arg (range 5)]
;=   (println (inc arg)))

(pprint (macroexpand '(reverse-it
                        (qesod [gra (egnar 5)]
                          (nltnirp (cni gra))))))
; (loop*
;  [seq_1647
;   (clojure.core/seq (range 5))
;   chunk_1648
;   nil
;   count_1649
;   (clojure.core/int 0)
;   i_1650
;   (clojure.core/int 0)]
;  (if
;   (clojure.core/< i_1650 count_1649)
;   (clojure.core/let
;    [arg (.nth chunk_1648 i_1650)]
;    (do (println (inc arg)))
;    (recur
;     seq_1647
;     chunk_1648
;     count_1649
;     (clojure.core/unchecked-inc i_1650)))
;   (clojure.core/when-let
;    [seq_1647 (clojure.core/seq seq_1647)]
;    (if
;     (clojure.core/chunked-seq? seq_1647)
;     (clojure.core/let
;      [c__3798__auto__ (clojure.core/chunk-first seq_1647)]
;      (recur
;       (clojure.core/chunk-rest seq_1647)
;       c__3798__auto__
;       (clojure.core/int (clojure.core/count c__3798__auto__))
;       (clojure.core/int 0)))
;     (clojure.core/let
;      [arg (clojure.core/first seq_1647)]
;      (do (println (inc arg)))
;      (recur
;       (clojure.core/next seq_1647)
;       nil
;       (clojure.core/int 0)
;       (clojure.core/int 0)))))))


;-----
(macroexpand '(cond a b c d))       
;= (if a b (clojure.core/cond c d))


;-----
(require '[clojure.walk :as w])

(w/macroexpand-all '(cond a b c d))
;= (if a b (if c d nil))                 


;-----
(w/macroexpand-all ''(when x a))
;= (quote (if x (do a)))


;-----
(defmacro hello
  [name]
  (list 'println name))

(macroexpand '(hello "Brian"))
;= (println "Brian")


;-----
(defmacro while
  [test & body]
  (list 'loop []
    (concat (list 'when test) body)
      '(recur)))


;-----
(defmacro while
  [test & body]
  `(loop []
     (when ~test
       ~@body
       (recur))))


;-----
(def foo 123)
;= #'user/foo
[foo (quote foo) 'foo `foo]
;= [123 foo foo user/foo]


;-----
(in-ns 'bar)

`foo
;= bar/foo


;-----
(ns baz (:require [user :as u]))

`map
;= clojure.core/map
`u/foo
;= user/foo
`foo
;= baz/foo


;-----
(list `map `println [foo])
;= (clojure.core/map clojure.core/println [123])


;-----
`(map println [~foo])
;= (clojure.core/map clojure.core/println [123])

`(map println ~[foo])
;= (clojure.core/map clojure.core/println [123])


;-----
`(println ~(keyword (str foo)))
;= (clojure.core/println :123)


;-----
(let [defs '((def x 123)
             (def y 456))]
  (concat (list 'do) defs))
;= (do (def x 123) (def y 456))


;-----
(let [defs '((def x 123)
             (def y 456))]
  `(do ~@defs))
;= (do (def x 123) (def y 456))


;-----
(defmacro foo
  [& body]
  `(do-something ~@body))

(macroexpand-1 '(foo (doseq [x (range 5)]
                       (println x))
                     :done))
;= (user/do-something
;=   (doseq [x (range 5)]
;=     (println x))
;=   :done)


;-----
'`(map println ~[foo])
;= (clojure.core/seq
;=   (clojure.core/concat 
;=     (clojure.core/list (quote clojure.core/map)) 
;=     (clojure.core/list (quote clojure.core/println)) 
;=     (clojure.core/list [foo])))


;-----
(defn fn-hello [x]
  (str "Hello, " x "!"))

(defmacro macro-hello [x]
  `(str "Hello, " ~x "!"))


;-----
(fn-hello "Brian")
;= "Hello, Brian!"
(macro-hello "Brian")
;= "Hello, Brian!"


;-----
(map fn-hello ["Brian" "Not Brian"])
;= ("Hello, Brian!" "Hello, Not Brian!")
(map macro-hello ["Brian" "Not Brian"])
;= #<CompilerException java.lang.RuntimeException:
;=   Can't take value of a macro: #'user/macro-hello, compiling:(NO_SOURCE_PATH:1)>


;-----
(map #(macro-hello %) ["Brian" "Not Brian"])
;= ("Hello, Brian!" "Hello, Not Brian!")


;-----
(defmacro unhygienic
      [& body]
      `(let [x :oops]
         ~@body))
;= #'user/unhygenic
(unhygienic (println "x:" x))
;= #<CompilerException java.lang.RuntimeException:
;=   Can't let qualified name: user/x, compiling:(NO_SOURCE_PATH:1)>


;-----
(macroexpand-1 `(unhygienic (println "x:" x)))
;= (clojure.core/let [user/x :oops] 
;=   (clojure.core/println "x:" user/x))


;-----
(defmacro still-unhygienic
  [& body]
  `(let [~'x :oops]
     ~@body))
;= #'user/still-unhygenic
(still-unhygienic (println "x:" x))
; x: :oops
;= nil
(macroexpand-1 '(still-unhygienic
                  (println "x:" x)))
;= (clojure.core/let [x :oops]
;=   (println "x:" x))


;-----
(let [x :this-is-important]
  (still-unhygienic
    (println "x:" x)))
; x: :oops


;-----
(gensym)
;= G__2386
(gensym)
;= G__2391


;-----
(gensym "sym")
;= sym2396
(gensym "sym")
;= sym2402


;-----
(defmacro hygienic
  [& body]
  (let [sym (gensym)]
    `(let [~sym :macro-value]
       ~@body)))
;= #'user/hygienic
(let [x :important-value]
  (hygienic (println "x:" x)))
; x: :important-value
;= nil


;-----
(defmacro hygienic
  [& body]
  `(let [x# :macro-value]
     ~@body))


;-----
`(x# x#)
;= (x__1447__auto__ x__1447__auto__)


;-----
(defmacro auto-gensyms
  [& numbers]
  `(let [x# (rand-int 10)]
     (+ x# ~@numbers)))
;= #'user/auto-gensyms
(auto-gensyms 1 2 3 4 5)
;= 22
(macroexpand-1 '(auto-gensyms 1 2 3 4 5))
;= (clojure.core/let [x__570__auto__ (clojure.core/rand-int 10)]  
;=   (clojure.core/+ x__570__auto__ 1 2 3 4 5))


;-----
[`x# `x#]
;= [x__1450__auto__ x__1451__auto__]


;-----
(defmacro our-doto [expr & forms]
  `(let [obj# ~expr]
     ~@(map (fn [[f & args]]
              `(~f obj# ~@args)) forms)
     obj#))


;-----
(our-doto "It works"
  (println "I can't believe it"))
;= #<CompilerException java.lang.RuntimeException: 
;=   Unable to resolve symbol: obj__1456__auto__ in this context, 
;=   compiling:(NO_SOURCE_PATH:1)>


;-----
(defmacro our-doto [expr & forms]
  (let [obj (gensym "obj")]
    `(let [~obj ~expr]
       ~@(map (fn [[f & args]]
                `(~f ~obj ~@args)) forms)
       ~obj)))


;-----
(our-doto "It works"
  (println "I can't believe it")
  (println "I still can't believe it"))
; It works I can't believe it
; It works I still can't believe it
;= "It works"


;-----
(defmacro with
  [name & body]
  `(let [~name 5]
     ~@body))
;= #'user/with
(with bar (+ 10 bar))
;= 15
(with foo (+ 40 foo))
;= 45


;-----
(defmacro spy [x]
  `(do
     (println "spied" '~x ~x)
     ~x))


;-----
(spy 2)
; spied 2 2
;= 2
(spy (rand-int 10))
; spied (rand-int 10) 9
;= 7


;-----
(macroexpand-1 '(spy (rand-int 10)))
;= (do (println (rand-int 10))
;=   (rand-int 10))


;-----
(defmacro spy [x] 
  `(let [x# ~x]
     (println "spied" '~x x#)
     x#))

(macroexpand-1 '(spy (rand-int 10)))
;= (let [x__725__auto__ (rand-int 10)]
;=   (println x__725__auto__ '(rand-int 10))
;=   x__725__auto__)


;-----
(spy (rand-int 10))
; spied (rand-int 10) 9
;= 9


;-----
(defn spy-helper [expr value]
  (println expr value)
  value)

(defmacro spy [x]
  `(spy-helper '~x ~x))


;-----
(let [a 42
      b "abc"]
  ...)

(if-let [x (test)]
  then
  else)

(with-open [in (input-stream ...)
            out (output-stream ...)]
  ...)

(for [x (range 10)
      y (range x)]
  [x y])               


;-----
(defmacro spy-env []
  (let [ks (keys &env)]
    `(prn (zipmap '~ks [~@ks]))))

(let [x 1 y 2]
  (spy-env)
  (+ x y))
; {x 1, y 2}
;= 3


;-----
(defmacro simplify
  [expr]
  (let [locals (set (keys &env))]
    (if (some locals (flatten expr))
      expr
      (do
        (println "Precomputing: " expr)
        (list `quote (eval expr))))))


;-----
(defn f
  [a b c]
  (+ a b c (simplify (apply + (range 5e7)))))
; Precomputing:  (apply + (range 5e7))
;= #'user/f
(f 1 2 3)            ;; returns instantly
;= 1249999975000006
(defn f'                                           
  [a b c]
  (simplify (apply + a b c (range 5e7))))
;= #'user/f'
(f' 1 2 3)           ;; takes ~2.5s to calculate
;= 1249999975000006


;-----
(@#'simplify nil {} '(inc 1))
; Precomputing:  (inc 1)
;= (quote 2)
(@#'simplify nil {'x nil} '(inc x))
;= (inc x)


;-----
(defmacro ontology
  [& triples]
  (every? #(or (== 3 (count %))
               (throw (IllegalArgumentException.
                        "All triples provided as arguments must have 3 elements")))
          triples)
  ;; build and emit pre-processed ontology here...
  )


;-----
(ontology ["Boston" :capital-of])
;= #<IllegalArgumentException java.lang.IllegalArgumentException:
;=   All triples provided as arguments must have 3 elements>
(pst)
;= IllegalArgumentException All triples provided as arguments must have 3 elements
;=   user/ontology (NO_SOURCE_FILE:3)


;-----
(defmacro ontology
  [& triples]
  (every? #(or (== 3 (count %))
               (throw (IllegalArgumentException.
                        (format "Vector `%s` provided to `%s` on line %s does not have 3 elements"
                                %
                                (first &form)
                                (-> &form meta :line)))))
          triples)
  ;; ...
  )


;-----
(ontology ["Boston" :capital-of])
;= #<IllegalArgumentException java.lang.IllegalArgumentException:
;=   Vector `["Boston" :capital-of]` provided to `ontology` on line 1 does not have 3 elements>


;-----
(ns com.clojurebook.macros)
;= nil
(refer 'user :rename '{ontology triples})
;= nil


;-----
(triples ["Boston" :capital-of])
;= #<IllegalArgumentException java.lang.IllegalArgumentException:
;=   Vector `["Boston" :capital-of]` provided to `triples` on line 1 does not have 3 elements>


;-----
(set! *warn-on-reflection* true)
;= true
(defn first-char-of-either
  [a b]
  (.substring ^String (or a b) 0 1))
; Reflection warning, NO_SOURCE_PATH:2 - call to substring can't be resolved.
;= #'user/first-char-of-either


;-----
(defn first-char-of-either
  [^String a ^String b]
  (.substring (or a b) 0 1))
;= #'user/first-char-of-either


;-----
(binding [*print-meta* true]
  (prn '^String (or a b)))
; ^{:tag String, :line 1} (or a b)


;-----
(binding [*print-meta* true]
  (prn (macroexpand '^String (or a b))))
; (let* [or__3548__auto__ a]
;   (if or__3548__auto__ or__3548__auto__ (clojure.core/or b)))


;-----
(defmacro or
  ([] nil)
  ([x] x)
  ([x & next]
    `(let [or# ~x]
       (if or# or# (or ~@next)))))


;-----
(defmacro OR
  ([] nil)
  ([x]
    (let [result (with-meta (gensym "res") (meta &form))]
      `(let [~result ~x]
         ~result)))
  ([x & next]
    (let [result (with-meta (gensym "res") (meta &form))]
      `(let [or# ~x
             ~result (if or# or# (OR ~@next))]
         ~result))))


;-----
(binding [*print-meta* true]
  (prn (macroexpand '^String (OR a b))))
; (let* [or__1176__auto__ a
;        ^{:tag String, :line 2}
;        res1186 (if or__1176__auto__ or__1176__auto__ (user/or b))]
;   ^{:tag String, :line 2} res1186)


;-----
(defn first-char-of-any
  [a b]
  (.substring ^String (OR a b) 0 1))
;= #'user/first-char-of-any


;-----
(defn preserve-metadata
  "Ensures that the body containing `expr` will carry the metadata
   from `&form`."
  [&form expr]
  (let [res (with-meta (gensym "res") (meta &form))]
     `(let [~res ~expr]
        ~res)))

(defmacro OR
  "Same as `clojure.core/or`, but preserves user-supplied metadata
   (e.g. type hints)."
  ([] nil)
  ([x] (preserve-metadata &form x))
  ([x & next]
    (preserve-metadata &form `(let [or# ~x]
                                (if or# or# (or ~@next))))))


;-----
^long (deep-aget aa 0 0)


;-----
(defmacro deep-aget 
 ([array-expr i]
  (let [tag ('{int ints float float long longs double doubles
               byte bytes char chars boolean booleans nil nil}
              (-> &form meta :tag) 'objects)]
    (with-meta `(aget ~(vary-meta array-expr assoc :tag tag) ~i)
      (meta &form))))
 ([array-expr i & js]
  (with-meta 
    `(deep-aget ^Object (deep-aget ~array-expr ~i) ~@js)
    (meta &form))))


;-----
(defn macroexpand1-env [env form]
  (if-let [[x & xs] (and (seq? form) (seq form))]
    (if-let [v (and (symbol? x) (resolve x))]
      (if (-> v meta :macro)
        (apply @v form env xs)
        form)
      form)
    form))


;-----
(macroexpand1-env '{} '(simplify (range 10)))
; Precomputing:  (range 10)
;= (quote (0 1 2 3 4 5 6 7 8 9))
(macroexpand1-env '{range nil} '(simplify (range 10)))
;= (range 10)


;-----
(defmacro spy [expr]
  `(let [value# ~expr]
     (println (str "line #" ~(-> &form meta :line) ",") 
              '~expr value#)
     value#))
;= #'user/spy
(let [a 1
      a (spy (inc a))
      a (spy (inc a))]
  a)
; line #2, (inc a) 2
; line #3, (inc a) 3
;= 3


;-----
(macroexpand1-env {} (with-meta '(spy (+ 1 1)) {:line 42}))
;= (clojure.core/let [value__602__auto__ (+ 1 1)]
;=   (clojure.core/println
;=     (clojure.core/str "line #" 42 ",")
;=     (quote (+ 1 1)) value__602__auto__)
;=   value__602__auto__)


;-----
(defn macroexpand1-env [env form]
  (if-all-let [[x & xs] (and (seq? form) (seq form))
               v (and (symbol? x) (resolve x))
               _ (-> v meta :macro)]
    (apply @v form env xs)
    form))


;-----
(defmacro if-all-let [bindings then else]
  (reduce (fn [subform binding] 
            `(if-let [~@binding] ~subform ~else)) 
    then (reverse (partition 2 bindings))))


;-----
(prn (conj (reverse [1 2 3]) 4))


;-----
(thread [1 2 3] reverse (conj 4) prn)


;-----
(-> foo (bar) (baz))


;-----
(-> foo bar baz)


;-----
(defn ensure-seq [x]
  (if (seq? x) x (list x)))

(ensure-seq 'x)
;= (x)
(ensure-seq '(x))
;= (x)


;-----
(defn insert-second
  "Insert x as the second item in seq y."
  [x ys]
  (let [ys (ensure-seq ys)]
    (concat (list (first ys) x)
            (rest ys))))


;-----
(defn insert-second
  "Insert x as the second item in seq y."
  [x ys]
  (let [ys (ensure-seq ys)]
    `(~(first ys) ~x ~@(rest ys))))


;-----
(defn insert-second
  "Insert x as the second item in seq y."
  [x ys]
  (let [ys (ensure-seq ys)]
    (list* (first ys) x (rest ys))))


;-----
(defmacro thread
  "Thread x through successive forms."
  ([x] x)
  ([x form] (insert-second x form))
  ([x form & more] `(thread (thread ~x ~form) ~@more)))


;-----
(thread [1 2 3] (conj 4) reverse println)
;= (4 3 2 1)
(-> [1 2 3] (conj 4) reverse println)
;= (4 3 2 1)


;-----
(defn thread-fns
  ([x] x)
  ([x form] (form x))
  ([x form & more] (apply thread-fns (form x) more)))

(thread-fns [1 2 3] reverse #(conj % 4) prn)
;= (4 3 2 1)


;-----
(thread [1 2 3] .toString (.split " ") seq)
;= ("[1" "2" "3]")

(thread-fns [1 2 3] .toString #(.split % " ") seq)
;= #<CompilerException java.lang.RuntimeException:
;=   Unable to resolve symbol: .toString in this context, compiling:(NO_SOURCE_PATH:1)>

;; This is starting to look a bit hairy...
(thread-fns [1 2 3] #(.toString %) #(.split % " ") seq)
;= ("[1" "2" "3]")


;-----
(->> (range 10) (map inc) (reduce +))
;= 55


