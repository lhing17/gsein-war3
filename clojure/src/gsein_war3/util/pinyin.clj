(ns gsein-war3.util.pinyin
  "中文字符转拼音工具。
   依赖 pinyin4j 库，输出无音调、v 代替 ü 的拼音字符串。"
  (:require [clojure.string :as str])
  (:import (net.sourceforge.pinyin4j PinyinHelper)
           (net.sourceforge.pinyin4j.format HanyuPinyinVCharType HanyuPinyinToneType HanyuPinyinOutputFormat)
           (net.sourceforge.pinyin4j.format.exception BadHanyuPinyinOutputFormatCombination)))

(def ^:private ^HanyuPinyinOutputFormat default-format
  "默认拼音输出格式：无音调、v 代替 ü。"
  (doto (HanyuPinyinOutputFormat.)
    (.setVCharType HanyuPinyinVCharType/WITH_V)
    (.setToneType HanyuPinyinToneType/WITHOUT_TONE)))

(def ^:private preserve-non-chinese
  "PinyinHelper/toHanYuPinyinString 的参数：true 表示保留非汉字字符原样。"
  true)

(defn get-pinyin-name
  "将中文字符串转为拼音字符串（全拼，无音调，v 代替 ü）。
   非中文字符（如英文、数字、标点）会被原样保留。
   输入 nil 或空字符串时返回 nil。"
  [name]
  {:pre [(or (nil? name) (string? name))]}
  (when (seq name)
    (try
      (PinyinHelper/toHanYuPinyinString name default-format "" preserve-non-chinese)
      (catch BadHanyuPinyinOutputFormatCombination e
        (throw (ex-info "Pinyin conversion failed due to invalid output format"
                        {:input name :cause e}))))))
