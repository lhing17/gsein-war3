(ns gsein-war3.mdx.converter
  (:require [clojure.java.io :as jio]
            [gsein-war3.mdx.parser :as parser])
  (:import (java.io RandomAccessFile File)
           (java.util Arrays)
           (java.nio ByteBuffer ByteOrder)
           (java.nio.charset StandardCharsets)
           (org.apache.commons.io FileUtils)))

(defn- write-str [^RandomAccessFile raf ^String s len]
  (let [bytes (.getBytes s StandardCharsets/UTF_8)]
    (when (> (alength bytes) len)
      (throw (ex-info (str "String too long (max " len " bytes): " s)
                      {:string s :byte-length (alength bytes) :max len})))
    (.write raf (Arrays/copyOf bytes len))))

(defn- write-int [^RandomAccessFile raf ^Integer i]
  (let [bb (ByteBuffer/allocate 4)]
    (.order bb ByteOrder/LITTLE_ENDIAN)
    (.putInt bb i)
    (.write raf (.array bb))))

(defn- write-textures [^RandomAccessFile wtr textures old-blp-path new-blp-path]
  (let [replaced (atom 0)]
    (doseq [texture textures]
      (let [path (:image texture)]
        (write-int wtr (:replace-id texture))
        (if (= path old-blp-path)
          (do (write-str wtr new-blp-path 256)
              (swap! replaced inc))
          (write-str wtr path 256))
        (write-int wtr (:unknown texture))
        (write-int wtr (:flags texture))))
    @replaced))

(defn replace-blp [^File mdx-file old-blp-path new-blp-path]
  "替换 MDX 文件中的 BLP 贴图路径，返回 {:status \"ok\" :replaced N}。
   使用临时文件 + 备份机制防止数据损坏。"
  (let [tmp (jio/file (System/getProperty "java.io.tmpdir")
                      (str (.getName mdx-file) ".tmp"))
        bak (jio/file (str (.getAbsolutePath mdx-file) ".bak"))
        replaced
        (let [rdr (RandomAccessFile. mdx-file "r")
              wtr (RandomAccessFile. tmp "rw")]
          (try
            (let [kw (parser/mdx-keyword rdr)]
              (when-not (= "MDLX" kw)
                (throw (IllegalStateException. "Not a mdx model")))
              (write-str wtr "MDLX" 4)
              (loop [cnt 0]
                (if (< (.getFilePointer rdr) (.length rdr))
                  (let [ckw (parser/mdx-keyword rdr)
                        csize (parser/read-int rdr)]
                    (write-str wtr ckw 4)
                    (write-int wtr csize)
                    (if (= ckw "TEXS")
                      (recur (+ cnt (write-textures wtr
                                                    (parser/parse-textures rdr csize)
                                                    old-blp-path
                                                    new-blp-path)))
                      (let [bytes (byte-array csize)]
                        (.read rdr bytes)
                        (.write wtr bytes)
                        (recur cnt))))
                  cnt)))
            (finally
              (.close wtr)
              (.close rdr))))]
    (FileUtils/copyFile mdx-file bak)
    (try
      (FileUtils/copyFile tmp mdx-file)
      (FileUtils/delete tmp)
      (FileUtils/delete bak)
      {:status "ok" :replaced replaced}
      (catch Exception e
        (FileUtils/copyFile bak mdx-file)
        (throw (ex-info "Failed to replace MDX file, restored from backup"
                        {:file mdx-file}
                        e))))))

(comment
  (def filename "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource\\4b3.mdx")
  (replace-blp (jio/file filename) "war3mapimported\\pikeman.blp" "war3mapimported\\pikeman2.blp")
  ,)
