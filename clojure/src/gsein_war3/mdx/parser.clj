(ns gsein-war3.mdx.parser
  (:import (java.io RandomAccessFile EOFException)))

(def ^:private chunk-keyword-size 4)
(def ^:private texture-path-len 256)

(defn read-int [^RandomAccessFile raf]
  "读取 4 个字节的小端序整数"
  (let [ch0 (.read raf)
        ch1 (.read raf)
        ch2 (.read raf)
        ch3 (.read raf)]
    (if (neg? (bit-or ch0 ch1 ch2 ch3))
      (throw (EOFException.))
      (+ (bit-shift-left ch3 24)
         (bit-shift-left ch2 16)
         (bit-shift-left ch1 8)
         ch0))))

(defn read-str [^RandomAccessFile raf len]
  "读取 len 个字节的字符串，若文件截断则抛出 EOFException"
  (let [ary (byte-array len)
        n (.read raf ary)]
    (when (or (neg? n) (< n len))
      (throw (EOFException. (str "Expected " len " bytes, got " n))))
    (loop [real-len len]
      (cond (<= real-len 0)
            ""
            (zero? (aget ary (dec real-len)))
            (recur (dec real-len))
            :else
            (String. ary 0 real-len)))))

(defn mdx-keyword [^RandomAccessFile raf]
  "读取 4 个字节的 chunk 关键字"
  (read-str raf chunk-keyword-size))

(defn parse-textures
  "解析 TEXS chunk，返回正序的 texture map 向量"
  [^RandomAccessFile raf size]
  (let [start-pos (.getFilePointer raf)]
    (loop [rs []]
      (if (>= (.getFilePointer raf) (+ start-pos size))
        rs
        (recur (conj rs
                     {:replace-id (read-int raf)
                      :image      (read-str raf texture-path-len)
                      :unknown    (read-int raf)
                      :flags      (read-int raf)}))))))

(defn- parse-content
  "解析 mdx 文件内容，返回所有 BLP 路径的向量"
  [^RandomAccessFile raf]
  (loop [kw (mdx-keyword raf) size (read-int raf) rs []]
    (cond
      (>= (.getFilePointer raf) (.length raf))
      rs

      (= "TEXS" kw)
      (recur (mdx-keyword raf)
             (read-int raf)
             (into rs (map :image) (parse-textures raf size)))

      :else
      (do
        (.skipBytes raf size)
        (recur (mdx-keyword raf) (read-int raf) rs)))))

(defn parse
  "解析 mdx 文件，返回 BLP 路径向量。异常时返回空向量。"
  [file]
  (let [raf (RandomAccessFile. file "r")]
    (try
      (let [kw (mdx-keyword raf)]
        (when-not (= "MDLX" kw)
          (throw (IllegalStateException. "Not a mdx model")))
        (parse-content raf))
      (catch Exception e
        (println "Parse error:" (.getMessage e))
        [])
      (finally
        (.close raf)))))

(comment
  (def filename "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource\\4b3.mdx")
  (parse filename)
  ,)
