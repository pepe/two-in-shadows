(ns two-in-shadows.components
  (:require [rum.core :as rum]
            [two-in-shadows.material :as material]
            [two-in-shadows.utils :as utils]))

(rum/defc Loading < rum/static []
  [:div "Loading"])


(rum/defc Greeting < rum/static [greeting]
  [:section#greeting
   [:div "Server says: " greeting]])


(rum/defc Calendar < rum/static [date]
  [:section#calendar
   [:div "Today is: " date]])


(rum/defc Page < rum/reactive [store]
  (let [state (utils/get-state store)
        greeting (utils/react-cursor state :ui/greeting)]
    [:div#app
     [material/fixed-toolbar
      [material/toolbar-row
       [material/toolbar-section-start
        [material/toolbar-title "Two In Shadows Client"]]]]
     [:main
      [material/adjust-fixed-toolbar]
      (if greeting
        [(rum/with-key (Greeting (:message greeting)) :greeting)
         (rum/with-key (Calendar (:date greeting)) :calendar)]
       (Loading))]]))
