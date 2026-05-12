<template>
  <div class="home">
    <h1>gsein-war3 开发工具</h1>
    <p>选择左侧菜单开始使用各项功能。</p>
    <el-card class="config-card">
      <template #header>
        <span>项目配置</span>
      </template>
      <el-form label-width="120px">
        <el-form-item label="项目目录">
          <el-input v-model="config.project_dir" placeholder="请选择项目目录" />
        </el-form-item>
        <el-form-item label="工作区">
          <el-input v-model="config.workspace" placeholder="请选择工作区" />
        </el-form-item>
        <el-form-item label="输出目录">
          <el-input v-model="config.out_dir" placeholder="请选择输出目录" />
        </el-form-item>
        <el-form-item label="临时目录">
          <el-input v-model="config.temp_dir" placeholder="请选择临时目录" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveConfig">保存配置</el-button>
          <el-button @click="loadConfig">加载配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useConfigStore } from '@/stores/config'
import { ElMessage } from 'element-plus'

const store = useConfigStore()
const config = ref({ ...store.config })

onMounted(() => {
  store.load().then(() => {
    config.value = { ...store.config }
  })
})

async function saveConfig() {
  await store.save(config.value)
  ElMessage.success('配置已保存')
}

async function loadConfig() {
  await store.load()
  config.value = { ...store.config }
  ElMessage.success('配置已加载')
}
</script>

<style scoped>
.home {
  padding: 20px;
}
.config-card {
  max-width: 600px;
  margin-top: 20px;
}
</style>
