<template>
  <div class="page">
    <h2>可用 ID 查询</h2>
    <el-form label-width="120px" class="form">
      <el-form-item label="项目目录">
        <el-input v-model="form.projectDir" placeholder="选择项目目录" readonly>
          <template #append>
            <el-button @click="pickProjectDir">浏览</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="form.type" placeholder="选择类型">
          <el-option label="技能 (ability)" value="ability" />
          <el-option label="物品 (item)" value="item" />
          <el-option label="单位 (unit)" value="unit" />
          <el-option label="英雄 (hero)" value="hero" />
          <el-option label="Buff (buff)" value="buff" />
          <el-option label="装饰物 (doodad)" value="doodad" />
        </el-select>
      </el-form-item>
      <el-form-item label="数量">
        <el-input-number v-model="form.count" :min="1" :max="100" />
      </el-form-item>
      <el-form-item label="起始 ID">
        <el-input v-model="form.startId" placeholder="可选，如 A000 / I000" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="query">查询</el-button>
      </el-form-item>
    </el-form>
    <el-card v-if="ids.length" class="result">
      <template #header>可用 ID 列表</template>
      <el-tag v-for="id in ids" :key="id" class="id-tag">{{ id }}</el-tag>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'
import { pickDirectory } from '@/api/dialog'

const form = reactive({
  projectDir: '',
  type: 'ability',
  count: 10,
  startId: '',
})

const loading = ref(false)
const ids = ref<string[]>([])

async function pickProjectDir() {
  const path = await pickDirectory()
  if (path) form.projectDir = path
}

async function query() {
  if (!form.projectDir) {
    ElMessage.warning('请选择项目目录')
    return
  }
  loading.value = true
  try {
    const args = [
      '-p', form.projectDir,
      '-t', form.type,
      '-c', String(form.count),
    ]
    if (form.startId) {
      args.push('-s', form.startId)
    }
    const res = await callClojure('available-ids', args)
    if (res.success) {
      const parsed = eval(res.stdout)
      ids.value = parsed.output || []
      ElMessage.success(`查询到 ${ids.value.length} 个可用 ID`)
    } else {
      ElMessage.error(res.stderr || '查询失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '请求失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.form {
  max-width: 600px;
}
.result {
  margin-top: 20px;
}
.id-tag {
  margin: 4px;
}
</style>
