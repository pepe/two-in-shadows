(ns two-in-shadows.utils
  "Utils for all parts"
  (:require [goog.object :as object]))


(defn greeting
  "Returns greeting"
  [name]
  (str "Hi, I am " name "!"))

(defn current-date
  "Returns current date"
  []
  (.toLocaleDateString (js/Date.)))

(defn set-body
  "Sets body of the context to js document"
  [context document]
  (object/set context "body" (clj->js document)))
