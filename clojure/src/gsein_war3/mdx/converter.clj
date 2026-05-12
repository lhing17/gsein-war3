(ns gsein-war3.mdx.converter
  (:require [clojure.java.io :as jio]
            [gsein-war3.mdx.parser :as parser])
  (:import (java.io RandomAccessFile File)
           (java.util Arrays)
           (java.nio.charset StandardCharsets)
           (org.apache.commons.io FileUtils)))

(defn write-str [^RandomAccessFile raf ^String s len]
  (.write raf (Arrays/copyOf (.getBytes s StandardCharsets/UTF_8) len)))

(defn write-int [^RandomAccessFile raf ^Integer i]
  (let [b (byte-array 4)]
    (aset b 3 (unchecked-byte (unsigned-bit-shift-right (bit-and i 0xFF000000) 24)))
    (aset b 2 (unchecked-byte (unsigned-bit-shift-right (bit-and i 0xFF0000) 16)))
    (aset b 1 (unchecked-byte (unsigned-bit-shift-right (bit-and i 0xFF00) 8)))
    (aset b 0 (unchecked-byte (bit-and i 0xFF)))
    (.write raf b)))

(defn write-textures [^RandomAccessFile wtr textures old-blp-path new-blp-path]
  (doseq [texture textures]
    (let [path (:image texture)]
      (write-int wtr (:replace-id texture))
      (write-str wtr (if (= path old-blp-path) new-blp-path path) 256)
      (write-int wtr (:unknown texture))
      (write-int wtr (:flags texture)))))


(defn replace-blp [^File mdx-file old-blp-path new-blp-path]
  (let [tmp (jio/file (str (.getAbsolutePath mdx-file) ".tmp"))
        rdr (RandomAccessFile. mdx-file "r")
        wtr (RandomAccessFile. tmp "rw")
        kw (parser/mdx-keyword rdr)]
    (when-not (= "MDLX" kw)
      (throw (IllegalStateException. "Not a mdx model")))
    (write-str wtr "MDLX" 4)
    (while (< (.getFilePointer rdr) (.length rdr))
      (let [ckw (parser/mdx-keyword rdr)
            csize (parser/read-int rdr)]
        (write-str wtr ckw 4)
        (write-int wtr csize)
        (if (= ckw "TEXS")
          (write-textures wtr (parser/parse-textures rdr csize) old-blp-path new-blp-path)
          (let [bytes (byte-array csize)]
            (.read rdr bytes)
            (.write wtr bytes )))))
    (.close wtr)
    (.close rdr)
    (FileUtils/copyFile tmp mdx-file)
    (FileUtils/delete tmp)))



(comment
  (def filename "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource\\4b3.mdx")
  (replace-blp (jio/file filename) "war3mapimported\\pikeman.blp" "war3mapimported\\pikeman2.blp")
  ,)
