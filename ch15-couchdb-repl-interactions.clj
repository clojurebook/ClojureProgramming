
;-----
(use '[com.ashafa.clutch :only (create-database with-db put-document
                                get-document delete-document)
                         :as clutch])

(def db (create-database "repl-crud"))

(put-document db {:_id "foo" :some-data "bar"})
;= {:_rev "1-2bd2719826", :some-data "bar", :_id "foo"}
(put-document db (assoc *1 :other-data "quux"))
;= {:other-data "quux", :_rev "2-9f29b39770", :some-data "bar", :_id "foo"}
(get-document db "foo")
;= {:_id "foo", :_rev "2-9f29b39770", :other-data "quux", :some-data "bar"}
(delete-document db *1)
;= {:ok true, :id "foo", :rev "3-3e98dd1028"}
(get-document db "foo")
;= nil


;-----
(clutch/create-document {:_id "foo"
                         :data ["bar" {:details ["bat" false 42]}]})
;= {:_id "foo", :data ["bar" {:details ["bat" false 42]}],
;=  :_rev "1-6d7460947434b90bf88f033785f81cdd"}
(->> (get-document db "foo")
  :data
  second
  :details
  (filter number?))
;= (42)


;-----
(clutch/bulk-update (create-database "logging")
  [{:evt-type "auth/new-user" :username "Chas"}
   {:evt-type "auth/new-user" :username "Dave"}
   {:evt-type "sales/purchase" :username "Chas" :products ["widget1"]}
   {:evt-type "sales/purchase" :username "Robin" :products ["widget14"]}
   {:evt-type "sales/RFQ" :username "Robin" :budget 20000}])



