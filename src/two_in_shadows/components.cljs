(ns two-in-shadows.components
  (:require [rum.core :as rum]
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
    (js/console.log greeting)
    [:div#app
     [:header
      {:style {:margin "1rem 0"}}
      [:h1 "Two In Shadows Client"]]
     [:main
      (if greeting
       [(Greeting (:message greeting))
        (Calendar (:date greeting))]
       (Loading))]]))
