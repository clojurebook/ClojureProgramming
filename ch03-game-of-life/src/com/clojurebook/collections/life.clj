(ns com.clojurebook.collections.life)

(defn empty-board
  "Creates a rectangular empty board of the specified width
   and height."
  [w h]
  (vec (repeat w (vec (repeat h nil)))))


(defn populate
  "Turns :on each of the cells specified as [y, x] coordinates."
  [board living-cells]
  (reduce (fn [board coordinates]
            (assoc-in board coordinates :on))
          board
          living-cells))

(def glider (populate (empty-board 6 6) #{[2 0] [2 1] [2 2] [1 2] [0 1]}))

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

(defn- step-row
  "Yields the next state of the center row."
  [rows-triple]
  (vec (map liveness (cell-block rows-triple)))) 

(defn index-free-step
  "Yields the next state of the board."
  [board]
  (vec (map step-row (window (repeat nil) board))))

(defn step 
 "Yields the next state of the world"
 [cells]
 (set (for [[loc n] (frequencies (mapcat neighbours cells))
            :when (or (= n 3) (and (= n 2) (cells loc)))]
        loc)))

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

(defn hex-neighbours
  [[x y]]
  (for [dx [-1 0 1] dy (if (zero? dx) [-2 2] [-1 1])] 
    [(+ dx x) (+ dy y)]))

(def hex-step (stepper hex-neighbours #{2} #{3 4}))

(defn rect-stepper 
  "Returns a step function for standard game of life on a (bounded) rectangular
   board of specified size."
  [w h]
  (stepper #(filter (fn [[i j]] (and (< -1 i w) (< -1 j h))) 
                    (neighbours %)) #{2 3} #{3}))

(defn draw
  [w h step cells]
  (let [state (atom cells)
        run (atom true)
        listener (proxy [java.awt.event.WindowAdapter] []
                   (windowClosing [_] (reset! run false)))
        pane
          (doto (proxy [javax.swing.JPanel] []
                  (paintComponent [^java.awt.Graphics g]
                    (let [g (doto ^java.awt.Graphics2D (.create g)
                              (.setColor java.awt.Color/BLACK)
                              (.fillRect 0 0 (* w 10) (* h 10))
                              (.setColor java.awt.Color/WHITE))]
                      (doseq [[x y] @state]
                        (.fillRect g (inc (* 10 x)) (inc (* 10 y)) 8 8)))))
            (.setPreferredSize (java.awt.Dimension. (* 10 w) (* 10 h))))] 
    (doto (javax.swing.JFrame. "Quad Life")
      (.setContentPane pane)
      (.addWindowListener listener)
      .pack
      (.setVisible true))
    (future (while @run
              (Thread/sleep 80)
              (swap! state step)
              (.repaint pane)))))

(defn rect-demo []
  (draw 30 30 (rect-stepper 30 30) 
      #{[15 15] [15 17] [16 16] [15 16]}))