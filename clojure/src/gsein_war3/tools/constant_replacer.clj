(ns gsein-war3.tools.constant-replacer
  (:require
    [clojure.java.io :as jio]
    [clojure.string :as str]
    [gsein-war3.util.pinyin :as pinyin]))

(def ^:private chinese-start 0x4E00)
(def ^:private chinese-end 0x9FA5)

(defn- starts-with-constant [s]
  (str/starts-with? (str/trim s) "constant"))

(defn- get-constant-name-and-value [s]
  "解析 JASS 常量声明，支持任意类型名和灵活空格。"
  (let [constant-pattern #"\s*constant\s+\w+\s+(\w+)\s*=\s*(\'[A-Za-z0-9]{4}\').*"]
    (when-let [[_ name value] (re-matches constant-pattern s)]
      [name value])))

(defn read-constants [file]
  "读取文件中的所有常量定义。"
  (with-open [rdr (jio/reader file)]
    (->> (line-seq rdr)
         (filter starts-with-constant)
         (map get-constant-name-and-value)
         (remove nil?)
         doall)))

(defn replace-literal-with-constant [s m]
  "按 key 长度降序替换，避免短 key 误匹配长 key 的子串。"
  (reduce (fn [rs [k v]]
            (str/replace rs k v))
          s
          (sort-by (comp count key) > m)))

(defn literal-counter [s original-map]
  "统计字符串中 FourCC 字面量出现次数。"
  (let [literal-pattern #"\'[A-Za-z0-9]{4}\'"]
    (->> (re-seq literal-pattern s)
         (reduce (fn [m k]
                   (update m k (fnil inc 0)))
                 original-map))))

(defn- keep-chinese [s]
  "保留字符串中的汉字字符。"
  (->> (str/split s #"")
       (map #(.charAt % 0))
       (filter #(< chinese-start (int %) chinese-end))
       (str/join "")))

(defn- to-pinyin [s]
  "将汉字字符串转为大写拼音，以下划线分隔。"
  (->> (str/split s #"")
       (map pinyin/get-pinyin-name)
       (map str/upper-case)
       (str/join "_")))

(comment
  (get-constant-name-and-value "constant integer RED_BIRD= 'n01I'")
  (get-constant-name-and-value "constant   real   MY_REAL = 'n01J'")

  (def constants (read-constants "D:/IdeaProjects/jzjh-reborn/jzjh/map/war3map.j"))
  (count constants)

  (def reverse-constant-map (into {} (map (fn [[k v]] [v k]) constants)))
  (replace-literal-with-constant "constant integer RED_BIRD= 'n01I'" reverse-constant-map)

  (literal-counter "constant integer RED_BIRD= 'n01I'" {})
  (to-pinyin (keep-chinese "【普通】皮质战甲"))
  )
