(ns com.clojurebook.concurrency)

(defmacro futures
  [n & exprs]
  (->> (for [expr exprs]
         `(future ~expr))
    (repeat n)
    (mapcat identity)
    vec))

(defmacro wait-futures
  [& args]
  `(doseq [f# (futures ~@args)]
     @f#))