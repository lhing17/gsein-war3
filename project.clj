(defproject gsein-war3 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [
                 [nrepl "0.9.0"]
                 ;; clojure SDK
                 [org.clojure/clojure "1.11.1"]
                 ;; 模板
                 [selmer "1.12.53"]
                 ;; 拼音
                 [com.belerweb/pinyin4j "2.5.1"]
                 ;; 可以动态加载依赖包
                 [clj-commons/pomegranate "1.2.1"]
                 ;; 数据可视化
                 [vlaaad/reveal "1.3.274"]
                 ;; apache commons io
                 [commons-io "2.11.0"]
                 ;; 生成blp
                 [com.github.PhoenixZeng/BLP_IIO_Plugins "f3cfe38a66"]
                 ]
  :repositories [["jitpack" "https://jitpack.io"]]
  :repl-options {:init-ns gsein-war3.core})
