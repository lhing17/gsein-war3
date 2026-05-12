<template>
  <div class="page">
    <h2>MDX BLP 路径替换</h2>
    <el-form label-width="140px" class="form">
      <el-form-item label="MDX 文件">
        <el-input v-model="form.mdxFile" placeholder="选择 MDX 文件" readonly>
          <template #append>
            <el-button @click="pickMdxFile">浏览</el-button>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="旧 BLP 路径">
        <el-input v-model="form.oldBlp" placeholder="如 war3mapImported\\old.blp" />
      </el-form-item>
      <el-form-item label="新 BLP 路径">
        <el-input v-model="form.newBlp" placeholder="如 war3mapImported\\new.blp" />
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
  mdxFile: '',
  oldBlp: '',
  newBlp: '',
})

const loading = ref(false)

async function pickMdxFile() {
  const path = await pickFile({ filters: [{ name: 'MDX', extensions: ['mdx'] }] })
  if (path) form.mdxFile = path
}

async function replace() {
  if (!form.mdxFile || !form.oldBlp || !form.newBlp) {
    ElMessage.warning('请填写所有必填项')
    return
  }
  loading.value = true
  try {
    const res = await callClojure('mdx-replace-blp', [
      '-m', form.mdxFile,
      '-o', form.oldBlp,
      '-n', form.newBlp,
    ])
    if (res.success) {
      ElMessage.success('替换成功')
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
