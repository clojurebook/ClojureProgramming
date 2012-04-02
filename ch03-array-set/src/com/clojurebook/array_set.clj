(ns com.clojurebook.array-set)

(declare empty-array-set)
(def ^:private ^:const max-size 4)

(deftype ArraySet [^objects items ^int size ^:unsynchronized-mutable ^int hashcode]
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
  (seq [this] (take size items))
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

(defn array-set
  "Creates an array-backed set containing the given values."
  [& vals]
  (into empty-array-set vals))
