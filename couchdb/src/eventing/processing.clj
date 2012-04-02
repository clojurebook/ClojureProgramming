(ns eventing.processing)

(derive 'sales/lead-generation 'processing/realtime)
(derive 'sales/purchase 'processing/realtime)

(derive 'security/all 'processing/archive)
(derive 'finance/all 'processing/archive)



(defmulti process-event :evt-type)
