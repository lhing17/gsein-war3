(ns gsein-war3.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as jio])
  (:import (java.io PushbackReader)))

(defn get-config []
  (-> (jio/resource "config.edn")
      (jio/reader)
      (PushbackReader.)
      (edn/read)))

(comment
  (get-config)
  ,)
