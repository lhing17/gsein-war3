(ns gsein-war3.tools.number-base-converter
  (:require [clojure.string :as str]
            [clojure.java.io :as jio]))



(defn fourcc [n]
  (let [n (mod n 4294967296)]
    (str (char (mod (quot n 16777216) 256))
         (char (mod (quot n 65536) 256))
         (char (mod (quot n 256) 256))
         (char (mod n 256)))))

(defn hex-to-fourcc [hex]
  (fourcc (Long/parseLong hex 16)))

(defn fourcc-to-decimal [s]
  (bit-or (bit-shift-left (int (nth s 0)) 24)
          (bit-shift-left (int (nth s 1)) 16)
          (bit-shift-left (int (nth s 2)) 8)
          (int (nth s 3))))

(defn fourcc-to-hex [s]
  (format "%08x" (fourcc-to-decimal s)))

(defn replace-hex-with-fourcc [s]
  ;; 识别$符号开头的8位16进制数
  (let [hex-pattern #"(\$[0-9a-fA-F]{8})"
        hex-replacement (fn [m]
                          (let [hex (subs (first m) 1)]
                            (str "'" (hex-to-fourcc hex) "'")))]
    (-> s
        (str/replace hex-pattern hex-replacement))))


(comment
  (fourcc 1865429066)
  (hex-to-fourcc "49303042")
  (fourcc-to-decimal "I00B")
  (fourcc-to-hex "I00B")
  (replace-hex-with-fourcc "AA$49303042BB$49303043CC")
  (->> (slurp "D:\\IdeaProjects\\jztd-reborn\\jass\\system\\UseAbility.j")
       (replace-hex-with-fourcc)
       (spit "D:\\IdeaProjects\\jztd-reborn\\jass\\system\\UseAbility.j"))

  (def jass-files (->> (file-seq (jio/file "D:\\IdeaProjects\\jztd-reborn\\jass"))
                       (filter #(.isFile %))
                       (map #(.getAbsolutePath %))
                       (filter #(str/ends-with? % ".j"))))
  (doseq [jass-file jass-files]
    (->> (slurp jass-file)
         (replace-hex-with-fourcc)
         (spit jass-file)))
  ,)
