(ns two-in-shadows.client
  (:require
   [rum.core :as rum]))


(rum/defc greeting []
  [:div
   [:h1 "Hi I am running, help me!"]])

(defn mount-root []
  (rum/mount (greeting) (. js/document (getElementById "app"))))

(mount-root)
  

