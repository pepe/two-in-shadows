(ns two-in-shadows.client.components
  (:require [rum.core :as rum]
            [two-in-shadows.utils :as utils]
            [two-in-shadows.client.material :as material]
            [two-in-shadows.client.events :as events]))

(def main-style {:style {:display :flex :width "100%" :flex-wrap :wrap}})


(rum/defc Loading < rum/static [loading]
  (when loading
    (material/card
     {:style {:position :absolute :z-index 10 :top "1.5rem" :right "2rem"
              :padding "0.5rem 1rem" :width "8rem"
              :transition "opacity 750ms ease-in" :opacity (if (= :fade loading) 0.1 1)}}
     [material/subheading "Loading"])))


(rum/defc Greeting < rum/static [greeting]
  (when greeting
    (material/card
      {:id "greeting"}
      [:div
        [material/title "Two in Shadows say"]
        [material/subheading greeting]])))


(rum/defc Calendar < rum/static [store date]
  (when date
    (material/card
      {:id "calendar"}
      [:div
        [material/title "Today is"]
        [material/subheading date]]
      (material/card-button
        {:on-click #(events/get-greeting store)} "reload"))))


(rum/defc Toolbar < rum/static []
  [[material/fixed-toolbar
    [material/toolbar-row
     [material/toolbar-section-start
      [material/toolbar-title "Two In Shadows Client"]]]
    [material/toolbar-section-end]]
   material/adjust-fixed-toolbar])


(rum/defc Page < rum/reactive [store]
  (let [state (utils/get-state store)
        loading (utils/react-cursor state :ui/loading)
        {:keys [message date] } (utils/react-cursor state :ui/greeting)]
    [:div#app
     (Loading loading) (Toolbar)
     [:main main-style [(Greeting message) (Calendar store date)]]]))
