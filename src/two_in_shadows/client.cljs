(ns two-in-shadows.client
  (:require [rum.core :as rum]
            [potok.core :as ptk]
            [two-in-shadows.events :as events]
            [two-in-shadows.components :as components]))

(defonce store
  (ptk/store {:state {:api/url "http://localhost:8270/"}}))

(defn mount-root
  "Mounts page component"
  []
  (rum/mount (components/Page store) (. js/document (getElementById "container"))))

(ptk/emit! store (events/->GetGreeting))

(mount-root)
