(ns gsein-war3.tools.rebuild-listfile-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.java.io :as jio]
            [gsein-war3.tools.rebuild-listfile :as rebuild]))

(deftest rebuild-listfile-test
  (testing "rebuild-listfile 生成相对路径列表"
    (let [temp-dir (jio/file (System/getProperty "java.io.tmpdir") (str "test-rebuild-" (System/currentTimeMillis)))
          out-file (jio/file temp-dir "(listfile)")]
      ;; 创建临时目录结构
      (.mkdirs (jio/file temp-dir "sub"))
      (spit (jio/file temp-dir "a.txt") "a")
      (spit (jio/file temp-dir "sub" "b.txt") "b")
      ;; 执行并断言
      (let [result (rebuild/rebuild-listfile temp-dir out-file)]
        (is (= 2 (count result)))
        (is (every? #(not (.contains % "test-rebuild")) result))
        (is (.exists out-file))
        (let [content (slurp out-file)]
          (is (.contains content "a.txt"))
          (is (.contains content (str (jio/file "sub" "b.txt"))))))
      ;; 清理
      (doseq [f (reverse (file-seq temp-dir))]
        (.delete ^java.io.File f)))))
