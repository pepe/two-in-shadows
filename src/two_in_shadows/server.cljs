(ns two-in-shadows.server
  "Server part of the project"
  (:require ["koa" :as Koa]
            ["koa-json" :as json]
            ["@koa/cors" :as cors]
            ["koa-conditional-get" :as conditional]
            ["koa-etag" :as etag]
            [two-in-shadows.routes :as routes]))


(defn main
  "Main function run when app starts"
  []
  (let [app (Koa.)]
    (doto app
      (.use (conditional))
      (.use (etag))
      (.use routes/allowed-methods)
      (.use routes/all)
      (.use (json))
      (.use (cors))
      (.listen 8270))))
