(ns gsein-war3.tools.rebuild-listfile
  "重建 MPQ (listfile) 的工具。
   遍历目录树，收集所有文件的相对路径，写入 listfile。"
  (:require [clojure.java.io :as jio]
            [clojure.string :as str]))

(defn rebuild-listfile
  "遍历 root-dir 下的所有文件，生成相对于 root-dir 的路径列表，
   以系统换行符连接后写入 out-file。返回相对路径的有序向量。"
  [root-dir out-file]
  (let [root (jio/file root-dir)
        root-path (.toPath root)
        relative-paths (->> (file-seq root)
                            (filter #(.isFile ^java.io.File %))
                            (map #(-> root-path
                                      (.relativize (.toPath ^java.io.File %))
                                      (.toString)))
                            (sort)
                            (vec))]
    (jio/make-parents out-file)
    (spit out-file (str/join (System/lineSeparator) relative-paths))
    relative-paths))
