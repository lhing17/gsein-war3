(ns gsein-war3.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [gsein-war3.core :as core]))

(deftest core-namespace-loads
  (testing "核心命名空间正常加载且导出了公共函数"
    (is (fn? core/generate-blps!))
    (is (fn? core/read-lni))
    (is (fn? core/write-lni))
    (is (fn? core/get-available-ids))
    (is (fn? core/generate-title!))
    (is (fn? core/unit-placer))
    (is (fn? core/xls->map))
    (is (fn? core/get-config))
    (is (fn? core/get-config-or-default))))
