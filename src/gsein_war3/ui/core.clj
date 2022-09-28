(ns gsein-war3.ui.core
  (:require [seesaw.core :refer :all]
            [seesaw.mig :refer :all]))

(defn make-button
  ([text action]
   (button :text text
           :listen [:action action]))
  ([text]
   (make-button text #(alert % text))))

(defn frame-content []
  (mig-panel :constraints ["", "[right]"]
             :items [
                     [(make-button "Button 1") "gap 10"]
                     [(make-button "Button 2") "gap 10"]
                     [(make-button "Button 3") "gap 10, wrap"]
                     [(make-button "Button 4") "gap 10"]
                     [(make-button "Button 5") "gap 10"]
                     [(make-button "Button 6") "gap 10"]]))

(defn create-frame []
  (-> (frame {:title    "gsein-war3"
              :size     [800 :by 600]
              :content  (frame-content)
              :on-close :exit})
      pack!
      show!))

(comment

  (def f (create-frame))
  (config (config f :content) :items),
  )
