(ns gsein-war3.tools.text_searcher
  (:require [clojure.string :as str]
            [clojure.java.io :as jio]))


(defn search-text [text dir]
  ;; 搜索指定目录下哪个文件中有指定文本
  (->> (file-seq dir)
       (filter #(.isFile %))
       (filter #(str/includes? (slurp %) text))
       (take 10)
       ))

(comment
  (search-text  "YDWE_OBJECT_TYPE_ABILITY" (jio/file "E:\\game\\YDWE 1.31.8 + 暴雪API 1.0"))
  )