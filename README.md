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


## License


