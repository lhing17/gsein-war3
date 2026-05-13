(ns gsein-war3.core
  "gsein-war3 库的统一入口命名空间。

   提供 Warcraft 3 地图开发中最常用的功能聚合，
   包括 BLP 图标生成、LNI 配置读写、MDX 模型处理、
   单位/物品/技能批量生成等。"
  (:require [gsein-war3.blp.generator :as blp]
            [gsein-war3.lni.available-id :as id]
            [gsein-war3.lni.reader :as lni-reader]
            [gsein-war3.lni.writer :as lni-writer]
            [gsein-war3.mdx.converter :as mdx-conv]
            [gsein-war3.mdx.classifier :as mdx-cls]
            [gsein-war3.tools.title-generator :as title]
            [gsein-war3.tools.image-splitter :as img-split]
            [gsein-war3.tools.constant-replacer :as const-rep]
            [gsein-war3.tools.text-searcher :as txt-search]
            [gsein-war3.tools.unit-placer :as unit-plc]
            [gsein-war3.tools.number-base-converter :as nbc]
            [gsein-war3.xls.reader :as xls-reader]
            [gsein-war3.tools.unit-generator :as unit-gen]
            [gsein-war3.tools.item-generator :as item-gen]
            [gsein-war3.tools.tower-generator :as tower-gen]
            [gsein-war3.tools.task-item-generator :as task-gen]
            [gsein-war3.config :as config])
  (:import (java.awt Color)))

;; ---------- BLP 生成 ----------

(def generate-blps! blp/generate-blps!)
(def image-to-blp blp/image-to-blp)
(def output-to-file blp/output-to-file)

;; ---------- LNI 配置 ----------

(def read-lni lni-reader/read-lni)
(def write-lni lni-writer/write-lni)
(def get-available-ids id/get-available-ids)
(def project-id-producer id/project-id-producer)

;; ---------- MDX 模型 ----------

(def replace-blp mdx-conv/replace-blp)
(def classify-mdx mdx-cls/classify)

;; ---------- 生成器 ----------

(def generate-title! title/generate-title!)
(def split-image img-split/split-image)
(def write-image img-split/write-image)
(def replace-constant const-rep/replace-literal-with-constant)
(def search-text txt-search/search-text)
(def unit-placer unit-plc/unit-placer)
(def fourcc nbc/fourcc)
(def hex-to-fourcc nbc/hex-to-fourcc)
(def fourcc-to-decimal nbc/fourcc-to-decimal)
(def fourcc-to-hex nbc/fourcc-to-hex)
(def xls->map xls-reader/xls->map)
(def generate-units unit-gen/generate-units)
(def generate-towers tower-gen/generate-tower-building-ability)
(def generate-tower-items tower-gen/generate-tower-building-item)
(def generate-tasks task-gen/generate-tasks)

;; ---------- 配置 ----------

(def get-config config/get-config)
(def get-config-or-default config/get-config-or-default)

;; ---------- 常用颜色常量 ----------

(def ^Color color-blue Color/BLUE)
(def ^Color color-red Color/RED)
(def ^Color color-green Color/GREEN)
(def ^Color color-black Color/BLACK)
(def ^Color color-white Color/WHITE)
