(ns gsein-war3.mdx.classifier
  (:require [gsein-war3.config :as config]
            [gsein-war3.mdx.parser :as parser]
            [clojure.string :as str]
            [clojure.java.io :as jio])
  (:import (java.io File)
           (org.apache.commons.io FileUtils)))


(def env (config/get-config))

(defn- get-mdx-files [^File root]
  "获取指定文件夹下所有的mdx文件（含子文件夹下的文件）"
  (->> (file-seq root)
       (filterv #(str/ends-with? (.getName %) ".mdx"))
       ))

(defn classify [^File root ^File out-dir mode]
  "分类mdx文件"
  (let [mdx-files (get-mdx-files root)]
    (doseq [mdx-file mdx-files]
      (let [file-name (.getName mdx-file)
            out-mdx-file (jio/file out-dir (str/replace file-name ".mdx" "") file-name)
            blp-files (filter (complement str/blank?) (parser/parse mdx-file))
            source-blp-files (map #(jio/file root %) blp-files)
            out-blp-files (map #(jio/file out-dir (str/replace file-name ".mdx" "") %) blp-files)
            ]

        (try (FileUtils/copyFile mdx-file out-mdx-file)
             (doseq [[blp-file out-blp-file] (map list source-blp-files out-blp-files)]
               (when (.exists blp-file)
                (FileUtils/copyFile blp-file out-blp-file)))
             (catch Exception e (.printStackTrace e)))
        (when (not= mode :copy)
          (FileUtils/delete mdx-file)
          (doseq [blp-file source-blp-files]
            (FileUtils/delete (jio/file blp-file))))
       ))))





(comment
  (def mdx-files (get-mdx-files (jio/file "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource")))
  (classify (jio/file "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource")
            (jio/file "D:\\IdeaProjects\\jzjh-reborn\\out")
            :copy)
  (doseq [mdx-file mdx-files]
    (println mdx-file)
    (println (parser/parse mdx-file))
    ),)