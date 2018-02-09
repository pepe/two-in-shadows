(ns two-in-shadows.components
  (:require [rum.core :as rum]
            [two-in-shadows.utils :as utils]))

(rum/defc Loading < rum/static []
  [:div "Loading"])


(rum/defc Greeting < rum/static [greeting]
  [:div "Server says: " greeting])


(rum/defc Calendar < rum/static [date]
  [:section#calendar
   [:header
    [:h2 "Today is: " date]]])


(rum/defc Page < rum/reactive [store]
  (let [state (utils/get-state store)
        greeting (utils/react-cursor state :ui/greeting)]
    (js/console.log greeting)
    [:div
      [:h1 "Two In Shadows Client"]
     (if greeting
       [:div
        (Greeting (:message greeting))
        (Calendar (:date greeting))]
       (Loading))]))

