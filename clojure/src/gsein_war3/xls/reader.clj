(ns gsein-war3.xls.reader
  (:require [dk.ative.docjure.spreadsheet :refer [load-workbook-from-file select-columns select-sheet]]
            [clojure.java.io :as jio]
            [clojure.string :as str]))

(defn xls->map
  "读取 Excel 文件的指定 sheet，根据 column-map 提取列数据。
   column-map 格式为 {:A :name :B :type ...}"
  [xls-file sheet-name column-map]
  (when (str/blank? xls-file)
    (throw (IllegalArgumentException. "xls-file is required")))
  (when (str/blank? sheet-name)
    (throw (IllegalArgumentException. "sheet-name is required")))
  (when (empty? column-map)
    (throw (IllegalArgumentException. "column-map is required and must not be empty")))
  (let [file (jio/file xls-file)]
    (when-not (.exists file)
      (throw (java.io.FileNotFoundException. (str "XLS file not found: " xls-file))))
    (let [workbook (load-workbook-from-file xls-file)
          sheet (select-sheet sheet-name workbook)]
      (when (nil? sheet)
        (throw (IllegalArgumentException. (str "Sheet not found: " sheet-name))))
      (select-columns column-map sheet))))
