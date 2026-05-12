# gsein-war3

基于 Tauri + Vue 3 + Clojure 的 Warcraft 3 地图开发桌面工具集。

## 功能概览

| 模块 | 功能 | 说明 |
|------|------|------|
| BLP 工具 | BLP 图标生成器 | 将 PNG/JPG 批量转为 War3 可用的 BLP 图标，支持主动/被动类型 |
| ID 管理 | 可用 ID 查询 | 自动计算 War3 四字符 ID 的下一个可用值（256 进制） |
| 模板生成 | 通魔技能生成器 | 填写参数一键生成通魔技能 LNI 配置 |
| | 单位批量生成 | 批量创建自定义单位定义 |
| | 物品批量生成 | 批量创建自定义物品定义 |
| | 塔生成器 | 一键生成造塔技能 + 造塔物品组合 |
| | 任务/NPC/装饰物 | 其他常用模板页面 |
| MDX 工具 | 贴图路径替换 | 批量修改 MDX 模型内的 BLP 路径 |
| | 模型分类整理 | 自动将 MDX 按类型归类到独立文件夹 |
| 资源处理 | LoadingScreen 切割 | 将图片自动四宫格切割并导出 BLP |
| | 头顶称号生成 | 输入文字生成 BLP + MDX 称号模型 |
| 数据转换 | Excel 转 LNI | 将 Excel 表格批量转为 LNI 配置文件 |
| | JASS 常量替换 | 将 JASS 代码中的字面量替换为常量定义 |
| 辅助工具 | 单位圆形分布计算 | 输入中心、距离、数量生成坐标列表 |
| | 文本搜索 | 在指定目录中搜索关键词 |
| | 进制转换 | FourCC / Hex / Decimal 互转 |
| | 彩色文本生成器 | 生成 War3 `\|cRRGGBBAA` 格式的彩色文本 |

## 系统要求

- **操作系统**：Windows 10 / Windows 11
- **Java**：Java 8 或更高版本（推荐 Java 21 LTS）
  - 如未安装，应用启动时会提示，请前往 [Adoptium](https://adoptium.net/) 下载

## 安装与使用

### 方式一：下载安装包（推荐）

1. 访问 [Releases](https://github.com/lhing17/gsein-war3/releases) 页面
2. 下载最新版本的 `.msi` 安装包
3. 双击运行安装程序，按向导完成安装
4. 从开始菜单或桌面快捷方式启动应用

### 方式二：源码构建

#### 环境准备

- [Java 21+](https://adoptium.net/)
- [Node.js 20+](https://nodejs.org/)
- [Rust](https://rustup.rs/)
- [Leiningen](https://leiningen.org/)

#### 构建步骤

```bash
# 1. 克隆仓库
git clone https://github.com/lhing17/gsein-war3.git
cd gsein-war3

# 2. 一键构建（Windows）
scripts\build.bat

# 或手动分步构建：
# 2a. 构建 Clojure uberjar
cd clojure
lein uberjar
cd ..

# 2b. 安装并构建前端
cd frontend
npm install
npm run build
cd ..

# 2c. 构建 Tauri 桌面应用
cd tauri
cargo tauri build
cd ..
```

构建完成后，安装包位于 `tauri/target/release/bundle/msi/` 目录下。

## 开发说明

### 项目结构

```
gsein-war3/
├── clojure/          # Clojure 后端逻辑（工具实现）
│   ├── src/          # 源代码
│   ├── resources/    # 模板与配置
│   └── project.clj   # Leiningen 构建配置
├── frontend/         # Vue 3 前端
│   ├── src/          # 页面组件与 API 封装
│   └── package.json  # npm 依赖
├── tauri/            # Tauri Rust 后端
│   ├── src/          # Commands 与配置持久化
│   └── Cargo.toml    # Rust 依赖
└── scripts/          # 构建脚本
```

### 启动开发服务器

```bash
# 终端 1：启动 Clojure REPL（可选，用于后端开发）
cd clojure
lein repl

# 终端 2：启动 Tauri 开发模式（自动启动前端 dev server）
cd tauri
cargo tauri dev
```

## JRE 处理策略

当前版本采用**运行时检测提示**策略：

- 应用启动时自动检测系统是否已安装 Java
- 如未安装，通过 UI 弹窗提示用户前往 Adoptium 下载
- 此策略避免了将完整 JRE 打包进安装包，显著减小分发体积

未来可考虑使用 `jlink` 生成精简 JRE 并随应用打包，实现零依赖安装。

## License

MIT License
