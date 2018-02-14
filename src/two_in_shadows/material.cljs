(ns two-in-shadows.material
  (:require [rum.core :as rum]
            ["@material/ripple" :as ripple]))

(def fixed-toolbar :header.mdc-toolbar.mdc-toolbar--fixed)

(def toolbar-row :div.mdc-toolbar__row)

(def toolbar-section-start :section.mdc-toolbar__section.mdc-toolbar__section--align-start)

(def toolbar-section-end :section.mdc-toolbar__section.mdc-toolbar__section--align-end)

(def toolbar-title :span.mdc-toolbar__title)

(def adjust-fixed-toolbar [:div.mdc-toolbar-fixed-adjust])

(def card :div.mdc-card)

(def card-media :div.mdc-card__media)

(def title :div.mdc-typography--title)

(def subheading :div.mdc-typography--subheading1)

(def ^:private attach-ripple
  {:did-mount (fn [state] (-> state rum/dom-node ripple/MDCRipple.attachTo) state)})

(rum/defc button < attach-ripple rum/static
  [opts label]
  [:button.mdc-button opts label])