;-----
(clutch/save-view "logging" "jsviews"
  (clutch/view-server-fns :javascript
    {:type-counts
     {:map "function(doc) {
              emit(doc['evt-type'], null);
            }"
     :reduce "function (keys, vals, rereduce) {
                 return vals.length;
              }"}}))


;-----
(clutch/get-view "logging" "jsviews" :type-counts {:group true})
;= ({:key "auth/new-user", :value 2}
;=  {:key "sales/purchase", :value 2}
;=  {:key "sales/RFQ", :value 1})


;-----
(->> (clutch/get-view "logging" "jsviews" :type-counts {:group true})
  (map (juxt :key :value))
  (into {}))
;= {"auth/new-user" 2, "sales/purchase" 2, "sales/RFQ" 1}


;-----
(use '[com.ashafa.clutch.view-server :only (view-server-exec-string)])

(clutch/configure-view-server "http://localhost:5984" (view-server-exec-string))
;= ""


;-----
(clutch/save-view "logging" "clj-views"
  (clutch/view-server-fns :clojure
    {:type-counts
     {:map (fn [doc]
             [[(:evt-type doc) nil]])
       :reduce (fn [keys vals rereduce]
                 (count vals))}}))


;-----
(->> (clutch/get-view "logging" "clj-views" :type-counts {:group true})
     (map (juxt :key :value))
     (into {}))
;= {"auth/new-user" 2, "sales/purchase" 2, "sales/RFQ" 1}


;-----
(ns eventing.types)

(derive 'sales/purchase 'sales/all)
(derive 'sales/purchase 'finance/accounts-receivable)
(derive 'finance/accounts-receivable 'finance/all)
(derive 'finance/all 'events/all)
(derive 'sales/all 'events/all)
(derive 'sales/RFQ 'sales/lead-generation)
(derive 'sales/lead-generation 'sales/all)
(derive 'auth/new-user 'sales/lead-generation)
(derive 'auth/new-user 'security/all)
(derive 'security/all 'events/all)


;-----
(clutch/save-view "logging" "clj-views"
  (clutch/view-server-fns :clojure
    {:type-counts
     {:map (do
             (require 'eventing.types)     
             (fn [doc]
               (let [concrete-type (-> doc :evt-type symbol)] 
                 (for [evtsym (cons concrete-type
                                    (ancestors concrete-type))]
                   [(str evtsym) nil]))))
      :reduce (fn [keys vals rereduce]
                (count vals))}}))
                 
(->> (clutch/with-db "logging"
          (clutch/get-view "clj-views" :type-counts {:group true}))
     (map (juxt :key :value))
     (into {}))
;= {"events/all" 5,
;=  "sales/all" 5,
;=  "finance/all" 2,
;=  "finance/accounts-receivable" 2,
;=  "sales/lead-generation" 3,
;=  "sales/purchase" 2,
;=  "sales/RFQ" 1,
;=  "security/all" 2,
;=  "auth/new-user" 2}


;-----
(clutch/create-database "changes")
(clutch/watch-changes "changes" :echo (partial println "changes:"))

(clutch/bulk-update "changes" [{:_id "doc1"} {:_id "doc2"}])
;= [{:id "doc1", :rev "5-f36e792166"}
;=  {:id "doc2", :rev "3-5570e8bbb3"}]
; change: {:seq 7, :id doc1, :changes [{:rev 5-f36e792166}]}
; change: {:seq 8, :id doc2, :changes [{:rev 3-5570e8bbb3}]}
(clutch/delete-document "changes" (zipmap [:_id :_rev]                               
                                    ((juxt :id :rev) (first *1))))
;= {:ok true, :id "doc1", :rev "6-616e3df68"}
; change: {:seq 9, :id doc1, :changes [{:rev 6-616e3df68}], :deleted true}
(clutch/stop-changes "changes" :echo)
;= nil


;-----
{:evt-type "auth/new-user" :username "Chas"}
{:evt-type "auth/new-user" :username "Dave"}
{:evt-type "sales/purchase" :username "Chas" :products ["widget1"]}
{:evt-type "sales/purchase" :username "Robin" :products ["widget14"]}
{:evt-type "sales/RFQ" :username "Robin" :budget 20000}


;-----
(ns eventing.processing)

(derive 'sales/lead-generation 'processing/realtime)
(derive 'sales/purchase 'processing/realtime)

(derive 'security/all 'processing/archive)
(derive 'finance/all 'processing/archive)


;-----
(clutch/save-filter "logging" "event-filters"
  (clutch/view-server-fns :clojure                      
    {:event-isa? (do
                   (require '[eventing types processing])
                   (fn [doc request]
                     (let [req-type (-> request :query :type)
                           evt-type (:evt-type doc)]
                       (and req-type evt-type
                         (isa? (symbol evt-type) (symbol req-type))))))}))


;-----
(clutch/watch-changes "logging" :echo-leads (partial println "change:")  
  :filter "event-filters/event-isa?"   
  :type "sales/lead-generation"   
  :include_docs true)
 
(clutch/put-document "logging"     
  {:evt-type "sales/RFQ" :username "Lilly" :budget 20000})
;= {:_id "8f264da359f887ec3e86c8d34801704b",
;=  :_rev "1-eb10044985c9dccb731bd5f31d0188c6",
;=  :budget 20000, :evt-type "sales/RFQ", :username "Lilly"}
; change: {:seq 26, :id 8f264da359f887ec3e86c8d34801704b,
;          :changes [{:rev 1-eb10044985c9dccb731bd5f31d0188c6}],
;          :doc {:_id 8f264da359f887ec3e86c8d34801704b,
;                :_rev 1-eb10044985c9dccb731bd5f31d0188c6,
;                :budget 20000,
;                :evt-type sales/RFQ,
;                :username Lilly}}
(clutch/stop-changes "logging" :echo-leads)
;= nil


;-----
(ns eventing.processing)

(defmulti process-event :evt-type)


;-----
(ns salesorg.event-handling
  (use [eventing.processing :only (process-event)]))

(defmethod process-event 'sales/purchase
  [evt]
  (println (format "We made a sale of %s to %s!" (:products evt) (:username evt))))

(defmethod process-event 'sales/lead-generation
  [evt]
  (println "Add prospect to CRM system: " evt))


;-----
(require 'eventing.processing 'salesorg.event-handling)

(clutch/watch-changes "logging" :process-events
  #(-> %
     :doc
     (dissoc :_id :_rev)
     (update-in [:evt-type] symbol)
     eventing.processing/process-event)
  :filter "event-filters/event-isa?"
  :type "processing/realtime"
  :include_docs true)

(clutch/bulk-update "logging"
  [{:evt-type "auth/new-user" :username "Chas"}
   {:evt-type "auth/new-user" :username "Dave"}
   {:evt-type "sales/purchase" :username "Chas" :products ["widget1"]}
   {:evt-type "sales/purchase" :username "Robin" :products ["widget14"]}
   {:evt-type "sales/RFQ" :username "Robin" :budget 20000}])
; Add prospect to CRM system:  {:evt-type auth/new-user, :username Chas}
; Add prospect to CRM system:  {:evt-type auth/new-user, :username Dave}
; We made a sale of ["widget1"] to Chas!
; We made a sale of ["widget14"] to Robin!
; Add prospect to CRM system:  {:budget 20000, :evt-type sales/RFQ, :username Robin}


