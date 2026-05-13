# gsein-war3 改造 TODO 清单

> 按优先级与实施顺序排列。完成项请打勾，进行中项标注 `[WIP]`。

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
