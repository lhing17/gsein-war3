<template>
  <el-container class="app-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">gsein-war3</div>
      <el-menu
        :default-active="$route.path"
        router
        class="menu"
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
        <span>Warcraft 3 地图开发工具</span>
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
}
.sidebar {
  background: #f5f7fa;
  border-right: 1px solid #e4e7ed;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 18px;
  border-bottom: 1px solid #e4e7ed;
}
.menu {
  border-right: none;
}
.header {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e4e7ed;
  font-weight: 500;
}
.main {
  background: #fff;
}
</style>
