(ns two-in-shadows.components
  (:require [rum.core :as rum]
            [two-in-shadows.material :as material]
            [two-in-shadows.utils :as utils]
            [two-in-shadows.events :as events]))

(def card-style {:width "20rem" :padding "1rem" :margin "1rem"})

(rum/defc Loading < rum/static []
  [:div
   {:style {:z-index 10
            :position :absolute
            :top "1rem"
            :right "2rem"
            :background :white
            :padding "1rem"
            :border-radius "0.5rem"}}
   "Loading"])


(rum/defc Greeting < rum/static [greeting]
  [material/card
   {:id "greeting" :style card-style}
   [:div
    {:style {:width "20rem"}}
    [material/title "Two in Shadows say"]
    [material/subheading greeting]]])


(rum/defc Calendar < rum/static [store date]
  [material/card
   {:id "calendar" :style card-style}
   [:div
    {:style {:width "20rem"}}
    [material/title "Today is"]
    [material/subheading date]
    (material/button {:on-click #(events/get-greeting store)} "reload")]])


(rum/defc Page < rum/reactive [store]
  (let [state (utils/get-state store)
        loading (utils/react-cursor state :ui/loading)
        {:keys [message date] } (utils/react-cursor state :ui/greeting)]
    [:div#app
     (when loading (Loading))
     [material/fixed-toolbar
      [material/toolbar-row
       [material/toolbar-section-start
        [material/toolbar-title "Two In Shadows Client"]]]
      [material/toolbar-section-end]]
     material/adjust-fixed-toolbar
     [:main
      {:style {:display :flex}}
      (if message
        [(rum/with-key (Greeting message) :greeting)
         (rum/with-key (Calendar store date) :calendar)])]]))
        
