<template>
  <div class="page">
    <h2>头顶称号生成</h2>
    <el-form label-width="140px" class="form">
      <el-form-item label="称号文字">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="颜色">
        <el-select v-model="form.color">
          <el-option label="蓝色" value="BLUE" />
          <el-option label="红色" value="RED" />
          <el-option label="绿色" value="GREEN" />
          <el-option label="黑色" value="BLACK" />
          <el-option label="白色" value="WHITE" />
        </el-select>
      </el-form-item>
      <el-form-item label="背景图片">
        <el-input v-model="form.wingFile" placeholder="可选" readonly>
          <template #append>
            <el-button @click="pickWingFile">浏览</el-button>
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
      <el-form-item label="字体名称">
        <el-input v-model="form.fontName" />
      </el-form-item>
      <el-form-item label="字体大小">
        <el-input-number v-model="form.fontSize" :min="10" :max="200" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="generate">生成</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'
import { pickFile, pickDirectory } from '@/api/dialog'

const form = reactive({
  name: '',
  color: 'BLUE',
  wingFile: '',
  outDir: '',
  fontName: '方正颜宋简体_粗',
  fontSize: 40,
})

const loading = ref(false)

async function pickWingFile() {
  const path = await pickFile({ filters: [{ name: 'Images', extensions: ['png', 'jpg', 'jpeg', 'bmp'] }] })
  if (path) form.wingFile = path
}

async function pickOutDir() {
  const path = await pickDirectory()
  if (path) form.outDir = path
}

async function generate() {
  if (!form.name || !form.outDir) {
    ElMessage.warning('请填写必填项')
    return
  }
  loading.value = true
  const args = [
    '-n', form.name,
    '-c', form.color,
    '-o', form.outDir,
    '-f', form.fontName,
    '-z', String(form.fontSize),
  ]
  if (form.wingFile) {
    args.push('-w', form.wingFile)
  }
  try {
    const res = await callClojure('title-generate', args)
    if (res.success) {
      ElMessage.success('生成成功')
    } else {
      ElMessage.error(res.stderr || '生成失败')
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
