<template>
  <div class="page">
    <h2>彩色文本生成器</h2>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form label-width="100px" class="form">
          <el-form-item label="文本内容">
            <el-input v-model="text" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="文字颜色">
            <el-color-picker v-model="color" show-alpha />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="applyColor">添加颜色</el-button>
            <el-button @click="clearColor">清除颜色</el-button>
            <el-button @click="copyResult">复制结果</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>结果</template>
          <pre class="preview">{{ result }}</pre>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const text = ref('')
const color = ref('rgba(255, 255, 255, 1)')
const result = ref('')

function hex(n: number): string {
  return n.toString(16).padStart(2, '0').toUpperCase()
}

function applyColor() {
  if (!text.value) return
  const rgb = color.value.match(/rgba?\((\d+),\s*(\d+),\s*(\d+)/)
  if (!rgb) return
  const r = parseInt(rgb[1])
  const g = parseInt(rgb[2])
  const b = parseInt(rgb[3])
  result.value = `|cFF${hex(r)}${hex(g)}${hex(b)}${text.value}|r`
}

function clearColor() {
  if (!result.value) return
  result.value = result.value.replace(/\|c[A-Fa-f0-9]{8}/g, '').replace(/\|r/g, '')
}

function copyResult() {
  if (!result.value) return
  navigator.clipboard.writeText(result.value)
  ElMessage.success('已复制到剪贴板')
}
</script>

<style scoped>
.form {
  max-width: 600px;
}
.preview {
  white-space: pre-wrap;
  word-break: break-all;
  font-family: monospace;
  font-size: 14px;
}
</style>
