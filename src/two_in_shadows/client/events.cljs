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

(defrecord SaveClowns [response]
  ptk/UpdateEvent
  (update [_ state]
    (let [{body :body} response
          clowns (js->clj (json/parse body) :keywordize-keys true)
          ui-clowns (take 10 (sort-by :age < clowns))]
      (->> state
           (S/setval :ui/clowns ui-clowns)
           (S/setval :data/clowns clowns))))
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


(defrecord EditClown []
  ptk/UpdateEvent
  (update [_ state]
    (let [clown (:ui/hovered-clown state)]
      (->> state
           (S/setval :ui/edited-clown clown)
           (S/setval [:ui/clowns S/ALL (S/selected? (S/must :hovered)) :edited] true)))))


(defn edit-clown
  "Sets clown for editing"
  [store]
  (ptk/emit! store (->EditClown)))


(defrecord CancelEditClown []
  ptk/UpdateEvent
  (update [_ state]
    (->> state
         (S/setval [:ui/edited-clown] S/NONE)
         (S/setval [:ui/clowns S/ALL (S/must :edited)] S/NONE))))


(defn cancel-edit-clown
  "Cancels the clown editing"
  [store]
  (ptk/emit! store (->CancelEditClown)))

(defrecord AddClown []
  ptk/UpdateEvent
  (update [_ state]
    (let [clown {:name "" :age 0}]
      (->> state
           (S/setval :ui/hovered-clown clown)
           (S/setval :ui/edited-clown clown)
           (S/setval [:ui/clowns S/AFTER-ELEM] (S/setval :edited true clown))))))


(defn add-clown [store]
  (ptk/emit! store (->AddClown)))



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
  (update [_ state] (S/setval :ui/edited-clown S/NONE state))
  ptk/WatchEvent
  (watch [_ state _]
    (let [old-clown (:ui/hovered-clown state)]
      (if (= old-clown clown)
        (rx/just (->CancelEditClown))
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

(defrecord UnHoverClown []
  ptk/UpdateEvent
  (update [_ state]
    (->>  state
          (S/setval :ui/hovered-clown nil)
          (S/setval [:ui/clowns S/ALL :hovered] S/NONE))))


(defn un-hover-clown
  "Sets clown as hovered"
  [store]
  (ptk/emit! store (->UnHoverClown)))



;;  >>>>>>>

(defonce store
  (ptk/store {:state {:api/url "http://localhost:8270/"}}))
