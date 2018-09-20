(ns two-in-shadows.client.events
  (:require [potok.core :as ptk]
            [beicon.core :as rx]
            [goog.json :as json]
            [com.rpl.specter :as S]
            [rxhttp.browser :as http]))


(defrecord FadeLoading []
  ptk/UpdateEvent
  (update [_ state] (S/setval :ui/loading :fade state)))


(defrecord HideLoading []
  ptk/UpdateEvent
  (update [_ state] (S/setval :ui/loading S/NONE state)))


(def fading
  (rx/concat
   (rx/delay 250 (rx/just (->FadeLoading)))
   (rx/delay 750 (rx/just (->HideLoading)))))


(defrecord ShowLoading []
  ptk/UpdateEvent
  (update [_ state] (S/setval :ui/loading true state)))


(defrecord SaveGreeting [response]
  ptk/UpdateEvent
  (update [_ state]
    (let [{body :body} response
          greeting (js->clj (json/parse body) :keywordize-keys true)]
      (S/setval :ui/greeting greeting state)))
  ptk/WatchEvent
  (watch [_ state _] fading))


(defrecord GetGreeting []
  ptk/WatchEvent
  (watch [_ state _]
    (rx/concat
     (rx/just (->ShowLoading))
     (rx/map ->SaveGreeting
             (http/send! {:method  :get
                          :url     (str (:api/url state) "greeting")
                          :headers {:content-type "application/json"}})))))

(defn get-greeting
  "Gets the greeting from server"
  [store]
  (ptk/emit! store (->GetGreeting)))

;;  >>>>>>>

(defonce store
  (ptk/store {:state {:api/url "http://localhost:8270/"}}))
