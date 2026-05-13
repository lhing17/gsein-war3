(ns gsein-war3.util.image
  (:require [clojure.java.io :as jio])
  (:import (java.awt.image BufferedImage)
           (java.awt Image Color)
           (javax.imageio ImageIO)
           (java.io IOException InputStream)))

(def passive-border-part
  "被动图标边框透明度权重序列（从外到内）。"
  (take 32 (cons 31 (iterate identity 255))))

(defn resize-to
  "将图像缩放到指定宽高。"
  [w h ^BufferedImage origin]
  (let [scaled (.getScaledInstance origin w h Image/SCALE_SMOOTH)
        new-img (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
        g (.createGraphics new-img)]
    (try
      (.drawImage g scaled 0 0 nil)
      (finally
        (.dispose g)))
    new-img))

(def resize-to-64
  "将图像缩放为 64×64。"
  (partial resize-to 64 64))

(defn copy
  "创建 BufferedImage 的深拷贝。"
  [^BufferedImage image]
  (let [w (.getWidth image)
        h (.getHeight image)
        new-image (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
        g (.createGraphics new-image)]
    (try
      (.drawImage g image 0 0 nil)
      (finally
        (.dispose g)))
    new-image))

(defn- do-add-border!
  "将边框图像叠加到目标图像上。is 会在函数结束时关闭。"
  [^BufferedImage image ^InputStream is]
  (with-open [s is]
    (let [border (ImageIO/read s)]
      (when border
        (let [g (.createGraphics image)]
          (try
            (.drawImage g border 0 0 nil)
            (finally
              (.dispose g)))))))
  image)

(defn add-active-border
  "为图像添加主动图标边框。opts 为可选配置 map，支持：
   - :filter-type — :none（不添加）、:default（使用默认边框）、:self-defined（自定义）
   - :filter-url  — 自定义边框图片路径（仅在 :self-defined 时使用）"
  [origin opts]
  (let [result (copy origin)]
    (case (:filter-type opts)
      :none result
      :default (do-add-border! result (try
                                        (jio/input-stream (jio/resource "blp/active_border2.png"))
                                        (catch IOException _
                                          (throw (IllegalStateException. "未找到默认滤镜，请使用自定义滤镜")))))
      :self-defined (do-add-border! result (try
                                             (jio/input-stream (:filter-url opts))
                                             (catch IOException _
                                               (throw (IllegalStateException. "滤镜图片无效")))))
      result)))

(defn- mix-pixels [px0 p0 px1 p1]
  (let [r0 (bit-and (bit-shift-right px0 16) 0xFF)
        r1 (bit-and (bit-shift-right px1 16) 0xFF)
        g0 (bit-and (bit-shift-right px0 8) 0xFF)
        g1 (bit-and (bit-shift-right px1 8) 0xFF)
        b0 (bit-and px0 0xFF)
        b1 (bit-and px1 0xFF)
        r (int (+ (* r0 p0) (* r1 p1)))
        g (int (+ (* g0 p0) (* g1 p1)))
        b (int (+ (* b0 p0) (* b1 p1)))]
    (bit-or (bit-shift-left (bit-shift-right px0 24) 24)
            (bit-shift-left r 16)
            (bit-shift-left g 8)
            b)))

(defn- mix-pixels-for-border [^BufferedImage image ^BufferedImage border x y p]
  (.setRGB image x y
           (mix-pixels (.getRGB image x y)
                       (/ p 255)
                       (.getRGB border x y)
                       (- 1 (/ p 255)))))

(defn add-passive-border
  "为图像添加被动图标边框（四角渐变）。"
  [origin]
  (let [result (copy origin)
        border (ImageIO/read (jio/resource "blp/passive_border.png"))]
    (loop [start 0 end 63]
      (if (< start end)
        (do (doseq [i (range start end)]
              (mix-pixels-for-border result border start i (nth passive-border-part start))
              (mix-pixels-for-border result border end i (nth passive-border-part start))
              (mix-pixels-for-border result border i start (nth passive-border-part start))
              (mix-pixels-for-border result border i end (nth passive-border-part start)))
            (recur (inc start) (dec end)))
        result))))

(defn make-valid
  "将颜色分量限制在 0–255 范围内。"
  [c]
  (cond (> c 255) 255
        (< c 0) 0
        :else c))

(defn- make-new-color [^Color color brightness]
  (Color.
    ^int (make-valid (+ (.getRed color) brightness))
    ^int (make-valid (+ (.getGreen color) brightness))
    ^int (make-valid (+ (.getBlue color) brightness))))

(defn adjust-brightness
  "调整图像亮度。brightness 为整数偏移量（可为负）。"
  [^BufferedImage origin brightness]
  (let [result (copy origin)
        w (.getWidth origin)
        h (.getHeight origin)]
    (doseq [i (range h) j (range w)]
      (.setRGB result j i
               (-> result
                   (.getRGB j i)
                   (Color.)
                   (make-new-color brightness)
                   (.getRGB))))
    result))

(defn add-border
  "为图像添加边框。type 为 :active 或 :passive；opts 为配置 map（仅 :active 使用）。"
  [^BufferedImage origin type opts]
  (case type
    :active (add-active-border origin opts)
    :passive (add-passive-border origin)
    (throw (IllegalArgumentException. (str "Unknown border type: " type)))))

(defn output-as-blp
  "将 BufferedImage 写入 BLP 文件。返回写入文件的绝对路径字符串。"
  [^BufferedImage image dir name prefix]
  (let [path (jio/file dir)
        blp-file (jio/file path (str prefix name ".blp"))]
    (.mkdirs path)
    (with-open [out (jio/output-stream blp-file)]
      (when-not (ImageIO/write image "blp" out)
        (throw (IOException. (str "Failed to write BLP: " blp-file)))))
    (.getAbsolutePath blp-file)))

(comment
  (doseq [n (map #(str "[ch]" %) (range 1 15))]
    (ImageIO/write
      (ImageIO/read (jio/file (str "D:\\IdeaProjects\\jzjh2\\resources\\" n ".png")))
      "tga"
      (jio/output-stream (jio/file (str "D:\\IdeaProjects\\jzjh2\\resources\\" n ".tga")))))

  (ImageIO/write
    (ImageIO/read (jio/file "D:\\IdeaProjects\\jzjh2\\resources\\已兑换.png"))
    "tga"
    (jio/output-stream (jio/file "D:\\IdeaProjects\\jzjh2\\resources\\exchanged.tga")))

  (ImageIO/write
    (ImageIO/read (jio/file "D:\\IdeaProjects\\JZJH\\resources\\RightArrow.png"))
    "tga"
    (jio/output-stream (jio/file "D:\\IdeaProjects\\JZJH\\resources\\RightArrow.tga")))

  (ImageIO/write
    (ImageIO/read (jio/file "D:\\IdeaProjects\\JZJH\\resources\\技能图标\\shenghua_hover.png"))
    "tga"
    (jio/output-stream (jio/file "D:\\IdeaProjects\\JZJH\\resources\\技能图标\\shenghua_hover.tga")))

  (ImageIO/write
    (ImageIO/read (jio/file "E:\\War3Map\\拆地图\\剑开天门\\剑开\\ui\\widgets\\tooltips\\human\\human-tooltip-background.blp"))
    "png"
    (jio/output-stream (jio/file "E:\\War3Map\\拆地图\\剑开天门\\剑开\\ui\\widgets\\tooltips\\human\\human-tooltip-background.png"))))
