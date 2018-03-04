(ns two-in-shadows.server.level-storage
  (:require ["level" :as level]
            [clojure.tools.reader.edn :as edn]))

(def db (level (str (.-PWD js/process.env) "/data/states.db")))

(defn store
  "Stores state to level"
  [state key]
  (.put db key (str state)))

(defn retrieve
  "Retrieves stored state"
  [key]
  (.get db key
        (fn [err val]
          (if err {} (edn/read-string val)))))
