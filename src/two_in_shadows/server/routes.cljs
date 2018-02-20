(ns two-in-shadows.server.routes
  "Routes for the server"
  (:require ["koa-router" :as Router]
            ["koa-body" :as body]
            [two-in-shadows.utils :as utils]
            [two-in-shadows.server.events :as events]))


(defn ^:private greeting
  "Renders document with greeting"
  [ctx next]
  (utils/set-body ctx {:message (utils/greeting "Welcome to Shadows")
                       :date    (utils/current-date)})
  (next))


(defn ^:private ping
  "Returns pong document with pong message"
  [ctx next]
  (utils/set-raw-body ctx #js{:message "pong"})
  (next))

(defn ^:private list-colls
  "Returns list of collections"
  [store]
  (fn [ctx next]
    (utils/set-body ctx (events/get-all store))
    (next)))

(defn ^:private list
  "Returns list of items"
  [store]
  (fn [ctx next]
    (let [{collection :collection} (utils/get-params ctx)]
      (utils/set-body ctx (events/get-all store collection)))
    (next)))

(defn ^:private add
  "Add item to store"
  [store]
  (fn [ctx next]
    (let [{collection :collection} (utils/get-params ctx)]
      (events/add-one store collection (utils/get-body ctx)))
    (utils/set-body ctx {:message "added"}) (next)))


(defn ^:private remove
  "Remove item from store"
  [store]
  (fn [ctx next]
    (let [{collection :collection} (utils/get-params ctx)]
      (events/remove-one store collection (utils/get-body ctx)))
    (utils/set-body ctx {:message "removed"}) (next)))


(def ^:private router
  "Application router"
  (let [router (Router.)
        store (events/init-store)]
    (doto router
      (.get "/collections" (list-colls store))
      (.get "/storage/:collection" (list store))
      (.delete "/storage/:collection" (body #js{:strict false}) (remove store))
      (.post "/storage/:collection" (body) (add store))
      (.get "/ping" ping)
      (.get "/greeting" greeting))))


(def allowed-methods
  "All allowed methods"
  (.allowedMethods router))


(def all
  "All routes"
  (.routes router))
