(ns gsein-war3.util.pinyin-test
  (:require [clojure.test :refer [deftest is testing]]
            [gsein-war3.util.pinyin :as pinyin]))

(deftest get-pinyin-name-test
  (testing "中文字符串转全拼"
    (is (= "buminggushu" (pinyin/get-pinyin-name "不明古书")))
    (is (= "lingqufuli" (pinyin/get-pinyin-name "领取福利"))))
  (testing "非中文字符原样保留"
    (is (= "1.5beijingyan" (pinyin/get-pinyin-name "1.5倍经验")))
    (is (= "ABC123" (pinyin/get-pinyin-name "ABC123"))))
  (testing "nil 与空字符串返回 nil"
    (is (nil? (pinyin/get-pinyin-name nil)))
    (is (nil? (pinyin/get-pinyin-name ""))))
  (testing "含文件名扩展名时不应被截断"
    (is (= "buminggushu.jpg" (pinyin/get-pinyin-name "不明古书.jpg"))
        "扩展名剥离逻辑已从 get-pinyin-name 移除，应由调用方处理")))
