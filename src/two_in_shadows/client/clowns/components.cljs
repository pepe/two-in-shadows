(ns two-in-shadows.client.clowns.components
  (:require [rum.core :as rum]
            [two-in-shadows.utils :as utils]
            [two-in-shadows.client.material :as material]
            [two-in-shadows.client.clowns.events :as events]
            [two-in-shadows.client.clowns.styles :as styles]))

(rum/defc CancelButton < rum/static [store]
  (material/Button
   {:on-click (fn [e]
                (.preventDefault e)
                (.stopPropagation e)
                (events/cancel-edit store))
    :class "secondary-text-button"}
   "Cancel"))


(rum/defc Form < rum/static [store clown]
  (let [{:keys [name age]} clown
        submit-fn (fn [e]
                    (.preventDefault e) (.stopPropagation e)
                    (events/save store
                                       (let [data (js/FormData. (.-target e))]
                                         {:name (.get data "name") :age (int (.get data "age"))})))]
    [:form.mdc-elevation--z2
     {:style styles/form
      :on-submit submit-fn}
     [material/title {:style styles/form-title} "Edit Clown"]
     [:div
      {:style {:display :flex :justify-content :space-around}}
      (material/TextField {:required true :default-value name :name "name" :style {:width "20rem"}} "Name")
      (material/TextField {:required true :default-value age :name "age":style {:width "6rem"}} "Age")]
     [:div
      {:style {:display :flex :justify-content :space-around}}
      (CancelButton store)
      (material/Button {} "Save")]]))


(rum/defc Line < rum/static
  {:key-fn (fn [_ {:keys [name age]}] (str name age))}
  [store {:keys [name age edited hovered] :as clown} edited-clown]
  (if edited
    (Form store clown)
    [:li {:on-mouse-enter #(when-not edited-clown (events/hover store clown))
          :on-mouse-leave #(when-not edited-clown (events/sink store))
          :class (if hovered "mdc-elevation--z2" "mdc-elevation--z0")
          :style (cond-> styles/line
                   hovered (merge {:font-weight 500})
                   edited-clown (merge {:opacity 0.8}))}
     [[:div age " yo - " name]
      [:div
       {:style {:padding-right "1rem"}}
       (when hovered
         [(material/Button {:on-click #(when (js/confirm "Really?") (events/delete store))} "Delete")
          (material/Button {:on-click #(events/edit store)} "Edit")])]]]))


(rum/defc Listing < rum/static
  [store clowns edited-clown]
  [:ul {:style styles/listing}
   (for [clown clowns] (Line store clown edited-clown))])


(rum/defc AddButton < rum/static
  [store]
  (material/Button {:on-click #(events/add store)} "Add"))


(rum/defc Main < rum/reactive [store]
  (let [state         (utils/get-state store)
        clowns        (utils/react-cursor state :ui/clowns)
        edited-clown (utils/react-cursor state :ui/edited-clown)]
    (material/card
     {:id "clowns"}
     [:div
      (if (seq clowns)
        [[material/title "Known clowns"] (Listing store clowns edited-clown)]
        [material/title "No known clowns"])]
     (when-not edited-clown [(when-not edited-clown (AddButton store)) (material/Button {:on-click #(events/get-all store)} "Reload")]))))
