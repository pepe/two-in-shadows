(ns two-in-shadows.events
  (:require [potok.core :as ptk]
            [beicon.core :as rx]
            [goog.json :as json]
            [rxhttp.browser :as http]))


(defrecord SaveGreeting [response]
  ptk/UpdateEvent
  (update [_ state]
    (let [{body :body} response
          greeting (js->clj (json/parse body) :keywordize-keys true)]
      (assoc state :ui/greeting greeting))))


(defrecord GetGreeting []
  ptk/WatchEvent
  (watch [_ state _]
    (rx/map ->SaveGreeting
            (http/send! {:method  :get
                         :url     (str (:api/url state) "greeting")
                         :headers {:content-type "application/json"}}))))
