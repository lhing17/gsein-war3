(ns gsein-war3.lni.writer
  (:require [clojure.java.io :as jio]
            [clojure.string :as str]))

(defn- write-chunk-body [chunk-body]
  (str/join (map (fn [[k v]] (str k " = " v "\n")) chunk-body)))

(defn- write-chunk [chunk]
  (str/join (map (fn [[id body]]
                    (str "[" id "]\n" (write-chunk-body body)))
                  chunk)))

(defn write-lni [lni-file chunk]
  "将 chunk 写入 lni 文件。若父目录不存在则自动创建。"
  (let [file (jio/file lni-file)]
    (when-let [parent (.getParentFile file)]
      (.mkdirs parent))
    (spit file (write-chunk chunk))))

(defn- update-attr [m f k]
  "更新指定 key 的属性值"
  (let [attr (get m k)]
    (if (nil? attr) m
                    (assoc m k (f attr)))))

(comment
  (require '[gsein-war3.lni.available-id :as aid]
           '[gsein-war3.lni.reader :as reader])

  (def mobs ["h005" "u000" "h006" "e002" "o004" "u001" "n00H" "h007" "z000" "z001" "u002"
             "o005" "n02T" "e003" "n00J" "u003" "e004" "e005" "u004" "n00K" "e006" "u005"
             "h008" "h009" "n00L" "n00M" "n00N" "n00O" "n00P" "n00Q" "u006" "o008" "h00A"
             "e007" "n00S" "u007" "o009" "n00T" "n00U" "n00V" "n00W" "u009" "e008" "u00A"
             "n00X" "n02U" "n02V" "n02W" "n02X" "n02Y" "n02Z" "n030" "n031" "n032" "n033"])
  (def lni (reader/read-lni "D:\\IdeaProjects\\jztd-reborn\\jztd\\table\\unit.ini"))
  (merge lni
         (update-vals (select-keys lni mobs) #(update-attr % (fn [hp] (str (int (* 0.7 (Integer/parseInt hp))))) "HP")))
  (write-lni "test.ini" (merge lni
                               (update-vals (select-keys lni mobs) #(update-attr % (fn [hp] (str (int (* 0.7 (Integer/parseInt hp))))) "HP"))))

  ;; 可用人口
  (def tower-food {"O100" 1
                   "O101" 1
                   "O102" 1
                   "O109" 1
                   "O10A" 1
                   "O10B" 1
                   "O10J" 1
                   "O10K" 1
                   "O10L" 1
                   "O10U" 1
                   "O10V" 1
                   "O10W" 1
                   "O115" 1
                   "O116" 1
                   "O117" 1
                   "O103" 1
                   "O104" 1
                   "O10C" 1
                   "O10D" 1
                   "O10M" 1
                   "O10N" 1
                   "O10O" 1
                   "O10X" 1
                   "O10Y" 1
                   "O10Z" 1
                   "O118" 1
                   "O119" 1
                   "O11A" 1
                   "O11B" 1
                   "O11J" 1
                   "O105" 1
                   "O106" 1
                   "O10E" 1
                   "O10F" 1
                   "O10G" 1
                   "O10P" 1
                   "O10Q" 1
                   "O10R" 1
                   "O110" 1
                   "O111" 1
                   "O112" 1
                   "O11C" 1
                   "O11D" 1
                   "O11E" 1
                   "O11K" 1
                   "O107" 1
                   "O108" 1
                   "O10H" 1
                   "O10I" 1
                   "O10S" 1
                   "O10T" 1
                   "O113" 1
                   "O114" 1
                   "O11F" 1
                   "O11G" 1
                   "O11H" 1
                   "O11I" 1
                   "O11L" 1})

  (write-lni
    "test.ini"
    (merge-with (fn [attr food]
                  (assoc attr "fused" food))
                lni
                tower-food))

  (def lni (reader/read-lni "D:\\IdeaProjects\\europe\\europe\\table\\item.ini"))

  (write-lni
    "test.ini"
    (merge-with (fn [attr food]
                  (assoc attr "fused" food))
                lni
                (take 22 (iterate aid/next-id "I008"))))

  (write-lni "test.ini"
             (merge lni
                    (update-vals (select-keys lni (take 22 (iterate aid/next-id "I008")))
                                 #(assoc % "HP" "101"))))

  )
