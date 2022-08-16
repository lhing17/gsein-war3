(ns gsein-war3.tools.xls-to-lni
  (:require [dk.ative.docjure.spreadsheet :refer :all]
            [selmer.parser :as sp]
            [clojure.java.io :as jio]
            [gsein-war3.lni.available-id :as aid]
            [gsein-war3.config :as config]
            [clojure.string :as str]
            [flatland.ordered.map :as fom]))

(def env (config/get-config))

(defn xls->obj [xls-file sheet-name column-map]
  (->> (load-workbook-from-file xls-file)
       (select-sheet sheet-name)
       (select-columns column-map)))

(defn get-base-attrs [obj attr-map]
  (->> (map (fn [kv]
              (let [v (get obj (first kv))
                    nm (second kv)]
                (if v (str nm "+" (if (== v (int v)) (int v) v) "|n") nil)
                )) attr-map)
       (filter identity)
       (str/join)))


(comment
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
                   :AC :mp-regen

                   })
  (def objs (xls->obj xls-file sn column-map))
  (def add-hps (->> (map :hp objs)
                    (filter identity)
                    (drop 2)
                    (distinct)
                    (map int)
                    (sort)))

  (sp/render-file (jio/resource "templates/加生命值物品技能.ini") {:id "A001" :add 200})
  (def ids (aid/get-available-ids (count add-hps) (aid/project-id-producer (:project-dir env)) :ability))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/加生命值物品技能.ini") {:id % :add %2}) ids add-hps)))

  (def add-defs (->> (map :def objs)
                     (filter identity)
                     (drop 2)
                     (distinct)
                     (map int)
                     (sort)))
  (def ids (aid/get-available-ids (count add-defs) (aid/project-id-producer (:project-dir env)) :ability))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/加防御力物品技能.ini") {:id % :add %2}) ids add-defs)))

  (def add-attack-speeds (->> (map :attack-speed objs)
                              (filter identity)
                              (drop 2)
                              (distinct)
                              (map int)
                              (sort)))
  (def ids (aid/get-available-ids (count add-attack-speeds) (aid/project-id-producer (:project-dir env)) :ability))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/加攻击速度物品技能.ini") {:id % :add (/ %2 100.0)}) ids add-attack-speeds)))

  (def add-move-speeds (->> (map :move-speed objs)
                            (filter identity)
                            (drop 2)
                            (distinct)
                            (map int)
                            (sort)))
  (def ids (aid/get-available-ids (count add-move-speeds) (aid/project-id-producer (:project-dir env)) :ability))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/加移动速度物品技能.ini") {:id % :add %2}) ids add-move-speeds)))
  (def obj (nth objs 3))
  (def attr-map (fom/ordered-map :intellect "悟性"
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
  (def ids (aid/get-available-ids (count attrs) (aid/project-id-producer (:project-dir env)) :item "I200"))
  (spit "a.txt" (str/join "\n" (map #(sp/render-file (jio/resource "templates/装备物品.ini") {:id % :art "" :basic-attr (second %2) :name (first %2) :skill ""}) ids attrs)))
  ,)
