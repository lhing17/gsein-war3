<template>
  <el-container class="app-container">
    <el-aside width="200px" class="sidebar">
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
        <el-menu-item index="/templates">
          <el-icon><Document /></el-icon>
          <span>模板生成</span>
        </el-menu-item>
        <el-menu-item index="/tools">
          <el-icon><Tools /></el-icon>
          <span>工具箱</span>
        </el-menu-item>
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
import { HomeFilled, Picture, Key, Document, Tools } from '@element-plus/icons-vue'
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
