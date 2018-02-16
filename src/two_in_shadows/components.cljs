(ns two-in-shadows.components
  (:require [rum.core :as rum]
            [two-in-shadows.material :as material]
            [two-in-shadows.utils :as utils]
            [two-in-shadows.events :as events]))

(def card-style {:width "20rem" :margin "1rem"})

(def card-primary {:width "20rem" :padding "1rem"})

(rum/defc Loading < rum/static [loading]
  (when loading
    [material/card
     {:style {:position :absolute :z-index 10 :top "1.5rem" :right "2rem"
              :padding "0.5rem 1rem"
              :transition "opacity 750ms ease-in" :opacity (if (= :fade loading) 0.1 1)}}
     [material/subheading "Loading"]]))


(rum/defc Greeting < rum/static [greeting]
  [material/card
   {:id "greeting" :style card-style}
   [:div
    {:style card-primary}
    [material/title "Two in Shadows say"]
    [material/subheading greeting]]])


(rum/defc Calendar < rum/static [store date]
  [material/card
   {:id "calendar" :style card-style}
   [[:div
     {:style card-primary}
     [material/title "Today is"]
     [material/subheading date]]
    (material/card-action-buttons
     (material/CardButton
      {:on-click #(events/get-greeting store)} "reload"))]])



(rum/defc Page < rum/reactive [store]
  (let [state (utils/get-state store)
        loading (utils/react-cursor state :ui/loading)
        {:keys [message date] } (utils/react-cursor state :ui/greeting)]
    [:div#app
     (Loading loading)
     [material/fixed-toolbar
      [material/toolbar-row
       [material/toolbar-section-start
        [material/toolbar-title "Two In Shadows Client"]]]
      [material/toolbar-section-end]]
     material/adjust-fixed-toolbar
     [:main
      {:style {:display :flex :width "100%" :flex-wrap :wrap}}
      (if message
        [(Greeting message) (Calendar store date)])]]))
