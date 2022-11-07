(ns gsein-war3.lni.reader
  (:require [clojure.java.io :as jio]
            [clojure.string :as str]
            [flatland.ordered.map :refer :all]))

(defn- is-id-line [line]
  ;; 判断LNI文件中的一行是否是ID行
  ;; 判断依据为trim后匹配正则表达式 [A-Za-z0-9]{4}
  (let [trimmed-line (str/trim line)]
    (re-matches #"^\[[A-Za-z0-9]{4}\]$" trimmed-line)))

(comment
  (is-id-line "[n001] "),)


(defn- read-chunk-body [chunk]
  ;; 读取一个chunk的body
  (loop [result (ordered-map) k nil v "" lines chunk]
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
                  nv (str pv "\r\n" cv)]
              (recur (assoc result k nv)
                     k nv (rest lines)))))))



(defn- read-chunk [lni-file]
  ;; 将lni文件读取为由id和body组成的map
  (let [chunk (->> (line-seq (jio/reader lni-file))
                   (partition-by is-id-line)
                   (apply hash-map))
        get-id (fn [id-line] (-> id-line
                                 first
                                 (#(subs % 1 (- (count %) 1)))
                                 ))]
    (-> chunk
        (update-keys get-id))))

(defn read-lni [lni-file]
  ;; 读取lni文件
  (let [chunk (read-chunk lni-file)]
    (-> chunk
         (update-vals read-chunk-body)
         )))

(comment
  (def chunks (read-chunk "D:\\IdeaProjects\\jztd-reborn\\jztd\\table\\ability.ini"))
  (get chunks "Aetl")
  (read-chunk-body (get chunks "Aetl"))
  (-> (read-lni "D:\\IdeaProjects\\jztd-reborn\\jztd\\table\\ability.ini")
      (update-vals #(get % "Name")))
  )