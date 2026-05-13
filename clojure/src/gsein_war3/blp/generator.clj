(ns gsein-war3.blp.generator
  (:require [clojure.java.io :as jio]
            [gsein-war3.util.image :as img]
            [gsein-war3.util.pinyin :as pinyin])
  (:import (java.io File)
           (javax.imageio ImageIO)
           (org.apache.commons.io FileUtils)
           (java.awt.image BufferedImage)))

;; War3 标准路径常量
(def ^:private command-buttons-dir "/ReplaceableTextures/CommandButtons")
(def ^:private command-buttons-disabled-dir "/ReplaceableTextures/CommandButtonsDisabled")
(def ^:private resource-dir-name "resource")

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
                  :prefix        ""}})

(defn- command-dir
  "获取亮图标的路径"
  [base-out-dir]
  (str base-out-dir command-buttons-dir))

(defn- command-disabled-dir
  "获取暗图标的路径"
  [base-out-dir]
  (str base-out-dir command-buttons-disabled-dir))

(defn- get-dir
  "根据类型获取输出目录"
  [type base-dir]
  (case type
    :command-dir (command-dir base-dir)
    :command-disabled-dir (command-disabled-dir base-dir)
    :default base-dir))

(defn image-to-blp
  "根据图像、类型、输出目录和文件名生成内存中的BLP实例"
  [^BufferedImage image type base-dir name]
  (let [config (get type-config type)
        brightness-fn (:brightness-fn config)
        border (:border config)
        handled-image (-> image
                          (img/resize-to-64)
                          (brightness-fn)
                          (img/add-border border {:filter-type :default}))
        dir (get-dir type base-dir)]
    {:name name :dir dir :image handled-image}))

(defn output-to-file
  "将BLP实例输出到文件"
  [{:keys [image dir name]}]
  (img/output-as-blp image dir name ""))

(defn- output-blp
  "获取输出blp的函数"
  [adjust-image-fn type dir-fn prefix]
  (fn ^String [image name out-dir opts]
    (println (str "name: " name ", out-dir: " out-dir ", opts: " opts))
    (let [border (img/add-border (adjust-image-fn image) type opts)]
      (img/output-as-blp border (dir-fn out-dir) (pinyin/get-pinyin-name name) prefix))))

(defn- output-blp-fn
  "根据类型获取输出blp的函数"
  [type]
  (let [config (get type-config type)
        brightness-fn (:brightness-fn config identity)
        border (:border config :passive)
        dir-fn (case (:dir config)
                 :command-dir command-dir
                 :command-disabled-dir command-disabled-dir
                 identity)
        prefix (:prefix config "")]
    (output-blp brightness-fn border dir-fn prefix)))

(defn- get-fn-by-type
  "根据类型获取输出blp的函数"
  [type image-name temp-dir]
  #((output-blp-fn (keyword type)) % image-name temp-dir {:filter-type :default}))

(defn get-fn-coll
  "根据类型获取输出blp的函数的集合"
  [type image-name temp-dir]
  [(get-fn-by-type type image-name temp-dir)
   (get-fn-by-type (str type "-dark") image-name temp-dir)])

(defn apply-processors
  "对图像集合应用函数"
  [image fn-coll]
  (map #(% image) fn-coll))

(defn- normalize-sep
  "统一使用正斜杠作为路径分隔符"
  [^String s]
  (.replace s "\\" "/"))

(defn get-project-target-dir
  "根据临时目录和项目目录获取目标目录"
  [^String path temp-dir project-dir]
  (let [parent (.. (jio/file path) getParentFile getCanonicalPath)
        temp-norm (normalize-sep temp-dir)
        parent-norm (normalize-sep parent)
        rel (.replaceFirst parent-norm (str "^" (java.util.regex.Pattern/quote temp-norm) "[/\\\\]?") "")
        rel (if (.startsWith rel "/") (subs rel 1) rel)]
    (jio/file project-dir resource-dir-name rel)))

(defn copy
  "将文件复制到目标目录，返回相对于临时目录的路径"
  ([^String path temp-dir project-dir]
   (when path
     (let [dest-dir (get-project-target-dir path temp-dir project-dir)]
       (FileUtils/copyFileToDirectory (jio/file path) dest-dir)
       (let [parent (.. (jio/file path) getParentFile getCanonicalPath)
             temp-norm (normalize-sep temp-dir)
             parent-norm (normalize-sep parent)
             rel (.replaceFirst parent-norm (str "^" (java.util.regex.Pattern/quote temp-norm) "[/\\\\]?") "")]
         (if (.startsWith rel "/") (subs rel 1) rel))))))

(defn generate-blps!
  "根据图片文件和类型生成blp文件，active-type可选值为active和passive，
   注意此函数会生成两个64*64的BLP文件，一个为亮图标，一个为暗图标"
  ([^File image-file active-type]
   (throw (IllegalArgumentException.
           "generate-blps! requires temp-dir and project-dir. Use (generate-blps! image-file active-type temp-dir project-dir)")))
  ([^File image-file active-type temp-dir project-dir]
   (let [fn-coll (get-fn-coll active-type (.getName image-file) temp-dir)
         image (img/resize-to-64 (ImageIO/read image-file))
         temp-blps (apply-processors image fn-coll)]
     (mapv #(copy % temp-dir project-dir) temp-blps))))
