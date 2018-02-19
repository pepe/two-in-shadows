(ns two-in-shadows.material
  (:require [rum.core :as rum]
            ["@material/ripple" :as ripple]))

(def fixed-toolbar :header.mdc-toolbar.mdc-toolbar--fixed)

(def toolbar-row :div.mdc-toolbar__row)

(def toolbar-section-start :section.mdc-toolbar__section.mdc-toolbar__section--align-start)

(def toolbar-section-end :section.mdc-toolbar__section.mdc-toolbar__section--align-end)

(def toolbar-title :span.mdc-toolbar__title)

(def adjust-fixed-toolbar [:div.mdc-toolbar-fixed-adjust])

(def card-media :div.mdc-card__media)

(def title :div.mdc-typography--title)

(def subheading :div.mdc-typography--subheading1)

(def elevation-z2 :div.mdc-elevation--z2)

(def ^:private attach-ripple
  {:did-mount (fn [state] (-> state rum/dom-node ripple/MDCRipple.attachTo) state)})

(def card-style {:width "30rem" :margin "1rem"})

(def card-primary {:width "30rem" :padding "1rem"})


(defn card-action-buttons
  "Scaffold for card action buttons"
  [& buttons]
  [:div.mdc-card__actions
   {:style {:display :flex :justify-content :flex-end}}
   [:div.mdc-card__action-buttons
    buttons]])


(defn card
  ([opts primary] (card opts primary nil))
  ([opts primary actions]
   [:div.mdc-card
    (merge {:style card-style} opts)
    [:div {:style card-primary} primary]
    (when actions
      (card-action-buttons actions))]))


(rum/defc Button < attach-ripple rum/static
  [opts label]
  [:button.mdc-button opts label])


(defn card-button
  [opts label]
  (Button (merge opts {:class "mdc-card__action mdc-card__action--button"}) label))
