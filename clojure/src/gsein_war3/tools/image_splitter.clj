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
  ([width height] (make-rectangle 0 0 width height))
  ([min-x min-y max-x max-y] (rectangle. min-x min-y max-x max-y)))

(defn split-rectangle [rect direction split-length]
  (assert (#{:horizontal :vertical} direction)
          "direction must be :horizontal or :vertical")
  (loop [rects [] rect rect]
    (let [width (- (:max-x rect) (:min-x rect))
          height (- (:max-y rect) (:min-y rect))]
      (if (= direction :horizontal)
        (if (<= width split-length)
          (conj rects rect)
          (recur (conj rects (make-rectangle (:min-x rect) (:min-y rect) (+ (:min-x rect) split-length) (:max-y rect)))
                 (make-rectangle (+ (:min-x rect) split-length) (:min-y rect) (:max-x rect) (:max-y rect))))
        (if (<= height split-length)
          (conj rects rect)
          (recur (conj rects (make-rectangle (:min-x rect) (:min-y rect) (:max-x rect) (+ (:min-y rect) split-length)))
                 (make-rectangle (:min-x rect) (+ (:min-y rect) split-length) (:max-x rect) (:max-y rect))))))))

(defn make-image [imgs]
  "创建图像容器，parts 为 BufferedImage 向量"
  {:parts (vec imgs)})

(defn- split-parts [^BufferedImage img splitter]
  (let [rect (make-rectangle (-> img .getWidth) (-> img .getHeight))
        rects (splitter rect)]
    (map #(.getSubimage img (:min-x %) (:min-y %) (- (:max-x %) (:min-x %)) (- (:max-y %) (:min-y %))) rects)))

(defn- scale [^BufferedImage img w h]
  (let [scaled (.getScaledInstance img w h Image/SCALE_SMOOTH)
        new-img (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
        g (.createGraphics new-img)]
    (try
      (.drawImage g scaled 0 0 nil)
      (finally
        (.dispose g)))
    new-img))

(defn split-image [img splitter]
  (make-image (mapcat #(split-parts % splitter) (:parts img))))

(defn write-image [img ^String path names format]
  (let [parts (:parts img)]
    (when (not= (count names) (count parts))
      (throw (ex-info "Names and parts count mismatch"
                      {:names-count (count names) :parts-count (count parts)})))
    (let [named-parts (map vector names parts)
          out-dir (jio/file path)]
      (.mkdirs out-dir)
      (doseq [[name part] named-parts]
        (let [out-file (jio/file out-dir (str name "." format))]
          (with-open [out (jio/output-stream out-file)]
            (when-not (ImageIO/write part format out)
              (throw (ex-info (str "Failed to write image: " name)
                              {:name name :format format :file out-file})))))))))

(comment
  (def img (javax.imageio.ImageIO/read (java.io.File. "D:\\IdeaProjects\\JZJH\\resources\\决战江湖1.7.png")))
  (def splitter (fn [rect]
                  (->> [rect]
                       (mapcat #(split-rectangle % :horizontal 512))
                       (mapcat #(split-rectangle % :vertical 512)))))
  (def my-image (split-image (make-image [img]) splitter))
  (write-image my-image "" ["LoadingScreenTL" "LoadingScreenBL" "LoadingScreenTR" "LoadingScreenBR"] "blp")
  (write-image (make-image [(scale img 512 512)]) "" ["war3mapPreview"] "tga")
  )
