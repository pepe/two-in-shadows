(ns two-in-shadows.server
  "Server part of the project"
  (:require ["koa" :as Koa]
            ["koa-json" :as json]
            [two-in-shadows.routes :as routes]))


(defn main
  "Main function run when app starts"
  []
  (let [app (Koa.)]
    (doto app
      (.use routes/allowed-methods)
      (.use routes/all)
      (.use json)
      (.listen 8270))))
