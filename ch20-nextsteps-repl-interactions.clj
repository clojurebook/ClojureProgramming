;-----
(use '[clojure.core.logic])

(run* [x] (conso 1 [2 3] x))
;= ((1 2 3))
(run* [x]
  (fresh [_]
    (conso x _ [1 2 3])))
;= (1)
(run* [x]
  (fresh [_]
    (conso _ 3 [1 2 3])))
;= ((2 3))
(run* [q]
  (fresh [x y]
    (conso x y [1 2 3])
    (== q [x y])))
;= ((1 (2 3)))


