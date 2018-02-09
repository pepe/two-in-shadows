(ns two-in-shadows.utils)

(defn greeting [name]
  (str "Hi, I am " name "!"))

(defn current-date []
  (.toLocaleDateString (js/Date.)))
