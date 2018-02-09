(ns two-in-shadows.routes
  "Routes for the server"
  (:require ["koa-router" :as Router]
            [two-in-shadows.utils :as utils]))

(defn greeting
  "Renders document with greeting"
  [ctx next]
  (utils/set-body ctx {:message (utils/greeting "Two In Shadows Server")
                       :date    (utils/current-date)})
  (next))


(defn ping
  "Returns pong document with pong message"
  [ctx next]
  (utils/set-body ctx {:message "pong"})
  (next))


(def all
  "Return all routes"
  (let [router (Router.)]
    (doto router
      (.get "/ping" ping)
      (.get "/greeting" greeting))
    (.routes router)))
