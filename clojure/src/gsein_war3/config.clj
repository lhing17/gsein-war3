(ns gsein-war3.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as jio])
  (:import (java.io PushbackReader)))

(def ^:private default-config
  "默认配置，当 config.edn 不存在或读取失败时作为兜底。"
  {})

(defn get-config
  "读取 classpath 下的 config.edn。
   若文件不存在或格式错误，抛出带有友好提示的异常。"
  []
  (if-let [res (jio/resource "config.edn")]
    (try
      (with-open [r (PushbackReader. (jio/reader res))]
        (edn/read r))
      (catch Exception e
        (throw (ex-info "Failed to read config.edn: EDN syntax error or IO issue"
                        {:resource res}
                        e))))
    (throw (ex-info "config.edn not found on classpath"
                    {:hint "Create resources/config.edn or use get-config-or-default"}))))

(defn get-config-or-default
  "读取 classpath 下的 config.edn；若不存在或格式错误则返回默认空配置。
   适合测试环境或配置可选的场景。"
  []
  (try (get-config)
       (catch Exception _
         default-config)))

(comment
  (get-config)
  (get-config-or-default)
  ,)
