<template>
  <div class="page">
    <h2>Excel 转 LNI</h2>
    <el-row :gutter="20">
      <el-col :span="10">
        <el-form label-width="120px" class="form">
          <el-form-item label="Excel 文件">
            <el-input v-model="form.xlsFile" placeholder="选择 Excel 文件" readonly>
              <template #append>
                <el-button @click="pickXls">浏览</el-button>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item label="Sheet 名称">
            <el-input v-model="form.sheet" />
          </el-form-item>
          <el-form-item label="列映射">
            <el-input v-model="form.columns" placeholder='如 {"A" "name" "B" "type"}' />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="convert">读取</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="14">
        <el-card v-if="rows.length">
          <template #header>数据预览 (前 10 行)</template>
          <el-table :data="rows.slice(0, 10)" border size="small">
            <el-table-column v-for="key in rowKeys" :key="key" :prop="key" :label="key" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'
import { pickFile } from '@/api/dialog'

const form = reactive({
  xlsFile: '',
  sheet: '',
  columns: '',
})

const loading = ref(false)
const rows = ref<any[]>([])

const rowKeys = computed(() => {
  if (!rows.value.length) return []
  return Object.keys(rows.value[0])
})

async function pickXls() {
  const path = await pickFile({ filters: [{ name: 'Excel', extensions: ['xls', 'xlsx'] }] })
  if (path) form.xlsFile = path
}

async function convert() {
  if (!form.xlsFile || !form.sheet) {
    ElMessage.warning('请选择文件并填写 Sheet 名称')
    return
  }
  loading.value = true
  try {
    const args = [
      '-x', form.xlsFile,
      '-s', form.sheet,
    ]
    if (form.columns) {
      args.push('-c', form.columns)
    }
    const res = await callClojure('xls-to-lni', args)
    if (res.success) {
      const parsed = eval(res.stdout)
      rows.value = parsed.output || []
      ElMessage.success(`读取到 ${rows.value.length} 行数据`)
    } else {
      ElMessage.error(res.stderr || '读取失败')
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
