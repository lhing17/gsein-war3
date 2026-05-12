(ns gsein-war3.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as jio])
  (:import (java.io PushbackReader)))

(defn get-config
  "读取 classpath 下的 config.edn。仅供 REPL 交互与向后兼容使用，
  生产代码中应通过函数参数显式传入配置。"
  []
  (-> (jio/resource "config.edn")
      (jio/reader)
      (PushbackReader.)
      (edn/read)))

(comment
  (get-config)
  ,)
