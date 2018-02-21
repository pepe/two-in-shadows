(ns two-in-shadows.server.events
  (:require [potok.core :as ptk]
            [beicon.core :as rx]
            [two-in-shadows.server.file-storage :as fs]))


(defrecord Sync []
  ptk/UpdateEvent
  (update [_ state] (dissoc state :state/dirty))
  ptk/EffectEvent
  (effect [_ state _]
    (fs/store state)))


(defrecord Add [collection item]
  ptk/UpdateEvent
  (update [_ state]
    (let [coll (get state collection)]
      (cond-> state
        (not coll)
        (assoc collection #{})
        (not (contains? coll item))
        (-> (assoc :state/dirty true)
            (update collection conj item)))))
  ptk/WatchEvent
  (watch [_ state _]
    (if (:state/dirty state) (rx/just (->Sync)) (rx/empty))))


(defrecord Remove [collection item]
  ptk/UpdateEvent
  (update [_ state]
    (cond-> state
      (contains? (get state collection) item)
      (-> (assoc :state/dirty true)
          (update collection disj item))))
  ptk/WatchEvent
  (watch [_ state _] (if (:state/dirty state) (rx/just (->Sync)) (rx/empty))))


(defrecord Init []
  ptk/UpdateEvent
  (update [_ state]
    (fs/retrieve)))


(defn add-one
  "Constructor for Add event"
  [store collection item]
  (ptk/emit! store (->Add collection item)))


(defn remove-one
  "Constructor for Remove event"
  [store collection item]
  (ptk/emit! store (->Remove collection item)))


(defn get-all
  "Gets collection from store"
  ([store] (keys @(rx/to-atom store)))
  ([store collection]
   (get @(rx/to-atom store) collection)))

(defn init-store
  "Initializes store and returns it"
  []
  (let [store (ptk/store {:state nil})]
    (ptk/emit! store (->Init))
    store))
