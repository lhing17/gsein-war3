(ns gsein-war3.ui.core
  (:require [seesaw.core :refer :all]
            [seesaw.mig :refer :all]
            [seesaw.chooser :refer :all])
  (:import (com.formdev.flatlaf FlatLightLaf)))

(defn make-button
  ([text action]
   (button :text text
           :listen [:action action]))
  ([text]
   (make-button text #(alert % text))))

(defn left-content []
  (let [item-config "width 100!, gap 10"]
    (mig-panel :constraints ["wrap 1", "[right]"]
               :border 5
               :items [
                       [(make-button "图片分割器") item-config]
                       [(make-button "一键头顶称号") item-config]
                       [(make-button "Excel转lni") item-config]
                       [(make-button "Button 4") item-config]
                       [(make-button "Button 5") item-config]
                       [(make-button "Button 6") item-config]
                       ])))

(defn image-splitter-content []
  ;; 图片分割器
  ;; 第一行 输入框加选择图片按钮
  ;; 第二行 说明文字加转换按钮

  (mig-panel :constraints ["", "[right]"]
             :items [
                     [(text) "width 200!, gap 10"]
                     [(make-button "选择图片") "gap 10, wrap"]
                     ]))

(defn frame-content []
  (mig-panel :constraints ["", "[right]"]
             :items [
                     [(left-content) "cell 0 0"]
                     [(image-splitter-content) "cell 1 0 5 1"]
                     ]))

(defn create-frame []
  (-> (frame {:title    "gsein-war3"
              :size     [800 :by 600]
              :content  (frame-content)
              :on-close :exit})
      pack!
      show!))

(defn -main []
  (FlatLightLaf/setup)
  (let [f (create-frame)
        ])

  )

(comment
  (FlatLightLaf/setup)
  (def f (create-frame)),


  )
