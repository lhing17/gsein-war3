(ns gsein-war3.util.pinyin
  (:require [clojure.string :as str])
  (:import (net.sourceforge.pinyin4j PinyinHelper)
           (net.sourceforge.pinyin4j.format HanyuPinyinVCharType HanyuPinyinToneType HanyuPinyinOutputFormat)
           (net.sourceforge.pinyin4j.format.exception BadHanyuPinyinOutputFormatCombination)))


(defn get-pinyin-name [name]
  (let [o-name (if (str/includes? name ".") (subs name 0 (str/index-of name ".")) name)]
    (try (PinyinHelper/toHanYuPinyinString o-name
                                           (doto (HanyuPinyinOutputFormat.)
                                             (.setVCharType HanyuPinyinVCharType/WITH_V)
                                             (.setToneType HanyuPinyinToneType/WITHOUT_TONE))
                                           ""
                                           true)
         (catch BadHanyuPinyinOutputFormatCombination _ o-name))
    )
  )

(comment
  (get-pinyin-name "不明古书.jpg")
  ,)