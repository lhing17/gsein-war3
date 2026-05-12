<template>
  <div class="page">
    <h2>单位圆形分布计算</h2>
    <el-row :gutter="20">
      <el-col :span="10">
        <el-form label-width="120px" class="form">
          <el-form-item label="中心 X">
            <el-input-number v-model="form.centerX" />
          </el-form-item>
          <el-form-item label="中心 Y">
            <el-input-number v-model="form.centerY" />
          </el-form-item>
          <el-form-item label="距离">
            <el-input-number v-model="form.dist" :min="0" />
          </el-form-item>
          <el-form-item label="数量">
            <el-input-number v-model="form.count" :min="1" :max="100" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="calculate">计算</el-button>
            <el-button @click="copyResult">复制结果</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="14">
        <el-card v-if="result.length">
          <template #header>坐标列表</template>
          <el-table :data="result" border size="small">
            <el-table-column type="index" width="50" />
            <el-table-column prop="x" label="X" />
            <el-table-column prop="y" label="Y" />
            <el-table-column prop="angle" label="角度" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { callClojure } from '@/api/clojure'

const form = reactive({
  centerX: 0,
  centerY: 0,
  dist: 330,
  count: 13,
})

const loading = ref(false)
const result = ref<any[]>([])

async function calculate() {
  loading.value = true
  try {
    const res = await callClojure('unit-place', [
      '-x', String(form.centerX),
      '-y', String(form.centerY),
      '-d', String(form.dist),
      '-c', String(form.count),
    ])
    if (res.success) {
      const parsed = JSON.parse(res.stdout)
      const lines: string[] = parsed.output || []
      result.value = lines.map(line => {
        const [x, y, angle] = line.split(',').map(s => s.trim())
        return { x, y, angle }
      })
    } else {
      ElMessage.error(res.stderr || '计算失败')
    }
  } catch (e: any) {
    ElMessage.error(e.message || '请求失败')
  } finally {
    loading.value = false
  }
}

function copyResult() {
  if (!result.value.length) return
  const text = result.value.map(r => `${r.x}, ${r.y}, ${r.angle}`).join('\n')
  navigator.clipboard.writeText(text)
  ElMessage.success('已复制到剪贴板')
}
</script>

<style scoped>
.form {
  max-width: 600px;
}
</style>
