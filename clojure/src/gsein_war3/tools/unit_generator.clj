(ns gsein-war3.tools.unit-generator
  "批量生成单位定义的模板渲染工具。"
  (:require [selmer.parser :as sp]
            [clojure.java.io :as jio]
            [clojure.string :as str]))

(def ^:private unit-template-path
  "单位基础模板资源路径。"
  "templates/单位基础.ini")

(defn generate-units
  "根据 units 数据列表渲染单位定义字符串。
   units 为 map 序列，每个 map 应包含模板所需的变量。
   返回渲染后的 LNI 格式字符串。"
  [units]
  {:pre [(sequential? units)]}
  (if-let [tpl (jio/resource unit-template-path)]
    (->> units
         (map #(sp/render-file tpl %))
         (str/join "\n"))
    (throw (ex-info "Template not found"
                    {:template unit-template-path
                     :units-count (count units)}))))

(comment
  (generate-units
    [{:id "n001" :unit-type "普通" :name "测试单位"}])
  )
