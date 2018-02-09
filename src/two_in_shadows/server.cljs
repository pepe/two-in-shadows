(ns two-in-shadows.server
  (:require ["koa" :as koa]))

(defn headers [ctx next]
  (set! (. ctx -headers)
        #js{:content-type "application/json"})
  (next))

(defn greeting
  "Renders document with greeting"
  [ctx next]
  (set! (. ctx -body)
        (js/JSON.stringify #js {:message "Hi I am running, help me!"}))
  (next))


(defn main []
  (let [app (koa.)]
      (doto app
        (.use headers)
        (.use greeting)
        (.listen 8270))))

