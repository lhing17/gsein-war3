(ns gsein-war3.cli.core
  (:require [clojure.tools.cli :as cli]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [cheshire.core :as json]
            [gsein-war3.cli.handlers :as handlers])
  (:gen-class))

(def global-options
  [["-h" "--help" "Show help"]])

(def subcommands
  {"blp-generate"
   {:desc "Generate BLP icons from an image"
    :options [["-i" "--input-file FILE" "Input image file"]
              ["-t" "--type TYPE" "Icon type: active or passive" :default "active"]
              ["-T" "--temp-dir DIR" "Temp directory for intermediate files"]
              ["-p" "--project-dir DIR" "Project directory for resource output"]]
    :handler handlers/blp-generate}

   "available-ids"
   {:desc "Get next available IDs for a given type"
    :options [["-p" "--project-dir DIR" "Project directory containing table/*.ini"]
              ["-t" "--type TYPE" "Type: ability, item, unit, hero, buff, doodad" :default "ability"]
              ["-c" "--count N" "Number of IDs to generate" :default "10"]
              ["-s" "--start-id ID" "Starting ID (optional)"]]
    :handler handlers/available-ids}

   "lni-read"
   {:desc "Read an LNI file into a map"
    :options [["-f" "--file FILE" "LNI file path"]]
    :handler handlers/lni-read}

   "lni-write"
   {:desc "Write a chunk map to an LNI file"
    :options [["-f" "--file FILE" "Output LNI file path"]
              ["-d" "--data EDN" "EDN string of the chunk map"]]
    :handler handlers/lni-write}

   "mdx-replace-blp"
   {:desc "Replace BLP texture path in an MDX file"
    :options [["-m" "--mdx-file FILE" "MDX file path"]
              ["-o" "--old-blp PATH" "Old BLP path to replace"]
              ["-n" "--new-blp PATH" "New BLP path"]]
    :handler handlers/mdx-replace-blp}

   "mdx-classify"
   {:desc "Classify and copy MDX files with their BLP dependencies"
    :options [["-s" "--source-dir DIR" "Source directory containing MDX/BLP files"]
              ["-o" "--out-dir DIR" "Output directory"]
              ["-m" "--mode MODE" "copy or move" :default "copy"]]
    :handler handlers/mdx-classify}

   "template-render"
   {:desc "Render a Selmer template with data"
    :options [["-t" "--template PATH" "Template resource path, e.g. templates/单位基础.ini"]
              ["-d" "--data EDN" "EDN map of template variables" :default "{}"]]
    :handler handlers/template-render}

   "xls-to-lni"
   {:desc "Read rows from an Excel file"
    :options [["-x" "--xls-file FILE" "Excel file path"]
              ["-s" "--sheet NAME" "Sheet name"]
              ["-c" "--columns EDN" "EDN map of column mappings, e.g. {:A :name :B :type}"]]
    :handler handlers/xls-to-lni}

   "title-generate"
   {:desc "Generate a title BLP + MDX"
    :options [["-n" "--name TEXT" "Title text"]
              ["-c" "--color COLOR" "Color: BLUE, RED, GREEN, BLACK, WHITE, or hex #RRGGBB" :default "BLUE"]
              ["-w" "--wing-file FILE" "Wing background image file (optional)"]
              ["-o" "--out-dir DIR" "Output directory"]
              ["-f" "--font-name NAME" "Font name" :default "方正颜宋简体_粗"]
              ["-z" "--font-size SIZE" "Font size" :default "40"]
              ["-m" "--template-mdx-path PATH" "Template MDX resource path (optional)"]
              ["-b" "--old-texture-path PATH" "Old texture path to replace (optional)"]]
    :handler handlers/title-generate}

   "image-split"
   {:desc "Split an image into parts (e.g. LoadingScreen)"
    :options [["-i" "--input-file FILE" "Input image file"]
              ["-o" "--out-dir DIR" "Output directory" :default "."]
              ["-n" "--names EDN" "EDN vector of output names, e.g. [\"LoadingScreenTL\" \"LoadingScreenTR\"]" :default "[]"]
              ["-f" "--format FORMAT" "Output image format: png, blp, tga" :default "blp"]]
    :handler handlers/image-split}

   "constant-replace"
   {:desc "Replace literal FourCC values with constants in JASS files"
    :options [["-i" "--input-file FILE" "JASS file to modify"]
              ["-c" "--constants-file FILE" "File containing constant definitions"]]
    :handler handlers/constant-replace}

   "text-search"
   {:desc "Search for text in files under a directory"
    :options [["-t" "--text TEXT" "Search text"]
              ["-d" "--dir DIR" "Directory to search"]]
    :handler handlers/text-search}

   "unit-place"
   {:desc "Calculate unit positions in a circle"
    :options [["-x" "--center-x X" "Center X coordinate"]
              ["-y" "--center-y Y" "Center Y coordinate"]
              ["-d" "--dist DIST" "Distance from center"]
              ["-c" "--count N" "Number of units"]]
    :handler handlers/unit-place}

   "fourcc-convert"
   {:desc "Convert between FourCC, hex, and decimal"
    :options [["-v" "--value VALUE" "Value to convert"]
              ["-m" "--mode MODE" "Conversion mode: fourcc, hex-to-fourcc, fourcc-to-decimal, fourcc-to-hex" :default "fourcc"]]
    :handler handlers/fourcc-convert}

   "general-skill-render"
   {:desc "Render a general skill template"
    :options [["-d" "--data JSON" "JSON map of template variables" :default "{}"]]
    :handler handlers/general-skill-render}

   "generate-units"
   {:desc "Batch generate units from names"
    :options [["-p" "--project-dir DIR" "Project directory"]
              ["-t" "--unit-type TYPE" "Unit type label" :default "普通"]
              ["-n" "--names EDN" "EDN vector of unit names"]]
    :handler handlers/generate-units}

   "generate-items"
   {:desc "Batch generate items from names"
    :options [["-p" "--project-dir DIR" "Project directory"]
              ["-n" "--names EDN" "EDN vector of item names"]]
    :handler handlers/generate-items}

   "generate-towers"
   {:desc "Generate tower building abilities and items"
    :options [["-p" "--project-dir DIR" "Project directory"]
              ["-l" "--lni-file FILE" "Unit LNI file path"]
              ["-i" "--tower-ids EDN" "EDN vector of tower unit IDs"]]
    :handler handlers/generate-towers}

   "generate-tasks"
   {:desc "Batch generate task items"
    :options [["-t" "--tasks EDN" "EDN vector of task maps"]]
    :handler handlers/generate-tasks}})

