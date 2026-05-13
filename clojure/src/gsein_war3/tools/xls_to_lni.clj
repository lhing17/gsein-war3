(ns gsein-war3.tools.xls-to-lni
  "将 Excel 数值设计表转换为 LNI 格式装备/技能定义的工具。"
  (:require [selmer.parser :as sp]
            [clojure.java.io :as jio]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [gsein-war3.lni.available-id :as aid]
            [gsein-war3.util.pinyin :as pinyin]
            [flatland.ordered.map :as fom]
            [gsein-war3.xls.reader :as xls-reader]))

(defn- normalize-number
  "若浮点数 v 实际上为整数，则转为 int；否则保留原值。"
  [v]
  (if (and (number? v) (== v (int v)))
    (int v)
    v))

(defn get-base-attrs
  "根据 attr-map 从 obj 中提取基础属性，拼接为 LNI 格式的增益字符串。
   attr-map 为 {:obj-key \"显示名称\" ...} 的有序映射。"
  [obj attr-map]
  (->> (map (fn [[k display-name]]
              (when-let [v (get obj k)]
                (str display-name "+" (normalize-number v) "|n")))
            attr-map)
       (filter identity)
       (str/join)))

(defn add-id-to-objs
  "为 objs 中的每个对象按顺序分配递增 ID（从 start-id 开始）。"
  [start-id objs]
  (let [ids (take (count objs) (iterate aid/next-id start-id))]
    (map #(assoc % :id %2) objs ids)))

(defn- load-balance-ids
  "从 classpath 加载 resources/balance-ids.edn。"
  []
  (some-> (jio/resource "balance-ids.edn")
          slurp
          edn/read-string))

(def ^:private cached-balance-ids (memoize load-balance-ids))

(def ^:dynamic *default-hp-map*
  "默认生命值→技能 ID 映射。可通过 binding 覆盖。"
  (:hp (cached-balance-ids)))

(def ^:dynamic *default-def-map*
  "默认防御力→技能 ID 映射。可通过 binding 覆盖。"
  (:def (cached-balance-ids)))

(def ^:dynamic *default-as-map*
  "默认攻击速度→技能 ID 映射。可通过 binding 覆盖。"
  (:as (cached-balance-ids)))

(def ^:dynamic *default-ms-map*
  "默认移动速度→技能 ID 映射。可通过 binding 覆盖。"
  (:ms (cached-balance-ids)))

(defn- build-ability
  "从 value-map 中查找 v 对应的技能 ID。v 会被转为 int 后查找。
   若 v 为 nil 或找不到，返回空字符串。"
  ([v value-map]
   (if v
     (get value-map (int v) "")
     "")))

(defn build-ability-list
  "根据 obj 中的 hp/def/attack-speed/move-speed 构建技能 ID 列表，以逗号分隔。
   支持传入自定义映射表，默认使用 *default-*-map*。"
  ([obj]
   (build-ability-list obj *default-hp-map* *default-def-map* *default-as-map* *default-ms-map*))
  ([obj hp-map def-map as-map ms-map]
   (->> [(build-ability (:hp obj) hp-map)
         (build-ability (:def obj) def-map)
         (build-ability (:attack-speed obj) as-map)
         (build-ability (:move-speed obj) ms-map)]
        (filter (complement str/blank?))
        (str/join ","))))

(comment
  (def project-dir "D:/IdeaProjects/small/small")
  (def xls-file (clojure.java.io/file "D:\\IdeaProjects\\small\\doc\\数值设计.xlsx"))
  (def sn "装备")
  (def column-map {:A  :name
                   :B  :type
                   :D  :level
                   :E  :rarity
                   :H  :intellect
                   :I  :strength
                   :J  :luck
                   :K  :medical
                   :L  :str
                   :M  :agi
                   :N  :int
                   :O  :hp
                   :P  :hp-regen
                   :Q  :hp-regen-percent
                   :R  :def
                   :S  :dodge
                   :T  :kill-regen
                   :U  :damage-absorb
                   :V  :critical-rate
                   :W  :critical-damage
                   :X  :understanding
                   :Y  :attack-speed
                   :Z  :damage-addition
                   :AA :move-speed
                   :AB :mp-max
                   :AC :mp-regen})
  (def objs (xls-reader/xls->map xls-file sn column-map))
  (def add-hps (->> (map :hp objs)
                    (filter identity)
                    (drop 2)
                    (distinct)
                    (map int)
                    (sort)))

  (def hp-map (zipmap add-hps (iterate aid/next-id "A00J")))

  (sp/render-file (jio/resource "templates/加生命值物品技能.ini") {:id "A001" :add 200})
  (def ids (aid/get-available-ids (count add-hps) (aid/project-id-producer project-dir) :ability))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/加生命值物品技能.ini") {:id % :add %2}) ids add-hps)))

  (def add-defs (->> (map :def objs)
                     (filter identity)
                     (drop 2)
                     (distinct)
                     (map int)
                     (sort)))
  (zipmap add-defs (iterate aid/next-id "A00W"))


  (def ids (aid/get-available-ids (count add-defs) (aid/project-id-producer project-dir) :ability))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/加防御力物品技能.ini") {:id % :add %2}) ids add-defs)))

  (def add-attack-speeds (->> (map :attack-speed objs)
                              (filter identity)
                              (drop 2)
                              (distinct)
                              (map int)
                              (sort)))
  (zipmap add-attack-speeds (iterate aid/next-id "A01D"))


  (def ids (aid/get-available-ids (count add-attack-speeds) (aid/project-id-producer project-dir) :ability))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/加攻击速度物品技能.ini") {:id % :add (/ %2 100.0)}) ids add-attack-speeds)))

  (def add-move-speeds (->> (map :move-speed objs)
                            (filter identity)
                            (drop 2)
                            (distinct)
                            (map int)
                            (sort)))
  (zipmap add-move-speeds (iterate aid/next-id "A01J"))

  (def ids (aid/get-available-ids (count add-move-speeds) (aid/project-id-producer project-dir) :ability))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/加移动速度物品技能.ini") {:id % :add %2}) ids add-move-speeds)))
  (def obj (nth objs 3))
  (def attr-map (fom/ordered-map
                  :intellect "悟性"
                  :strength "根骨"
                  :luck "福缘"
                  :medical "医术"
                  :str "招式"
                  :agi "身法"
                  :int "内力"
                  :hp "生命值"
                  :hp-regen "生命回复"
                  :hp-regen-percent "百分比生命回复"
                  :def "防御"
                  :dodge "闪避"
                  :kill-regen "杀怪回复"
                  :damage-absorb "伤害吸收"
                  :critical-rate "暴击率"
                  :critical-damage "暴击伤害"
                  :understanding "绝学领悟"
                  :attack-speed "攻击速度"
                  :damage-addition "伤害加成"
                  :move-speed "移动速度"
                  :mp-max "法力上限"
                  :mp-regen "法力回复"))
  (def attrs (map #(vector (:name %) (get-base-attrs % attr-map)) (drop 2 objs)))
  (def ids (aid/get-available-ids (count attrs) (aid/project-id-producer project-dir) :item "I200"))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/装备物品.ini") {:id % :art "" :basic-attr (second %2) :name (first %2) :skill ""}) ids attrs)))

  (def id-objs (->> objs
                    (drop 2)
                    (add-id-to-objs "I200")))
  (def full-objs (map #(assoc % :basic-attr (get-base-attrs % attr-map)) (map #(assoc % :ability-list %2) id-objs (map build-ability-list id-objs))))
  (def full-objs-with-pinyin (map #(assoc % :pinyin (pinyin/get-pinyin-name (:name %))) full-objs))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/装备物品.ini") %) full-objs-with-pinyin)))
  )
