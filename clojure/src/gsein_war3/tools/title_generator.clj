(ns gsein-war3.tools.title-generator
  "头顶称号生成器：将文字渲染到背景图片上，生成 BLP 贴图并替换 MDX 模型中的贴图路径。"
  (:require [clojure.java.io :as jio]
            [clojure.string :as str]
            [gsein-war3.mdx.converter :as converter]
            [gsein-war3.util.pinyin :as pinyin])
  (:import (java.io File)
           (java.awt.image BufferedImage)
           (java.awt Graphics2D Image RenderingHints Font BasicStroke Color)
           (javax.imageio ImageIO)
           (java.awt.geom AffineTransform)
           (java.awt.font TextLayout)))

(def ^:private default-font-name
  "默认字体名称。"
  "方正颜宋简体_粗")

(def ^:private default-font-size
  "默认字体大小。"
  40)

(def ^:private default-width
  "默认输出图像宽度。"
  256)

(def ^:private default-height
  "默认输出图像高度。"
  128)

(def ^:private default-template-mdx
  "默认 MDX 模板资源路径。"
  "mdx/template.mdx")

(def ^:private default-old-texture
  "默认旧贴图路径（MDX 中将被替换的路径）。"
  "war3mapImported\\kangkang.blp")

(defn create-img
  "在背景图上渲染文字，返回 BufferedImage。"
  [name w h color wing-file font-name font-size]
  (let [img (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
        bg-img (ImageIO/read wing-file)
        scaled-instance (.getScaledInstance bg-img w h Image/SCALE_SMOOTH)
        g ^Graphics2D (.createGraphics img)]
    (try
      (.drawImage g scaled-instance 0 0 nil)
      (.setRenderingHint g RenderingHints/KEY_ANTIALIASING RenderingHints/VALUE_ANTIALIAS_ON)
      (.setRenderingHint g RenderingHints/KEY_STROKE_CONTROL RenderingHints/VALUE_STROKE_PURE)
      (.setRenderingHint g RenderingHints/KEY_COLOR_RENDERING RenderingHints/VALUE_COLOR_RENDER_QUALITY)
      (.setClip g 0 0 w h)
      (let [clip (.getClipBounds g)
            font (Font. font-name Font/PLAIN font-size)
            fm (.getFontMetrics g font)
            ascent (.getAscent fm)
            descent (.getDescent fm)
            str-width (.stringWidth fm name)
            x (/ (- (.getWidth clip) str-width) 2)
            y (+ (/ (- (.getHeight clip) ascent descent) 2) ascent)
            frc (.getFontRenderContext g)
            tl (TextLayout. name font frc)
            tx (AffineTransform/getTranslateInstance x y)
            sha (.getOutline tl tx)]
        (.setFont g font)
        (.setPaint g color)
        (.setStroke g (BasicStroke. 5))
        (.draw g sha)
        (.setColor g Color/WHITE)
        (.fill g sha))
      img
      (finally
        (.dispose g)))))

(defn create-img-default
  "使用默认字体渲染文字。"
  [name w h color wing-file]
  (create-img name w h color wing-file default-font-name default-font-size))

(defn- get-scaled-image
  "将图像缩放到指定尺寸。"
  [^BufferedImage img w h]
  (let [scaled-instance (.getScaledInstance img w h Image/SCALE_SMOOTH)
        new-img (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
        g ^Graphics2D (.createGraphics new-img)]
    (try
      (.drawImage g scaled-instance 0 0 nil)
      new-img
      (finally
        (.dispose g)))))

(defn get-file-pinyin-name-without-extension
  "获取文件名的拼音（去除扩展名）。"
  [^File file]
  (let [name (.getName file)
        idx (.lastIndexOf name ".")]
    (pinyin/get-pinyin-name (if (pos? idx) (subs name 0 idx) name))))

(defn generate-title!
  "一键生成头顶称号。
   在 out-dir/temp 目录下生成 BLP 和 MDX 文件。
   支持通过关键字参数覆盖默认值：
     :template-mdx-path — MDX 模板资源路径
     :old-texture-path  — 旧贴图路径
     :font-name         — 字体名称
     :font-size         — 字体大小"
  [name color wing-file out-dir & {:keys [template-mdx-path old-texture-path font-name font-size]
                                   :or   {template-mdx-path default-template-mdx
                                          old-texture-path  default-old-texture
                                          font-name         default-font-name
                                          font-size         default-font-size}}]
  (let [eng-name (pinyin/get-pinyin-name name)
        blp-file (jio/file out-dir "temp" (str eng-name ".blp"))
        mdx-file (jio/file out-dir "temp" (str eng-name ".mdx"))
        blp-parent (.getParentFile blp-file)]
    (.mkdirs blp-parent)
    (let [img (create-img name default-width default-height color wing-file font-name font-size)]
      (ImageIO/write img "blp" blp-file))
    (with-open [in (jio/input-stream (jio/resource template-mdx-path))]
      (jio/copy in mdx-file))
    (converter/replace-blp mdx-file old-texture-path (.getName blp-file))))

(defn generate-title-with-image!
  "使用现有图片生成头顶称号。
   img-path 为图片文件路径字符串。"
  [img-path blp-name out-dir & {:keys [template-mdx-path old-texture-path]
                                :or   {template-mdx-path default-template-mdx
                                       old-texture-path  default-old-texture}}]
  (let [blp-file (jio/file out-dir "temp" (str blp-name ".blp"))
        mdx-file (jio/file out-dir "temp" (str blp-name ".mdx"))
        blp-parent (.getParentFile blp-file)
        buffered-img (get-scaled-image (ImageIO/read (jio/file img-path)) default-width default-height)]
    (.mkdirs blp-parent)
    (ImageIO/write buffered-img "blp" blp-file)
    (with-open [in (jio/input-stream (jio/resource template-mdx-path))]
      (jio/copy in mdx-file))
    (converter/replace-blp mdx-file old-texture-path (.getName blp-file))))

(comment
  (def out-dir "D:/tmp/out")
  (generate-title! "领取福利" Color/BLUE (jio/file (jio/resource "images/background/3.png")) out-dir)
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/主线历练.png" "ch_zhuxian" out-dir)
  )
