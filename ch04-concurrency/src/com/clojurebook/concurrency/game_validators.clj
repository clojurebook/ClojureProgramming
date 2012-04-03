(ns com.clojurebook.concurrency.game-validators
  (:use [com.clojurebook.concurrency :only (futures wait-futures)])
  (:require [clojure.set :as set]
            [clojure.java.io :as io]
            clojure.pprint))

(def console (agent *out*)) ;(java.io.PrintWriter. System/out)
(def character-log (agent (io/writer "character-states.log" :append true)))

(defn write
  [^java.io.Writer w & content]
  (doseq [x (interpose " " content)]
    (.write w (str x)))
  (doto w
    (.write "\n")
    .flush))

(defn log-reference
  [reference & writer-agents]
  (add-watch reference :log
             (fn [_ reference old new]
               (doseq [writer-agent writer-agents]
                 (send-off writer-agent write new)))))



(def alive? (comp pos? :health))

(defn- enforce-max-health
  [name max-health]
  (fn [character-data]
    (or (<= (:health character-data) max-health)
      (throw (IllegalStateException. (str name " is already at max health!"))))))

(defn character
  [name & {:as opts}]
  (let [cdata (merge {:name name :items #{} :health 500}
                     opts)
        cdata (assoc cdata :max-health (:health cdata))
        validators (list* (enforce-max-health name (:health cdata))
                          (:validator cdata))]
    (ref (dissoc cdata :validator)
         :validator #(every? (fn [v] (v %)) validators))))

(def daylight (ref 1))

(defn attack
  "Attacks `target` based on `:strength` of `aggressor`."
  [aggressor target]
  (dosync
    (let [damage (* (rand 0.1) (:strength @aggressor) (ensure daylight))]
      (send-off console write
        (:name @aggressor) "hits" (:name @target) "for" damage)
      (commute target update-in [:health] #(max 0 (- % damage))))))

(defn sunset!
  []
  (dosync (ref-set daylight 0.1)))

(defn heal
  "Heals `target` based on available `:mana` of `healer`; the latter
   is decreased proportional to the amount of health restored to `target`.
   Also ensures that heals never increase `target`'s health beyond their
   `:max-health` value."
  [healer target]
  (dosync
    (let [aid (min (* (rand 0.1) (:mana @healer))
                   (- (:max-health @target) (:health @target)))]
      (when (pos? aid)
        (send-off console write
          (:name @healer) "heals" (:name @target) "for" aid)
        (commute healer update-in [:mana] - (max 5 (/ aid 5)))
        (alter target update-in [:health] + aid)))))

(defn- play
  [character action other]
  (while (and (alive? @character)
              (alive? @other)
              (action character other))
    (Thread/sleep (rand-int 100))))

(defn -main []
  (def smaug (character "Smaug" :health 500 :strength 400))
  (def bilbo (character "Bilbo" :health 100 :strength 100))
  (def gandalf (character "Gandalf" :health 75 :mana 1000))

  (log-reference bilbo console character-log)
  (log-reference smaug console character-log)

  (wait-futures 1
                (play bilbo attack smaug)
                (play smaug attack bilbo)
                (play gandalf heal bilbo))

  (clojure.pprint/pprint
    (map
      (comp #(select-keys % [:name :health :mana]) deref)
      [smaug bilbo gandalf]))

  (shutdown-agents))




