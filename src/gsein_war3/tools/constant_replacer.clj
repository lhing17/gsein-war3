(ns gsein-war3.tools.constant-replacer
  (:require
    [clojure.java.io :as jio]
    [clojure.string :as str]
    [gsein-war3.lni.reader :as reader]
    [gsein-war3.util.pinyin :as pinyin]))

; 判断某个字符串除空格外是否以constant开头
(defn- starts-with-constant [s]
  (str/starts-with? (str/trim s) "constant"))

; 获取某个字符串中的常量名和值
(defn- get-constant-name-and-value [s]
  (let [constant-pattern #"\s*constant\s+integer\s+(\w+)\s*=\s*(\'[A-Za-z0-9]{4}\').*"]
    (when-let [[_ name value] (re-matches constant-pattern s)]
      [name value])))

(comment
  (get-constant-name-and-value "constant integer RED_BIRD= 'n01I'")
  )

; 读取一个文件中的所有常量
(defn- read-constants [file]
  (->> (jio/reader file)
       (line-seq)
       (filter starts-with-constant)
       (map get-constant-name-and-value)
       (remove nil?)
       ))

; 用常量替换字符串中的常量值
(defn replace-literal-with-constant [s m]
  (reduce-kv (fn [rs k v]
               (str/replace rs k v))
             s
             m))

; 使用{'n01I' 1}这种形式来记录字面量出现的次数
(defn literal-counter [s original-map]
  (let [literal-pattern #"\'[A-Za-z0-9]{4}\'"]
    (->> (re-seq literal-pattern s)
         (reduce (fn [m k]
                   (update m k (fn [v] (if v (inc v) 1))))
                 original-map))))

(comment
  (literal-counter "constant integer RED_BIRD= 'n01I'" {})
  )

; 一个字符串只保留汉字
(defn- keep-chinese [s]
  (->> (str/split s #"")
       (map #(.charAt % 0))
       (filter #(< 19968 (int %) 40869))
       (str/join "")))

; 将汉字字符串转为大写字母以下划线分隔的拼音
(defn- to-pinyin [s]
  (->> (str/split s #"")
       (map pinyin/get-pinyin-name)
       (map #(.toUpperCase %))
       (str/join "_")))


(comment
  (def constants (read-constants "D:/IdeaProjects/jzjh-reborn/jzjh/map/war3map.j"))
  (count constants)
  (def reverse-constant-map (into {} (map (fn [[k v]] [v k]) constants)))

  (replace-literal-with-constant "constant integer RED_BIRD= 'n01I'" reverse-constant-map)

  (def jass-files (->> (file-seq (jio/file "D:/IdeaProjects/jzjh-reborn/jass"))
                       (filter #(.isFile %))
                       (map #(.getAbsolutePath %))
                       (filter #(str/ends-with? % ".j"))))

  (doseq [jass-file jass-files]
    (->> (jio/reader jass-file)
         (line-seq)
         (map (fn [s]
                (if (starts-with-constant s)
                  s
                  (replace-literal-with-constant s reverse-constant-map))))
         (str/join "\n")
         ((fn [s] (str s "\n")))
         (spit jass-file)))

  (def literal-map (->> jass-files
                        (map jio/reader)
                        (mapcat line-seq)
                        (reduce (fn [m s]
                                  (literal-counter s m)) {})
                        ))
  (def literals (->> literal-map
                     (filter (fn [[k v]] (> v 1)))
                     (filter (fn [[k v]] (or (= "O" (subs k 1 2)) (= "H" (subs k 1 2)))))
                     (sort-by key)
                     (map first)
                     (map #(subs % 1 (- (count %) 1)))
                     ))

  (def ability-map (-> (reader/read-lni "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\table\\ability.ini")
                       (update-vals #(get % "Name"))))
  (def item-map (-> (reader/read-lni "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\table\\item.ini")
                    (update-vals #(get % "Name"))))

  (def unit-map (-> (reader/read-lni "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\table\\unit.ini")
                    (update-vals #(get % "Name"))))

  (->> (select-keys (merge ability-map item-map unit-map) literals)
       (filter (fn [[k v]] (not (nil? v))))
       (sort-by key)
        (map (fn [[k v]] (str "constant integer "
                              (if (str/starts-with? k "I") "ITEM_" "")
                              (to-pinyin (keep-chinese v))
                              " = '" k "' //" (keep-chinese v))))
        (str/join "\n")
       )

  )
