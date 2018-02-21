(ns two-in-shadows.client.components
  (:require [rum.core :as rum]
            [two-in-shadows.utils :as utils]
            [two-in-shadows.client.material :as material]
            [two-in-shadows.client.events :as events]))

(def main-style {:style {:display :flex :width "100%" :flex-wrap :wrap}})


(rum/defc Toolbar < rum/static []
  [[material/fixed-toolbar
    [material/toolbar-row
     [material/toolbar-section-start
      [material/toolbar-title "Two In Shadows Client"]]]
    [material/toolbar-section-end]]
   material/adjust-fixed-toolbar])


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
      [material/subheading date]])))

(rum/defc CancelButton < rum/static [store]
  (material/Button
   {:on-click (fn [e]
                (.preventDefault e)
                (.stopPropagation e)
                (events/edit-clown store nil))
    :class "secondary-text-button"}
   "Cancel"))

(rum/defc ClownForm < rum/static [store clown]
  (let [{:keys [name age]} clown
        submit-fn (fn [e]
                    (.preventDefault e) (.stopPropagation e)
                    (events/save-clown store
                                       (let [data (js/FormData. (.-target e))]
                                         {:name (.get data "name") :age (.get data "age")})))]
    [:form
     {:style {:width "28rem" :display "flex" :flex-direction "column"}
      :on-submit submit-fn}
     (material/text-field {:default-value name :name "name"} "Name")
     (material/text-field {:default-value age :name "age"} "Age")
     [:div
      (CancelButton store)
      (material/Button {} "Save")]]))

(rum/defc ClownLine < rum/static
  {:key-fn (fn [_ {:keys [name age]}] (str name age))}
  [store {:keys [name age] :as clown}]
  [:li {:style {:display "flex" :align-items "flex-end"
                :justify-content "space-between"}}
   [[:div age " yo - " name]
    [:div
     (material/Button {:on-click #(events/remove-clown store clown)} "Remove")
     (material/Button {:on-click #(events/edit-clown store clown)} "Edit")]]])

(rum/defc ClownList < rum/static
  [store clowns]
  [:ul {:style {:padding 0 :width "28rem"}}
   (for [clown clowns] (ClownLine store clown))])


(rum/defc AddClownButton < rum/static
  [store]
  (material/Button
   {:on-click #(events/edit-clown store {:name "" :age ""})} "Add"))


(rum/defc Clowns < rum/reactive [store]
  (let [state        (utils/get-state store)
        clowns       (utils/react-cursor state :data/clowns)
        edited-clown (utils/react-cursor state :ui/edited-clown)]
    (material/card
     {:id "clowns"}
     [:div
      (if edited-clown
        [[material/title "Editing clown"] (ClownForm store edited-clown)]
        (if (seq clowns)
          [[material/title "Known clowns"] (ClownList store clowns) (AddClownButton store)]
          [[material/title "No known clowns"] (AddClownButton store)]))]
     (material/Button {:on-click #(events/get-clowns store)} "Reload"))))

(rum/defc Page < rum/reactive [store]
  (let [state (utils/get-state store)
        loading (utils/react-cursor state :ui/loading)
        {:keys [message date]} (utils/react-cursor state :ui/greeting)]
    [:div#app
     (Loading loading) (Toolbar)
     [:main main-style
      [(Greeting message)
       (Calendar store date)
       (Clowns store)]]]))
