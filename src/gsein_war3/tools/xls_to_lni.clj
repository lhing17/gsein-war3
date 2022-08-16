(ns gsein-war3.tools.xls-to-lni
  (:require [dk.ative.docjure.spreadsheet :refer :all]
            [selmer.parser :as sp]
            [clojure.java.io :as jio]
            [gsein-war3.lni.available-id :as aid]
            [gsein-war3.config :as config]
            [clojure.string :as str]))

(def env (config/get-config))

(defn xls->obj [xls-file sheet-name column-map]
  (->> (load-workbook-from-file xls-file)
       (select-sheet sheet-name)
       (select-columns column-map)))


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
  ,)
