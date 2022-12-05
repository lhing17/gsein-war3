(ns gsein-war3.tools.image-splitter
  (:require [clojure.java.io :as jio])
  (:import (java.awt Image)
           (java.awt.image BufferedImage)
           (javax.imageio ImageIO)))

(defrecord rectangle [min-x min-y max-x max-y]
  Object
  (toString [_]
    (str "rectangle: " min-x " " min-y " " max-x " " max-y)))


(defn make-rectangle
  ([x y] (make-rectangle 0 0 x y))
  ([min-x min-y max-x max-y] (rectangle. min-x min-y max-x max-y)))

(defn split-rectangle [rect direction split-length]
  (loop [rects [] rect rect]
    (let [width (- (:max-x rect) (:min-x rect))
          height (- (:max-y rect) (:min-y rect))]
      (if (= direction :horizontal)
        ;; 水平方向切割
        (if (<= width split-length)
          (conj rects rect)
          (recur (conj rects (make-rectangle (:min-x rect) (:min-y rect) (+ (:min-x rect) split-length) (:max-y rect)))
                 (make-rectangle (+ (:min-x rect) split-length) (:min-y rect) (:max-x rect) (:max-y rect))))
        ;; 垂直方向切割
        (if (<= height split-length)
          (conj rects rect)
          (recur (conj rects (make-rectangle (:min-x rect) (:min-y rect) (:max-x rect) (+ (:min-y rect) split-length)))
                 (make-rectangle (:min-x rect) (+ (:min-y rect) split-length) (:max-x rect) (:max-y rect))))))))

(comment
  (def rect (make-rectangle 1024 768))
  (split-rectangle rect :horizontal 512)
  (split-rectangle rect :vertical 512)
  (->> [rect]
       (mapcat #(split-rectangle % :horizontal 512))
       (mapcat #(split-rectangle % :vertical 512))
       ),)

(defrecord image [parts])

(defn make-image [imgs] (image. (vec imgs)))

(defn- split-parts [^BufferedImage img splitter]
  (let [rect (make-rectangle (-> img .getWidth) (-> img .getHeight))
        rects (splitter rect)]
    (map #(.getSubimage img (:min-x %) (:min-y %) (- (:max-x %) (:min-x %)) (- (:max-y %) (:min-y %))) rects)))

(defn- scale [^BufferedImage img w h]
  (let [scaled (.getScaledInstance img w h Image/SCALE_SMOOTH)]
    (let [new-img (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)]
      (let [g (.createGraphics new-img)]
        (.drawImage g scaled 0 0 nil)
        (.dispose g)
        new-img))))

(defn split-image [^image img splitter]
  (make-image (mapcat #(split-parts % splitter) (:parts img))))

(defn write-image [^image img ^String path names format]
  (let [parts (:parts img)
        named-parts (map vector names parts)]
    (doseq [[name part] named-parts]
      (with-open [out (jio/output-stream (str path name "." format))]
        (ImageIO/write part format out)))))

(comment
  (def img (javax.imageio.ImageIO/read (java.io.File. "D:\\IdeaProjects\\JZJH\\resources\\决战江湖1.6.60.png")))
  (def splitter (fn [rect]
                  (->> [rect]
                       (mapcat #(split-rectangle % :horizontal 512))
                       (mapcat #(split-rectangle % :vertical 512))
                       )))
  (def my-image (split-image (make-image [img]) splitter))
  (doseq [part (map-indexed vector (:parts my-image))]
    (javax.imageio.ImageIO/write (second part) "png" (java.io.File. (str (first part) ".png"))))

  (write-image my-image "" ["LoadingScreenTL" "LoadingScreenBL" "LoadingScreenTR" "LoadingScreenBR"] "blp")
  (write-image (make-image [(scale img 512 512)]) "" ["war3mapPreview"] "tga"),)



