(ns two-in-shadows.routes
  "Routes for the server"
  (:require ["koa-router" :as Router]
            [two-in-shadows.utils :as utils]))


(defn ^:private greeting
  "Renders document with greeting"
  [ctx next]
  (utils/set-body ctx {:message (utils/greeting "Two In Shadows Server")
                       :date    (utils/current-date)})
  (next))


(defn ^:private ping
  "Returns pong document with pong message"
  [ctx next]
  (utils/set-body ctx {:message "pong"})
  (next))

(def ^:private router
  "Application router"
  (let [router (Router.)]
    (doto router
      (.get "/ping" ping)
      (.get "/greeting" greeting))))


(def allowed-methods
  "All allowed methods"
  (.allowedMethods router))


(def all
  "All routes"
    (.routes router))
