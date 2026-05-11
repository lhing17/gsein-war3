# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

这是一个使用 Clojure 开发的 Warcraft 3（魔兽争霸3）地图开发工具库，采用 Leiningen 构建。

## 常用命令

```bash
# 启动 REPL（默认命名空间为 gsein-war3.core，集成 Reveal 中间件）
lein repl

# 运行测试
lein test

# 打包 JAR
lein jar
lein uberjar
```

## 架构与命名空间组织

项目按 War3 资产类型和功能域组织命名空间：

- `gsein-war3.blp.*` — BLP 图片格式生成（图标、边框叠加、亮度调整）
- `gsein-war3.lni.*` — LNI（类 INI）配置文件的读写与可用 ID 分配
- `gsein-war3.mdx.*` — MDX 模型二进制文件的解析、分类与 BLP 路径替换
- `gsein-war3.tools.*` — 各类生成器和工具（单位/物品/技能/任务生成器、常量替换、LoadingScreen 切分等）
- `gsein-war3.util.*` — 图片处理与拼音转换等共享工具
- `gsein-war3.ui.*` — 基于 Seesaw + FlatLaf 的 Swing GUI（目前功能未完全接入）
- `gsein-war3.http.*` — http-kit HTTP/WebSocket 服务器

## 关键设计模式

**模板驱动生成**：`resources/templates/*.ini` 是 Selmer 模板，用于生成 War3 对象定义（单位、物品、技能、塔）。工具函数接收数据 map，渲染为 LNI 格式的文本片段。

**自定义 256 进制 ID 管理**：`lni/available_id.clj` 实现了 War3 的 4 字符 ID 递增逻辑（`0-9` → `A-Z` → `a-z`），用于分配技能、物品、单位等的可用 ID。

**EDN 配置**：`resources/config.edn` 存储本地路径（`:project-dir`、`:workspace`、`:out-dir`、`:temp-dir`）。多个命名空间在顶层以 `(def env (config/get-config))` 加载配置。

**二进制文件解析**：`mdx/parser.clj` 和 `mdx/converter.clj` 使用 `java.io.RandomAccessFile` 直接读写 MDX 二进制格式，处理 4 字节块、整型和空终止字符串。

**图片处理流水线**：`blp/generator.clj` 和 `util/image.clj` 组合 resize → 亮度调整 → 边框叠加 → BLP 输出。

## 开发与 REPL 工作流

- 大多数源文件包含大量 `comment` 块，内有硬编码的 Windows 绝对路径（如 `D:\\IdeaProjects\\...`），作为交互式用法示例和临时脚本。这是典型的个人 Clojure REPL 工作流。
- 许多工具函数设计为在 REPL 中交互调用，而非通过统一入口运行。
- `http/server.clj` 在命名空间加载时即启动服务器（顶层副作用），REPL reload 时需注意。

## 依赖与外部工具

- JitPack 仓库用于引入 BLP ImageIO 插件：`com.github.PhoenixZeng/BLP_IIO_Plugins`
- `py/text/text_colorizer.py` 是一个独立的 Python/Tkinter 小工具，用于生成 War3 彩色文本（`|cRRGGBBAA` 格式）。
