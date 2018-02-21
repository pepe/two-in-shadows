(ns two-in-shadows.client.events
  (:require [potok.core :as ptk]
            [beicon.core :as rx]
            [goog.json :as json]
            [rxhttp.browser :as http]))


(defrecord ShowLoading []
  ptk/UpdateEvent
  (update [_ state] (assoc state :ui/loading true)))


(defrecord FadeLoading []
  ptk/UpdateEvent
  (update [_ state] (assoc state :ui/loading :fade)))


(defrecord HideLoading []
  ptk/UpdateEvent
  (update [_ state] (assoc state :ui/loading false)))


(defrecord SaveGreeting [response]
  ptk/UpdateEvent
  (update [_ state]
    (let [{body :body} response
          greeting (js->clj (json/parse body) :keywordize-keys true)]
      (assoc state :ui/greeting greeting)))
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

(def fading
  (rx/concat
    (rx/delay 250 (rx/just (->FadeLoading)))
    (rx/delay 750 (rx/just (->HideLoading)))))

(defrecord SaveClowns [response]
  ptk/UpdateEvent
  (update [_ state]
    (let [{body :body} response
          clowns (js->clj (json/parse body) :keywordize-keys true)]
      (assoc state :data/clowns (sort #(get % "name") clowns))))
  ptk/WatchEvent
  (watch [_ _ _] fading))


(defrecord GetClowns []
  ptk/WatchEvent
  (watch [_ state _]
    (rx/concat
     (rx/just (->ShowLoading))
     (rx/map ->SaveClowns
             (http/send! {:method  :get
                          :url     (str (:api/url state) "storage/clowns")
                          :headers {:content-type "application/json"}})))))

(defn get-clowns
  "Gets clowns from server"
  [store]
  (ptk/emit! store (->GetClowns)))


(defrecord EditClown [clown]
  ptk/UpdateEvent
  (update [_ state] (assoc state :ui/edited-clown clown)))


(defn edit-clown
  "Sets clown for editing"
  [store clown]
  (ptk/emit! store (->EditClown clown)))

(defrecord RemoveClown [old-clown]
  ptk/WatchEvent
  (watch [_ state _]
    (if (empty? (:name old-clown))
      (rx/of (->EditClown nil) (->GetClowns))
      (rx/concat
       (rx/just (->EditClown nil))
       (rx/map ->GetClowns
               (http/send!
                {:method  :delete
                 :url     (str (:api/url state) "storage/clowns")
                 :headers {:content-type "application/json"}
                 :body (js/JSON.stringify (clj->js old-clown))}))))))


(defn remove-clown
  "Removes clown from storage"
  [store clown]
  (ptk/emit! store (->RemoveClown clown)))


(defrecord SaveClown [clown]
  ptk/WatchEvent
  (watch [_ state _]
    (let [old-clown (:ui/edited-clown state)]
      (if (= old-clown clown)
        (rx/just (->EditClown nil))
        (rx/map #(->RemoveClown old-clown)
                (http/send!
                 {:method  :post
                  :url     (str (:api/url state) "storage/clowns")
                  :headers {:content-type "application/json"}
                  :body    (js/JSON.stringify (clj->js clown))}))))))


(defn save-clown
  "Saves edited clown if needed"
  [store clown]
  (ptk/emit! store (->SaveClown clown)))






(defonce store
  (ptk/store {:state {:api/url "http://localhost:8270/"}}))
