<template>
  <div class="page">
    <h2>JASS 常量替换</h2>
    <el-form label-width="160px" class="form">
      <el-form-item label="JASS 文件">
        <el-input v-model="form.inputFile" placeholder="选择 JASS 文件" readonly>
          <template #append>
            <el-button @click="pickInput">浏览</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="常量定义文件">
        <el-input v-model="form.constantsFile" placeholder="选择包含 constant 定义的文件" readonly>
          <template #append>
            <el-button @click="pickConstants">浏览</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="replace">执行替换</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'
import { pickFile } from '@/api/dialog'

const form = reactive({
  inputFile: '',
  constantsFile: '',
})

const loading = ref(false)

async function pickInput() {
  const path = await pickFile({ filters: [{ name: 'JASS', extensions: ['j', 'jass'] }] })
  if (path) form.inputFile = path
}

async function pickConstants() {
  const path = await pickFile()
  if (path) form.constantsFile = path
}

async function replace() {
  if (!form.inputFile || !form.constantsFile) {
    ElMessage.warning('请选择两个文件')
    return
  }
  loading.value = true
  try {
    const res = await callClojure('constant-replace', [
      '-i', form.inputFile,
      '-c', form.constantsFile,
    ])
    if (res.success) {
      ElMessage.success('替换完成')
    } else {
      ElMessage.error(res.stderr || '替换失败')
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
