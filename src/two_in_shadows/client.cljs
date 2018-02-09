(ns two-in-shadows.client
  (:require [rum.core :as rum]
            [two-in-shadows.utils :as utils]))

(rum/defc calendar < rum/static []
  [:section#calendar
    [:header
      [:h2 "Today is " (utils/current-date)]]])

(rum/defc page []
  [:div
   [:h1 (utils/greeting "Two In Shadows Client")]
   (calendar)])

(defn mount-root []
  (rum/mount (page) (. js/document (getElementById "app"))))

(mount-root)
