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