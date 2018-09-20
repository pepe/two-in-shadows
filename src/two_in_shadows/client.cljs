(ns two-in-shadows.client
  (:require [rum.core :as rum]
            [potok.core :as ptk]
            [two-in-shadows.client.events :as events]
            [two-in-shadows.client.clowns.events :as clowns.events]
            [two-in-shadows.client.components :as components]))

(defn ^:dev/after-load mount []
  (rum/mount (components/Page events/store) (. js/document (getElementById "container"))) )

(defn ^:export init
  "Mounts page component"
  []
  (events/get-greeting events/store)
  (clowns.events/get-all events/store)
  (mount))
