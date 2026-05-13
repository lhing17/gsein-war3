(ns gsein-war3.tools.unit-placer-test
  (:require [clojure.test :refer [deftest is testing]]
            [gsein-war3.tools.unit-placer :as up]))

(deftest unit-positions-test
  (testing "计算圆形排列坐标"
    (let [result (up/unit-positions 0 0 100 4)]
      (is (= 4 (count result)))
      (is (every? #(= 3 (count %)) result))
      (is (every? #(<= 0 (nth % 2) 360) result))))
  (testing "n=1 时只有中心正上方一个点"
    (let [[[x y angle]] (up/unit-positions 0 0 100 1)]
      (is (<= 99 x 101))
      (is (<= -1 y 1))
      (is (= 180.0 angle))))
  (testing "n <= 0 时抛出断言异常"
    (is (thrown? AssertionError (up/unit-positions 0 0 100 0)))
    (is (thrown? AssertionError (up/unit-positions 0 0 100 -1)))))

(deftest unit-placer-test
  (testing "返回格式化字符串列表"
    (let [result (up/unit-placer 0 0 100 4)]
      (is (= 4 (count result)))
      (is (every? string? result))
      (is (every? #(.contains % ", ") result)))))
