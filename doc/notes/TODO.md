# gsein-war3 改造 TODO 清单

> 按优先级与实施顺序排列。完成项请打勾，进行中项标注 `[WIP]`。

---

## 阶段 1：Clojure 工程化改造（规范化 CLI 接口）

### 1.1 清理与构建配置
- [x] `project.clj`：移除 `seesaw`、`flatlaf`、`http-kit`、`pomegranate`
- [x] `project.clj`：新增依赖 `org.clojure/tools.cli`
- [x] 删除 `src/gsein_war3/ui/core.clj`（Swing UI）
- [x] 删除 `src/gsein_war3/http/server.clj`（http-kit demo）
- [x] `lein uberjar` 验证构建成功

### 1.2 解耦全局硬编码配置
- [x] `src/gsein_war3/config.clj`：移除所有命名空间顶层的 `(def env (config/get-config))`
- [x] `src/gsein_war3/config.clj`：改为函数参数传入配置，或废弃全局读取
- [x] `resources/config.edn`：删除硬编码 Windows 绝对路径
- [x] 全局排查：确保所有函数改为显式接收 `project-dir`、`out-dir`、`temp-dir` 参数

### 1.3 参数化高耦合模块
- [x] `src/gsein_war3/blp/generator.clj`：`generate-blps` 增加 `temp-dir` / `project-dir` 参数，移除对全局 `env` 的依赖
- [x] `src/gsein_war3/lni/available_id.clj`：`project-id-producer` 改为纯函数，接收 `project-dir` 参数
- [x] `src/gsein_war3/tools/title_generator.clj`：硬编码字体 `"方正颜宋简体_粗"` 改为 `font-name` / `font-size` 参数传入
- [x] `src/gsein_war3/tools/xls_to_lni.clj`：将 `default-hp-map`、`default-def-map` 等硬编码映射提取为动态 var 与函数参数

### 1.4 新增 CLI 统一入口
- [x] 新增 `src/gsein_war3/cli/core.clj`：使用 `clojure.tools.cli` 实现 `-main`，子命令分发
- [x] 新增 `src/gsein_war3/cli/handlers.clj`：各功能标准化 handler（接收 options map，返回结果 map）
- [x] CLI handler：`blp-generate` — BLP 图标生成
- [x] CLI handler：`available-ids` — 可用 ID 查询
- [x] CLI handler：`lni-read` / `lni-write` — LNI 文件读写
- [x] CLI handler：`mdx-replace-blp` — MDX 贴图路径替换
- [x] CLI handler：`mdx-classify` — MDX 模型分类整理
- [x] CLI handler：`template-render` — Selmer 模板批量渲染（单位/物品/技能/塔等）
- [x] CLI handler：`xls-to-lni` — Excel 转 LNI
- [x] CLI handler：`title-generate` — 头顶称号生成
- [x] CLI handler：`image-split` — 图片切割
- [x] CLI handler：`constant-replace` — JASS 常量替换
- [x] CLI handler：`text-search` — 文本搜索
- [x] CLI handler：`unit-place` — 单位圆形分布计算
- [x] CLI handler：`fourcc-convert` — 进制转换
- [x] 端到端验证：`java -jar target/...-standalone.jar <subcommand> [options]` 通过

---

## 阶段 2：Tauri + Vue 3 框架搭建

### 2.1 工程目录重构
- [x] 创建 `clojure/` 目录，将原项目根内容迁移至此
- [x] 创建 `tauri/` 目录，初始化 Tauri Rust 项目
- [x] 创建 `frontend/` 目录，初始化 Vue 3 + Vite + TypeScript 项目
- [x] 根目录增加 `scripts/build.bat`（`lein uberjar` → `tauri build`）

### 2.2 Tauri Rust 后端
- [x] `tauri/tauri.conf.json`：配置应用名称、版本、权限（`fs`、`dialog`）
- [x] `tauri/src/main.rs`：注册暴露给前端的 Commands
- [x] `tauri/src/commands.rs`：封装 `call_clojure(cmd, args)` → 调用 `java -jar` → 解析 stdout JSON
- [x] `tauri/src/config.rs`：使用 `dirs` crate 持久化配置到 `%APPDATA%/gsein-war3/config.json`
- [ ] 验证：`tauri dev` 能正常启动桌面窗口（待 GUI 环境验证）

### 2.3 Vue 3 前端骨架
- [x] `frontend/package.json`：安装 Vue 3 + TypeScript + Vite + Pinia + Vue Router + Element Plus
- [x] `frontend/src/main.ts`：应用入口初始化
- [x] `frontend/src/App.vue`：主布局（侧边栏导航 + 内容区）
- [x] `frontend/src/router/`：配置各功能页面路由
- [x] `frontend/src/stores/config.ts`：Pinia store 管理项目目录、工作区等配置
- [x] `frontend/src/api/tauri.ts`：封装 Tauri `invoke` 调用
- [x] `frontend/src/api/clojure.ts`：封装 Clojure CLI 调用，统一 loading / 错误 / 超时处理
- [ ] 验证：前端能成功调用 Rust Command 并返回 JSON（待端到端测试）

