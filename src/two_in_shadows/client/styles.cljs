(ns two-in-shadows.client.styles)

(def ^:const main {:display :flex :width "100%" :flex-wrap :wrap})

(def ^:const loading {:position :fixed :z-index 10 :top "1.5rem" :right "2rem"
                      :padding "0.25rem 0.5rem" :width "8rem"
                      :transition "opacity 750ms ease-in" :opacity (if (= :fade loading) 0.1 1)})
