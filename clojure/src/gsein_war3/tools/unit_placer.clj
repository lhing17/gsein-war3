(ns gsein-war3.tools.unit-placer
  "计算圆形排列的单位坐标和朝向角度。"
  (:require [clojure.string :as str]))

(defn unit-positions
  "计算圆形排列的单位坐标和朝向。
   参数：
     center-x — 圆心 X 坐标（游戏内坐标单位）
     center-y — 圆心 Y 坐标（游戏内坐标单位）
     dist     — 半径（游戏内坐标单位）
     n        — 单位数量（必须 > 0）
   返回：[[x y angle] ...]，其中 angle 为角度（0–360 度）。"
  [^double center-x ^double center-y ^double dist ^long n]
  {:pre [(pos-int? n)]}
  (mapv (fn [i]
          (let [angle (* i (/ (* 2 Math/PI) n))]
            [(+ center-x (* dist (Math/cos angle)))
             (+ center-y (* dist (Math/sin angle)))
             (mod (+ 180 (* i (/ 360.0 n))) 360)]))
        (range n)))

(defn unit-placer
  "返回格式化字符串列表，便于直接粘贴到编辑器中使用。
   格式：['x, y, angle', ...]，其中 x、y、angle 均已四舍五入为整数。"
  [center-x center-y dist n]
  (->> (unit-positions center-x center-y dist n)
       (map (fn [[x y angle]]
              (str/join ", " [(Math/round x) (Math/round y) (Math/round angle)])))))

(comment
  (unit-placer 1700 -4100 330 13)
  (unit-positions 1700 -4100 330 13))
