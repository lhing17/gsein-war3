<template>
  <el-container class="app-container">
    <el-aside width="var(--gw-sidebar-width)" class="sidebar">
      <div class="logo">
        <span class="logo-icon">GW</span>
        <span class="logo-text">gsein-war3</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        class="menu"
        background-color="transparent"
        text-color="var(--gw-text-secondary)"
        active-text-color="var(--gw-accent-gold)"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/blp">
          <el-icon><Picture /></el-icon>
          <span>BLP 生成</span>
        </el-menu-item>
        <el-menu-item index="/ids">
          <el-icon><Key /></el-icon>
          <span>可用 ID</span>
        </el-menu-item>
        <el-sub-menu index="/templates">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>模板生成</span>
          </template>
          <el-menu-item index="/templates/general-skill">通魔技能</el-menu-item>
          <el-menu-item index="/templates/units">单位批量生成</el-menu-item>
          <el-menu-item index="/templates/items">物品批量生成</el-menu-item>
          <el-menu-item index="/templates/towers">塔生成</el-menu-item>
          <el-menu-item index="/templates/tasks">任务/NPC/装饰物</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/tools">
          <template #title>
            <el-icon><Tools /></el-icon>
            <span>工具箱</span>
          </template>
          <el-menu-item index="/tools/mdx-replace">MDX 贴图替换</el-menu-item>
          <el-menu-item index="/tools/mdx-classify">MDX 分类整理</el-menu-item>
          <el-menu-item index="/tools/image-split">图片切割</el-menu-item>
          <el-menu-item index="/tools/title">头顶称号</el-menu-item>
          <el-menu-item index="/tools/xls-to-lni">Excel 转 LNI</el-menu-item>
          <el-menu-item index="/tools/constant-replace">常量替换</el-menu-item>
          <el-menu-item index="/tools/unit-place">单位分布计算</el-menu-item>
          <el-menu-item index="/tools/text-search">文本搜索</el-menu-item>
          <el-menu-item index="/tools/fourcc">进制转换</el-menu-item>
          <el-menu-item index="/tools/color-text">彩色文本</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="header-title">Warcraft 3 地图开发工具</span>
        <div class="header-accent"></div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { ElNotification } from 'element-plus'
import { HomeFilled, Picture, Key, Document, Tools } from '@element-plus/icons-vue'
import { checkJava } from './api/tauri'

onMounted(async () => {
  try {
    const result = await checkJava()
    if (!result.installed) {
      ElNotification({
        title: 'Java 运行时未检测到',
        message: '本工具需要 Java 8 或更高版本。请访问 https://adoptium.net/ 下载并安装后重新启动应用。',
        type: 'warning',
        duration: 0,
      })
    }
  } catch (e) {
    // Silent fail - backend may be older version without check_java
  }
})
</script>

<style scoped>
.app-container {
  height: 100vh;
  background-color: var(--gw-bg-deep);
}
.sidebar {
  background: linear-gradient(180deg, var(--gw-bg-base) 0%, var(--gw-bg-deep) 100%);
  border-right: 1px solid var(--gw-border-default);
}
.logo {
  height: var(--gw-header-height);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid var(--gw-border-default);
  background: rgba(0, 0, 0, 0.2);
}
.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background: linear-gradient(135deg, var(--gw-accent-gold) 0%, var(--gw-accent-gold-light) 100%);
  color: var(--gw-text-inverse);
  font-weight: 800;
  font-size: 13px;
  letter-spacing: 0.5px;
  box-shadow: var(--gw-shadow-glow);
}
.logo-text {
  font-weight: 700;
  font-size: 16px;
  color: var(--gw-text-primary);
  letter-spacing: 0.3px;
}
.menu {
  border-right: none;
  --el-menu-bg-color: transparent;
  --el-menu-hover-bg-color: var(--gw-bg-hover);
  --el-menu-item-height: 46px;
}
.menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(201, 162, 39, 0.12) 0%, transparent 100%);
  border-left: 3px solid var(--gw-accent-gold);
}
.menu :deep(.el-sub-menu__title:hover) {
  background-color: var(--gw-bg-hover);
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--gw-border-default);
  background: var(--gw-bg-base);
  box-shadow: var(--gw-shadow-sm);
  position: relative;
  overflow: hidden;
}
.header-title {
  font-weight: 600;
  font-size: 15px;
  color: var(--gw-text-primary);
  letter-spacing: 0.3px;
}
.header-accent {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 120px;
  height: 2px;
  background: linear-gradient(90deg, var(--gw-accent-gold) 0%, transparent 100%);
  opacity: 0.6;
}
.main {
  background: var(--gw-bg-deep);
  color: var(--gw-text-primary);
}
</style>
