<template>
  <div class="page">
    <h2>文本搜索</h2>
    <el-row :gutter="20">
      <el-col :span="10">
        <el-form label-width="120px" class="form">
          <el-form-item label="搜索关键词">
            <el-input v-model="form.text" />
          </el-form-item>
          <el-form-item label="搜索目录">
            <el-input v-model="form.dir" placeholder="选择搜索目录" readonly>
              <template #append>
                <el-button @click="pickDir">浏览</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="search">搜索</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="14">
        <el-card v-if="files.length">
          <template #header>结果 (最多 10 个)</template>
          <el-table :data="files" border size="small">
            <el-table-column type="index" width="50" />
            <el-table-column prop="path" label="文件路径" show-overflow-tooltip />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'
import { pickDirectory } from '@/api/dialog'

const form = reactive({
  text: '',
  dir: '',
})

const loading = ref(false)
const files = ref<any[]>([])

async function pickDir() {
  const path = await pickDirectory()
  if (path) form.dir = path
}

async function search() {
  if (!form.text || !form.dir) {
    ElMessage.warning('请填写关键词和目录')
    return
  }
  loading.value = true
  try {
    const res = await callClojure('text-search', [
      '-t', form.text,
      '-d', form.dir,
    ])
    if (res.success) {
      const parsed = eval(res.stdout)
      const paths: string[] = parsed.output || []
      files.value = paths.map(p => ({ path: p }))
      ElMessage.success(`找到 ${files.value.length} 个文件`)
    } else {
      ElMessage.error(res.stderr || '搜索失败')
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
</style>
