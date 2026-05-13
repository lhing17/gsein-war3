(ns gsein-war3.lni.reader
  (:require [clojure.java.io :as jio]
            [clojure.string :as str]
            [flatland.ordered.map :as ordered]))

(defn- is-id-line [line]
  ;; 判断LNI文件中的一行是否是ID行
  ;; 判断依据为trim后匹配正则表达式 [A-Za-z0-9]{4}
  (let [trimmed-line (str/trim line)]
    (re-matches #"^\[[A-Za-z0-9]{4}\]$" trimmed-line)))

(defn- read-chunk-body [chunk]
  ;; 读取一个chunk的body
  (loop [result (ordered/ordered-map) k nil v "" lines chunk]
    (let [line (first lines)]
      (cond (empty? lines)
            result

            ;; skip blank lines
            (str/blank? line)
            (recur result k v (rest lines))

            ;; skip comments
            (str/starts-with? line "--")
            (recur result k v (rest lines))

            (str/includes? line " = ")
            (let [[left right] (str/split line #"=" 2)
                  ck (str/trim left)
                  cv (str/trim right)]
              (recur (assoc result ck cv)
                     ck cv (rest lines)))

            :else
            (let [cv (str/trim line)
                  pv (get result k)
                  nv (str pv (System/lineSeparator) cv)]
              (recur (assoc result k nv)
                     k nv (rest lines)))))))

(defn- read-chunk [lni-file]
  ;; 将lni文件读取为由id和body组成的map
  (let [partitions (->> (line-seq (jio/reader lni-file))
                        (partition-by is-id-line))
        ;; 过滤掉开头的非 ID 分区（如空行、注释、BOM）
        id-partitions (partition 2 (drop-while #(not (is-id-line (first %))) partitions))
        get-id (fn [id-lines]
                 (let [line (first id-lines)]
                   (when (< 2 (count line))
                     (subs line 1 (dec (count line))))))]
    (into {} (map (fn [[id body]] [(get-id id) (vec body)]) id-partitions))))

(defn read-lni [lni-file]
  ;; 读取lni文件
  (let [file (jio/file lni-file)]
    (cond
      (not (.exists file))
      (throw (java.io.FileNotFoundException. (str "LNI file not found: " lni-file)))

      (not (.isFile file))
      (throw (IllegalArgumentException. (str "Not a file: " lni-file)))

      :else
      (-> (read-chunk file)
          (update-vals read-chunk-body)))))
