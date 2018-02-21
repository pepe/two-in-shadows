(ns two-in-shadows.client
  (:require [rum.core :as rum]
            [potok.core :as ptk]
            [two-in-shadows.client.events :as events]
            [two-in-shadows.client.components :as components]))

(defn mount-root
  "Mounts page component"
  []
  (events/get-greeting events/store)
  (events/get-clowns events/store)
  (rum/mount (components/Page events/store) (. js/document (getElementById "container"))))

(mount-root)
