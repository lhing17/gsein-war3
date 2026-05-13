(ns gsein-war3.cli.core
  (:require [clojure.tools.cli :as cli]
            [clojure.java.io :as jio]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [cheshire.core :as json]
            [gsein-war3.cli.handlers :as handlers])
  (:gen-class))

(def global-options
  [["-h" "--help" "Show help"]])

(def handler-dispatch
  {:blp-generate            handlers/blp-generate
   :available-ids           handlers/available-ids
   :lni-read                handlers/lni-read
   :lni-write               handlers/lni-write
   :mdx-replace-blp         handlers/mdx-replace-blp
   :mdx-classify            handlers/mdx-classify
   :template-render         handlers/template-render
   :xls-to-lni              handlers/xls-to-lni
   :title-generate          handlers/title-generate
   :image-split             handlers/image-split
   :constant-replace        handlers/constant-replace
   :text-search             handlers/text-search
   :unit-place              handlers/unit-place
   :fourcc-convert          handlers/fourcc-convert
   :general-skill-render    handlers/general-skill-render
   :generate-units          handlers/generate-units
   :generate-items          handlers/generate-items
   :generate-towers         handlers/generate-towers
   :generate-tasks          handlers/generate-tasks})

(defn- load-command-config
  "从资源文件加载子命令配置"
  []
  (edn/read-string (slurp (jio/resource "cli-commands.edn"))))

(defn- usage
  [command config]
  (let [{:keys [desc options]} (get config command)]
    (str "Usage: gsein-war3 " (name command) " [options]\n\n"
         desc "\n\nOptions:\n"
         (:summary (cli/parse-opts [] (into global-options options))))))

(defn- general-usage
  [config]
  (str "gsein-war3 CLI\n\n"
       "Usage: gsein-war3 <subcommand> [options]\n\n"
       "Subcommands:\n"
       (str/join "\n"
                 (map (fn [[k v]] (str "  " (name k) "  " (:desc v)))
                      (sort-by key config)))
       "\n\n"
       "Use 'gsein-war3 <subcommand> --help' for subcommand-specific help."))

(defn run-command
  "执行子命令并返回结果。纯函数，不调用 System/exit。
   返回 {:type :ok/:error/:help :output ...}"
  [command args]
  (let [config (load-command-config)]
    (if (or (nil? command) (= command "--help") (= command "-h"))
      {:type :help :output (general-usage config)}
      (let [cmd-kw (keyword command)
            {:keys [options]} (get config cmd-kw)
            handler (get handler-dispatch cmd-kw)]
        (if (nil? options)
          {:type :error :output (str "Unknown subcommand: " command "\n" (general-usage config))}
          (let [parsed (cli/parse-opts args (into global-options options))
                {:keys [options errors summary]} parsed]
            (cond
              (:help options)
              {:type :help :output (usage cmd-kw config)}

              (seq errors)
              {:type :error :output (str/join "\n" errors)}

              :else
              (try
                (let [result (handler options)]
                  {:type (if (= "ok" (:status result)) :ok :error)
                   :output (json/generate-string result)})
                (catch Exception e
                  {:type :error
                   :output (json/generate-string {:status "error" :message (.getMessage e)})})))))))))

(defn -main [& args]
  (let [command (first args)
        sub-args (rest args)
        {:keys [type output]} (run-command command sub-args)]
    (println output)
    (System/exit (case type :ok 0 :error 1 :help 0))))
