# gsein-war3

使用Clojure开发的吉森war3库

## Usage

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



## License
MIT License


