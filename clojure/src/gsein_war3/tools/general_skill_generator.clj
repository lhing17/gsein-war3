(ns gsein-war3.tools.general-skill-generator
  "通魔技能生成器配置入口。
   数据来源于 resources/general-skill-config.edn，支持热加载。"
  (:require [clojure.java.io :as jio]
            [clojure.edn :as edn]))

(defn load-config
  "从 classpath 加载 general-skill-config.edn"
  []
  (some-> (jio/resource "general-skill-config.edn")
          slurp
          edn/read-string))

(def ^:private cached-config (memoize load-config))

(defn orders
  "War3 订单字符串列表"
  [] (:orders (cached-config)))

(defn multi-val-attr
  "支持多值赋值的属性列表"
  [] (:multi-val-attr (cached-config)))

(defn target-type-const
  "目标类型常量：0-无目标 1-单位目标 2-点目标 3-单位或点目标"
  [] (:target-type-const (cached-config)))

(defn ability-flag-const
  "通魔选项常量：1-图标可见 2-目标选取图像 4-物理魔法 8-通用魔法 16-单独施放"
  [] (:ability-flag-const (cached-config)))

(defn default-values
  "技能默认值"
  [] (:default-values (cached-config)))

(defn meta-info
  "配置元数据（来源、版本等）"
  [] (:meta (cached-config)))

(defn valid-target-type?
  "检查目标类型是否合法"
  [v]
  (contains? (set (target-type-const)) v))

(defn valid-flag?
  "检查能力标志是否合法"
  [v]
  (contains? (set (ability-flag-const)) v))
