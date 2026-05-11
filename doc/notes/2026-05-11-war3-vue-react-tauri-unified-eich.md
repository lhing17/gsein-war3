# gsein-war3 改造方案：Clojure 核心 + Tauri + Vue 3 桌面应用

## Context

本项目是一个使用 Clojure 开发的 Warcraft 3 地图开发个人工具库，目前以 REPL 交互和零散脚本为主，存在以下问题：
- 功能分散在 13+ 个工具模块中，通过 `comment` 块和硬编码路径调用
- 配置 `resources/config.edn` 写死 Windows 绝对路径，不具备跨平台能力
- 现有的 Swing UI (`seesaw`) 功能未完全接入，仅占位
- 缺少标准化的入口和工程结构

改造目标：保留所有核心业务能力，将其封装为标准化 CLI 接口，并用 Vue 3 + Tauri 构建现代桌面 UI，实现配置持久化、文件拖拽、批量处理和结果预览。

---

## 技术选型

| 层级 | 技术 | 说明 |
|------|------|------|
| 前端 | Vue 3 + TypeScript + Vite + Pinia + Vue Router + Element Plus | 工具型应用表单密集，Vue 模板语法效率更高 |
| 桌面壳 | Tauri (Rust) | 提供文件系统、对话框、配置持久化、进程调用能力 |
| 业务核心 | Clojure (JVM) | 保留全部现有逻辑：BLP 生成、LNI 读写、MDX 解析、模板渲染、Excel 读取、拼音转换 |
| 通信方式 | Tauri Command → `std::process::Command` 调用 `java -jar gsein-war3.jar <cmd> <args>` | JSON 通过 stdin/stdout 交换 |

**不迁移到 Rust 的核心原因**：`BLP_IIO_Plugins` 是 JVM 专属 ImageIO SPI 插件，Rust 生态无成熟 BLP 编码库；`docjure` / `selmer` / `pinyin4j` 同样无等价的 Rust 成熟库。

---

## 分阶段实施

### 阶段 1：Clojure 工程化改造（规范化 CLI 接口）

**目标**：删除 Swing/http-kit，将零散的 REPL 脚本改造为参数化的 CLI 命令。

#### 1.1 修改构建配置
- **`project.clj`**：移除 `seesaw`、`flatlaf`、`http-kit`、`pomegranate`；新增 `org.clojure/tools.cli`
- **`src/gsein_war3/ui/core.clj`**：删除
- **`src/gsein_war3/http/server.clj`**：删除

#### 1.2 解耦全局配置
- **`src/gsein_war3/config.clj`**：移除所有命名空间不再顶层 `(def env (config/get-config))`；`get-config` 改为从指定路径读取，或直接被废弃
- **`resources/config.edn`**：删除硬编码路径，仅保留与应用无关的默认常量（如有）
- 所有函数改为显式接收 `project-dir`、`out-dir`、`temp-dir` 等参数

#### 1.3 参数化高耦合模块
- **`src/gsein_war3/blp/generator.clj`**：`generate-blps` 增加 `out-dir` 参数，移除对全局 `env` 的依赖
- **`src/gsein_war3/lni/available_id.clj`**：`project-id-producer` 改为纯函数，接收 `project-dir` 参数
- **`src/gsein_war3/tools/title_generator.clj`**：硬编码字体 `"方正颜宋简体_粗"` 改为 `font-path` 参数传入
- **`src/gsein_war3/tools/xls_to_lni.clj`**：将 `default-hp-map`、`default-def-map` 等硬编码映射提取为外部 EDN 配置或函数参数

#### 1.4 新增 CLI 入口
- **`src/gsein_war3/cli/core.clj`**：使用 `clojure.tools.cli` 实现 `-main`，根据子命令分发到 handler
- **`src/gsein_war3/cli/handlers.clj`**：为每个功能提供标准化 handler，接收 EDN/JSON map，返回结果 map

