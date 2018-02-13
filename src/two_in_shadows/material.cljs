(ns two-in-shadows.material
  (:require [rum.core :as rum]
            ["material-components-web" :as mcw]))

(def fixed-toolbar :header.mdc-toolbar.mdc-toolbar--fixed)

(def toolbar-row :div.mdc-toolbar__row)

(def toolbar-section-start :section.mdc-toolbar__section.mdc-toolbar__section--align-start)

(def toolbar-section-end :section.mdc-toolbar__section.mdc-toolbar__section--align-end)

(def toolbar-title :span.mdc-toolbar__title)

(def adjust-fixed-toolbar [:div.mdc-toolbar-fixed-adjust])
