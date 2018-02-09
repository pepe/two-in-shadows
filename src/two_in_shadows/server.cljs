(ns two-in-shadows.server
  (:require ["koa" :as koa]
            ["koa-route" :as route]
            [goog.object :as object]
            [goog.json :as json]
            [two-in-shadows.utils :as utils]))

(defn headers [ctx next]
  (object/set ctx "headers" #js{:content-type "application/json"})
  (next))

(defn greeting
  "Renders document with greeting"
  [ctx next]
  (object/set ctx "body"
              (json/serialize #js{:message (utils/greeting "Two In Shadows Server")
                                  :date    (utils/current-date)}))
  (next))


(defn main []
  (let [app (koa.)]
      (doto app
        (.use headers)
        (.use (route/get "/greeting" greeting))
        (.listen 8270))))

