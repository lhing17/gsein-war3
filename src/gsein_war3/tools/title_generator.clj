(ns gsein-war3.tools.title-generator
  (:require [clojure.java.io :as jio]
            [gsein-war3.mdx.converter :as converter]
            [gsein-war3.util.pinyin :as pinyin])
  (:import (org.apache.commons.io FileUtils)
           (java.io File)
           (java.awt.image BufferedImage)
           (java.awt Graphics2D Image RenderingHints Font BasicStroke Color)
           (javax.imageio ImageIO)
           (java.awt.geom AffineTransform)
           (java.awt.font TextLayout)))

(defn create-img [name w h color wing-file font-name font-size]
  (let [img (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
        g ^Graphics2D (.getGraphics img)
        bg-img (ImageIO/read wing-file)
        scaled-instance (.getScaledInstance bg-img w h Image/SCALE_SMOOTH)]
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
      (.fill g sha)
      (.dispose g)
      img)))

(defn create-img-default [name w h color wing-file]
  (create-img name w h color wing-file "方正颜宋简体_粗" 40))

(defn create-blp! [name out w h color wing-file]
  (let [img (create-img-default name w h color wing-file)]
    (ImageIO/write img "blp" out)))

(defn- delete-files-in-dir
  "删除指定目录下的所有文件"
  [^File dir]
  (let [files (file-seq dir)]
    (doseq [file files]
      (when (and (.exists file) (.isFile file))
        (FileUtils/delete file)))))

(defn generate-title!
  "一键生成头顶称号"
  [name color wing-file out-dir & {:keys [template-mdx-path old-texture-path font-name font-size]
                                   :or   {template-mdx-path "mdx/template.mdx"
                                          old-texture-path  "war3mapImported\\kangkang.blp"
                                          font-name         "方正颜宋简体_粗"
                                          font-size         40}}]
  (let [eng-name (pinyin/get-pinyin-name name)
        blp-file (jio/file out-dir "temp" (str eng-name ".blp"))
        blp-parent (.getParentFile blp-file)
        template-mdx (jio/file (jio/resource template-mdx-path))
        mdx-file (jio/file out-dir "temp" (str eng-name ".mdx"))]
    (println (.getAbsolutePath blp-parent))
    (.mkdirs blp-parent)
    (delete-files-in-dir blp-parent)
    (let [img (create-img name 256 128 color wing-file font-name font-size)]
      (ImageIO/write img "blp" blp-file))
    (FileUtils/copyFile template-mdx mdx-file)
    (converter/replace-blp mdx-file old-texture-path (.getName blp-file))))

