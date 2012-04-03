;-----
(class (inc (Integer. 5)))
;= java.lang.Long


;-----
(dec 1)
;= 0
(dec 1.0)
;= 0.0
(dec 1N)
;= 0N
(dec 1M)
;= 0M
(dec 5/4)
;= 1/4


;-----
(* 3 0.08 1/4 6N 1.2M)
;= 0.432
(< 1 1.6 7/3 9N 14e9000M)
;= true


;-----
(+ 0.1 0.1 0.1)
;= 0.30000000000000004


;-----
(+ 1/10 1/10 1/10)
;= 3/10


;-----
(+ 7/10 1/10 1/10 1/10)
;= 1


;-----
(double 1/3)
;= 0.3333333333333333


;-----
(rationalize 0.45)
;= 9/20


;-----
(+ 1 1)
;= 2
(+ 1 1.5)
;= 2.5
(+ 1 1N)
;= 2N
(+ 1.1M 1N)
;= 2.1M


;-----
(defn squares-sum
  [& vals]
  (reduce + (map * vals vals)))
;= #'user/squares-sum
(squares-sum 1 4 10)
;= 117


;-----
(squares-sum 1 4 10 20.5)
;= 537.25
(squares-sum 1 4 10 9N)
;= 198N
(squares-sum 1 4 10 9N 5.6M)
;= 229.36M
(squares-sum 1 4 10 25/2)
;= 1093/4


;-----
(.hashCode (BigInteger. "6948736584"))
;= -1641197977
(.hashCode (Long. 6948736584))
;= -1641198007


;-----
(def k Long/MAX_VALUE)
;= #'user/k
k
;= 9223372036854775807


;-----
(inc k)
;= ArithmeticException integer overflow


;-----
(inc (bigint k))
;= 9223372036854775808N
(* 100 (bigdec Double/MAX_VALUE))
;= 1.797693134862315700E+310M


;-----
(dec 10223372636454715900N)
;= 10223372636454715899N
(* 0.5M 1e403M)
;= 5E+402M


;-----
10223372636454715900
;= 10223372636454715900N
(* 2 10223372636454715900)
;= 20446745272909431800N


