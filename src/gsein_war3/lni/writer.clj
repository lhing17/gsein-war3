(ns gsein-war3.lni.writer
  (:require [gsein-war3.lni.reader :as reader]))

(defn- write-chunk-body [chunk-body]
  ;; 将chunk的body写入文件
  (let [write-line (fn [line]
                     (str line "\n"))
        write-kv (fn [[k v]]
                   (str k " = " v "\n"))]
    (->> chunk-body
         (mapcat write-kv)
         (apply str))))

(defn- write-chunk [chunk]
  ;; 将chunk写入文件
  (let [write-id (fn [id]
                   (str "[" id "]\n"))
        write-body (fn [body]
                     (write-chunk-body body))]
    (->> chunk
         (mapcat (fn [[id body]]
                   (concat [(write-id id)] [(write-body body)])))
         (apply str))))

(defn write-lni [lni-file chunk]
  ;; 将chunk写入lni文件
  (spit lni-file (write-chunk chunk)))

(defn- update-hp [m f]
  ;; 更新hp
  (let [hp (get m "HP")]
    (if (nil? hp) m
                  (assoc m "HP" (f hp)))))



(comment
  (def mobs ["h005" "u000" "h006" "e002" "o004" "u001" "n00H" "h007" "z000" "z001" "u002"
             "o005" "n02T" "e003" "n00J" "u003" "e004" "e005" "u004" "n00K" "e006" "u005"
             "h008" "h009" "n00L" "n00M" "n00N" "n00O" "n00P" "n00Q" "u006" "o008" "h00A"
             "e007" "n00S" "u007" "o009" "n00T" "n00U" "n00V" "n00W" "u009" "e008" "u00A"
             "n00X" "n02U" "n02V" "n02W" "n02X" "n02Y" "n02Z" "n030" "n031" "n032" "n033"])
  (def lni (reader/read-lni "D:\\IdeaProjects\\jztd-reborn\\jztd\\table\\unit.ini"))
  (merge lni
         (update-vals (select-keys lni mobs) #(update-hp % (fn [hp] (str (int (* 0.7 (Integer/parseInt hp))))))))
  (write-lni "test.ini"   (merge lni
                                 (update-vals (select-keys lni mobs) #(update-hp % (fn [hp] (str (int (* 0.7 (Integer/parseInt hp)))))))))
  )