(ns gsein-war3.blp.generator
  (:require [clojure.java.io :as jio]
            [gsein-war3.config :as config]
            [gsein-war3.util.image :as img]
            [gsein-war3.util.pinyin :as pinyin])
  (:import (java.io File)
           (java.nio.file Paths)
           (javax.imageio ImageIO)
           (org.apache.commons.io FileUtils)
           (java.awt.image BufferedImage)))

;; 读取图像 -> 缩放到64*64 -> 根据类型进行亮度处理 -> 根据类型决定是否加上边框 -> 保存到blp文件

;; 类型对应的配置
(def type-config
  {:active       {:brightness-fn identity
                  :border        :active
                  :dir           :command-dir
                  :prefix        "BTN"}
   :active-dark  {:brightness-fn #(img/adjust-brightness % -50)
                  :border        :passive
                  :dir           :command-disabled-dir
                  :prefix        "DISBTN"}
   :passive      {:brightness-fn identity
                  :border        :passive
                  :dir           :command-dir
                  :prefix        "PASBTN"}
   :passive-dark {:brightness-fn #(img/adjust-brightness % -64)
                  :border        :passive
                  :dir           :command-disabled-dir
                  :prefix        "DISPASBTN"}
   :default      {:brightness-fn identity
                  :border        :passive
                  :dir           :default
                  :prefix        ""}
   })

(def env (config/get-config))

(defrecord blp [name dir image])

(defn- command-dir [base-out-dir]
  "获取亮图标的路径"
  (str base-out-dir "/ReplaceableTextures/CommandButtons"))

(defn- command-disabled-dir [base-out-dir]
  "获取暗图标的路径"
  (str base-out-dir "/ReplaceableTextures/CommandButtonsDisabled"))

(defn- get-dir [type base-dir]
  (case type
    :command-dir (command-dir base-dir)
    :command-disabled-dir (command-disabled-dir base-dir)
    :default base-dir))

(defn image-to-blp [^BufferedImage image type base-dir name]
  (let [brightness-fn (:brightness-fn (get type-config type))
        border (:border (get type-config type))
        handled-image (-> image
                          (img/resize-to-64)
                          (brightness-fn)
                          (img/add-border border {:filter-type :default}))
        dir (get-dir type base-dir)]
    (->blp name dir handled-image)))

(defn output-to-file [^blp {:keys [image dir name]}]
  (img/output-as-blp image dir name ""))


(comment
  (def base-dir (:project-dir env))
  (-> (ImageIO/read (File. "E:\\Downloads\\帮助.png"))
      (image-to-blp :default base-dir "helpme")
      (output-to-file))
  ,)


(defn- output-blp [adjust-image-fn type dir-fn prefix]
  "获取输出blp的函数"
  (fn ^String [image name out-dir opts]
    (println (str "name: " name ", out-dir: " out-dir ", opts: " opts))
    (let [border (img/add-border (adjust-image-fn image) type opts)]
      (img/output-as-blp border (dir-fn out-dir) (pinyin/get-pinyin-name name) prefix))))

