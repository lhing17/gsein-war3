(ns gsein-war3.cli.handlers
  (:require [clojure.java.io :as jio]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [cheshire.core :as json]
            [selmer.parser :as sp]
            [gsein-war3.blp.generator :as blp]
            [gsein-war3.lni.available-id :as aid]
            [gsein-war3.lni.reader :as lni-reader]
            [gsein-war3.lni.writer :as lni-writer]
            [gsein-war3.mdx.converter :as mdx-conv]
            [gsein-war3.mdx.classifier :as mdx-cls]
            [gsein-war3.tools.title-generator :as title]
            [gsein-war3.tools.image-splitter :as img-split]
            [gsein-war3.tools.constant-replacer :as const-rep]
            [gsein-war3.tools.text-searcher :as txt-search]
            [gsein-war3.tools.unit-placer :as unit-plc]
            [gsein-war3.tools.number-base-converter :as nbc]
            [gsein-war3.tools.xls-to-lni :as xls]
            [gsein-war3.tools.general-skill-generator :as gskill]
            [gsein-war3.tools.unit-generator :as unit-gen]
            [gsein-war3.tools.item-generator :as item-gen]
            [gsein-war3.tools.tower-generator :as tower-gen]
            [gsein-war3.tools.task-item-generator :as task-gen])
  (:import (java.io File)
           (java.awt Color)
           (javax.imageio ImageIO)))

(defn- ok [output]
  {:status "ok" :output output})

(defn- err [message]
  {:status "error" :message message})

(defn- parse-int [s]
  (when s (Integer/parseInt s)))

(defn- parse-edn [s]
  (when s (edn/read-string s)))

(defn- file-exists? [path]
  (when path (.exists (jio/file path))))

