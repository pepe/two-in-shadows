(ns two-in-shadows.client
  (:require [rum.core :as rum]
            [two-in-shadows.utils :as utils]))


(rum/defc greeting []
  [:div
   [:h1 (utils/greeting "Two In Shadows Client")]])

(defn mount-root []
  (rum/mount (greeting) (. js/document (getElementById "app"))))

(mount-root)
