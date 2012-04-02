(ns com.clojurebook.test-array-set
  (:use [com.clojurebook.array-set :only (array-set)])
  (:use [clojure.test]))

(deftest test-array-set
  (is (array-set))
  (is (= (array-set) #{}))
  (is (= #{} (array-set)))
  (is (= #{1} (array-set 1)))
  (is (false? ((array-set false) false)))
  (is (nil? ((array-set nil) nil)))
  (let [hello (apply array-set "hello")]
    (is (= #{\h \e \l \o} hello))
    (is (nil? (get hello \w)))
    (is (nil? (hello \w)))
    (is (= \h (hello \h)))
    (is (= \h (apply hello [\h])))
    (is (contains? hello \h))
    (is (= (hash (into #{} "hello")) (hash hello)))))

(deftest verify-immutability
  (let [hello (apply array-set "hello")]
    (is (= (apply array-set "hello")
           (into (reduce disj hello "hello") "hello")))
    (is (= (apply array-set "hello") hello))))

(deftest verify-promotion
  (is (instance? clojure.lang.PersistentHashSet (apply array-set (range 20)))))

(defn bench-set
  [f & {:keys [size trials] :or {size 4 trials 1e6}}]
  (let [items (repeatedly size gensym)]
    (time (loop [s (apply f items)
                 n trials]
            (when (pos? n)
              (doseq [x items] (contains? s x))
              (let [x (rand-nth items)]
                (recur (-> s (disj x) (conj x)) (dec n))))))))

(defn microbenchmark []
  (doseq [n (range 1 5)
          f [#'array-set #'hash-set]]
    (print "size" n (-> f meta :name) ": ")
    (bench-set @f :size n)))
