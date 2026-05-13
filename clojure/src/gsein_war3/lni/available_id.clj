(ns gsein-war3.lni.available-id
  (:require [clojure.java.io :as jio]
            [clojure.string :as str]))

(defn- next-char
  "计算下一个字符：0-8 → +1，9→A，A-Y → +1，Z→0，a-y → +1，z→0"
  [c]
  (let [i (int c)]
    (cond
      (and (>= i 48) (< i 57)) (char (inc i))
      (= i 57) \A
      (and (>= i 65) (< i 90)) (char (inc i))
      (= i 90) \0
      (and (>= i 97) (< i 122)) (char (inc i))
      (= i 122) \0)))

(defn- inc-by-index
  "对下标为 index 的字符取 next-char，使用 StringBuilder 避免中间集合。"
  [s index]
  (let [sb (StringBuilder. s)]
    (.setCharAt sb index (next-char (.charAt sb index)))
    (.toString sb)))

(defn next-id
  "计算下一个 ID。若 ID 空间耗尽则抛出异常。"
  [id]
  (loop [index (dec (count id))
         cid (inc-by-index id index)]
    (cond
      (neg? index) (throw (ex-info "ID space exhausted" {:last-id id}))
      (= \0 (nth cid index)) (recur (dec index) (inc-by-index cid (dec index)))
      :else cid)))

(defn project-id-producer
  "获取某类型当前所有的 ID。结果按 type 缓存，避免重复读取文件。"
  [project-dir]
  (memoize
    (fn [type]
      (let [real-type (if (= type :hero) :unit type)
            object-path (str "table/" (name real-type) ".ini")]
        (with-open [rdr (jio/reader (str project-dir "/" object-path))]
          (->> (line-seq rdr)
               (filterv #(and (str/starts-with? % "[") (str/ends-with? % "]")))
               (mapv #(str/replace % #"\[|\]" ""))))))))

(defn- available? [id ids-set]
  (not (contains? ids-set (str/lower-case id))))

(defn get-available-ids
  "获取 n 个可用 ID。预先读取当前 ID 集合并转为小写 set，避免线性扫描。"
  ([n id-producer type start-id]
   (let [current-ids (id-producer type)
         ids-set (into #{} (map str/lower-case) current-ids)]
     (->> (iterate next-id start-id)
          (filter #(available? % ids-set))
          (take n))))
  ([n id-producer type]
   (let [start-id (case type
                    :ability "A000"
                    :item    "I000"
                    :unit    "e000"
                    :hero    "E000"
                    :buff    "B000"
                    :doodad  "D000"
                    "A000")]
     (get-available-ids n id-producer type start-id))))

(defn get-available-id
  "获取一个可用 ID"
  [id-producer type]
  (first (get-available-ids 1 id-producer type)))

(comment
  (next-char \9)
  (next-id "A00Z")
  (take 5 (iterate next-id "A000"))
  ,)
