(ns gsein-war3.tools.item-generator
  (:require [clojure.string :as str]
            [gsein-war3.lni.available-id :as aid]
            [selmer.parser :as sp]))


(def tpl "[{{id}}]\n_parent = \"prvt\"\n-- 名字\nName = \"{{name}}\"\n-- 可以被抵押\npawnable = 0\n")

(def names
  [
   "【普通】皮质战甲"
   "【普通】轻钢甲"
   "|cFF32CD99【精良】链甲胸甲|r"
   "|cFF32CD99【精良】重型铁甲|r"
   "|cFF871F78【史诗】不朽铠甲|r"
   "|cFF871F78【史诗】龙鳞战甲|r"
   "|cFFFF7F00【传说】神秘之铠|r"
   "|cFFFF7F00【传说】圣骑士铠甲|r"
   "|cFFFF0000【神话】巨神战铠|r"
   "|cFFFF0000【神话】至尊神铠|r"
   "【普通】翡翠戒指"
   "【普通】皮质护腕"
   "【普通】青铜项链"
   "【普通】石英项链"
   "【普通】贪婪戒指"
   "【普通】铁制戒指"
   "|cFF32CD99【精良】白银项链|r"
   "|cFF32CD99【精良】坚韧戒指|r"
   "|cFF32CD99【精良】蓝宝石项链|r"
   "|cFF32CD99【精良】链甲护腕|r"
   "|cFF32CD99【精良】魔法护腕|r"
   "|cFF32CD99【精良】银制戒指|r"
   "|cFF871F78【史诗】飞燕护腕|r"
   "|cFF871F78【史诗】红宝石项链|r"
   "|cFF871F78【史诗】黄金项链|r"
   "|cFF871F78【史诗】金质戒指|r"
   "|cFF871F78【史诗】龙鳞护腕|r"
   "|cFF871F78【史诗】勇气戒指|r"
   "|cFFFF7F00【传说】幻影护腕|r"
   "|cFFFF7F00【传说】力量之戒|r"
   "|cFFFF7F00【传说】闪电之戒|r"
   "|cFFFF7F00【传说】神秘项链|r"
   "|cFFFF7F00【传说】无尽潜力|r"
   "|cFFFF7F00【传说】血之祭品|r"
   "|cFFFF0000【神话】风暴护腕|r"
   "|cFFFF0000【神话】黑暗项链|r"
   "|cFFFF0000【神话】灵魂护符|r"
   "|cFFFF0000【神话】天使之翼|r"
   "|cFFFF0000【神话】永恒之链|r"
   "|cFFFF0000【神话】宇宙之心|r"
   "【普通】旅行鞋"
   "【普通】皮靴"
   "|cFF32CD99【精良】神行靴|r"
   "|cFF32CD99【精良】锁子甲靴|r"
   "|cFF871F78【史诗】疾行之靴|r"
   "|cFF871F78【史诗】龙皮靴|r"
   "|cFFFF7F00【传说】飞天之靴|r"
   "|cFFFF7F00【传说】闪电步履|r"
   "|cFFFF0000【神话】飞越天际|r"
   "|cFFFF0000【神话】流星追击|r"
   ])

(comment
  (def ids (aid/get-available-ids (count names) (aid/project-id-producer "D:/IdeaProjects/europe/europe") :item "I000"))
  (spit "a.txt" (str/join "\n"(map #(sp/render tpl {:id % :name %2}) ids names)))
  )