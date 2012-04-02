(ns com.clojurebook.hibernate
  (:import (javax.persistence Id Entity GeneratedValue)
           org.hibernate.annotations.GenericGenerator
           org.hibernate.SessionFactory
           org.hibernate.cfg.Configuration
           com.clojurebook.hibernate.Author))

(defonce session-factory
  (delay (-> (Configuration.)
           .configure
           .buildSessionFactory)))

(defn add-authors
  [& authors]
  (with-open [session (.openSession @session-factory)]
    (let [tx (.beginTransaction session)]
      (doseq [author authors]
        (.save session author))
      (.commit tx))))

(defn get-authors
  []
  (with-open [session (.openSession @session-factory)]
    (-> session
      (.createQuery "from Author")
      .list)))

(defmacro with-session
  [session-factory & body]
  `(with-open [~'session (.openSession ~(with-meta session-factory '{:tag SessionFactory}))]
     ~@body))

(defn get-authors
  "A simplified implementation of get-authors, benefitting from the
   with-session macro."
  []
  (with-session @session-factory
    (-> session
      (.createQuery "from Author")
      .list)))

(defn get-authors
  "A simplified implementation of get-authors, benefitting from the
   with-session macro."
  []
  (with-session @session-factory
    (-> session
      (.createQuery "from ClojureAuthor")
      .list)))

(defmacro with-transaction
  [& body]
  `(let [tx# (.beginTransaction ~'session)]
     ~@body
     (.commit tx#)))

(defn add-authors
  "A simplified implementation of add-authors, benefitting from the
   with-session and with-transaction macros."
  [& authors]
  (with-session @session-factory
    (with-transaction
      (doseq [author authors]
        (.save session author)))))
