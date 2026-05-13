(ns gsein-war3.tools.tower-generator
  (:require [selmer.parser :as sp]
            [clojure.java.io :as jio]
            [clojure.string :as str]
            [gsein-war3.lni.available-id :as aid]))

(def default-art "ReplaceableTextures\\CommandButtons\\BTNChaosWarlockGreen.blp")

(defn strip-quotes [s]
  "安全去除字符串首尾的双引号，长度不足时原样返回"
  (if (and (string? s) (> (count s) 1))
    (str/replace s #"^\"|\"$" "")
    s))

(defn- get-tower [lni-map id]
  (let [tower (get lni-map id)]
    (assoc tower :id id)))

(defn get-towers [lni-map ids]
  (->> ids
       (map #(get-tower lni-map %))))

(defn generate-tower-building-ability [towers project-dir]
  (let [aids (aid/get-available-ids (count towers) (aid/project-id-producer project-dir) :ability "A100")
        abilities (map (fn [id tower] {:id id :unit-id (:id tower) :name (strip-quotes (:name tower))}) aids towers)]
    (->> abilities
         (map #(sp/render-file (jio/resource "templates/造塔技能.ini") %))
         (str/join "\n"))))

(defn generate-tower-building-item [abilities project-dir]
  (let [aids (aid/get-available-ids (count abilities) (aid/project-id-producer project-dir) :item "I100")
        items (map (fn [id ability] {:id id :ability-id (:id ability) :name (:name ability) :art (:art ability)}) aids abilities)]
    (->> items
         (map #(sp/render-file (jio/resource "templates/造塔物品.ini") %))
         (str/join "\n"))))
