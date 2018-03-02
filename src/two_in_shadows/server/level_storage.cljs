(ns two-in-shadows.server.level-storage
  (:require ["level" :as level]
            [clojure.tools.reader.edn :as edn]))

(def db (level (str (.-PWD js/process.env) "/data/storage.db")))

(defn store
  "Stores state to level"
  [state]
  (.put db "state" (str state)))

(defn retrieve
  "Retrieves stored state"
  []
  (.get db "state"
        (fn [err val]
          (println err val)
          (if err {} (edn/read-string val)))))
