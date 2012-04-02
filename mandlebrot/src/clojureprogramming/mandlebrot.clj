(ns clojureprogramming.mandlebrot
  (:import java.awt.image.BufferedImage
           (java.awt Color RenderingHints)))

(defn- escape
  "Returns an integer indicating how many iterations were required
   before the value of z (using the components `a` and `b`) could
   be determined to have escaped the Mandlebrot set.  If z
   will not escape, -1 is returned."
  [a0 b0 depth]                                         
  (loop [a a0                                       
         b b0
         iteration 0]
    (cond
      (< 4 (+ (* a a) (* b b))) iteration
      (>= iteration depth) -1
      :else (recur (+ a0 (- (* a a) (* b b)))
                   (+ b0 (* 2 (* a b)))
                   (inc iteration)))))

(defn mandlebrot
  "Calculates membership within and number of iterations to escape
   from the Mandlebrot set for the region defined by `rmin`, `rmax`
   `imin` and `imax` (real and imaginary components of z, respectively).
   
   Optional kwargs include `:depth` (maximum number of iterations
   to calculate escape of a point from the set), `:height` ('pixel'
   height of the rendering), and `:width` ('pixel' width of the
   rendering).

   Returns a seq of row vectors containing iteration numbers for when
   the corresponding point escaped from the set. -1 indicates points
   that did not escape in fewer than `depth` iterations, i.e. they
   belong to the set.  These integers can be used to drive most common
   Mandlebrot set visualizations."
  [rmin rmax imin imax & {:keys [width height depth]
                          :or {width 80 height 40 depth 1000}}]
  (let [rmin (double rmin)                                              
        imin (double imin)
        stride-w (/ (- rmax rmin) width)
        stride-h (/ (- imax imin) height)]
    (loop [x 0
           y (dec height)
           escapes []]
      (if (== x width)
        (if (zero? y)
          (partition width escapes)
          (recur 0 (dec y) escapes))
        (recur (inc x) y (conj escapes (escape (+ rmin (* x stride-w))  
                                               (+ imin (* y stride-h))
                                               depth)))))))

(defn render-text
  "Prints a basic textual rendering of mandlebrot set membership,
   as returned by a call to `mandlebrot`."
  [mandlebrot-grid]
  (doseq [row mandlebrot-grid]
    (doseq [escape-iter row]
      (print (if (neg? escape-iter) \* \space)))
    (println)))

(defn render-image
  "Given a mandlebrot set membership grid as returned by a call to
   `mandlebrot`, returns a BufferedImage with the same resolution as the
   grid that uses a discrete grayscale color palette."
  [mandlebrot-grid]
  (let [palette (vec (for [c (range 500)]
                       (Color/getHSBColor 0.0 0.0 (/ (Math/log c) (Math/log 500)))))
        height (count mandlebrot-grid)
        width (count (first mandlebrot-grid))
        img (BufferedImage. width height BufferedImage/TYPE_INT_RGB)
        ^java.awt.Graphics2D g (.getGraphics img)]
    (doseq [[y row] (map-indexed vector mandlebrot-grid)
            [x escape-iter] (map-indexed vector row)]
      (.setColor g (if (neg? escape-iter)
                     (palette 0)
                     (palette (mod (dec (count palette)) (inc escape-iter)))))
      (.drawRect g x y 1 1))
    (.dispose g)
    img))