**CLI 命令清单**：
- `blp-generate` — BLP 图标生成
- `lni-read` / `lni-write` — LNI 文件读写
- `available-ids` — 可用 ID 查询
- `mdx-replace-blp` — MDX 贴图路径替换
- `mdx-classify` — MDX 模型分类整理
- `template-render` — Selmer 模板批量渲染（单位、物品、技能、塔等）
- `xls-to-lni` — Excel 转 LNI
- `title-generate` — 头顶称号生成
- `image-split` — 图片切割
- `constant-replace` — JASS 常量替换
- `text-search` — 文本搜索
- `unit-place` — 单位圆形分布计算
- `fourcc-convert` — 进制转换

---

### 阶段 2：Tauri + Vue 3 框架搭建

**目标**：初始化前后端项目骨架，建立 IPC 通道，实现配置持久化。

#### 2.1 目录结构
```
gsein-war3/
├── clojure/              ← 原项目根目录内容迁移至此
│   ├── project.clj
│   └── src/...
├── tauri/                ← Tauri Rust 后端
│   ├── src/main.rs
│   ├── src/commands.rs   ← 暴露给前端的 Commands
│   ├── src/config.rs     ← 应用配置持久化
│   └── tauri.conf.json
└── frontend/             ← Vue 3 前端
    ├── package.json
    ├── src/main.ts
    ├── src/App.vue
    ├── src/stores/config.ts
    ├── src/api/tauri.ts
    ├── src/api/clojure.ts
    └── src/router/
```

#### 2.2 Tauri 核心能力
- **`commands.rs`**：封装 `call_clojure(cmd, args)` → 执行 `java -jar gsein-war3.jar ...` → 解析 stdout JSON
- **`config.rs`**：使用 `dirs` crate 将配置持久化到 `%APPDATA%/gsein-war3/config.json`
- 开启 `fs`（限定工作目录）、`dialog`（文件/目录选择）权限

#### 2.3 前端核心能力
- **Pinia store (`stores/config.ts`)**：管理项目目录、工作区、最近使用的项目列表
- **API 层 (`api/clojure.ts`)**：统一封装 Clojure CLI 调用，处理 loading、错误提示、超时
- **Element Plus**：表单、表格、文件上传、消息提示

---

### 阶段 3：功能逐个接入前端

按优先级分批实现前端页面：

| 优先级 | 功能 | 前端页面 | 后端 Clojure 改造 |
|--------|------|----------|-------------------|
| P0 | BLP 图标生成 | 拖拽上传/选择图片，类型选择（主动/被动），批量处理，结果预览 | `blp/generator.clj` 增加批量接口 |
| P0 | 可用 ID 查询 | 选择项目目录，类型选择，显示下 N 个可用 ID | `lni/available_id.clj` 保持 |
| P0 | 模板生成器 | 通魔/单位/物品/塔等表单填写，实时预览生成的 LNI 文本，一键导出 | `tools/*_generator.clj` 包装为 handler |
| P1 | MDX BLP 路径替换 | 选择 MDX 文件，输入旧/新路径，执行替换 | `mdx/converter.clj` 包装为 handler |
| P1 | MDX 模型分类 | 选择源目录和目标目录，执行分类，显示进度 | `mdx/classifier.clj` 包装为 handler |
| P1 | LoadingScreen 切割 | 选择图片，自动四宫格切割，导出 BLP | `tools/image_splitter.clj` 包装为 handler |
| P1 | 头顶称号生成 | 输入文字/选择图片，颜色/背景选择，生成 BLP + MDX | `tools/title_generator.clj` 字体参数化 |
| P2 | Excel 转 LNI | 选择 Excel，配置列映射，预览，导出 | `tools/xls_to_lni.clj` 参数化映射 |
| P2 | JASS 常量替换 | 选择 JASS 目录，读取常量，执行替换 | `tools/constant_replacer.clj` 包装为 handler |
| P2 | 单位圆形分布 | 输入中心坐标、距离、数量，生成坐标列表 | `tools/unit_placer.clj` 包装为 handler |
| P2 | 文本搜索 | 输入关键词，选择搜索目录，显示结果 | `tools/text_searcher.clj` 包装为 handler |
| P2 | 进制转换 | FourCC / Hex / Decimal 互转 | `tools/number_base_converter.clj` 包装为 handler |
| P3 | 彩色文本生成器 | **纯前端实现**，替代原 Python/Tkinter 工具 | 无需 Clojure |

