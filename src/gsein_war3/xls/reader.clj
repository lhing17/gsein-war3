(ns gsein-war3.xls.reader
  (:require [dk.ative.docjure.spreadsheet :refer :all]))

(defn xls->map [xls-file sheet-name column-map]
  (->> (load-workbook-from-file xls-file)
       (select-sheet sheet-name)
       (select-columns column-map)))

(comment
  (def xls-file "D:\\IdeaProjects\\jzjh2\\doc\\决战江湖II数值.xlsx")
  (def sheet "装备")
  (def column-map {:A  :name
                   :B  :type
                   :C  :quality
                   :D  :fetch-ways
                   :E  :knife
                   :F  :sword
                   :G  :fist
                   :H  :stick
                   :I  :hit
                   :J  :dodge
                   :K  :critical-rate
                   :L  :critical-times
                   :M  :damage-subtract
                   :N  :attack-speed
                   :O  :move-speed
                   :P  :str
                   :Q  :agi
                   :R  :int
                   :S  :wuxing
                   :T  :gengu
                   :U  :fuyuan
                   :V  :yishu
                   :W  :max-hp
                   :X  :max-mp
                   :Y  :hp-regen
                   :Z  :mp-regen
                   :AA :other1
                   :AB :other2
                   })
  (xls->map xls-file sheet column-map)


  (def xls-file "D:\\IdeaProjects\\jztd-reborn\\doc\\决战TD数值.xlsx")
  (def column-map {:A :number
                   :B :name
                   :C :level
                   :D :color
                   :E :total-name
                   :F :total-attr
                   :G :denomination
                   :H :dmg
                   :I :attack-speed
                   :J :range
                   :K :primary
                   :L :str
                   :M :agi
                   :N :int
                   :O :ability-1
                   :P :ability-2
                   :Q :denom-ability
                   :R :damage-formula
                   })

  (xls->map xls-file "英雄塔" column-map)
  )

; 名称	类型	品质	品质等级	护甲	生命值	生命回复	法力值	法力回复	护甲穿透	力量	敏捷	智力	攻击	暴击率%	暴击伤害	增伤%	冷却缩减
(def column-map
  {:A  :name
   :B  :type
   :C  :quality
   :D  :quality-level
   :E  :armor
   :F  :max-hp
   :G  :hp-regen
   :H  :max-mp
   :I  :mp-regen
   :J  :armor-penetrate
   :K  :str
   :L  :agi
   :M  :int
   :N  :attack
   :O  :critical-rate
   :P  :critical-times
   :Q  :damage-addition
   :R  :cooldown-reduce
   })

(def xls-file "D:\\IdeaProjects\\europe\\doc\\数值设计.xlsx")
(comment
  (def data (xls->map xls-file "装备" column-map))
  (def title-map (first data))

  (def item-desc
    (->> data
         (drop 1)
         (map #(select-keys % [:armor :max-hp :hp-regen :max-mp :mp-regen :armor-penetrate
                               :str :agi :int :attack :critical-rate :critical-times
                               :damage-addition :cooldown-reduce]))
         (map (fn [item] (reduce-kv (fn [s k v]
                                      (if (nil? v)
                                        s
                                        (str s
                                             (str (name (get title-map k)) "+" (int v) "\n")
                                             )))
                                    ""
                                    item)))
         ))

  )