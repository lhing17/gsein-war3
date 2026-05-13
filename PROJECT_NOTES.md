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