(defn- output-blp-fn [type]
  "根据类型获取输出blp的函数"
  (case type
    :active (output-blp identity :active command-dir "BTN")
    :active-dark (output-blp #(img/adjust-brightness % -50) :passive command-disabled-dir "DISBTN")
    :passive (output-blp identity :passive command-dir "PASBTN")
    :passive-dark (output-blp #(img/adjust-brightness % -64) :passive command-disabled-dir "DISPASBTN")))

(defn- get-fn-by-type [type image-name]
  #((output-blp-fn (keyword type)) % image-name (:temp-dir env) {:filter-type :default}))

(defn get-fn-coll [type image-name]
  [(get-fn-by-type type image-name)
   (get-fn-by-type (str type "-dark") image-name)])

(defn map-deal-image! [image coll]
  (map #(% image) coll))


(defn- to-path [^String path]
  (Paths/get path (into-array String [])))

(defn get-project-target-dir [^String path]
  (->> path
       (to-path)
       (.getParent)
       (.relativize (to-path (:temp-dir env)))
       (.resolve (Paths/get (:project-dir env) (into-array ["resource"])))
       (.toFile)))

(defn copy [^String path]
  (when path
    (FileUtils/copyFileToDirectory (jio/file path) (get-project-target-dir path))
    (str (.relativize (to-path (:temp-dir env)) (to-path path)))))


(defn generate-blps [^File image-file active-type]
  (let [coll (get-fn-coll active-type (.getName image-file))
        image (img/resize-to-64 (ImageIO/read image-file))
        temp-blps (map-deal-image! image coll)]
    (mapv #(copy %) temp-blps)))

(comment
  (def books (map-deal-image!
               (img/resize-to-64 (ImageIO/read (java.io.File. "D:\\IdeaProjects\\small\\resources\\images\\天鹰.png")))
               (get-fn-coll "active" "天鹰.png")
               ))

  (def image (img/resize-to-64 (ImageIO/read (java.io.File. "D:\\IdeaProjects\\small\\resources\\images\\天鹰.png"))))
  (def border (img/add-border (identity image) :active {:filter-type :default}))
  (ImageIO/write border "png" (java.io.File. "D:\\tmp\\天鹰.png"))
  (img/output-as-blp border (command-dir "D:\\tmp") (pinyin/get-pinyin-name "天鹰.png") "BTN")

  (doseq [book books] (copy book))

  (generate-blps (java.io.File. "D:\\IdeaProjects\\jztd-reborn\\resources\\图标\\sync.png") "active")

  (doseq [file (file-seq (jio/file "D:\\IdeaProjects\\europe\\resources\\图标\\装备"))]
    (when (.isFile file)
      (generate-blps file "active")))


  (doseq [file (file-seq (jio/file "D:\\IdeaProjects\\jzjh-reborn\\resources\\美食"))]
    (when (.isFile file)
      (generate-blps file "active")))



  (generate-blps (jio/file "D:\\IdeaProjects\\europe\\resources\\图标\\药水\\智力药水.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\jztd-reborn\\resources\\高级抽卡.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\jztd-reborn\\resources\\购买人口.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\jztd-reborn\\resources\\解锁抽卡.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\jztd-reborn\\resources\\金币.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\jztd-reborn\\resources\\木材.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\jztd-reborn\\resources\\中级抽卡.png") "active")


  (generate-blps (jio/file "D:\\IdeaProjects\\jzjh-reborn\\resources\\美食\\黑暗料理.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\jzjh-reborn\\resources\\美食\\烹饪.png") "active")


  (generate-blps (jio/file "D:\\IdeaProjects\\jztd-reborn\\resources\\图标\\完成\\合成.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\jztd-reborn\\resources\\图标\\完成\\升级.png") "active")

  (generate-blps (jio/file "D:\\IdeaProjects\\jzjh-reborn\\resources\\食材\\酱.png") "passive")

  (generate-blps (jio/file "D:\\IdeaProjects\\JZJH\\resources\\技能图标\\苗人凤.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\JZJH\\resources\\技能图标\\温青.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\JZJH\\resources\\技能图标\\河马.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\JZJH\\resources\\技能图标\\赤龙.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\JZJH\\resources\\技能图标\\紫罗兰.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\JZJH\\resources\\技能图标\\蓝色.png") "active")
  (generate-blps (jio/file "D:\\IdeaProjects\\JZJH\\resources\\技能图标\\雪影.png") "passive")
  (generate-blps (jio/file "D:\\IdeaProjects\\JZJH\\resources\\技能图标\\龙威.png") "passive")

  (generate-blps (jio/file "D:\\IdeaProjects\\europe\\resources\\图标\\技能\\升级.png") "active")


  )