---

## 阶段 3：功能逐个接入前端

### P0 — 核心功能（优先实现）
- [ ] **BLP 图标生成器**：拖拽上传/选择图片，类型选择（主动/被动），批量处理，结果预览
  - 后端：`blp/generator.clj` 增加批量接口
- [ ] **可用 ID 查询**：选择项目目录，类型选择（单位/技能/物品等），显示下 N 个可用 ID
  - 后端：`lni/available_id.clj` 保持
- [ ] **模板生成器 — 通魔技能**：表单填写参数，实时预览 LNI 文本，一键导出
  - 后端：`tools/general_skill_generator.clj` 包装为 handler
- [ ] **模板生成器 — 单位批量生成**：表单 + 批量输入，实时预览，导出
  - 后端：`tools/unit_generator.clj` 包装为 handler
- [ ] **模板生成器 — 物品批量生成**：表单 + 批量输入，实时预览，导出
  - 后端：`tools/item_generator.clj` 包装为 handler
- [ ] **模板生成器 — 塔生成（造塔技能 + 造塔物品）**：表单填写，一键生成
  - 后端：`tools/tower_generator.clj` 包装为 handler
- [ ] **模板生成器 — 任务/NPC/装饰物**：其他模板页面接入
  - 后端：`tools/task_item_generator.clj` 等包装为 handler

### P1 — 资源处理工具
- [ ] **MDX BLP 路径替换**：选择 MDX 文件，输入旧/新路径，执行替换
  - 后端：`mdx/converter.clj` 包装为 handler
- [ ] **MDX 模型分类整理**：选择源目录和目标目录，执行分类，显示进度
  - 后端：`mdx/classifier.clj` 包装为 handler
- [ ] **LoadingScreen 切割**：选择图片，自动四宫格切割，导出 BLP
  - 后端：`tools/image_splitter.clj` 包装为 handler
- [ ] **头顶称号生成**：输入文字/选择图片，颜色/背景选择，生成 BLP + MDX
  - 后端：`tools/title_generator.clj` 字体参数化改造后包装为 handler

### P2 — 辅助工具
- [ ] **Excel 转 LNI**：选择 Excel 文件，配置列映射，预览表格，导出 LNI
  - 后端：`tools/xls_to_lni.clj` 参数化映射后包装为 handler
- [ ] **JASS 常量替换**：选择 JASS 目录，读取常量定义，执行字面量替换
  - 后端：`tools/constant_replacer.clj` 包装为 handler
- [ ] **单位圆形分布计算**：输入中心坐标、距离、数量，生成坐标列表
  - 后端：`tools/unit_placer.clj` 包装为 handler
- [ ] **文本搜索**：输入关键词，选择搜索目录，显示结果文件列表
  - 后端：`tools/text_searcher.clj` 包装为 handler
- [ ] **进制转换工具**：FourCC / Hex / Decimal 互转页面
  - 后端：`tools/number_base_converter.clj` 包装为 handler

### P3 — 纯前端/独立工具
- [ ] **彩色文本生成器**：替代原 Python/Tkinter 工具，纯 Vue 组件实现（颜色选择 + `|cRRGGBBAA` 格式输出）
  - 无需 Clojure 后端

---

## 阶段 4：打包与发布

- [ ] `scripts/build.bat`：Windows 本地一键构建脚本（`lein uberjar` → `tauri build`）
- [ ] `.github/workflows/release.yml`：GitHub Actions 自动构建 Windows 安装包
- [ ] `tauri/tauri.conf.json`：配置打包图标、窗口参数、NSIS 安装器
- [ ] JRE 处理策略选型（运行时检测提示 / jlink 精简 JRE 打包）
- [ ] 验证：`tauri build` 成功生成 `.msi`
- [ ] 验证：在干净 Windows 环境安装并运行核心功能（BLP 生成 + 模板渲染）
- [ ] 更新 `README.md`：改为 Tauri 桌面版使用说明

---

## 阶段 5：优化与清理（可选）

- [ ] 移除 `resources/config.edn` 中残余的无用配置
- [ ] 清理各 `tools/*.clj` 中不再需要的 `comment` 块（或迁移到 `doc/examples/`）
- [ ] 前端增加深色模式支持
- [ ] 前端增加操作日志/历史记录面板
- [ ] 评估是否用 Rust 重写部分纯工具（如 `text_searcher`、`number_base_converter`），减少 JVM 调用次数
