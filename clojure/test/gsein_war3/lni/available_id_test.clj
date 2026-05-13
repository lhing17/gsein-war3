(ns gsein-war3.lni.available-id-test
  (:require [clojure.test :refer [deftest is testing]]
            [gsein-war3.lni.available-id :as aid]))

(deftest next-char-test
  (testing "next-char 字符递增"
    (is (= \1 (#'aid/next-char \0)))
    (is (= \A (#'aid/next-char \9)))
    (is (= \B (#'aid/next-char \A)))
    (is (= \0 (#'aid/next-char \Z)))
    (is (= \b (#'aid/next-char \a)))
    (is (= \0 (#'aid/next-char \z)))))

(deftest next-id-test
  (testing "next-id 正常递增"
    (is (= "A001" (aid/next-id "A000")))
    (is (= "A00A" (aid/next-id "A009")))
    (is (= "A010" (aid/next-id "A00Z")))
    (is (= "A0A0" (aid/next-id "A09Z")))
    (is (= "A100" (aid/next-id "A0ZZ"))))
  (testing "next-id 溢出时抛出异常"
    (is (thrown? Exception (aid/next-id "zzzz")))))

(deftest available?-test
  (testing "available? 大小写不敏感"
    (let [ids-set #{"a000" "b001"}]
      (is (true? (#'aid/available? "A002" ids-set)))
      (is (false? (#'aid/available? "A000" ids-set)))
      (is (false? (#'aid/available? "a000" ids-set))))))

(deftest get-available-ids-test
  (testing "get-available-ids 跳过已占用 ID"
    (let [id-producer (fn [_] ["A000" "A001" "A002"])]
      (is (= ["A003" "A004" "A005"]
             (aid/get-available-ids 3 id-producer :ability "A000")))))
  (testing "get-available-ids 默认起始 ID"
    (let [id-producer (constantly [])]
      (is (= ["I000" "I001" "I002"]
             (aid/get-available-ids 3 id-producer :item))))))
