(ns gsein-war3.tools.task-item-generator
  (:require [selmer.parser :as sp]
            [clojure.java.io :as jio]
            [clojure.string :as str]))

(defn generate-tasks [tasks]
  (->> tasks
       (map #(sp/render-file (jio/resource "templates/任务物品.ini") %))
       (str/join "\n")))

(comment
  (def tasks [{:description "天气转凉了，天地会的英雄们还没有过冬的衣服穿，朋友请帮我打些狼皮和野猪皮吧，我要用来做过冬的衣服"
               :hint "杀掉5只野狼和5只野猪"
               :difficulty "简单，游戏开局时即可完成"
               :award "江湖经验+1000、声望+50、"
               :name "新手任务"}])

  ,)
