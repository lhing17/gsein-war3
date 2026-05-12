<template>
  <div class="page">
    <h2>单位批量生成</h2>
    <el-row :gutter="20">
      <el-col :span="10">
        <el-form label-width="120px" class="form">
          <el-form-item label="项目目录">
            <el-input v-model="form.projectDir" placeholder="选择项目目录" readonly>
              <template #append>
                <el-button @click="pickProjectDir">浏览</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="单位类型">
            <el-input v-model="form.unitType" placeholder="如 普通 / BOSS" />
          </el-form-item>
          <el-form-item label="名称列表">
            <el-input v-model="namesText" type="textarea" :rows="10" placeholder="每行一个单位名称" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="generate">生成</el-button>
            <el-button @click="copyResult">复制结果</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="14">
        <el-card>
          <template #header>预览</template>
          <pre class="preview">{{ result }}</pre>
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
  projectDir: '',
  unitType: '普通',
})
const namesText = ref('')
const loading = ref(false)
const result = ref('')

async function pickProjectDir() {
  const path = await pickDirectory()
  if (path) form.projectDir = path
}

function toEdnVector(arr: string[]): string {
  return '[' + arr.map(s => '"' + s + '"').join(' ') + ']'
}

async function generate() {
  if (!form.projectDir) {
    ElMessage.warning('请选择项目目录')
    return
  }
  const names = namesText.value.split('\n').map(s => s.trim()).filter(Boolean)
  if (!names.length) {
    ElMessage.warning('请输入至少一个单位名称')
    return
  }
  loading.value = true
  try {
    const res = await callClojure('generate-units', [
      '-p', form.projectDir,
      '-t', form.unitType,
      '-n', toEdnVector(names),
    ])
    if (res.success) {
      const parsed = eval(res.stdout)
      result.value = parsed.output || ''
    } else {
      ElMessage.error(res.stderr || '生成失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '请求失败')
  } finally {
    loading.value = false
  }
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
  font-size: 12px;
  max-height: 70vh;
  overflow: auto;
}
</style>
