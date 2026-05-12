<template>
  <div class="page">
    <h2>塔生成（造塔技能 + 造塔物品）</h2>
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
          <el-form-item label="单位 LNI 文件">
            <el-input v-model="form.lniFile" placeholder="选择 table/unit.ini" readonly>
              <template #append>
                <el-button @click="pickLniFile">浏览</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="塔单位 ID">
            <el-input v-model="idsText" type="textarea" :rows="8" placeholder="每行一个塔单位 ID，如 O100" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="generate">生成</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="14">
        <el-card v-if="result">
          <template #header>造塔技能</template>
          <pre class="preview">{{ result.abilities }}</pre>
        </el-card>
        <el-card v-if="result" style="margin-top: 16px">
          <template #header>造塔物品</template>
          <pre class="preview">{{ result.items }}</pre>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'
import { pickDirectory, pickFile } from '@/api/dialog'

const form = reactive({
  projectDir: '',
  lniFile: '',
})
const idsText = ref('')
const loading = ref(false)
const result = ref<any>(null)

async function pickProjectDir() {
  const path = await pickDirectory()
  if (path) form.projectDir = path
}

async function pickLniFile() {
  const path = await pickFile({
    filters: [{ name: 'INI files', extensions: ['ini'] }],
  })
  if (path) form.lniFile = path
}

function toEdnVector(arr: string[]): string {
  return '[' + arr.map(s => '"' + s + '"').join(' ') + ']'
}

async function generate() {
  if (!form.projectDir || !form.lniFile) {
    ElMessage.warning('请填写所有必填项')
    return
  }
  const ids = idsText.value.split('\n').map(s => s.trim()).filter(Boolean)
  if (!ids.length) {
    ElMessage.warning('请输入至少一个塔单位 ID')
    return
  }
  loading.value = true
  try {
    const res = await callClojure('generate-towers', [
      '-p', form.projectDir,
      '-l', form.lniFile,
      '-i', toEdnVector(ids),
    ])
    if (res.success) {
      const parsed = JSON.parse(res.stdout)
      result.value = parsed.output || {}
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
.preview {
  white-space: pre-wrap;
  word-break: break-all;
  font-family: monospace;
  font-size: 12px;
  max-height: 40vh;
  overflow: auto;
}
</style>
