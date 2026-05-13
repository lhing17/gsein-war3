(ns gsein-war3.tools.unit-generator-test
  (:require [clojure.test :refer [deftest is testing]]
            [gsein-war3.tools.unit-generator :as ug]))

(deftest generate-units-test
  (testing "空列表返回空字符串"
    (is (= "" (ug/generate-units []))))
  (testing "非序列参数抛出断言异常"
    (is (thrown? AssertionError (ug/generate-units nil)))
    (is (thrown? AssertionError (ug/generate-units {}))))
  (testing "渲染单个单位"
    (let [result (ug/generate-units [{:id "n001" :unit-type "普通" :name "测试"}])]
      (is (string? result))
      (is (.contains result "n001")))))
