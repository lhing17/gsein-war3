<template>
  <div class="page">
    <h2>LoadingScreen 切割</h2>
    <el-form label-width="140px" class="form">
      <el-form-item label="图片文件">
        <el-input v-model="form.inputFile" placeholder="选择图片文件" readonly>
          <template #append>
            <el-button @click="pickImage">浏览</el-button>
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
      <el-form-item label="输出名称">
        <el-input v-model="namesText" type="textarea" :rows="3" placeholder="每行一个输出文件名，如 LoadingScreenTL" />
      </el-form-item>
      <el-form-item label="输出格式">
        <el-radio-group v-model="form.format">
          <el-radio-button label="blp">BLP</el-radio-button>
          <el-radio-button label="png">PNG</el-radio-button>
          <el-radio-button label="tga">TGA</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="split">执行切割</el-button>
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
  inputFile: '',
  outDir: '.',
  format: 'blp',
})

const namesText = ref('')
const loading = ref(false)

async function pickImage() {
  const path = await pickFile({ filters: [{ name: 'Images', extensions: ['png', 'jpg', 'jpeg', 'bmp', 'tga'] }] })
  if (path) form.inputFile = path
}

async function pickOutDir() {
  const path = await pickDirectory()
  if (path) form.outDir = path
}

function toEdnVector(arr: string[]): string {
  return '[' + arr.map(s => '"' + s + '"').join(' ') + ']'
}

async function split() {
  if (!form.inputFile) {
    ElMessage.warning('请选择图片文件')
    return
  }
  const names = namesText.value.split('\n').map(s => s.trim()).filter(Boolean)
  const args = [
    '-i', form.inputFile,
    '-o', form.outDir,
    '-f', form.format,
  ]
  if (names.length) {
    args.push('-n', toEdnVector(names))
  }
  loading.value = true
  try {
    const res = await callClojure('image-split', args)
    if (res.success) {
      ElMessage.success('切割完成')
    } else {
      ElMessage.error(res.stderr || '切割失败')
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
