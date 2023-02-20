(ns gsein-war3.lni.writer
  (:require
    [gsein-war3.lni.available-id :as aid]
    [gsein-war3.lni.reader :as reader]))

(defn- write-chunk-body [chunk-body]
  ;; 将chunk的body写入文件
  (let [write-line (fn [line]
                     (str line "\n"))
        write-kv (fn [[k v]]
                   (str k " = " v "\n"))]
    (->> chunk-body
         (mapcat write-kv)
         (apply str))))

(defn- write-chunk [chunk]
  ;; 将chunk写入文件
  (let [write-id (fn [id]
                   (str "[" id "]\n"))
        write-body (fn [body]
                     (write-chunk-body body))]
    (->> chunk
         (mapcat (fn [[id body]]
                   (concat [(write-id id)] [(write-body body)])))
         (apply str))))

(defn write-lni [lni-file chunk]
  ;; 将chunk写入lni文件
  (spit lni-file (write-chunk chunk)))

(defn- update-hp [m f]
  ;; 更新hp
  (let [hp (get m "HP")]
    (if (nil? hp) m
                  (assoc m "HP" (f hp)))))

(defn- update-attr [m f k]
  ;; 更新攻击力
  (let [attr (get m k)]
    (if (nil? attr) m
                    (assoc m k (f attr)))))



(comment
  (def mobs ["h005" "u000" "h006" "e002" "o004" "u001" "n00H" "h007" "z000" "z001" "u002"
             "o005" "n02T" "e003" "n00J" "u003" "e004" "e005" "u004" "n00K" "e006" "u005"
             "h008" "h009" "n00L" "n00M" "n00N" "n00O" "n00P" "n00Q" "u006" "o008" "h00A"
             "e007" "n00S" "u007" "o009" "n00T" "n00U" "n00V" "n00W" "u009" "e008" "u00A"
             "n00X" "n02U" "n02V" "n02W" "n02X" "n02Y" "n02Z" "n030" "n031" "n032" "n033"])
  (def lni (reader/read-lni "D:\\IdeaProjects\\jztd-reborn\\jztd\\table\\unit.ini"))
  (merge lni
         (update-vals (select-keys lni mobs) #(update-hp % (fn [hp] (str (int (* 0.7 (Integer/parseInt hp))))))))
  (write-lni "test.ini" (merge lni
                               (update-vals (select-keys lni mobs) #(update-hp % (fn [hp] (str (int (* 0.7 (Integer/parseInt hp)))))))))

  (def towers [
               "o000" "o00B" "o001" "o00C" "o00D" "n000" "n011" "n017" "n001" "n012"
               "n00Y" "n014" "n00Z" "n015" "n010" "n013" "n002" "n016" "o002" "O003"
               "h001" "h00B" "h00F" "h00G" "h00C" "h00H" "n007" "n018" "n019" "n008"
               "n01A" "n01B" "n01C" "n01D" "n01E" "n01F" "n01G" "n01H" "n01K" "n01I"
               "n01J" "n01L" "n01M" "n01N" "n01O" "H004" "e000" "e00A" "e00B" "e001"
               "e00D" "e00C" "n003" "n01Q" "n01R" "n01S" "n01T" "n01U" "n01V" "n01W"
               "n01X" "n01Y" "n01Z" "n020" "n021" "n022" "n023" "n024" "n025" "n026"
               "n027" "H000" "n00A" "n028" "n029" "n02A" "n02B" "n02C" "n02D" "n02E"
               "n00C" "n02F" "n02G" "n00B" "n02H" "n02I" "n02J" "n02K" "n02L" "n02M"
               "n00E" "n02N" "n02O" "n02P" "n02Q" "n02R" "n02S" "N00G" "h00O" "h00P"
               "h00Q" "h00R" "h00S" "h00T" "h00U" "h00V" "h00W" "h00X" "h00Y" "h00Z"
               "h010" "h011" "h012" "h013" "h014" "h015" "H017" "o00S" "o00U" "o00T"
               "o00V" "o00W" "O00X" "O00Y" "o010" "o00Z"])

  (write-lni "test.ini"
             (merge lni
                    (update-vals (select-keys lni mobs)
                                 #(update-attr %
                                               (fn [dmg] (str (int (* 0.33 (Integer/parseInt dmg)))))
                                               "HP"))))



  ;; 可用人口
  (def tower-food {"O100" 1
                   "O101" 1
                   "O102" 1
                   "O109" 1
                   "O10A" 1
                   "O10B" 1
                   "O10J" 1
                   "O10K" 1
                   "O10L" 1
                   "O10U" 1
                   "O10V" 1
                   "O10W" 1
                   "O115" 1
                   "O116" 1
                   "O117" 1
                   "O103" 1
                   "O104" 1
                   "O10C" 1
                   "O10D" 1
                   "O10M" 1
                   "O10N" 1
                   "O10O" 1
                   "O10X" 1
                   "O10Y" 1
                   "O10Z" 1
                   "O118" 1
                   "O119" 1
                   "O11A" 1
                   "O11B" 1
                   "O11J" 1
                   "O105" 1
                   "O106" 1
                   "O10E" 1
                   "O10F" 1
                   "O10G" 1
                   "O10P" 1
                   "O10Q" 1
                   "O10R" 1
                   "O110" 1
                   "O111" 1
                   "O112" 1
                   "O11C" 1
                   "O11D" 1
                   "O11E" 1
                   "O11K" 1
                   "O107" 1
                   "O108" 1
                   "O10H" 1
                   "O10I" 1
                   "O10S" 1
                   "O10T" 1
                   "O113" 1
                   "O114" 1
                   "O11F" 1
                   "O11G" 1
                   "O11H" 1
                   "O11I" 1
                   "O11L" 1})
  ;(write-lni "test.ini"
  ;           (merge lni
  ;                  (update-vals (select-keys lni (keys tower-food))
  ;                               #(update-attr %
  ;                                             (fn [food] (str (get tower-food food)))
  ;                                             "fused"))))

  (write-lni
    "test.ini"
    (merge-with (fn [attr food]
                  (assoc attr "fused" food))
                lni
                tower-food))

  (def lni (reader/read-lni "D:\\IdeaProjects\\europe\\europe\\table\\item.ini"))

  (write-lni
    "test.ini"
    (merge-with (fn [attr food]
                  (assoc attr "fused" food))
                lni
                (take 22 (iterate aid/next-id "I008"))))

  (write-lni "test.ini"
             (merge lni
                    (update-vals (select-keys lni (take 22 (iterate aid/next-id "I008")))
                                 #(assoc % "HP" "101"))))

  )