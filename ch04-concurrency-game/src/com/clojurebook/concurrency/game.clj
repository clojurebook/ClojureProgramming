(ns com.clojurebook.concurrency.game
  (:use [com.clojurebook.concurrency :only (futures wait-futures)])
  (:require clojure.pprint
            [clojure.set :as set]))

(defn character
  [name & {:as opts}]
  (ref (merge {:name name :items #{} :health 500}
              opts)))

(defn attack
  "Attacks `target` based on `:strength` of `aggressor`."
  [aggressor target]
  (dosync
    (let [damage (* (rand 0.1) (:strength @aggressor))]
      (commute target update-in [:health] #(max 0 (- % damage))))))

(defn heal
  "Heals `target` based on available `:mana` of `healer`; the latter
   is decreased proportional to the amount of health restored to `target`."
  [healer target]
  (dosync
    (let [aid (* (rand 0.1) (:mana @healer))]
      (when (pos? aid)
        (commute healer update-in [:mana] - (max 5 (/ aid 5)))
        (commute target update-in [:health] + aid)))))

(defn idle
  [x]
  (dosync
    (doseq [stat #{:mana :health}
            :when (stat @x)]
      (commute x update-in [stat] + (rand-int 100)))))

(defn change-name
  [x]
  (dosync
    (commute x assoc :name x)))

(defn loot
  "Transfers one value from (:items @from) to (:items @to).
   Assumes that each is a set.  Returns the new state of 
   from."
  [from to]
  (dosync
    (when-let [item (first (:items @from))]
      (commute to update-in [:items] conj item)
      (alter from update-in [:items] disj item))))

(defn flawed-loot
  "Transfers one value from (:items @from) to (:items @to).
   Assumes that each is a set.  Returns the new state of 
   from.

   *Will* produce invalid results, due to inappropriate use
   of commute instead of alter."
  [from to]
  (dosync
    (when-let [item (first (:items @from))]
      (commute to update-in [:items] conj item)
      (commute from update-in [:items] disj item))))

(def alive? (comp pos? :health))

(defn- play
  [character action other]
  (while (and (alive? @character)
              (alive? @other)
              (action character other))
    (Thread/sleep (rand-int 100))))

(defn -battle-demo []
  (def smaug (character "Smaug" :health 500 :strength 400))
  (def bilbo (character "Bilbo" :health 100 :strength 100))
  (def gandalf (character "Gandalf" :health 75 :mana 1000))

  (wait-futures 1
                (play bilbo attack smaug)
                (play smaug attack bilbo)
                (play gandalf heal bilbo))

  (clojure.pprint/pprint
    (map
      (comp #(select-keys % [:name :health :mana]) deref)
      [smaug bilbo gandalf]))

  (shutdown-agents))


(defn -loot-demo []
  (def smaug (character "Smaug" :health 500 :strength 400 :items (set (range 50))))
  (def bilbo (character "Bilbo" :health 100 :strength 100))
  (def gandalf (character "Gandalf" :health 75 :mana 1000))
  (wait-futures 1
                (while (loot smaug bilbo))
                (while (loot smaug gandalf)))

  (println "Bilbo's and Gandalf's item counts (should always == 50):"
    (map (comp count :items deref) [bilbo gandalf]))
  (println "Overlap in Bilbo's and Gandalf's items (should always be empty):"
    (filter (:items @bilbo) (:items @gandalf)))

  (shutdown-agents))



