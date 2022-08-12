(ns gsein-war3.mdx.parser
  (:import (java.io RandomAccessFile EOFException)))




(defn read-int [^RandomAccessFile raf]
  ;; 读取4个字节的整数
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
  ;; 读取len个字节的字符串
  (let [ary (byte-array len)]
    (.read raf ary)
    (loop [real-len len]
      (cond (<= real-len 0)
            ""

            (zero? (aget ary (dec real-len)))
            (recur (dec real-len))

            :else
            (String. ary 0 real-len)))))

(defn mdx-keyword [^RandomAccessFile raf]
  ;; 读取4个字节的字符串，作为关键字
  (read-str raf 4))

(defn parse-textures [^RandomAccessFile raf size]
  "解析材质文件"
  (let [start-pos (.getFilePointer raf)]
    (loop [rs []]
      (if (>= (.getFilePointer raf) (+ start-pos size))
        rs
        (recur (cons
                 {:replace-id (read-int raf)
                  :image      (read-str raf 256)
                  :unknown    (read-int raf)
                  :flags      (read-int raf)}
                 rs))))))


(defn- parse-content [^RandomAccessFile raf]
  "解析mdx文件的内容"
  (loop [kw (mdx-keyword raf) size (read-int raf) rs []]
    (cond (>= (.getFilePointer raf) (.length raf))
          rs

          (= "TEXS" kw)
          (map :image (parse-textures raf size))

          :else
          (do
            (.skipBytes raf size)
            (recur (mdx-keyword raf) (read-int raf) [])))))

(defn parse [file]
  "解析mdx文件结构，返回blp路径列表"
  (let [raf (RandomAccessFile. file, "r")
        kw (mdx-keyword raf)]
    (when-not (= "MDLX" kw)
      (throw (IllegalStateException. "Not a mdx model")))
    (try
      (parse-content raf)
      (catch Exception _ "")
      (finally (.close raf)))
    )
  )

(comment
  (def filename "D:\\IdeaProjects\\jzjh-reborn\\jzjh\\resource\\4b3.mdx")
  (def raf (RandomAccessFile. filename, "r"))
  (mdx-keyword raf)
  (parse filename),)