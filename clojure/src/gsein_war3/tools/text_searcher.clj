(ns gsein-war3.tools.text-searcher
  "在目录树中搜索包含指定文本的文件。"
  (:require [clojure.string :as str]
            [clojure.java.io :as jio]))

(def ^:private text-extensions
  "被视为可搜索文本文件的扩展名集合（小写）。"
  #{"j" "ini" "txt" "md" "edn" "clj"})

(defn- text-file?
  "判断文件是否为可搜索的文本文件。"
  [^java.io.File file]
  (let [name (.getName file)
        idx (.lastIndexOf name ".")]
    (and (pos? idx)
         (contains? text-extensions (str/lower-case (subs name (inc idx)))))))

(defn- file-contains?
  "以 UTF-8 流式读取文件，检查是否包含 text。读取失败时返回 false 并打印警告。"
  [^java.io.File file text]
  (try
    (with-open [rdr (jio/reader file :encoding "UTF-8")]
      (some #(str/includes? % text) (line-seq rdr)))
    (catch Exception e
      (println "Warning: cannot read" (.getPath file) ":" (.getMessage e))
      false)))

(defn search-files
  "在 dir 目录树中搜索内容包含 text 的文本文件。
   仅搜索扩展名在白名单内的文件，跳过二进制文件与大文件。
   单个文件读取失败不会中断整体搜索。
   返回匹配文件对象序列（最多 10 个）。"
  [text dir]
  (->> (file-seq dir)
       (filter #(.isFile ^java.io.File %))
       (filter text-file?)
       (filter #(file-contains? % text))
       (take 10)))

(defn search-text
  "兼容旧名的别名，语义同上。"
  [text dir]
  (search-files text dir))

(comment
  (search-files "YDWE_OBJECT_TYPE_ABILITY" (jio/file "E:\\game\\YDWE 1.31.8 + 暴雪API 1.0")))
