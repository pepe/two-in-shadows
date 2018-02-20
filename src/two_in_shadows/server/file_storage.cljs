(ns two-in-shadows.server.file-storage
  (:require ["fs" :as fs]
            [clojure.tools.reader.edn :as edn]))

(defn store
  "Stores state to file"
  [state]
  (.writeFileSync fs (str (.-PWD js/process.env) "/data/storage.edn") (str state) "utf8"))

(defn retrieve
  "Retrieves stored state"
  []
  (edn/read-string
    (.readFileSync fs (str (.-PWD js/process.env) "/data/storage.edn") "utf8")))
