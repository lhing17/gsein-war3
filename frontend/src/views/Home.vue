<template>
  <div class="home">
    <h1><gw-icon icon="game-icons:castle" :size="28" glow /> gsein-war3 开发工具</h1>
    <p>选择左侧菜单开始使用各项功能。</p>
    <el-card class="config-card">
      <template #header>
        <span><gw-icon icon="game-icons:gear-hammer" :size="16" /> 项目配置</span>
      </template>
      <el-form label-width="120px">
        <el-form-item>
          <template #label>
            <span class="label-with-icon">
              <span>项目目录</span>
              <el-tooltip content="War3 地图项目的根目录，包含地图文件与资源">
                <gw-icon icon="game-icons:info" :size="16" class="info-icon" />
              </el-tooltip>
            </span>
          </template>
          <el-input v-model="config.project_dir" placeholder="请选择项目目录">
            <template #append>
              <el-button @click="pickDirectory('project_dir')">浏览</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <template #label>
            <span class="label-with-icon">
              <span>工作区</span>
              <el-tooltip content="用于存放中间生成文件和编辑工作区的目录">
                <gw-icon icon="game-icons:info" :size="16" class="info-icon" />
              </el-tooltip>
            </span>
          </template>
          <el-input v-model="config.workspace" placeholder="请选择工作区">
            <template #append>
              <el-button @click="pickDirectory('workspace')">浏览</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <template #label>
            <span class="label-with-icon">
              <span>输出目录</span>
              <el-tooltip content="最终生成文件（如 BLP、INI）的输出位置">
                <gw-icon icon="game-icons:info" :size="16" class="info-icon" />
              </el-tooltip>
            </span>
          </template>
          <el-input v-model="config.out_dir" placeholder="请选择输出目录">
            <template #append>
              <el-button @click="pickDirectory('out_dir')">浏览</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <template #label>
            <span class="label-with-icon">
              <span>临时目录</span>
              <el-tooltip content="程序运行过程中临时文件的存放位置">
                <gw-icon icon="game-icons:info" :size="16" class="info-icon" />
              </el-tooltip>
            </span>
          </template>
          <el-input v-model="config.temp_dir" placeholder="请选择临时目录">
            <template #append>
              <el-button @click="pickDirectory('temp_dir')">浏览</el-button>
            </template>
          </el-input>
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
import { open } from '@tauri-apps/plugin-dialog'
import GwIcon from '@/components/GwIcon.vue'
import { useConfigStore } from '@/stores/config'
import { ElMessage } from 'element-plus'

const store = useConfigStore()
const config = ref({ ...store.config })

onMounted(() => {
  store.load().then(() => {
    config.value = { ...store.config }
  })
})

async function pickDirectory(field: 'project_dir' | 'workspace' | 'out_dir' | 'temp_dir') {
  const dir = await open({ directory: true })
  if (dir) {
    config.value[field] = dir
  }
}

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
  padding: 24px;
}
.home h1 {
  font-size: 24px;
  font-weight: 700;
  color: var(--gw-text-primary);
  margin: 0 0 8px 0;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.home p {
  color: var(--gw-text-secondary);
  margin: 0 0 24px 0;
  font-size: 14px;
}
.config-card {
  max-width: 600px;
  margin-top: 20px;
  background: var(--gw-bg-surface);
  border: 1px solid var(--gw-border-default);
  border-radius: 10px;
  box-shadow: var(--gw-shadow-md);
}
.config-card :deep(.el-card__header) {
  border-bottom: 1px solid var(--gw-border-default);
  color: var(--gw-text-primary);
  font-weight: 600;
  padding: 16px 20px;
  background: var(--gw-bg-elevated);
  border-radius: 10px 10px 0 0;
  display: flex;
  align-items: center;
  gap: 8px;
}
.label-with-icon {
  display: inline-flex;
  align-items: center;
}
.info-icon {
  margin-left: 6px;
  color: var(--gw-text-muted);
  cursor: help;
  transition: color 0.2s;
}
.info-icon:hover {
  color: var(--gw-accent-gold);
}
</style>
