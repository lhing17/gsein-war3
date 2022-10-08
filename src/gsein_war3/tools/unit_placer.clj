(ns gsein-war3.tools.unit-placer
  (:require [clojure.string :as str]))

(defn unit-placer [center-x center-y dist cnt]
  (->> (range cnt)
       (map (fn [i]
             (let [angle (* i (/ (* 2 Math/PI) cnt))]
               [(+ center-x (* dist (Math/cos angle)))
                (+ center-y (* dist (Math/sin angle)))
                (mod (+ 180 (* i (/ 360 cnt))) 360)])))
       (map (fn [[x y angle]]
             (let [x (Math/round x)
                   y (Math/round y)]
               (str/join ", " [x y angle]))))))


(comment
  (unit-placer 1700 -4100 300 9)

  ,)

