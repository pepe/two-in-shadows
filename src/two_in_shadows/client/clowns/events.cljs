(ns two-in-shadows.client.clowns.events
  (:require [potok.core :as ptk]
            [beicon.core :as rx]
            [goog.json :as json]
            [com.rpl.specter :as S]
            [rxhttp.browser :as http]
            [two-in-shadows.client.events :as events]))


(defrecord Store [response]
  ptk/UpdateEvent
  (update [_ state]
    (let [{body :body} response
          clowns (js->clj (json/parse body) :keywordize-keys true)
          ui-clowns (take 10 (sort-by :age < clowns))]
      (S/multi-transform
       (S/multi-path [:ui/clowns (S/terminal-val ui-clowns)]
                     [:data/clowns (S/terminal-val clowns)])
       state)))
  ptk/WatchEvent
  (watch [_ _ _] events/fading))


(defrecord GetAll []
  ptk/WatchEvent
  (watch [_ state _]
    (rx/concat
     (rx/just (events/->ShowLoading))
     (rx/map ->Store
             (http/send! {:method  :get
                          :url     (str (:api/url state) "storage/clowns")
                          :headers {:content-type "application/json"}})))))

(defn get-all
  "Gets clowns from server"
  [store]
  (ptk/emit! store (->GetAll)))


(defrecord Edit []
  ptk/UpdateEvent
  (update [_ state]
    (let [clown (:ui/hovered-clown state)]
      (S/multi-transform
       (S/multi-path [:ui/edited-clown (S/terminal-val clown)]
                     [:ui/clowns S/ALL (S/selected? (S/must :hovered)) :edited (S/terminal-val true)])
       state))))


(defn edit
  "Sets clown for editing"
  [store]
  (ptk/emit! store (->Edit)))


(defrecord CancelEdit []
  ptk/UpdateEvent
  (update [_ state]
    (S/setval (S/multi-path [:ui/edited-clown]
                            [:ui/clowns S/ALL (S/multi-path (S/must :edited) (S/must :hovered))])
              S/NONE state)))


(defn cancel-edit
  "Cancels the clown editing"
  [store]
  (ptk/emit! store (->CancelEdit)))


(defrecord Add []
  ptk/UpdateEvent
  (update [_ state]
    (let [clown {:name "" :age 0 :edited true}]
      (S/setval
       (S/multi-path :ui/hovered-clown :ui/edited-clown [:ui/clowns S/AFTER-ELEM])
       clown state))))


(defn add [store]
  (ptk/emit! store (->Add)))


(defrecord Delete []
  ptk/WatchEvent
  (watch [_ state _]
    (let [clown (:ui/hovered-clown state)]
      (rx/map ->GetAll
              (http/send!
               {:method  :delete
                :url     (str (:api/url state) "storage/clowns")
                :headers {:content-type "application/json"}
                :body (js/JSON.stringify (clj->js clown))})))))


(defn delete
  "Deletes clown from storage"
  [store]
  (ptk/emit! store (->Delete)))


(defrecord Save [clown]
  ptk/UpdateEvent
  (update [_ state] (S/setval :ui/edited-clown S/NONE state))
  ptk/WatchEvent
  (watch [_ state _]
    (let [old-clown (:ui/hovered-clown state)]
      (if (= old-clown clown)
        (rx/just (->CancelEdit))
        (rx/map #(->Delete)
                (http/send!
                 {:method  :post
                  :url     (str (:api/url state) "storage/clowns")
                  :headers {:content-type "application/json"}
                  :body    (js/JSON.stringify (clj->js clown))}))))))


(defn save
  "Saves edited clown if needed"
  [store clown]
  (ptk/emit! store (->Save clown)))


(defrecord Hover [clown]
  ptk/UpdateEvent
  (update [_ state]
    (S/multi-transform
     (S/multi-path [:ui/hovered-clown (S/terminal-val clown)]
                   [:ui/clowns S/ALL (S/pred= clown) :hovered (S/terminal-val true)])
     state)))

(defn hover
  "Sets clown as hovered"
  [store clown]
  (ptk/emit! store (->Hover clown)))

(defrecord Sink []
  ptk/UpdateEvent
  (update [_ state]
    (S/setval (S/multi-path [:ui/clowns S/ALL :hovered] :ui/hovered-clown) S/NONE state)))


(defn sink
  "Sets clown as hovered"
  [store]
  (ptk/emit! store (->Sink)))
