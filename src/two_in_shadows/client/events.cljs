(ns two-in-shadows.client.events
  (:require [potok.core :as ptk]
            [beicon.core :as rx]
            [goog.json :as json]
            [com.rpl.specter :as S]
            [rxhttp.browser :as http]))


(defrecord FadeLoading []
  ptk/UpdateEvent
  (update [_ state] (assoc state :ui/loading :fade)))


(defrecord HideLoading []
  ptk/UpdateEvent
  (update [_ state] (assoc state :ui/loading false)))


(def fading
  (rx/concat
   (rx/delay 250 (rx/just (->FadeLoading)))
   (rx/delay 750 (rx/just (->HideLoading)))))


(defrecord ShowLoading []
  ptk/UpdateEvent
  (update [_ state] (assoc state :ui/loading true)))


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

(defrecord SaveClowns [response]
  ptk/UpdateEvent
  (update [_ state]
    (let [{body :body} response
          clowns (js->clj (json/parse body) :keywordize-keys true)]
      (-> state
          (assoc :ui/clowns (take  (sort-by :age < clowns)))
          (assoc :data/clowns (set (sort :name clowns))))))
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
  (update [_ state]
    (let []
      (cond->> (S/setval :ui/edited-clown clown state)
        clown
        (S/setval [:ui/clowns S/ALL (S/pred= clown) :edited] true)
        (nil? clown)
        (S/setval [:ui/clowns S/ALL :edited true?] false)))))


(defn edit-clown
  "Sets clown for editing"
  [store clown]
  (ptk/emit! store (->EditClown clown)))

(defrecord RemoveClown []
  ptk/WatchEvent
  (watch [_ state _]
      (let [clown (:ui/hovered-clown state)]
       (rx/map ->GetClowns
               (http/send!
                {:method  :delete
                 :url     (str (:api/url state) "storage/clowns")
                 :headers {:content-type "application/json"}
                 :body (js/JSON.stringify (clj->js clown))})))))


(defn remove-clown
  "Removes clown from storage"
  [store]
  (ptk/emit! store (->RemoveClown)))


(defrecord SaveClown [clown]
  ptk/UpdateEvent
  (update [_ state]
    (let [old-clown (:ui/hovered-clown state)]
      (S/setval [:ui/clowns S/ALL (S/pred= old-clown) :edited] clown state)))
  ptk/WatchEvent
  (watch [_ state _]
    (let [old-clown (:ui/edited-clown state)]
      (if (= old-clown clown)
        (rx/just (->EditClown nil))
        (rx/map #(->RemoveClown)
                (http/send!
                 {:method  :post
                  :url     (str (:api/url state) "storage/clowns")
                  :headers {:content-type "application/json"}
                  :body    (js/JSON.stringify (clj->js clown))}))))))


(defn save-clown
  "Saves edited clown if needed"
  [store clown]
  (ptk/emit! store (->SaveClown clown)))

(defrecord HoverClown [clown]
  ptk/UpdateEvent
  (update [_ state]
    (->> state
         (S/setval :ui/hovered-clown clown)
         (S/setval [:ui/clowns S/ALL (S/pred= clown) :hovered] true))))


(defn hover-clown
  "Sets clown as hovered"
  [store clown]
  (ptk/emit! store (->HoverClown clown)))

(defrecord UnHoverClown [clown]
  ptk/UpdateEvent
  (update [_ state]
    (->>  state
          (S/setval :ui/hovered-clown nil)
          (S/setval [:ui/clowns S/ALL (S/pred= clown) :hovered] false))))


(defn un-hover-clown
  "Sets clown as hovered"
  [store clown]
  (js/console.log "hohoho")
  (ptk/emit! store (->UnHoverClown clown)))



;;  >>>>>>>

(defonce store
  (ptk/store {:state {:api/url "http://localhost:8270/"}}))
