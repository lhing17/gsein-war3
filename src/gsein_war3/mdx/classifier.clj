(ns gsein-war3.mdx.classifier
  (:require [gsein-war3.config :as config]
            [gsein-war3.mdx.parser :as parser]
            [clojure.string :as str]
            [clojure.java.io :as jio]
            [gsein-war3.lni.reader :as lni-reader]
            [clojure.set :as set])
  (:import (java.io File)
           (org.apache.commons.io FileUtils)))


(def env (config/get-config))

(defn- get-mdx-files [^File root]
  "获取指定文件夹下所有的mdx文件（含子文件夹下的文件）"
  (->> (file-seq root)
       (filterv #(str/ends-with? (.getName %) ".mdx"))
       ))

(defn classify
  ([^File root mdx-filter ^File out-dir mode]
   "分类mdx文件"
   (let [mdx-files (get-mdx-files root)
         filtered-mdx-files (filterv mdx-filter mdx-files)]
     (doseq [mdx-file filtered-mdx-files]
       (let [file-name (.getName mdx-file)
             out-mdx-file (jio/file out-dir (str/replace file-name ".mdx" "") file-name)
             blp-files (filter (complement str/blank?) (parser/parse mdx-file))
             source-blp-files (map #(jio/file root %) blp-files)
             out-blp-files (map #(jio/file out-dir (str/replace file-name ".mdx" "") %) blp-files)
             ]

         (try (FileUtils/copyFile mdx-file out-mdx-file)
              (doseq [[blp-file out-blp-file] (map list source-blp-files out-blp-files)]
                (when (.exists blp-file)
                  ;(println (.getName mdx-file) (.getName blp-file))
                  (FileUtils/copyFile blp-file out-blp-file)))
              (catch Exception e (.printStackTrace e)))
         (when (not= mode :copy)
           (FileUtils/delete mdx-file)
           (doseq [blp-file source-blp-files]
             (FileUtils/delete (jio/file blp-file))))
         ))))

  ([^File root ^File out-dir mode]
   (classify root (constantly true) out-dir mode))

  ([^File root ^File out-dir]
   (classify root (constantly true) out-dir :copy)))






(comment
  (def mdx-files (get-mdx-files (jio/file "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource")))
  (classify (jio/file "E:\\War3Map\\拆地图\\命运之轮\\命运之轮")
            (jio/file "E:\\War3Map\\拆地图\\命运之轮\\out"))
  (doseq [mdx-file mdx-files]
    (println mdx-file)
    (println (parser/parse mdx-file))
    )

  (def mdx-set (->> (lni-reader/read-lni "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\table\\doodad.ini")
                    (map val)
                    (map #(get % "file"))
                    (filter identity)
                    (map #(read-string %))
                    (distinct)
                    (sort)
                    (map #(str "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource\\" %))
                    (map #(str/replace % ".mdl" ".mdx"))
                    (set)
                    ))

  (def mdx-set2 (->> (take 21 (iterate inc 1))
                     (map #(str "war3mapImported\\shumu (" % ").mdx"))
                     (map #(str "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource\\" %))
                     ))

  (def mdx-set3 (->> ["war3mapImported\\shanmoxing.mdx"
                      "war3mapImported\\freezingfield.mdx"
                      "war3mapImported\\chaosrunicaura.mdx"]
                     (map #(str "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource\\" %))))

  (def current-mdx-set (set (map #(.getAbsolutePath %) mdx-files)))

  (set/intersection mdx-set current-mdx-set)



  (def filtered-mdx-files (filterv #(some (fn [x] (= x (.getAbsolutePath %))) mdx-set) mdx-files))

  (classify (jio/file "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource")
            #(some (fn [x] (= x (.getAbsolutePath %))) mdx-set3)
            (jio/file "D:\\IdeaProjects\\jzjh2\\out")
            :copy)

  (def root "E:\\War3Map\\拆地图\\命运之轮\\out")
  (def subdirectories (.listFiles (jio/file root)))



  (doseq [d subdirectories]
    (doseq [f (.listFiles d)]
      (FileUtils/copyToDirectory f (jio/file "E:\\War3Map\\拆地图\\命运之轮\\out2"))))

  ,)