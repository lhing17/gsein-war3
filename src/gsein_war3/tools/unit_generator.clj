(ns gsein-war3.tools.unit-generator
  (:require [selmer.parser :as sp]
            [clojure.java.io :as jio]
            [clojure.string :as str]
            [gsein-war3.lni.available-id :as aid]
            [gsein-war3.config :as config]))

(def env (config/get-config))

;; 批量生成单位
(defn generate-units [units]
  (->> units
       (map #(sp/render-file (jio/resource "templates/单位基础.ini") %))
       (str/join "\n")))


(comment
  (def mobs ["马贼" "强盗" "五虎门小喽啰" "白莲教众" "茅山弟子" "崆峒弟子" "熊熊" "蒙古骑兵" "十恶不赦"])
  (def bosses ["马贼王" "马贼王中王" "凤天南" "方腊" "王璁儿" "葛洪" "茅盈" "唐文亮" "关能" "南海鳄神" "云中鹤" "段延庆" "叶二娘"
               "采花大盗" "宋远桥" "俞莲舟" "俞岱岩" "张松溪" "张翠山" "殷梨亭" "莫声谷" "黄药师" "欧阳锋" "一灯大师" "洪七公" "王重阳"
               "李莫愁" "丁敏君" "丘处机" ])
  (def mob-ids (aid/get-available-ids (count mobs) (aid/project-id-producer (:project-dir env)) :unit "n007"))
  (->> mobs
       (map #(hash-map :id % :unit-type "普通" :name %2) mob-ids)
       (generate-units)
       (spit "a.txt")
       )

  (def boss-ids (aid/get-available-ids (count bosses) (aid/project-id-producer (:project-dir env)) :unit "n00G"))
  (->> bosses
       (map #(hash-map :id % :unit-type "BOSS" :name %2) boss-ids)
       (generate-units)
       (spit "a.txt"))

  ,)
