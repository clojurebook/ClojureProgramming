(ns salesorg.event-handling
  (use [eventing.processing :only (process-event)]))

(defmethod process-event 'sales/purchase
  [evt]
  (println (format "We made a sale of %s to %s!" (:products evt) (:username evt))))

(defmethod process-event 'sales/lead-generation
  [evt]
  (println "Add prospect to CRM system: " evt))
  