(defn- get-scaled-image [^BufferedImage img w h]
  (let [scaled-instance (.getScaledInstance img w h Image/SCALE_SMOOTH)
        img (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
        g ^Graphics2D (.getGraphics img)]
    (.drawImage g scaled-instance 0 0 nil)
    (.dispose g)
    img))

(defn get-file-pinyin-name-without-extension [^File file]
  (let [name (.getName file)
        raw-name (subs name 0 (.lastIndexOf name "."))]
    (pinyin/get-pinyin-name raw-name)))

(defn generate-title-with-image!
  [img blp-name out-dir & {:keys [template-mdx-path old-texture-path]
                           :or   {template-mdx-path "mdx/template.mdx"
                                  old-texture-path  "war3mapImported\\kangkang.blp"}}]
  (let [blp-file (jio/file out-dir "temp" (str blp-name ".blp"))
        blp-parent (.getParentFile blp-file)
        template-mdx (jio/file (jio/resource template-mdx-path))
        mdx-file (jio/file out-dir "temp" (str blp-name ".mdx"))
        buffered-img (get-scaled-image (ImageIO/read (jio/file img)) 256 128)]
    (.mkdirs blp-parent)
    (ImageIO/write buffered-img "blp" blp-file)
    (FileUtils/copyFile template-mdx mdx-file)
    (converter/replace-blp mdx-file old-texture-path (.getName blp-file))))

(comment
  (def out-dir "D:/IdeaProjects/small/out")
  (generate-title! "领取福利" Color/BLUE (jio/file (jio/resource "images/background/3.png")) out-dir)
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/主线历练.png" "ch_zhuxian" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/场景传送.png" "ch_changjing" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/副本传送.png" "ch_fuben" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/古董商人.png" "ch_gudong" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/任务传送.png" "ch_renwu" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/声望兑换.png" "ch_shengwang" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/选择副职.png" "ch_fuzhi" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/装备商人.png" "ch_zhuangbei" "D:/IdeaProjects/jzjh2/out")

  (generate-title-with-image! "D:\\IdeaProjects\\europe\\resources\\头顶称号\\主线任务.png" "ch_zhuxian" "D:/IdeaProjects/europe/out")
  (generate-title-with-image! "D:\\IdeaProjects\\europe\\resources\\头顶称号\\吃货一枚.png" "ch_chihuo" "D:/IdeaProjects/europe/out")
  (generate-title-with-image! "D:\\IdeaProjects\\europe\\resources\\头顶称号\\打铁汉子.png" "ch_datie" "D:/IdeaProjects/europe/out")
  (generate-title-with-image! "D:\\IdeaProjects\\europe\\resources\\头顶称号\\酒水饮料.png" "ch_jiushui" "D:/IdeaProjects/europe/out")
  (generate-title-with-image! "D:\\IdeaProjects\\europe\\resources\\头顶称号\\流浪青年.png" "ch_liulang" "D:/IdeaProjects/europe/out")
  (generate-title-with-image! "D:\\IdeaProjects\\europe\\resources\\头顶称号\\无奸不商.png" "ch_jianshang" "D:/IdeaProjects/europe/out")
  (generate-title-with-image! "D:\\IdeaProjects\\europe\\resources\\头顶称号\\酸菜亦菲.png" "ch_suancai" "D:/IdeaProjects/jzjh-reborn/out")

  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/新建文件夹/购买武器.png" "ch_wuqi" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/新建文件夹/精英挑战.png" "ch_tiaozhan" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/新建文件夹/科技升级.png" "ch_keji" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/新建文件夹/幸运抽取.png" "ch_chouqu" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/新建文件夹/招募侠士.png" "ch_zhaomu" "D:/IdeaProjects/jzjh2/out")

  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/新建文件夹/购买丹药.png" "ch_danyao" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/新建文件夹/绝世武功.png" "ch_wugong" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/新建文件夹/领取福利.png" "ch_fuli" "D:/IdeaProjects/jzjh2/out")
  (generate-title-with-image! "D:/IdeaProjects/jzjh2/resources/新建文件夹/武器熔炼.png" "ch_ronglian" "D:/IdeaProjects/jzjh2/out")

  (doseq [file (file-seq (jio/file "D:\\IdeaProjects\\JZJH\\resources\\头顶称号\\png"))]
    (when (.isFile file)
      (generate-title-with-image! (.getAbsolutePath file) (get-file-pinyin-name-without-extension file) out-dir)))

  (get-file-pinyin-name-without-extension (jio/file "D:\\IdeaProjects\\jzjh-reborn\\out\\temp\\lingqifu.blp"))

  (def titles ["不堪一击" "毫不足虑" "不足挂齿" "初学乍练" "勉勉强强" "初窥门径" "初出茅庐" "略知一二" "普普通通" "平平常常" "平淡无奇"
               "粗懂皮毛" "半生不熟" "登堂入室" "略有小成" "已有小成" "鹤立鸡群" "驾轻就熟" "青出於蓝" "融会贯通" "心领神会" "炉火纯青"
               "了然於胸" "略有大成" "已有大成" "豁然贯通" "非比寻常" "出类拔萃" "罕有敌手" "技冠群雄" "神乎其技" "出神入化" "傲视群雄"
               "登峰造极" "无与伦比" "所向披靡" "一代宗师" "精深奥妙" "神功盖世" "举世无双" "惊世骇俗" "撼天动地" "震古铄今" "超凡入圣"
               "威镇寰宇" "空前绝后" "天人合一" "深藏不露" "深不可测" "返璞归真"])
  (doseq [v (map-indexed vector titles)]
    (ImageIO/write (create-img-default (second v) 64 64 (Color. 0x312B43) (jio/file (jio/resource (str "images/wing" (inc (quot (first v) 10)) ".png")))) "png" (jio/file "D:\\tmp\\out" (str "title" (first v) ".png"))))
  ,)
