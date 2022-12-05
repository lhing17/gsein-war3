(ns gsein-war3.lni.available-id
  (:require [clojure.string :as str]
            [clojure.java.io :as jio]
            [gsein-war3.config :as config]
            [selmer.parser :as sp]))


(def env (config/get-config))

(defn- available? [id current-ids]
  (not-any? #(= (str/lower-case id) (str/lower-case %)) current-ids))

(defn- next-char [c]
  "计算下一个字符：9的下一个字符为A，Z的下一个字符为0"
  (let [i (int c)]
    (cond
      ;; 0到9
      (and (>= i 48) (<= i 56)) (char (inc i))
      (= i 57) \A

      ;; A到Z
      (and (>= i 65) (<= i 89)) (char (inc i))
      (= i 90) \0

      ;; a到z
      (and (>= i 97) (<= i 121)) (char (inc i))
      (= i 122) \0)))

(defn- inc-by-index [s index]
  "对下标为index的字符取next-char"
  (apply str (concat (take index s)
                     (cons (next-char (nth s index))
                           (nthrest s (inc index))))))

(defn next-id [id]
  "计算下一个ID"
  (loop [index (dec (count id)), cid (inc-by-index id index)]
    (if (= \0 (nth cid index))
      (recur (dec index) (inc-by-index cid (dec index)))
      cid)))

(defn project-id-producer [project-dir]
  "获取某类型当前所有的ID"
  (fn [type]
    (let [real-type (if (= type :hero) :unit type)
          object-path (str "table/" (name real-type) ".ini")]
      (with-open [rdr (jio/reader (str project-dir "/" object-path))]
        (->> (line-seq rdr)
             (filterv #(and (str/starts-with? % "[") (str/ends-with? % "]")))
             (mapv #(str/replace % #"\[|\]" "")))))))

(defn get-available-ids

  ([n id-producer type start-id]
   "获取n个可用ID"

   (->> (iterate next-id start-id)
        (filter #(available? % (id-producer type)))
        (take n)))
  ([n id-producer type]
   (let [start-id
         (case type :ability "A000"
                    :item "I000"
                    :unit "e000"
                    :hero "E000"
                    :buff "B000"
                    :doodad "D000"
                    "A000")]
     (get-available-ids n id-producer type start-id)
     )))

(defn get-available-id [id-producer type]
  "获取一个可用ID"
  (first (get-available-ids 1 id-producer type)))

(comment
  (next-char \9)
  (next-id "A00Z")
  (take 5 (iterate next-id "A000"))
  (->> (iterate next-id "A000")
       (filter #(available? % ((project-id-producer project-dir) :ability)))
       (take 5))
  (def project-dir (:project-dir env))
  ((project-id-producer project-dir) :ability)
  (get-available-ids 5 (project-id-producer project-dir) :item)
  ,)
