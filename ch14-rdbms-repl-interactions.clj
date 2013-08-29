;-----
(require '[clojure.java.jdbc :as jdbc])
;= nil
(def db-spec {:classname "org.sqlite.JDBC"    
              :subprotocol "sqlite"
              :subname "test.db"})
;= #'user/db


;-----
{:classname "com.mysql.jdbc.Driver"
 :subprotocol "mysql"
 :subname "//localhost:3306/databasename"
 :user "login"
 :password "password"}


;-----
{:datasource datasource-instance
 :user "login"
 :password "password"}


;-----
{:name "java:/comp/env/jdbc/postgres"
 :environment {}} ; optional JNDI parameters for initializing javax.naming.InitialContext



;-----
(jdbc/with-connection db-spec)                                      
;= nil


;-----
(jdbc/with-connection db-spec                                        
  (jdbc/create-table :authors 
    [:id "integer primary key"]
    [:first_name "varchar"]
    [:last_name "varchar"]))
;= (0)


;-----
(jdbc/with-connection db-spec                                              
  (jdbc/insert-records :authors 
    {:first_name "Chas" :last_name "Emerick"}
    {:first_name "Christophe" :last_name "Grand"}
    {:first_name "Brian" :last_name "Carper"}))
;= ({:last_insert_rowid() 1}
;=  {:last_insert_rowid() 2}
;=  {:last_insert_rowid() 3})


;-----
(jdbc/with-connection db-spec                                              
  (jdbc/with-query-results res ["SELECT * FROM authors"]
    (doall res)))
;= ({:id 1, :first_name "Chas", :last_name "Emerick"}
;=  {:id 2, :first_name "Christophe", :last_name "Grand"}
;=  {:id 3, :first_name "Brian", :last_name "Carper"})


;-----
(jdbc/with-connection db-spec                                              
  (jdbc/with-query-results res ["SELECT * FROM authors"]
    (doall (map #(str (:first_name %) " " (:last_name %)) res))))
;= ("Chas Emerick" "Christophe Grand" "Brian Carper")


;-----
(jdbc/with-connection db-spec  
  (jdbc/with-query-results res ["SELECT * FROM authors WHERE id = ?" 2]  
    (doall res)))
;= ({:id 2, :first_name "Christophe", :last_name "Grand"})


;-----
(jdbc/with-connection db-spec  
  (jdbc/with-query-results res ["SELECT * FROM authors"] 
    res))
;= ({:id 1, :first_name "Chas", :last_name "Emerick"})


;-----
(defn fetch-results [db-spec query]
  (jdbc/with-connection db-spec  
    (jdbc/with-query-results res query
      (doall res))))
;= #'user/fetch-results
(fetch-results db-spec ["SELECT * FROM authors"])
;= ({:id 1, :first_name "Chas", :last_name "Emerick"}
;=  {:id 2, :first_name "Christophe", :last_name "Grand"}
;=  {:id 3, :first_name "Brian", :last_name "Carper"})


;-----
(jdbc/with-connection db-spec  
  (jdbc/transaction
    (jdbc/delete-rows :authors ["id = ?" 1])
    (throw (Exception. "Abort transaction!"))))
;= ; Exception Abort transaction!
(fetch-results ["SELECT * FROM authors where id = ?" 1])
;= ({:id 1, :first_name "Chas", :last_name "Emerick"})


;-----
(jdbc/with-connection db-spec
  (.setTransactionIsolation (jdbc/connection) java.sql.Connection/TRANSACTION_SERIALIZABLE)
  (jdbc/transaction
    (jdbc/delete-rows :authors ["id = ?" 2])))



;-----
(import 'com.mchange.v2.c3p0.ComboPooledDataSource)
; Feb 05, 2011 2:26:40 AM com.mchange.v2.log.MLog <clinit>
; INFO: MLog clients using java 1.4+ standard logging.
;= com.mchange.v2.c3p0.ComboPooledDataSource

(defn pooled-spec
  [{:keys [classname subprotocol subname username password] :as other-spec}]
  (let [cpds (doto (ComboPooledDataSource.)
                     (.setDriverClass classname)
                     (.setJdbcUrl (str "jdbc:" subprotocol ":" subname))
                     (.setUser username)
                     (.setPassword password))]
    {:datasource cpds}))


;-----
(def pooled-db (pooled-spec db-spec))
; Dec 27, 2011 8:49:28 AM com.mchange.v2.c3p0.C3P0Registry banner
; INFO: Initializing c3p0-0.9.1.2 [built 21-May-2007 15:04:56; debug? true; trace: 10]
;= #'user/pooled-db

(fetch-results pooled-db ["SELECT * FROM authors"])
; Dec 27, 2011 8:56:40 AM com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource getPoolManager
; INFO: Initializing c3p0 pool... com.mchange.v2.c3p0.ComboPooledDataSource
; [ acquireIncrement -> 3, acquireRetryAttempts -> 30, acquireRetryDelay -> 1000, ...
;= ({:id 1, :first_name "Chas", :last_name "Emerick"}
;=  {:id 2, :first_name "Christophe", :last_name "Grand"}
;=  {:id 3, :first_name "Brian", :last_name "Carper"})

(fetch-results pooled-db ["SELECT * FROM authors"])
;= ({:id 1, :first_name "Chas", :last_name "Emerick"}
;=  {:id 2, :first_name "Christophe", :last_name "Grand"}
;=  {:id 3, :first_name "Brian", :last_name "Carper"})



;-----
(require '[clojure.java.jdbc :as jdbc])

(def db-spec {:classname "org.sqlite.JDBC"    
              :subprotocol "sqlite"
              :subname "test.db"})

(defn setup
  []
  (jdbc/with-connection db-spec
    (jdbc/create-table :country
                       [:id "integer primary key"]
                       [:country "varchar"])
    (jdbc/create-table :author
                       [:id "integer primary key"]
                       [:country_id "integer constraint fk_country_id references country (id)"]
                       [:first_name "varchar"]
                       [:last_name "varchar"])
    (jdbc/insert-records :country
                         {:id 1 :country "USA"}
                         {:id 2 :country "Canada"}
                         {:id 3 :country "France"})
    (jdbc/insert-records :author
                         {:first_name "Chas" :last_name "Emerick" :country_id 1}
                         {:first_name "Christophe" :last_name "Grand" :country_id 3}
                         {:first_name "Brian" :last_name "Carper" :country_id 2}
                         {:first_name "Mark" :last_name "Twain" :country_id 1})))

(setup)
;= ({:id 1, :country_id 1, :first_name "Chas", :last_name "Emerick"}
;=  {:id 2, :country_id 3, :first_name "Christophe", :last_name "Grand"}
;=  {:id 3, :country_id 2, :first_name "Brian", :last_name "Carper"}
;=  {:id 4, :country_id 1, :first_name "Mark", :last_name "Twain"})


;-----
(use '[korma db core])
(defdb korma-db db-spec)


;-----
(declare author)

(defentity country
  (pk :id)
  (has-many author))

(defentity author
  (pk :id)
  (table :author)
  (belongs-to country))


;-----
(select author
  (with country)
  (where {:first_name "Chas"}))
;= [{:id 1, :country_id 1, :first_name "Chas", :last_name "Emerick", :id_2 1, :country "USA"}]


;-----
(select author
  (with country)
  (where (like :first_name "Ch%"))
  (order :last_name :asc)
  (limit 1)
  (offset 1))
;= [{:id 2, :country_id 3, :first_name "Christophe", :last_name "Grand", :id_2 3, :country "France"}]


;-----
(select author
     (fields :first_name :last_name)
     (where (or (like :last_name "C%")
                (= :first_name "Mark"))))
;= [{:first_name "Brian", :last_name "Carper"}
;=  {:first_name "Mark", :last_name "Twain"}]


;-----
(println (sql-only (select author
                     (with country)
                     (where (like :first_name "Ch%"))
                     (order :last_name :asc)
                     (limit 1) 
                     (offset 1))))
;= ; SELECT "author".* FROM "author" LEFT JOIN "country"
;= ; ON "country"."id" = "author"."country_id"
;= ; WHERE "author"."first_name" LIKE ?
;= ; ORDER BY "author"."last_name" ASC LIMIT 1 OFFSET 1


;-----
(def query (-> (select* author)
             (fields :last_name :first_name)
             (limit 5)))
;= #'user/query


;-----
{:group [],
 :from
 [{:table "author",
   :name "author",
   :pk :id,
   :db nil,
   :transforms (),
   :prepares (),
   :fields [],
   :rel
   {"country"
    #<Delay@54f690e4: 
      {:table "country",
       :alias nil,
       :rel-type :belongs-to,
       :pk {:korma.sql.utils/generated "\"country\".\"id\""},
       :fk
       {:korma.sql.utils/generated "\"author\".\"country_id\""}}>}}],
 :joins [],
 :where [],
 :ent
 {:table "author",
  :name "author",
  :pk :id,
  :db nil,
  :transforms (),
  :prepares (),
  :fields [],
  :rel
  {"country"
   #<Delay@54f690e4: 
     {:table "country",
      :alias nil,
      :rel-type :belongs-to,
      :pk {:korma.sql.utils/generated "\"country\".\"id\""},
      :fk {:korma.sql.utils/generated "\"author\".\"country_id\""}}>}},
 :limit 5,
 :type :select,
 :alias nil,
 :options nil,
 :fields (:last_name :first_name),
 :results :results,
 :table "author",
 :order [],
 :modifiers [],
 :db nil,
 :aliases #{}}



;-----
(def employees (where (select* employees) {:type "employee"}))

;; ... later ...
(let [managers (-> employees
                 (where {:role "manager"})
                 (order :last_name))]
  (doseq [e (exec managers)]
    ; ... process results ...
    ))


;-----
(def humans (-> (select* humans)
                (order :date_of_birth)))

(let [kings-of-germany (-> humans
                         (where {:country "Germany" :profession "King"}))]
  (doseq [start (range 0 100 10)
          k (select kings-of-germany
              (offset start)
              (limit 10))]
    ...)



;-----
(import 'org.hibernate.SessionFactory
        'org.hibernate.cfg.Configuration
        'com.clojurebook.hibernate.Author)


;-----
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        }
        catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}


;-----
(defonce session-factory
  (delay (-> (Configuration.)
           .configure
           .buildSessionFactory)))


;-----
public static void saveAuthors (Author... authors) {
  Session session = sessionFactory.openSession();
  session.beginTransaction();
  for (Author author : authors) {
    session.save(author);  
  }
  session.getTransaction().commit();
  session.close();
}

saveAuthors(new Author("Christophe", "Grand"), new Author("Brian", "Carper"), ...);


;-----
(defn add-authors
  [& authors]
  (with-open [session (.openSession @session-factory)]
    (let [tx (.beginTransaction session)]
      (doseq [author authors]
        (.save session author))
      (.commit tx))))

(add-authors (Author. "Christophe" "Grand") (Author. "Brian" "Carper") (Author. "Chas" "Emerick"))


;-----
Session session = HibernateUtil.getSessionFactory().openSession();

try {
  return (List<Author>)newSession.createQuery("from Author").list();
} finally {
  session.close();
}


;-----
(defn get-authors
  []
  (with-open [session (.openSession @session-factory)]
    (-> session
      (.createQuery "from Author")
      .list)))


;-----
(for [{:keys [firstName lastName]} (map bean (get-authors))]
  (str lastName ", " firstName))
;= ("Carper, Brian" "Emerick, Chas" "Grand, Christophe")


;-----
(defmacro with-session
  [session-factory & body]
  `(with-open [~'session (.openSession ~(vary-meta session-factory assoc :tag 'SessionFactory))]
     ~@body))


;-----
(defn get-authors
  []
  (with-session @session-factory
    (-> session
      (.createQuery "from Author")
      .list)))


;-----
(defmacro with-transaction
  [& body]
  `(let [~'tx (.beginTransaction ~'session)]
     ~@body
     (.commit ~'tx)))


;-----
(defn add-authors
  [& authors]
  (with-session @session-factory
    (with-transaction
      (doseq [author authors]
        (.save session author)))))