---

### 阶段 4：打包与发布

- **`scripts/build.bat`**：Windows 本地构建脚本（`lein uberjar` → `tauri build`）
- **`.github/workflows/release.yml`**：GitHub Actions 自动构建 Windows 安装包
- **JRE 处理**：
  - 默认：安装包内附带 `gsein-war3.jar`，运行时检测 JRE，未安装则提示下载
  - 进阶（可选）：用 `jlink` 生成精简 JRE（约 50-80MB）一并打包，实现零依赖

---

## 关键重构点

### 1. 解耦硬编码路径
所有 Clojure 函数改为显式参数；前端通过 Tauri `dialog` API 选择目录；配置使用 Tauri `appDataDir` 持久化到 JSON。

### 2. `comment` 块 → 标准化 API
提取每个 `comment` 块的核心逻辑到 `cli/handlers.clj`，输入输出统一为 JSON/EDN map。

### 3. BLP 生成必须在 JVM 中
`BLP_IIO_Plugins` 无替代方案，保留在 Clojure 侧。前端如需预览，Clojure 可额外输出 PNG 到临时目录。

### 4. Excel / 模板渲染保留在 Clojure
`docjure` 和 `selmer` 无 Rust 等价库，通过 CLI 参数化调用。

### 5. 字体参数化
`title_generator.clj` 的硬编码字体改为 `font-path` 参数；前端提供字体文件选择器，或使用系统默认中文字体降级。

---

## 风险与取舍

| 风险 | 应对 |
|------|------|
| BLP 生成依赖 JVM ImageIO 插件，无法去 JVM | 保留 Clojure CLI，Tauri 调用 jar |
| 用户需安装 JRE | 打包时检测并提示；可选 jlink 精简 JRE |
| 硬编码 Windows 字体可能缺失 | 参数化字体路径，提供系统字体降级 |
| 进程通信开销 | 数据量小，可忽略 |

---

## 验证方式

1. **阶段 1 验证**：`lein uberjar` 成功生成 jar；命令行执行 `java -jar target/gsein-war3.jar blp-generate --input-file test.png --type active --out-dir ./out` 成功输出 BLP 文件
2. **阶段 2 验证**：`tauri dev` 正常启动桌面窗口；前端能成功调用 Rust Command 并返回 JSON
3. **阶段 3 验证**：逐个页面进行端到端测试：选择文件 → 前端传参 → Rust 调用 Clojure → 返回结果 → 前端展示/下载
4. **阶段 4 验证**：`tauri build` 成功生成 `.msi` 安装包；在干净 Windows 环境安装并运行核心功能

---

## 关键文件清单

| 路径 | 操作 | 说明 |
|------|------|------|
| `clojure/project.clj` | 修改 | 移除 Swing/http-kit，新增 tools.cli |
| `clojure/src/gsein_war3/cli/core.clj` | 新增 | CLI 统一入口 |
| `clojure/src/gsein_war3/cli/handlers.clj` | 新增 | 各功能标准化 handler |
| `clojure/src/gsein_war3/config.clj` | 修改 | 解耦全局状态 |
| `clojure/src/gsein_war3/blp/generator.clj` | 修改 | 参数化 out-dir |
| `clojure/src/gsein_war3/lni/available_id.clj` | 修改 | 参数化 project-dir |
| `clojure/src/gsein_war3/tools/title_generator.clj` | 修改 | 参数化字体路径 |
| `clojure/src/gsein_war3/tools/xls_to_lni.clj` | 修改 | 提取硬编码映射为配置 |
| `tauri/src/commands.rs` | 新增 | Tauri 暴露给前端的 Commands |
| `tauri/src/config.rs` | 新增 | 配置持久化 |
| `frontend/src/api/clojure.ts` | 新增 | 前端 Clojure CLI 调用封装 |
| `frontend/src/stores/config.ts` | 新增 | Pinia 配置管理 |
