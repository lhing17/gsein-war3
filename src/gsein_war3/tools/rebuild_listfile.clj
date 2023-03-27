(ns gsein-war3.tools.rebuild-listfile
  (:require [clojure.java.io :as jio]
            [clojure.string :as str])
  (:import (java.nio.file Paths)))

;; 重建listfile文件

(comment
  (->> (file-seq (jio/file "E:\\War3Map\\拆地图\\剑开天门\\剑开"))
       (filter #(.isFile %))
       (map #(.toPath %))
       (map #(.relativize (Paths/get "E:\\War3Map\\拆地图\\剑开天门\\剑开" (into-array String [])) %))
       (str/join "\n")
       (spit "(listfile)"))

  )