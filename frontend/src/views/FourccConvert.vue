<template>
  <div class="page">
    <h2>进制转换工具</h2>
    <el-form label-width="140px" class="form">
      <el-form-item label="数值">
        <el-input v-model="form.value" placeholder="输入数值" />
      </el-form-item>
      <el-form-item label="转换模式">
        <el-radio-group v-model="form.mode">
          <el-radio-button label="fourcc">Decimal → FourCC</el-radio-button>
          <el-radio-button label="hex-to-fourcc">Hex → FourCC</el-radio-button>
          <el-radio-button label="fourcc-to-decimal">FourCC → Decimal</el-radio-button>
          <el-radio-button label="fourcc-to-hex">FourCC → Hex</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="convert">转换</el-button>
        <el-button @click="copyResult">复制结果</el-button>
      </el-form-item>
      <el-form-item v-if="result" label="结果">
        <el-input v-model="result" readonly />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'

const form = reactive({
  value: '',
  mode: 'fourcc',
})

const loading = ref(false)
const result = ref('')

async function convert() {
  if (!form.value) {
    ElMessage.warning('请输入数值')
    return
  }
  loading.value = true
  try {
    const res = await callClojure('fourcc-convert', [
      '-v', form.value,
      '-m', form.mode,
    ])
    if (res.success) {
      const parsed = JSON.parse(res.stdout)
      result.value = parsed.output || ''
    } else {
      ElMessage.error(res.stderr || '转换失败')
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
</style>
