<template>
  <div class="page">
    <h2>MDX 模型分类整理</h2>
    <el-form label-width="140px" class="form">
      <el-form-item label="源目录">
        <el-input v-model="form.sourceDir" placeholder="选择源目录" readonly>
          <template #append>
            <el-button @click="pickSourceDir">浏览</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="输出目录">
        <el-input v-model="form.outDir" placeholder="选择输出目录" readonly>
          <template #append>
            <el-button @click="pickOutDir">浏览</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="操作模式">
        <el-radio-group v-model="form.mode">
          <el-radio-button label="copy">复制</el-radio-button>
          <el-radio-button label="move">移动</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="classify">执行分类</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'
import { pickDirectory } from '@/api/dialog'

const form = reactive({
  sourceDir: '',
  outDir: '',
  mode: 'copy',
})

const loading = ref(false)

async function pickSourceDir() {
  const path = await pickDirectory()
  if (path) form.sourceDir = path
}

async function pickOutDir() {
  const path = await pickDirectory()
  if (path) form.outDir = path
}

async function classify() {
  if (!form.sourceDir || !form.outDir) {
    ElMessage.warning('请选择源目录和输出目录')
    return
  }
  loading.value = true
  try {
    const res = await callClojure('mdx-classify', [
      '-s', form.sourceDir,
      '-o', form.outDir,
      '-m', form.mode,
    ])
    if (res.success) {
      ElMessage.success('分类整理完成')
    } else {
      ElMessage.error(res.stderr || '执行失败')
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