(defmacro defhandler
  "定义一个 handler，自动包装 try/catch 和统一返回格式"
  [name args & body]
  `(defn ~name ~args
     (try
       ~@body
       (catch Exception e#
         (err (.getMessage e#))))))

;; ---------- handlers ----------

(defhandler blp-generate [opts]
  (let [input-file (jio/file (:input-file opts))
        active-type (:type opts "active")
        temp-dir (:temp-dir opts)
        project-dir (:project-dir opts)]
    (cond
      (not (file-exists? (:input-file opts)))
      (err (str "Input file not found: " (:input-file opts)))
      (str/blank? temp-dir)
      (err "--temp-dir is required")
      (str/blank? project-dir)
      (err "--project-dir is required")
      :else
      (ok (blp/generate-blps! input-file active-type temp-dir project-dir)))))

(defhandler available-ids [opts]
  (let [project-dir (:project-dir opts)
        type (keyword (:type opts "ability"))
        n (parse-int (:count opts "10"))
        start-id (:start-id opts)]
    (cond
      (str/blank? project-dir)
      (err "--project-dir is required")
      :else
      (ok (if start-id
            (aid/get-available-ids n (aid/project-id-producer project-dir) type start-id)
            (aid/get-available-ids n (aid/project-id-producer project-dir) type))))))

(defhandler lni-read [opts]
  (let [file (:file opts)]
    (cond
      (not (file-exists? file))
      (err (str "File not found: " file))
      :else
      (ok (lni-reader/read-lni file)))))

(defhandler lni-write [opts]
  (let [file (:file opts)
        data (parse-edn (:data opts))]
    (cond
      (str/blank? file)
      (err "--file is required")
      (nil? data)
      (err "--data is required (EDN string)")
      :else
      (do (lni-writer/write-lni file data)
          (ok {:file file})))))

(defhandler mdx-replace-blp [opts]
  (let [mdx-file (:mdx-file opts)
        old-blp (:old-blp opts)
        new-blp (:new-blp opts)]
    (cond
      (not (file-exists? mdx-file))
      (err (str "MDX file not found: " mdx-file))
      (str/blank? old-blp)
      (err "--old-blp is required")
      (str/blank? new-blp)
      (err "--new-blp is required")
      :else
      (do (mdx-conv/replace-blp (jio/file mdx-file) old-blp new-blp)
          (ok {:mdx-file mdx-file :replaced old-blp :with new-blp})))))

(defhandler mdx-classify [opts]
  (let [source-dir (:source-dir opts)
        out-dir (:out-dir opts)
        mode (keyword (:mode opts "copy"))]
    (cond
      (not (file-exists? source-dir))
      (err (str "Source directory not found: " source-dir))
      (str/blank? out-dir)
      (err "--out-dir is required")
      :else
      (do (mdx-cls/classify (jio/file source-dir) (jio/file out-dir) mode)
          (ok {:source-dir source-dir :out-dir out-dir :mode mode})))))

(defhandler template-render [opts]
  (let [template (:template opts)
        data (parse-edn (:data opts "{}"))]
    (cond
      (str/blank? template)
      (err "--template is required (resource path)")
      :else
      (ok (sp/render-file (jio/resource template) data)))))

(defhandler xls-to-lni [opts]
  (let [xls-file (:xls-file opts)
        sheet (:sheet opts)
        columns (parse-edn (:columns opts "{}"))]
    (cond
      (not (file-exists? xls-file))
      (err (str "XLS file not found: " xls-file))
      (str/blank? sheet)
      (err "--sheet is required")
      :else
      (ok (xls/xls->obj xls-file sheet columns)))))

(defhandler title-generate [opts]
  (let [name (:name opts)
        color-str (:color opts "BLUE")
        wing-file (:wing-file opts)
        out-dir (:out-dir opts)
        font-name (:font-name opts "方正颜宋简体_粗")
        font-size (parse-int (:font-size opts "40"))
        template-mdx-path (:template-mdx-path opts)
        old-texture-path (:old-texture-path opts)]
    (cond
      (str/blank? name)
      (err "--name is required")
      (str/blank? out-dir)
      (err "--out-dir is required")
      :else
      (let [color (case color-str
                    "BLUE" Color/BLUE
                    "RED" Color/RED
                    "GREEN" Color/GREEN
                    "BLACK" Color/BLACK
                    "WHITE" Color/WHITE
                    (Color/decode color-str))
            wing (if wing-file (jio/file wing-file) (jio/resource "images/background/3.png"))
            kwargs (cond-> {:font-name font-name
                            :font-size font-size}
                    template-mdx-path (assoc :template-mdx-path template-mdx-path)
                    old-texture-path (assoc :old-texture-path old-texture-path))]
        (apply title/generate-title! name color wing out-dir (mapcat identity kwargs))
        (ok {:name name :out-dir out-dir})))))

(defhandler image-split [opts]
  (let [input-file (:input-file opts)
        out-dir (:out-dir opts ".")
        names (parse-edn (:names opts "[]"))
        format (:format opts "blp")]
    (cond
      (not (file-exists? input-file))
      (err (str "Input file not found: " input-file))
      :else
      (let [img (ImageIO/read (jio/file input-file))
            splitter (fn [rect]
                       (->> [rect]
                            (mapcat #(img-split/split-rectangle % :horizontal 512))
                            (mapcat #(img-split/split-rectangle % :vertical 512))))
            result (img-split/split-image (img-split/make-image [img]) splitter)]
        (img-split/write-image result out-dir names format)
        (ok {:out-dir out-dir :names names :format format})))))

(defhandler constant-replace [opts]
  (let [input-file (:input-file opts)
        constants-file (:constants-file opts)]
    (cond
      (not (file-exists? input-file))
      (err (str "Input file not found: " input-file))
      (not (file-exists? constants-file))
      (err (str "Constants file not found: " constants-file))
      :else
      (let [constants (const-rep/read-constants (jio/file constants-file))
            reverse-map (into {} (map (fn [[k v]] [v k]) constants))
            content (slurp input-file)
            result (const-rep/replace-literal-with-constant content reverse-map)]
        (spit input-file result)
        (ok {:input-file input-file :replaced (count reverse-map)})))))

(defhandler text-search [opts]
  (let [text (:text opts)
        dir (:dir opts)]
    (cond
      (str/blank? text)
      (err "--text is required")
      (not (file-exists? dir))
      (err (str "Directory not found: " dir))
      :else
      (ok (map #(.getAbsolutePath %) (txt-search/search-text text (jio/file dir)))))))

(defhandler unit-place [opts]
  (let [center-x (Double/parseDouble (:center-x opts))
        center-y (Double/parseDouble (:center-y opts))
        dist (Double/parseDouble (:dist opts))
        cnt (parse-int (:count opts))]
    (ok (unit-plc/unit-placer center-x center-y dist cnt))))

(defhandler fourcc-convert [opts]
  (let [value (:value opts)
        mode (:mode opts "fourcc")]
    (cond
      (str/blank? value)
      (err "--value is required")
      (not (#{"fourcc" "hex-to-fourcc" "fourcc-to-decimal" "fourcc-to-hex"} mode))
      (err (str "Unknown mode: " mode))
      :else
      (ok (case mode
            "fourcc" (nbc/fourcc (Long/parseLong value))
            "hex-to-fourcc" (nbc/hex-to-fourcc value)
            "fourcc-to-decimal" (nbc/fourcc-to-decimal value)
            "fourcc-to-hex" (nbc/fourcc-to-hex value))))))

;; ---------- template generators ----------

(defhandler general-skill-render [opts]
  (let [data (json/parse-string (:data opts "{}") true)]
    (cond
      (empty? data)
      (err "--data is required (JSON map)")
      :else
      (ok (sp/render-file (jio/resource "templates/通魔.ini") data)))))

(defhandler generate-units [opts]
  (let [project-dir (:project-dir opts)
        unit-type (:unit-type opts "普通")
        names (parse-edn (:names opts "[]"))]
    (cond
      (str/blank? project-dir)
      (err "--project-dir is required")
      (empty? names)
      (err "--names is required (EDN vector)")
      :else
      (let [ids (aid/get-available-ids (count names) (aid/project-id-producer project-dir) :unit)
            units (map #(hash-map :id %1 :unit-type unit-type :name %2) ids names)]
        (ok (unit-gen/generate-units units))))))

(defhandler generate-items [opts]
  (let [project-dir (:project-dir opts)
        names (parse-edn (:names opts "[]"))]
    (cond
      (str/blank? project-dir)
      (err "--project-dir is required")
      (empty? names)
      (err "--names is required (EDN vector)")
      :else
      (let [ids (aid/get-available-ids (count names) (aid/project-id-producer project-dir) :item)
            items (map #(hash-map :id %1 :name %2 :parent "prvt" :pawnable 0) ids names)]
        (ok (->> items
                 (map #(sp/render item-gen/tpl %))
                 (str/join "\n")))))))

(defhandler generate-towers [opts]
  (let [project-dir (:project-dir opts)
        lni-file (:lni-file opts)
        tower-ids (parse-edn (:tower-ids opts "[]"))]
    (cond
      (str/blank? project-dir)
      (err "--project-dir is required")
      (not (file-exists? lni-file))
      (err (str "LNI file not found: " lni-file))
      (empty? tower-ids)
      (err "--tower-ids is required (EDN vector)")
      :else
      (let [lni-map (lni-reader/read-lni lni-file)
            towers (->> (tower-gen/get-towers lni-map tower-ids)
                        (map #(assoc % :name (tower-gen/strip-quotes (get % "Name")))))
            ability-ids (aid/get-available-ids (count towers) (aid/project-id-producer project-dir) :ability "A100")
            ability-list (map (fn [id tower] {:id id :unit-id (:id tower) :name (:name tower)}) ability-ids towers)
            abilities-with-art (map (fn [a] (assoc a :art (get-in lni-map [(:unit-id a) "Art"] tower-gen/default-art))) ability-list)
            abilities (tower-gen/generate-tower-building-ability towers project-dir)
            items (tower-gen/generate-tower-building-item abilities-with-art project-dir)]
        (ok {:abilities abilities :items items})))))

(defhandler generate-tasks [opts]
  (let [tasks (parse-edn (:tasks opts "[]"))]
    (cond
      (empty? tasks)
      (err "--tasks is required (EDN vector)")
      :else
      (ok (task-gen/generate-tasks tasks)))))
