;-----
(defmulti fill
  "Fill a xml/html node (as per clojure.xml)
   with the provided value."
  (fn [node value] (:tag node)))

(defmethod fill :div
  [node value]                
  (assoc node :content [(str value)]))

(defmethod fill :input
  [node value]
  (assoc-in node [:attrs :value] (str value)))


;-----
(fill {:tag :div} "hello")
;= {:content ["hello"], :tag :div}
(fill {:tag :input} "hello")
;= {:attrs {:value "hello"}, :tag :input}
(fill {:span :input} "hello")
;= #<IllegalArgumentException java.lang.IllegalArgumentException:
;=   No method in multimethod 'fill' for dispatch value: null>


;-----
(defmethod fill :default
  [node value]
  (assoc node :content [(str value)]))

(fill {:span :input} "hello")
;= {:content ["hello"], :span :input}
(fill {:span :input} "hello")
;= {:content ["hello"], :span :input}


;-----
(defmulti fill
  "Fill a xml/html node (as per clojure.xml)
   with the provided value."
  (fn [node value] (:tag node))
  :default nil)

(defmethod fill nil
  [node value]                
  (assoc node :content [(str value)]))

(defmethod fill :input
  [node value]
  (assoc-in node [:attrs :value] (str value)))

(defmethod fill :default
  [node value]
  (assoc-in node [:attrs :name] (str value)))


