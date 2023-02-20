(ns gsein-war3.tools.tower-generator
  (:require [selmer.parser :as sp]
            [clojure.java.io :as jio]
            [clojure.string :as str]
            [gsein-war3.lni.available-id :as aid]
            [gsein-war3.config :as config]
            [gsein-war3.lni.reader :as lni-reader]))

(def default-art "ReplaceableTextures\\CommandButtons\\BTNChaosWarlockGreen.blp")

(def env (config/get-config))

(def tower-ids ["O100" "O101" "O102" "O103" "O104" "O105" "O106" "O107" "O108" "O109"
                "O10A" "O10B" "O10C" "O10D" "O10E" "O10F" "O10G" "O10H" "O10I" "O10J"
                "O10K" "O10L" "O10M" "O10N" "O10O" "O10P" "O10Q" "O10R" "O10S" "O10T"
                "O10U" "O10V" "O10W" "O10X" "O10Y" "O10Z" "O110" "O111" "O112" "O113"
                "O114" "O115" "O116" "O117" "O118" "O119" "O11A" "O11B" "O11C" "O11D"
                "O11E" "O11F" "O11G" "O11H" "O11I" "O11J" "O11K" "O11L"])

(defn- get-tower [lni-map id]
  (let [tower (get lni-map id)]
    (assoc tower :id id)))

(defn get-towers [lni-map ids]
  (->> ids
       distinct
       (map #(get-tower lni-map %))))

(defn generate-tower-building-ability [towers project-dir]
  (let [aids (aid/get-available-ids (count towers) (aid/project-id-producer project-dir) :ability "A100")
        abilities (map (fn [id tower] {:id id :unit-id (:id tower) :name (subs (:name tower) 1 (dec (count (:name tower))))}) aids towers)]
    (->> abilities
         (map #(sp/render-file (jio/resource "templates/造塔技能.ini") %))
         (str/join "\n"))))

(defn generate-tower-building-item [abilities project-dir]
  (let [aids (aid/get-available-ids (count abilities) (aid/project-id-producer project-dir) :item "I100")
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

  (def aids (aid/get-available-ids (count towers) (aid/project-id-producer "D:\\IdeaProjects\\jztd-reborn\\jztd") :ability "A100"))
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