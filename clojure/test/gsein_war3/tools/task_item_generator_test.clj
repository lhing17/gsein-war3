(ns gsein-war3.tools.task-item-generator-test
  (:require [clojure.test :refer [deftest is testing]]
            [gsein-war3.tools.task-item-generator :as tig]))

(deftest generate-tasks-test
  (testing "空列表返回空字符串"
    (is (= "" (tig/generate-tasks []))))
  (testing "非序列参数抛出断言异常"
    (is (thrown? AssertionError (tig/generate-tasks nil)))
    (is (thrown? AssertionError (tig/generate-tasks {}))))
  (testing "渲染单个任务"
    (let [result (tig/generate-tasks [{:description "测试" :hint "提示" :difficulty "简单" :award "+100" :name "任务1"}])]
      (is (string? result))
      (is (.contains result "任务1")))))
