# gsein-war3

使用Clojure开发的吉森war3库

## 已经支持的功能

### 1. 将png或jpg图片生成BLP格式图片
- 使用blp/generator.clj
- 支持生成主动技能和被动技能的图标（包括亮图标和暗图标）
- 支持批量操作

```clojure
;; 生成主动技能的亮图标和暗图标
(generate-blps (jio/file "my/path/to/image.png") "active")

;; 批量生成主动技能的图标
(doseq [file (file-seq (jio/file "my/path/to/dir"))]
    (when (.isFile file)
        (generate-blps file "active")))

```

### 2. 可以自动批量识别下一个可用ID
- 使用lni/available_id.clj
- 支持计算在256进制下指定ID的下一个ID
- 支持获取物品、技能、单位、BUFF、可破坏物的下一个/n个可用ID

```clojure
;; 计算下一个ID
(next-id "A00Z")

;; 计算下一个可用的主动技能ID
(get-available-id (project-id-producer project-dir) :ability)

;; 获取下5个可用的物品ID
(get-available-ids 5 (project-id-producer project-dir) :item)
```

### 3. 支持lni文件的读写
- 使用lni/reader.clj和lni/writer.clj
- 支持将lni文件读取成clojure数据结构
- 支持将clojure数据结构写入lni文件

### 4. 支持mdx文件的分类
- 使用mdx/classifier.clj
- 支持将解压地图格式中的mdx文件及 单位模型mdx文件分别放到指定文件夹（以mdx文件名作为文件夹名）

```clojure
;; 分类mdx文件
(classify (jio/file "my/path/to/map/")
            (jio/file "my/path/to/out/")
            :mdx)
```

### 5. 支持修改mdx文件中的blp路径
- 使用mdx/converter.clj
- 支持将mdx文件中的blp路径修改为指定路径

```clojure
;; 修改mdx文件中的blp路径
(replace-blp (jio/file filename) "my/path/to/blp/" "my/new/path/to/blp/")
```

### 6. 将jass代码中的字面量替换为指定常量
- 使用tools/constant_replacer.clj
- 支持将jass代码中的字面量替换为指定常量

```clojure
  
  ;; 从war3map.j读取全部常量
  (def constants (read-constants "my/path/to/war3map.j"))

  ;; 将常量map的key-value对交换，用于替换常量为字面量
  (def reverse-constant-map (into {} (map (fn [[k v]] [v k]) constants)))

  ;; 读取地图中的所有jass文件
  (def jass-files (->> (file-seq (jio/file "my/path/to/jass"))
                       (filter #(.isFile %))
                       (map #(.getAbsolutePath %))
                       (filter #(str/ends-with? % ".j"))))

  ;; 将所有jass文件中的字面量替换为指定常量
  (doseq [jass-file jass-files]
    (->> (jio/reader jass-file)
         (line-seq)
         (map (fn [s]
                (if (starts-with-constant s)
                  s
                  (replace-literal-with-constant s reverse-constant-map))))
         (str/join "\n")
         ((fn [s] (str s "\n")))
         (spit jass-file)))
```

### 7. 制作war3的LoadingScreen
- 使用tools/image_splitter.clj
- 支持将LoadingScreen.png切分成左上、左下、右上、右下四个部分

```clojure

;; 先准备好一张1024 * 768的png格式图片，用于制作LoadingScreen
 (def img (javax.imageio.ImageIO/read (java.io.File. "my/path/to/LoadingScreen.png")))

 ;; 定义切割函数，将图片每512像素切割一次
  (def splitter (fn [rect]
                  (->> [rect]
                       (mapcat #(split-rectangle % :horizontal 512))
                       (mapcat #(split-rectangle % :vertical 512))
                       )))

  ;; 定义切割图片实例
  (def my-image (split-image (make-image [img]) splitter))
  (doseq [part (map-indexed vector (:parts my-image))]
    (javax.imageio.ImageIO/write (second part) "png" (java.io.File. (str (first part) ".png"))))

  ;; 最后将切割后的图片写入blp格式，分别命名为LoadingScreenTL、LoadingScreenBL、LoadingScreenTR、LoadingScreenBR
  (write-image my-image "" ["LoadingScreenTL" "LoadingScreenBL" "LoadingScreenTR" "LoadingScreenBR"] "blp")
```

## 计划支持的功能
- [X] 通魔技能生成器


## License
MIT License


