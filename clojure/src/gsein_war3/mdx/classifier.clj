(ns gsein-war3.mdx.classifier
  (:require [gsein-war3.mdx.parser :as parser]
            [clojure.string :as str]
            [clojure.java.io :as jio])
  (:import (java.io File)
           (org.apache.commons.io FileUtils)))

(defn- get-mdx-files
  "获取指定文件夹下所有的mdx文件（含子文件夹下的文件）"
  [^File root]
  (->> (file-seq root)
       (filter #(str/ends-with? (.getName %) ".mdx"))))

(defn classify
  "分类mdx文件"
  ([^File root mdx-filter ^File out-dir mode]
   (let [mdx-files (get-mdx-files root)
         filtered-mdx-files (filter mdx-filter mdx-files)]
     (println "mdx files:" (count mdx-files))
     (doseq [mdx-file filtered-mdx-files]
       (println (.getName mdx-file))
       (let [file-name (.getName mdx-file)
             out-mdx-file (jio/file out-dir (str/replace file-name ".mdx" "") file-name)
             blp-files (filter (complement str/blank?) (parser/parse mdx-file))
             source-blp-files (map #(jio/file root %) blp-files)
             out-blp-files (map #(jio/file out-dir (str/replace file-name ".mdx" "") %) blp-files)]
         (try
           (.mkdirs (.getParentFile out-mdx-file))
           (FileUtils/copyFile mdx-file out-mdx-file)
           (doseq [[blp-file out-blp-file] (map vector source-blp-files out-blp-files)]
             (when (.exists blp-file)
               (.mkdirs (.getParentFile out-blp-file))
               (FileUtils/copyFile blp-file out-blp-file)))
           (when (not= mode :copy)
             (FileUtils/delete mdx-file)
             (doseq [blp-file source-blp-files]
               (FileUtils/delete (jio/file blp-file))))
           (catch Exception e
             (.printStackTrace e)))))))

  ([^File root ^File out-dir mode]
   (classify root (constantly true) out-dir mode))

  ([^File root ^File out-dir]
   (classify root (constantly true) out-dir :copy)))
