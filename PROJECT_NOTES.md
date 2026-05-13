# 项目修改记录

## 2026-05-13

- 修复 `tools.image-splitter/write-image` 中输出路径拼接错误导致的图片切割结果未写入目标目录的问题（#5）
- 启动暗色主题视觉升级（#6）：建立 War3 风格配色系统，完成 App.vue 全局布局与 Home.vue 首页暗色化，前端构建验证通过
- 修复 `title-generator` 中 JAR 模式下默认背景图片与模板 MDX 资源读取失败的问题（#7）：将 `(jio/file (jio/resource ...))` 改为直接传递 URL 或使用流复制，验证默认背景和自定义背景均可正常生成称号
- 推进暗色主题视觉升级（#6）：完成 ElementPlus 全组件暗色覆盖（按钮渐变、输入框金色聚焦、卡片/表格/表单/标签等），并添加页面切换淡入动效与侧边栏菜单发光指示条
- 推进暗色主题视觉升级（#6）：建立全局页面布局系统（统一页头装饰下划线、段落排版、卡片阴影层级），构建验证通过
- 重构 `blp/generator.clj`（#8）：重命名 `map-deal-image!`→`apply-processors`、`generate-blps`→`generate-blps!`；提取 War3 路径常量；简化 `get-project-target-dir` 为字符串操作；清理大量 REPL 示例 comment 块；CLI 验证 BLP 生成正常
- 修复 `lni/reader.clj`（#14）：修复文件以空行/注释开头时的 `StringIndexOutOfBoundsException` 崩溃；将 `an-alias` 改为 `ordered`；换行符拼接改用 `System/lineSeparator`；`read-lni` 添加文件存在性检查；CLI 验证空文件与注释开头文件均正常解析
- 修复 `mdx/classifier.clj`（#16）：修复 move 模式下复制失败仍删除源文件的数据丢失 bug（将删除操作移入 try 块）；复制前自动创建目标目录（`.mkdirs`）；清理 REPL 示例 comment 块