;-----
(ns-unmap *ns* 'fill)                         

(defn- fill-dispatch [node value]
  (if (= :input (:tag node))
    [(:tag node) (-> node :attrs :type)]
    (:tag node)))

(defmulti fill
  "Fill a xml/html node (as per clojure.xml)
   with the provided value."
  #'fill-dispatch
  :default nil)

(defmethod fill nil
  [node value]
  (assoc node :content [(str value)]))

(defmethod fill [:input nil]
  [node value]
  (assoc-in node [:attrs :value] (str value)))

(defmethod fill [:input "hidden"]
  [node value]
  (assoc-in node [:attrs :value] (str value)))

(defmethod fill [:input "text"]
  [node value]
  (assoc-in node [:attrs :value] (str value)))

(defmethod fill [:input "radio"]
  [node value]
  (if (= value (-> node :attrs :value))
    (assoc-in node [:attrs :checked] "checked")
    (update-in node [:attrs] dissoc :checked)))

(defmethod fill [:input "checkbox"]
  [node value]
  (if (= value (-> node :attrs :value))
    (assoc-in node [:attrs :checked] "checked")
    (update-in node [:attrs] dissoc :checked)))

(defmethod fill :default
  [node value]
  (assoc-in node [:attrs :name] (str value)))


;-----
(fill {:tag :input
       :attrs {:value "first choice"
               :type "checkbox"}}
      "first choice")
;= {:tag :input,
;=  :attrs {:checked "checked",
;=          :type "checkbox",
;=          :value "first choice"}}
(fill *1 "off")
;= {:tag :input
;=  :attrs {:type "checkbox",
;=          :value "first choice"}}


;-----
(derive ::checkbox ::checkable)
;= nil
(derive ::radio ::checkable)
;= nil
(derive ::checkable ::input)
;= nil
(derive ::text ::input)
;= nil


;-----
(isa? ::radio ::input)
;= true
(isa? ::radio ::text)
;= false


;-----
(isa? java.util.ArrayList Object)
;= true
(isa? java.util.ArrayList java.util.List)
;= true
(isa? java.util.ArrayList java.util.Map)
;= false
(derive java.util.Map ::collection)
;= nil
(derive java.util.Collection ::collection)
;= nil
(isa? java.util.ArrayList ::collection)
;= true
(isa? java.util.HashMap ::collection)
;= true


;-----
(def h (make-hierarchy))
;= #'user/h
(isa? h java.util.ArrayList java.util.Collection)
;= true


;-----
(ns-unmap *ns* 'fill)

(def fill-hierarchy (-> (make-hierarchy)
                      (derive :input.radio ::checkable)
                      (derive :input.checkbox ::checkable)
                      (derive ::checkable :input)
                      (derive :input.text :input)
                      (derive :input.hidden :input)))

(defn- fill-dispatch [node value]
  (if-let [type (and (= :input (:tag node)) 
                  (-> node :attrs :type))]
    (keyword (str "input." type))
    (:tag node)))

(defmulti fill
  "Fill a xml/html node (as per clojure.xml)
   with the provided value."
  #'fill-dispatch                             
  :default nil
  :hierarchy #'fill-hierarchy)

(defmethod fill nil [node value]
  (assoc node :content [(str value)]))

(defmethod fill :input [node value]
  (assoc-in node [:attrs :value] (str value)))

(defmethod fill ::checkable [node value]
  (if (= value (-> node :attrs :value))
    (assoc-in node [:attrs :checked] "checked")
    (update-in node [:attrs] dissoc :checked)))


;-----
(fill {:tag :input
       :attrs {:type "date"}}
      "20110820")
;= {:content ["20110820"], :attrs {:type "date"}, :tag :input}


;-----
(defmethod fill nil [node value]
  (if (= :input (:tag node))
    (do
      (alter-var-root #'fill-hierarchy
        derive (fill-dispatch node value) :input)
      (fill node value))
    (assoc node :content [(str value)]))) 


;-----
(fill {:tag :input
       :attrs {:type "date"}}
      "20110820")
;= {:attrs {:value "20110820", :type "date"}, :tag :input}


;-----
(ns-unmap *ns* 'fill)

(def input-hierarchy (-> (make-hierarchy)
                         (derive :input.radio ::checkable)   
                         (derive :input.checkbox ::checkable)))

(defn- fill-dispatch [node value]
  (:tag node))

(defmulti fill
  "Fill a xml/html node (as per clojure.xml)
   with the provided value."
  #'fill-dispatch                             
  :default nil)

(defmulti fill-input
  "Fill an input field."
  (fn [node value] (-> node :attrs :type))
  :default nil
  :hierarchy #'input-hierarchy)
  
(defmethod fill nil [node value]
  (assoc node :content [(str value)]))

(defmethod fill :input [node value]
  (fill-input node value))

(defmethod fill-input nil [node value]
  (assoc-in node [:attrs :value] (str value)))

(defmethod fill-input ::checkable [node value]
  (if (= value (-> node :attrs :value))
    (assoc-in node [:attrs :checked] "checked")
    (update-in node [:attrs] dissoc :checked)))


;-----
(isa? fill-hierarchy [:input.checkbox :text] [::checkable :input])
;= true


;-----
(isa? fill-hierarchy [:input.checkbox String] [::checkable CharSequence])
;= true


;-----
(defn- fill-dispatch [node value]
  (if-let [type (and (= :input (:tag node)) 
                  (-> node :attrs :type))]
    [(keyword (str "input." type)) (class value)]
    [(:tag node) (class value)]))


;-----
(ns-unmap *ns* 'fill)

(def fill-hierarchy (-> (make-hierarchy)
                      (derive :input.radio ::checkable)   
                      (derive :input.checkbox ::checkable)))

(defn- fill-dispatch [node value]
  (if-let [type (and (= :input (:tag node)) 
                  (-> node :attrs :type))]
    [(keyword (str "input." type)) (class value)]
    [(:tag node) (class value)]))

(defmulti fill
  "Fill a xml/html node (as per clojure.xml)
   with the provided value."
  #'fill-dispatch                             
  :default nil
  :hierarchy #'fill-hierarchy)  

(defmethod fill nil
  [node value]
  (if (= :input (:tag node))
    (do
      (alter-var-root #'fill-hierarchy 
        derive (first (fill-dispatch node value)) :input) 
      (fill node value)) 
    (assoc node :content [(str value)]))) 

(defmethod fill
  [:input Object] [node value]
  (assoc-in node [:attrs :value] (str value)))

(defmethod fill [::checkable clojure.lang.IPersistentSet]
  [node value]
  (if (contains? value (-> node :attrs :value))
    (assoc-in node [:attrs :checked] "checked")
    (update-in node [:attrs] dissoc :checked)))


;-----
(fill {:tag :input
       :attrs {:value "yes"
               :type "checkbox"}}
         #{"yes" "y"})
;= {:attrs {:checked "checked", :type "checkbox", :value "yes"}, :tag :input}
(fill *1 #{"no" "n"})
;= {:attrs {:type "checkbox", :value "yes"}, :tag :input}


;-----
(fill {:tag :input :attrs {:type "text"}} "some text")
;= {:attrs {:value "some text", :type "text"}, :tag :input}
(fill {:tag :h1} "Big Title!")
;= {:content ["Big Title!"], :tag :h1}


;-----
(defmulti run "Executes the computation." class)

(defmethod run Runnable
  [x]
  (.run x))

(defmethod run java.util.concurrent.Callable
  [x]
  (.call x))


;-----
(run #(println "hello!"))
;= #<IllegalArgumentException java.lang.IllegalArgumentException:
;=   Multiple methods in multimethod 'run' match dispatch value:
;=     class user$fn__1422 -> interface java.util.concurrent.Callable and
;=                            interface java.lang.Runnable, and neither is preferred>


;-----
(prefer-method run java.util.concurrent.Callable Runnable) 
;= #<MultiFn clojure.lang.MultiFn@6dc98c1b>
(run #(println "hello!"))
;= hello!
;= nil


;-----
(macroexpand-1 '(defmethod mmethod-name dispatch-value [args] body))
;= (. mmethod-name clojure.core/addMethod dispatch-value (clojure.core/fn [args] body))


;-----
(defn add-method [multifn dispatch-val f]
  (.addMethod multifn dispatch-val f))


;-----
(class {})
;= clojure.lang.PersistentArrayMap
(type {})
;= clojure.lang.PersistentArrayMap
(class ^{:type :a-tag} {})
;= clojure.lang.PersistentArrayMap
(type ^{:type :a-tag} {})
;= :a-tag


;-----
(ns-unmap *ns* 'run)

(defmulti run "Executes the computation." type)

(defmethod run Runnable
  [x]
  (.run x))

(defmethod run java.util.concurrent.Callable
  [x]
  (.call x))

(prefer-method run java.util.concurrent.Callable Runnable)

(defmethod run :runnable-map
  [m]
  (run (:run m)))

(run #(println "hello!"))
;= hello!
;= nil
(run (reify Runnable
       (run [this] (println "hello!"))))
;= hello!
;= nil
(run ^{:type :runnable-map}
      {:run #(println "hello!") :other :data})
;= hello!
;= nil


;-----
(def priorities (atom {:911-call :high
                       :evacuation :high
                       :pothole-report :low
                       :tree-down :low}))

(defmulti route-message
  (fn [message] (@priorities (:type message))))

(defmethod route-message :low
  [{:keys [type]}]
  (println (format "Oh, there's another %s. Put it in the log." (name type))))

(defmethod route-message :high
  [{:keys [type]}]
  (println (format "Alert the authorities, there's a %s!" (name type))))


;-----
(route-message {:type :911-call})
;= Alert the authorities, there's a 911-call!
;= nil
(route-message {:type :tree-down})
;= Oh, there's another tree-down. Put it in the log.
;= nil


;-----
(swap! priorities assoc :tree-down :high)
;= {:911-call :high, :pothole-report :low, :tree-down :high, :evacuation :high}
(route-message {:type :tree-down})
;= Alert the authorities, there's a tree-down!
;= nil


