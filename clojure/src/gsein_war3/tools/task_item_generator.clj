(ns gsein-war3.tools.task-item-generator
  "任务物品生成器：接收任务数据列表，渲染为 LNI 格式的物品定义。"
  (:require [selmer.parser :as sp]
            [clojure.java.io :as jio]
            [clojure.string :as str]))

(def ^:private task-template-path
  "任务物品模板资源路径。"
  "templates/任务物品.ini")

(defn generate-tasks
  "根据 tasks 数据列表渲染任务物品定义字符串。
   tasks 为 map 序列，每个 map 应包含模板所需的变量。
   返回渲染后的 LNI 格式字符串。"
  [tasks]
  {:pre [(sequential? tasks)]}
  (if-let [tpl (jio/resource task-template-path)]
    (->> tasks
         (map #(sp/render-file tpl %))
         (str/join "\n"))
    (throw (ex-info "Template not found"
                    {:template task-template-path
                     :tasks-count (count tasks)}))))

(comment
  (generate-tasks
    [{:description "测试任务描述"
      :hint "测试提示"
      :difficulty "简单"
      :award "经验+100"
      :name "测试任务"}])
  )
