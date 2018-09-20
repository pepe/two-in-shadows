(ns two-in-shadows.client.components
  (:require [rum.core :as rum]
            [two-in-shadows.utils :as utils]
            [two-in-shadows.client.events :as events]
            [two-in-shadows.client.material :as material]
            [two-in-shadows.client.styles :as styles]
            [two-in-shadows.client.clowns.components :as clowns]))

(rum/defc Toolbar < rum/static []
  [[material/fixed-toolbar
    [material/toolbar-row
     [material/toolbar-section-start
      [material/toolbar-title "Two In Shadows Client"]]]
    [material/toolbar-section-end]]
   [material/adjust-fixed-toolbar]])


(rum/defc Loading < rum/static [loading]
  (when loading
    (material/card
     {:style styles/loading}
     [material/subheading "Loading"])))


(rum/defc Greeting < rum/static [greeting]
  (when greeting
    (material/card
     {:id "greeting"}
     [:div
      [material/title "Two in Shadows say"]
      [material/subheading greeting]])))


(rum/defc Calendar < rum/static [date]
  (when date
    (material/card
     {:id "calendar"}
     [:div
      [material/title "Today is"]
      [material/subheading date]])))


(rum/defc Page < rum/reactive [store]
  (let [state                  (utils/get-state store)
        loading                (utils/react-cursor state :ui/loading)
        {:keys [message date]} (utils/react-cursor state :ui/greeting)]
    [:div#app
     (Loading loading) (Toolbar)
     [:main {:style styles/main}
      [(Greeting message)
       (Calendar date)
       (clowns/Main store)]]]))
