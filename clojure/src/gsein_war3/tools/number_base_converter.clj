(ns gsein-war3.tools.number-base-converter
  (:require [clojure.string :as str]
            [clojure.java.io :as jio]))

(def ^:private fourcc-max-value 4294967295)
(def ^:private fourcc-mod 4294967296)
(def ^:private byte-mask 0xFF)
(def ^:private shift-24 16777216)
(def ^:private shift-16 65536)
(def ^:private shift-8 256)

(defn fourcc [n]
  "将非负整数转为 4 字符 FourCC 字符串。"
  {:pre [(integer? n) (>= n 0)]}
  (let [n (mod n fourcc-mod)]
    (str (char (mod (quot n shift-24) shift-8))
         (char (mod (quot n shift-16) shift-8))
         (char (mod (quot n shift-8) shift-8))
         (char (mod n shift-8)))))

(defn hex-to-fourcc [hex]
  "将 8 位十六进制字符串转为 FourCC。"
  {:pre [(string? hex) (= 8 (count hex)) (re-matches #"[0-9a-fA-F]{8}" hex)]}
  (fourcc (Long/parseLong hex 16)))

(defn fourcc-to-decimal [s]
  "将 FourCC 字符串转为十进制整数。"
  {:pre [(string? s) (= 4 (count s))]}
  (bit-or (bit-shift-left (int (nth s 0)) 24)
          (bit-shift-left (int (nth s 1)) 16)
          (bit-shift-left (int (nth s 2)) 8)
          (int (nth s 3))))

(defn fourcc-to-hex [s]
  "将 FourCC 字符串转为 8 位十六进制小写字符串。"
  (format "%08x" (fourcc-to-decimal s)))

(defn valid-fourcc-decimal? [s]
  "检查字符串是否为合法的 FourCC 十进制表示（0–4294967295）。"
  (try
    (let [n (Long/parseLong s)]
      (and (<= 0 n fourcc-max-value)))
    (catch Exception _
      false)))

(defn replace-hex-with-fourcc [s]
  "将字符串中的 $ 开头的 8 位十六进制替换为 FourCC（带引号）。
   注意：当前实现不排除字符串常量内的匹配。"
  (let [hex-pattern #"(?<![A-Za-z0-9_\"])(\$[0-9a-fA-F]{8})(?![A-Za-z0-9_\"])"
        hex-replacement (fn [m]
                          (let [hex (subs (first m) 1)]
                            (try
                              (str "'" (hex-to-fourcc hex) "'")
                              (catch Exception _ (first m)))))]
    (str/replace s hex-pattern hex-replacement)))

(defn replace-decimal-with-fourcc [s]
  "将字符串中的 7–10 位十进制数（且在合法 FourCC 范围内）替换为 FourCC（带引号）。"
  (let [decimal-pattern #"\b(\d{7,10})\b"
        decimal-replacement (fn [m]
                              (let [decimal (first m)]
                                (if (valid-fourcc-decimal? decimal)
                                  (try
                                    (str "'" (fourcc (Long/parseLong decimal)) "'")
                                    (catch Exception _ decimal))
                                  decimal)))]
    (str/replace s decimal-pattern decimal-replacement)))

(comment
  (fourcc 1852207212)
  (hex-to-fourcc "49303042")
  (fourcc-to-decimal "I00B")
  (fourcc-to-hex "I00B")
  (replace-hex-with-fourcc "AA$49303042 BB$49303043CC")
  (replace-decimal-with-fourcc "1852207212")
  (replace-decimal-with-fourcc "set x = 1000000000")
  )
