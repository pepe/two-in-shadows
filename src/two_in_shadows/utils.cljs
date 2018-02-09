(ns two-in-shadows.utils
  "Utils for all parts"
  (:require [goog.object :as object]
            [rum.core :as rum]
            [beicon.core :as rx]))


(defn greeting
  "Returns greeting"
  [name]
  (str "Hi, I am " name "!"))


(defn current-date
  "Returns current date string"
  []
  (.toLocaleDateString (js/Date.)))


(defn set-body
  "Sets body of the context to js document"
  [context document]
  (object/set context "body" (clj->js document)))


(defn get-state
  "Gets state from potok store"
  [store]
  (rx/to-atom store))


(defn react-cursor
  "Return cursor to key in state"
  [state key]
  (-> state (rum/cursor key) rum/react))
