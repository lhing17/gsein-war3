(ns gsein-war3.tools.tower-generator
  (:require [selmer.parser :as sp]
            [clojure.java.io :as jio]
            [clojure.string :as str]
            [gsein-war3.lni.available-id :as aid]
            [gsein-war3.config :as config]
            [gsein-war3.lni.reader :as lni-reader]))

(def default-art "ReplaceableTextures\\CommandButtons\\BTNChaosWarlockGreen.blp")

(def env (config/get-config))

(def tower-ids ["o000" "o00B" "o001" "o00C" "o00D" "n000" "n011" "n017" "n001" "n012" "n00Y"
                "n00Y" "n014" "n014" "n00Z" "n00Z" "n015" "n015" "n010" "n013" "n002" "n002"
                "n016" "n016" "o002" "o002" "o002" "O003" "O003" "h001" "h00B" "h00F" "h00G"
                "h00C" "h00H" "n007" "n018" "n018" "n019" "n019" "n008" "n01A" "n01A" "n01B"
                "n01B" "n01C" "n01D" "n01E" "n01E" "n01F" "n01G" "n01H" "n01H" "n01K" "n01I"
                "n01J" "n01J" "n01L" "n01M" "n01N" "n01N" "n01N" "n01O" "n01O" "n01O" "H004"
                "H004" "H004" "e000" "e000" "e00A" "e00A" "e00B" "e00B" "e001" "e001" "e00D"
                "e00D" "e00C" "e00C" "n003" "n003" "n01Q" "n01Q" "n01R" "n01R" "n01S" "n01S"
                "n01T" "n01T" "n01U" "n01U" "n01V" "n01V" "n01W" "n01W" "n01X" "n01X" "n01Y"
                "n01Y" "n01Z" "n01Z" "n020" "n020" "n021" "n021" "n022" "n022" "n023" "n023"
                "n024" "n024" "n025" "n025" "n026" "n026" "n027" "n027" "n027" "H000" "H000"
                "H000" "n00A" "n028" "n029" "n02A" "n02B" "n02C" "n02D" "n02E" "n00C" "n02F"
                "n02G" "n00B" "n00B" "n02H" "n02H" "n02I" "n02I" "n02J" "n02J" "n02K" "n02K"
                "n02L" "n02L" "n02M" "n02M" "n00E" "n00E" "n02N" "n02N" "n02O" "n02O" "n02P"
                "n02Q" "n02Q" "n02R" "n02R" "n02S" "n02S" "n02S" "N00G" "N00G" "N00G" "N00G"
                "h00O" "h00P" "h00Q" "h00R" "h00S" "h00T" "h00U" "h00V" "h00V" "h00W" "h00W"
                "h00X" "h00X" "h00Y" "h00Z" "h010" "h011" "h011" "h012" "h013" "h014" "h015"
                "h015" "H017" "H017" "H017" "o00U" "o00T" "o00V" "o00W" "O00X"])

(defn- get-tower [lni-map id]
  (let [tower (get lni-map id)]
    (assoc tower :id id)))

(defn get-towers [lni-map ids]
  (->> ids
       distinct
       (map #(get-tower lni-map %))))

(defn generate-tower-building-ability [towers project-dir]
  (let [aids (aid/get-available-ids (count towers) (aid/project-id-producer project-dir) :ability "A000")
        abilities (map (fn [id tower] {:id id :unit-id (:id tower) :name (subs (:name tower) 1 (dec (count (:name tower))))}) aids towers)]
    (->> abilities
         (map #(sp/render-file (jio/resource "templates/造塔技能.ini") %))
         (str/join "\n"))))

(defn generate-tower-building-item [abilities project-dir]
  (let [aids (aid/get-available-ids (count abilities) (aid/project-id-producer project-dir) :item "I000")
        items (map (fn [id ability] {:id id :ability-id (:id ability) :name (:name ability) :art (:art ability)}) aids abilities)]
    (->> items
         (map #(sp/render-file (jio/resource "templates/造塔物品.ini") %))
         (str/join "\n"))))


(comment
  (def lni-map (lni-reader/read-lni "D:\\IdeaProjects\\jztd-reborn\\jztd\\table\\unit.ini"))
  (def towers (->> (get-towers lni-map tower-ids)
                   (map (fn [tower] {:id (:id tower) :name (get tower "Name")}))))
  (map :name towers)
  (distinct tower-ids)
  (->> (generate-tower-building-ability towers "D:\\IdeaProjects\\jztd-reborn\\jztd")
       (spit "a.ini"))

  (def aids (aid/get-available-ids (count towers) (aid/project-id-producer "D:\\IdeaProjects\\jztd-reborn\\jztd") :ability "A000"))
  (def abilities (map (fn [id tower] {:id id :unit-id (:id tower) :name (subs (:name tower) 1 (dec (count (:name tower))))}) aids towers))

  (def abilities-with-art
    (map (fn [ability] (assoc ability :art (get-in lni-map [(:unit-id ability) "Art"] default-art))) abilities))

  (->> (generate-tower-building-item abilities-with-art "D:\\IdeaProjects\\jztd-reborn\\jztd")
       (spit "b.ini"))

  (def item-map (lni-reader/read-lni "D:\\IdeaProjects\\jztd-reborn\\jztd\\table\\item.ini"))

  (def item-abilities (-> item-map
                           (update-vals (fn [item] (get item "abilList")))))

  (def ability-unit (->> abilities
                         (map (fn [ability] [(:id ability) (:unit-id ability)]))
                         (into {})
                         ))

  (->> item-abilities
       (filter (fn [[k v]] (identity v)))
       (filter (fn [[k v]] (= 6 (count v))))
       (map (fn [[k v]] [k (subs v 1 (dec (count v)))]))
        (map (fn [[k v]] [k (get ability-unit v)]))
       (filter (fn [[k v]] (identity v)))
       (map #(str "call SaveInteger(NHT, '" (second %) "', 0, '" (first %) "')"))
       (str/join "\n")
       (print)
       )

  (->> item-abilities
       (filter (fn [[k v]] (identity v)))
       (filter (fn [[k v]] (= 6 (count v))))
       (map (fn [[k v]] [k (subs v 1 (dec (count v)))]))
       (map (fn [[k v]] [k (get ability-unit v)]))
       (filter (fn [[k v]] (identity v)))
       (map (fn [[k v]] [v k]))
       (into {})
       (#(map % (distinct tower-ids)))
       )

  )