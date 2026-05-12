<template>
  <div class="page">
    <h2>BLP 图标生成器</h2>
    <el-form label-width="120px" class="form">
      <el-form-item label="图片文件">
        <el-input v-model="filesText" type="textarea" :rows="3" placeholder="选择图片文件" readonly>
          <template #append>
            <el-button @click="pickImages">浏览</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="图标类型">
        <el-radio-group v-model="form.type">
          <el-radio-button label="active">主动</el-radio-button>
          <el-radio-button label="passive">被动</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="临时目录">
        <el-input v-model="form.tempDir" placeholder="选择临时目录" readonly>
          <template #append>
            <el-button @click="pickTempDir">浏览</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="项目目录">
        <el-input v-model="form.projectDir" placeholder="选择项目目录" readonly>
          <template #append>
            <el-button @click="pickProjectDir">浏览</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="generate">批量生成 BLP</el-button>
      </el-form-item>
    </el-form>
    <el-card v-if="results.length" class="result">
      <template #header>处理结果</template>
      <el-table :data="results" border size="small">
        <el-table-column prop="file" label="文件" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.success ? 'success' : 'danger'">{{ row.success ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="信息" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'
import { pickFiles, pickDirectory } from '@/api/dialog'

const form = reactive({
  files: [] as string[],
  type: 'active',
  tempDir: '',
  projectDir: '',
})

const filesText = computed(() => form.files.join('\n'))
const loading = ref(false)
const results = ref<any[]>([])

async function pickImages() {
  const paths = await pickFiles({
    filters: [{ name: 'Images', extensions: ['png', 'jpg', 'jpeg', 'bmp', 'tga'] }],
  })
  if (paths) form.files = paths
}

async function pickTempDir() {
  const path = await pickDirectory()
  if (path) form.tempDir = path
}

async function pickProjectDir() {
  const path = await pickDirectory()
  if (path) form.projectDir = path
}

async function generate() {
  if (!form.files.length || !form.tempDir || !form.projectDir) {
    ElMessage.warning('请选择图片文件并填写所有必填项')
    return
  }
  loading.value = true
  results.value = []
  try {
    for (const file of form.files) {
      const res = await callClojure('blp-generate', [
        '-i', file,
        '-t', form.type,
        '-T', form.tempDir,
        '-p', form.projectDir,
      ])
      results.value.push({
        file,
        success: res.success,
        message: res.success ? '生成成功' : (res.stderr || '失败'),
      })
    }
    const successCount = results.value.filter(r => r.success).length
    ElMessage.success(`处理完成：${successCount}/${form.files.length} 成功`)
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
</style>