(defn- usage [subcommand]
  (let [{:keys [desc options]} (get subcommands subcommand)]
    (str "Usage: gsein-war3 " subcommand " [options]\n\n"
         desc "\n\nOptions:\n"
         (:summary (cli/parse-opts [] options)))))

(defn- general-usage []
  (str "gsein-war3 CLI\n\n"
       "Usage: gsein-war3 <subcommand> [options]\n\n"
       "Subcommands:\n"
       (str/join "\n"
                 (map (fn [[k v]] (str "  " k "  " (:desc v)))
                      (sort-by key subcommands)))
       "\n\n"
       "Use 'gsein-war3 <subcommand> --help' for subcommand-specific help."))

(defn -main [& args]
  (let [subcommand (first args)
        sub-args (rest args)]
    (cond
      (or (nil? subcommand) (= subcommand "--help") (= subcommand "-h"))
      (do (println (general-usage))
          (System/exit 0))

      :else
      (if-let [{:keys [options handler]} (get subcommands subcommand)]
        (let [parsed (cli/parse-opts sub-args (into global-options options))
              {:keys [options errors summary]} parsed]
          (cond
            (:help options)
            (do (println (usage subcommand))
                (System/exit 0))

            (seq errors)
            (do (println (str/join "\n" errors))
                (System/exit 1))

            :else
            (let [result (handler options)]
              (println (json/write-str result))
              (System/exit (if (= "ok" (:status result)) 0 1)))))
        (do (println (str "Unknown subcommand: " subcommand))
            (println (general-usage))
            (System/exit 1))))))
