# 项目修改记录

## 2026-05-13

- 修复 `tools.image-splitter/write-image` 中输出路径拼接错误导致的图片切割结果未写入目标目录的问题（#5）
- 启动暗色主题视觉升级（#6）：建立 War3 风格配色系统，完成 App.vue 全局布局与 Home.vue 首页暗色化，前端构建验证通过
- 修复 `title-generator` 中 JAR 模式下默认背景图片与模板 MDX 资源读取失败的问题（#7）：将 `(jio/file (jio/resource ...))` 改为直接传递 URL 或使用流复制，验证默认背景和自定义背景均可正常生成称号
- 推进暗色主题视觉升级（#6）：完成 ElementPlus 全组件暗色覆盖（按钮渐变、输入框金色聚焦、卡片/表格/表单/标签等），并添加页面切换淡入动效与侧边栏菜单发光指示条
- 推进暗色主题视觉升级（#6）：建立全局页面布局系统（统一页头装饰下划线、段落排版、卡片阴影层级），构建验证通过
- 重构 `blp/generator.clj`（#8）：重命名 `map-deal-image!`→`apply-processors`、`generate-blps`→`generate-blps!`；提取 War3 路径常量；简化 `get-project-target-dir` 为字符串操作；清理大量 REPL 示例 comment 块；统一 `type-config` 与 `output-blp-fn` 的抽象，`output-blp-fn` 改为从 `type-config` 派生，消除重复 case 分支；构建验证通过
- 修复 `lni/reader.clj`（#14）：修复文件以空行/注释开头时的 `StringIndexOutOfBoundsException` 崩溃；将 `an-alias` 改为 `ordered`；换行符拼接改用 `System/lineSeparator`；`read-lni` 添加文件存在性检查；CLI 验证空文件与注释开头文件均正常解析
- 修复 `mdx/classifier.clj`（#16）：修复 move 模式下复制失败仍删除源文件的数据丢失 bug（将删除操作移入 try 块）；复制前自动创建目标目录（`.mkdirs`）；清理 REPL 示例 comment 块
- 修复 `tools/item_generator.clj`（#22）：移除未使用的 `gsein-war3.lni.available-id` require；模板参数化 `_parent` 和 `pawnable`（默认值保持向后兼容）；清理不安全的 `(spit "a.txt" ...)` comment 块；CLI 验证生成正常
- 修复 `tools/tower_generator.clj`（#28）：新增 `strip-quotes` 安全去除首尾引号，修复 `subs` 无边界检查导致的 `StringIndexOutOfBoundsException`；移除硬编码 `tower-ids`；清理 REPL 示例 comment 块；同步更新 handler 使用安全函数；CLI 验证生成正常
- 重构 `xls/reader.clj` 与 `tools/xls_to_lni.clj`（#34）：消除 `xls->map` 与 `xls->obj` 功能重复，移除命名空间级 `def column-map`/`def xls-file` 污染；`xls->map` 新增 docstring 与输入校验（文件存在性、sheet 存在性、列映射非空）；`tools/xls_to_lni` 改为依赖 `xls-reader/xls->map`；CLI handler 同步更新；构建验证通过
- 重构 `cli/core.clj`（#9）：将 `subcommands` 数据抽离到 `resources/cli-commands.edn`，核心逻辑提取为纯函数 `run-command`；`-main` 仅保留一处 `System/exit`；子命令键改为 keyword；添加顶层 try/catch 兜底；CLI 验证正常
- 重构 `cli/handlers.clj`（#10）：重命名 `ok`→`success-response`、`err`→`error-response`；`parse-int`/`parse-edn` 增加安全校验；新增 `safe-parse-double` 避免 `NumberFormatException`；`unit-place` 增加参数校验；提取 `generate-batch-objects` 统一 `generate-units` 与 `generate-items` 公共逻辑；拆分 `generate-towers` 长链式表达式；`defhandler` 支持 `debug=true` 保留堆栈；构建与 CLI 验证通过
- 重构 `config.clj`（#11）：`get-config` 增加异常处理（文件缺失、EDN 语法错误均给出友好提示）；新增 `get-config-or-default` 在缺失时返回空 map；使用 `with-open` 确保流关闭；构建验证通过
- 重构 `core.clj`（#12）：为核心命名空间添加 docstring，重新导出各子模块最常用的公共 API（BLP 生成、LNI 读写、MDX 处理、批量生成器等）；补充常用颜色常量；更新 core-test 验证导出；构建与测试通过
- 重构 `lni/available_id.clj`（#13）：修复 `next-id` 溢出崩溃隐患（ID 空间耗尽时抛出友好异常）；`next-char` 消除魔法数字；`inc-by-index` 改用 `StringBuilder` 避免中间集合；`available?` 预转小写 set 实现 O(1) 查询；`project-id-producer` 使用 `memoize` 缓存避免重复读文件；`get-available-ids` 预计算 ID 集合；新增 5 个单元测试覆盖边界情况；构建与测试通过
- 重构 `lni/writer.clj`（#15）：`write-chunk-body`/`write-chunk` 改用 `str/join`+`map` 替代低效的 `mapcat`+`apply str`；删除重复的 `update-hp`，统一使用 `update-attr`；`write-lni` 自动创建父目录；将 comment 块专用 require 移入 comment；构建与测试通过
- 重构 `mdx/converter.clj`（#17）：修复 `RandomAccessFile` 资源泄漏（try/finally 确保关闭）；`write-int` 改用 `ByteBuffer` 简化；`write-str` 增加长度溢出检查；`replace-blp` 增加备份恢复机制防止数据损坏；使用系统临时目录存放 tmp 文件；返回替换统计 `{:replaced N}`；构建与测试通过
- 重构 `mdx/parser.clj`（#18）：修复 `parse` 资源泄漏（将整个解析逻辑移入 try）；统一返回值为向量；`parse-content` 修复 `:else` 分支丢弃 `rs` 的 bug，使用 `into` 累积 TEXS 结果；`parse-textures` 改用 `conj` 保持正序；`read-str` 增加截断检测；提取 `chunk-keyword-size`/`texture-path-len` 常量；构建与测试通过
- 重构 `tools/constant_replacer.clj`（#19）：`read-constants` 使用 `with-open` 修复资源泄漏；`replace-literal-with-constant` 按 key 长度降序排序避免误匹配；放宽正则支持任意类型名；`literal-counter` 改用 `fnil`；`keep-chinese` 提取 Unicode 常量；`to-pinyin` 改用 `str/upper-case`；清理过长 comment 块；构建与测试通过
- 重构 `tools/general_skill_generator.clj`（#20）：将硬编码数据迁移到 `resources/general-skill-config.edn`；提供 `load-config` 与 `cached-config` 支持热加载；新增 `valid-target-type?` 和 `valid-flag?` 验证函数；配置文件中注明数据来源与适用版本；清理空 comment 块；构建与测试通过
- 重构 `tools/image_splitter.clj`（#21）：`scale` 使用 try/finally 确保 Graphics 释放；`write-image` 增加 names/parts 长度校验；`ImageIO/write` 返回值检查；`make-rectangle` 单参数改为 `[width height]`；`split-rectangle` 增加方向断言；`image` record 简化为 plain map；构建与测试通过
- 重构 `tools/number_base_converter.clj`（#23）：`replace-decimal-with-fourcc` 改为匹配 7–10 位数字并验证 FourCC 范围；`replace-hex-with-fourcc` 增加前后字符检查减少误匹配；`fourcc`/`hex-to-fourcc`/`fourcc-to-decimal` 增加 `:pre` 输入校验；提取魔法数字为命名常量；清理危险的批量替换脚本；构建与测试通过
- 重构 `tools/rebuild_listfile.clj`（#24）：将硬编码示例 comment 块提取为公共函数 `rebuild-listfile`；使用 `clojure.java.io` + NIO `Path#relativize` 正确生成相对路径；自动创建输出父目录；添加 CLI handler `rebuild-listfile` 与对应命令配置；导出到 `core.clj`；新增单元测试覆盖临时目录树扫描与输出验证；构建与测试通过
- 重构 `util/pinyin.clj`（#33）：移除内嵌的扩展名截断逻辑（避免误截 `1.5 倍经验` 等含点名称），由调用方 `blp/generator.clj` 自行剥离；`get-pinyin-name` 添加 docstring、`:pre` 校验与 nil/空串防御；魔法布尔值提取为命名常量 `preserve-non-chinese`；异常处理改为抛出携带上下文信息的 `ex-info`；新增单元测试覆盖中文转拼音、非中文保留、nil 输入；构建与测试通过
- 重构 `util/image.clj`（#32）：修复 `resize-to`、`copy`、`do-add-border!` 中 Graphics2D 未 `.dispose` 导致的资源泄漏；`add-active-border` 改为显式接收 opts map，消除 `& opts` + `(first opts)` 的别扭模式；`add-border` 添加默认分支抛出 `IllegalArgumentException`；`adjust-brightness` 消除硬编码 `(range 64)`，改用实际图像宽高；移除 `write-blp-and-close!`，由 `output-as-blp` 使用 `with-open` 管理流生命周期；统一代码格式；构建与测试通过
- 重构 `tools/xls_to_lni.clj`（#31）：将 4 组硬编码 ID 映射表迁移到 `resources/balance-ids.edn`；统一 `build-*-ability` 为单一通用函数 `build-ability`；提取 `normalize-number` 消除嵌套 `(if (== v (int v)) ...)`；为 `get-base-attrs`、`add-id-to-objs`、`build-ability-list` 等公共函数添加 docstring；动态变量仍可通过 `binding` 覆盖以保持 REPL 灵活性；构建与测试通过
- 重构 `tools/unit_placer.clj`（#30）：拆分 `unit-placer` 为 `unit-positions`（返回原始数值向量）与 `unit-placer`（返回格式化字符串），提升下游复用性；添加 `:pre [(pos-int? n)]` 输入校验；为两函数添加 docstring 与参数类型提示；清理文件开头空行与缩进；同步更新 `core.clj` 导出与 core-test 验证；新增单元测试覆盖坐标计算、单点边界与非法输入；构建与测试通过