;-----
(inc' k)
;= 9223372036854775808N


;-----
(inc' 1)
;= 2
(inc' (dec' Long/MAX_VALUE))
;= 9223372036854775807


;-----
System.out.println(Long.MAX_VALUE);
System.out.println(Long.MAX_VALUE + 1);


;-----
9223372036854775807
-9223372036854775808


;-----
Long/MIN_VALUE
;= -9223372036854775808
(dec Long/MIN_VALUE)
;= #<ArithmeticException java.lang.ArithmeticException: integer overflow>


;-----
(unchecked-dec Long/MIN_VALUE)
;= 9223372036854775807
(unchecked-multiply 92233720368547758 1000)
;= -80


;-----
(inc Long/MAX_VALUE)
;= #<ArithmeticException java.lang.ArithmeticException: integer overflow>
(set! *unchecked-math* true)
;= true
(inc Long/MAX_VALUE)
;= -9223372036854775808
(set! *unchecked-math* false)
;= false


;-----
(binding [*unchecked-math* true]
  (inc Long/MAX_VALUE))
;= #<ArithmeticException java.lang.ArithmeticException: integer overflow>


;-----
new BigDecimal(1).divide(new BigDecimal(3));

= java.lang.ArithmeticException:
=   Non-terminating decimal expansion; no exact representable decimal result.


;-----
new BigDecimal(1).divide(new BigDecimal(3), new MathContext(10, RoundingMode.HALF_UP));

= 0.3333333333


;-----
(/ 22M 7)
;= #<ArithmeticException java.lang.ArithmeticException:
;=   Non-terminating decimal expansion; no exact representable decimal result.>
(with-precision 10 (/ 22M 7))
;= 3.142857143M
(with-precision 10 :rounding FLOOR
  (/ 22M 7))
;= 3.142857142M


;-----
(set! *math-context* (java.math.MathContext. 10 java.math.RoundingMode/FLOOR))
;= #<MathContext precision=10 roundingMode=FLOOR>
(/ 22M 7)
;= 3.142857142M


;-----
(identical? "foot" (str "fo" "ot"))
;= false
(let [a (range 10)]
  (identical? a a))
;= true


;-----
(identical? 5/4 (+ 3/4 1/2))
;= false
(identical? 5.4321 5.4321)
;= false
(identical? 2600 2600)
;= false


;-----
(identical? 127 (dec 128))
;= true
(identical? 128 (dec 129))
;= false


;-----
(= {:a 1 :b ["hi"]}
   (into (sorted-map) [[:b ["hi"]] [:a 1]])
   (doto (java.util.HashMap.)
     (.put :a 1)
     (.put :b ["hi"])))
;= true


;-----
(= 1 1N (Integer. 1) (Short. (short 1)) (Byte. (byte 1)))
;= true
(= 1.25 (Float. 1.25))
;= true


;-----
(= 1 1.0)
;= false
(= 1N 1M)
;= false
(= 1.25 5/4)
;= false


;-----
(== 0.125 0.125M 1/8)
;= true
(== 4 4N 4.0 4.0M)
;= true


;-----
(defn equiv?
  "Same as `==`, but doesn't throw an exception if any arguments are not numbers."
  [& args]
  (and (every? number? args)
       (apply == args)))
;= #'user/equiv?
(equiv? "foo" 1)
;= false
(equiv? 4 4N 4.0 4.0M)
;= true
(equiv? 0.125 0.125M 1/8)
;= true


;-----
java.util.Map m = new java.util.HashMap();
m.put(1, "integer");
m.put(1L, "long");
m.put(java.math.BigInteger.valueOf(1), "bigint");
System.out.println(m);

>> {1=bigint, 1=long, 1=integer}


;-----
(into #{} [1 1N (Integer. 1) (Short. (short 1))])
;= #{1}
(into {}
      [[1 :long]
       [1N :bigint]
       [(Integer. 1) :integer]])
;= {1 :integer}


;-----
(+ 0.1 0.2)
;= 0.30000000000000004


;-----
(== 1.1 (float 1.1))
;= false


;-----
(double (float 1.1))
;= 1.100000023841858


;-----
1.1f == 1.1d


;-----
(defn foo [a] 0)
;= #'user/foo
(seq (.getDeclaredMethods (class foo)))
;= (#<Method public java.lang.Object user$foo.invoke(java.lang.Object)>)


;-----
(defn foo [^Double a] 0)
;= #'user/foo
(seq (.getDeclaredMethods (class foo)))
;= (#<Method public java.lang.Object user$foo.invoke(java.lang.Object)>)


;-----
(defn round ^long [^double a] (Math/round a))
;= #'user/round
(seq (.getDeclaredMethods (round foo)))
;= (#<Method public java.lang.Object user$round.invoke(java.lang.Object)>
;=  #<Method public final long user$round.invokePrim(double)>)


;-----
(round "string")
;= #<ClassCastException java.lang.ClassCastException:
;=   java.lang.String cannot be cast to java.lang.Number>


;-----
(defn idem ^long [^long x] x)
;= #'user/long
(idem 18/5)
;= 3
(idem 3.14M)
;= 3
(idem 1e15)
;= 1000000000000000
(idem 1e150)
;= #<IllegalArgumentException java.lang.IllegalArgumentException:
;=   Value out of range for long: 1.0E150>


;-----
(map round [4.5 6.9 8.2])
;= (5 7 8)
(apply round [4.2])
;= 4


;-----
(defn foo ^long [a b c d e] 0)
;= #<CompilerException java.lang.IllegalArgumentException:
;=   fns taking primitives support only 4 or fewer args, compiling:(NO_SOURCE_PATH:1)>


;-----
(defn foo ^long [^int a] 0)
;= #<CompilerException java.lang.IllegalArgumentException:
;=   Only long and double primitives are supported>
(defn foo ^long [^double a] a)
;= #<CompilerException java.lang.IllegalArgumentException:
;=   Mismatched primitive return, expected: long, had: double>


;-----
(set! *warn-on-reflection* true)
;= true
(loop [x 5]
  (when-not (zero? x)
    (recur (dec x))))
;= nil


;-----
(loop [x 5]
  (when-not (zero? x)
    (recur (dec' x))))
; NO_SOURCE_FILE:2 recur arg for primitive local:
;                x is not matching primitive, had: Object, needed: long
; Auto-boxing loop arg: x
;= nil


;-----
(loop [x 5]
  (when-not (zero? x)
    (recur 0.0)))
; NO_SOURCE_FILE:2 recur arg for primitive local:
;                x is not matching primitive, had: double, needed: long
; Auto-boxing loop arg: x
;= nil


;-----
(defn dfoo ^double [^double a] a)
;= #'user/dfoo
(loop [x 5]
  (when-not (zero? x)
    (recur (dfoo (dec x)))))
; NO_SOURCE_FILE:2 recur arg for primitive local:
;                x is not matching primitive, had: double, needed: long
; Auto-boxing loop arg: x
;= nil


;-----
(loop [x 5]
  (when-not (zero? x)
    (recur (long (dfoo (dec x))))))
;= nil


;-----
(defn round [v]
  (Math/round v))
; Reflection warning, NO_SOURCE_PATH:2 - call to round can't be resolved.
;= #'user/round
(defn round [v]
  (Math/round (double v)))
;= #'user/round


;-----
(class (int 5))
;= java.lang.Long


;-----
(defn vector-histogram
  [data]
  (reduce (fn [hist v]
            (update-in hist [v] inc))
    (vec (repeat 10 0))
    data))


;-----
(def data (doall (repeatedly 1e6 #(rand-int 10))))
;= #'user/data
(time (vector-histogram data))
; "Elapsed time: 505.409 msecs"
;= [100383 100099 99120 100694 100003 99940 100247 99731 99681 100102]


;-----
(defn array-histogram
  [data]
  (vec
    (reduce (fn [^longs hist v]
              (aset hist v (inc (aget hist v)))
              hist)
            (long-array 10)
            data)))


;-----
(time (array-histogram data))
; "Elapsed time: 25.925 msecs"
;= [100383 100099 99120 100694 100003 99940 100247 99731 99681 100102]


;-----
(into-array ["a" "b" "c"])
;= #<String[] [Ljava.lang.String;@4413515e>
(into-array CharSequence ["a" "b" "c"])
;= #<CharSequence[] [Ljava.lang.CharSequence;@5acad437>


;-----
(into-array Long/TYPE (range 5))
;= #<long[] [J@21e3cc77>


;-----
(long-array 10)
;= #<long[] [J@12ee6d57>
(long-array (range 10))
;= #<long[] [J@676982f8>


;-----
(seq (long-array 20 (range 10)))
;= (0 1 2 3 4 5 6 7 8 9 0 0 0 0 0 0 0 0 0 0)


;-----
(def arr (make-array String 5 5))
;= #'user/arr
(aget arr 0 0)
;= nil
(def arr (make-array Boolean/TYPE 10))
;= #'user/arr
(aget arr 0)
;= false


;-----
(class (make-array Character/TYPE 0 0 0))
;= [[[C


;-----
(Class/forName "[[Z")
;= [[Z
(.getComponentType *1)
;= [Z
(.getComponentType *1)
;= boolean


;-----
^objects
^booleans
^bytes
^chars
^longs
^ints
^shorts
^doubles
^floats


;-----
(let [arr (long-array 10)]
  (aset arr 0 50)
  (aget arr 0))
;= 50


;-----
(let [a (int-array (range 10))] 
  (amap a i res
    (inc (aget a i))))
;= #<int[] [I@eaf261a>
(seq *1)
;= (1 2 3 4 5 6 7 8 9 10)


;-----
(let [a (int-array (range 10))] 
  (areduce a i sum 0
    (+ sum (aget a i))))
;= 45


;-----
(def arr (make-array Double/TYPE 1000 1000))
;= #'user/arr
(time (dotimes [i 1000]
        (dotimes [j 1000]
          (aset arr i j 1.0)
          (aget arr i j))))
; "Elapsed time: 50802.798 msecs"


;-----
(time (dotimes [i 1000]
        (dotimes [j 1000]
          (let [^doubles darr (aget ^objects arr i)]
            (aset darr j 1.0)
            (aget darr j)))))
; "Elapsed time: 21.543 msecs"
;= nil


;-----
(defmacro deep-aget
  "Gets a value from a multidimensional array as if via `aget`,
   but with automatic application of appropriate type hints to
   each step in the array traversal as guided by the hint added
   to the source array.

   e.g. (deep-aget ^doubles arr i j)"
  ([array idx]
    `(aget ~array ~idx))
  ([array idx & idxs]
    (let [a-sym (gensym "a")]
      `(let [~a-sym (aget ~(vary-meta array assoc :tag 'objects) ~idx)]
         (deep-aget ~(with-meta a-sym {:tag (-> array meta :tag)}) ~@idxs)))))<3>


;-----
(defmacro deep-aset
  "Sets a value in a multidimensional array as if via `aset`,
   but with automatic application of appropriate type hints to
   each step in the array traversal as guided by the hint added
   to the target array.

   e.g. (deep-aset ^doubles arr i j 1.0)"
  [array & idxsv]
  (let [hints '{booleans boolean, bytes byte
                chars char, longs long
                ints int, shorts short
                doubles double, floats float}
        hint (-> array meta :tag)
        [v idx & sxdi] (reverse idxsv)                     
        idxs (reverse sxdi)
        v (if-let [h (hints hint)] (list h v) v)
        nested-array (if (seq idxs)
                       `(deep-aget ~(vary-meta array assoc :tag 'objects) ~@idxs)
                        array)
        a-sym (gensym "a")]
    `(let [~a-sym ~nested-array]
       (aset ~(with-meta a-sym {:tag hint}) ~idx ~v))))


;-----
(time (dotimes [i 1000]
        (dotimes [j 1000]
          (deep-aset ^doubles arr i j 1.0)
          (deep-aget ^doubles arr i j))))
; "Elapsed time: 25.033 msecs"                 


;-----
(render-text (mandlebrot -2.25 0.75 -1.5 1.5 :width 80 :height 40 :depth 100))
                                                     ***
                                                   ******
                                                    ****
                                           **   *************
                                           ***********************
                                          ***********************
                                         **************************
                                       ****************************
                           *******    ******************************
                         ***********  ******************************
                        ************* *****************************
    ************************************************************
                        ************* *****************************
                         ***********  ******************************
                           *******    ******************************
                                       ****************************
                                         **************************
                                          ***********************
                                           ***********************
                                           **   *************
                                                    ****
                                                   ******
                                                     ***


;-----
(do (time (mandlebrot -2.25 0.75 -1.5 1.5
            :width 1600 :height 1200 :depth 1000))
  nil)
; "Elapsed time: 82714.764 msecs"


;-----
(defn- escape
  [^double a0 ^double b0 depth]
  (loop [a a0
         b b0
         iteration 0]
    (cond
      (< 4 (+ (* a a) (* b b))) iteration
      (>= iteration depth) -1
      :else (recur (+ a0 (- (* a a) (* b b)))
                   (+ b0 (* 2 (* a b)))
                   (inc iteration)))))


;-----
(do (time (mandlebrot -2.25 0.75 -1.5 1.5
            :width 1600 :height 1200 :depth 1000))
  nil)
; "Elapsed time: 8663.841 msecs"


;-----
(render-image (mandlebrot -2.25 0.75 -1.5 1.5 :width 800 :height 800 :depth 500))


;-----
(render-image (mandlebrot -1.5 -1.3 -0.1 0.1 :width 800 :height 800 :depth 500))


;-----
(javax.imageio.ImageIO/write *1 "png" (java.io.File. "mandlebrot.png"))


