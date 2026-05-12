<template>
  <div class="page">
    <h2>任务/NPC/装饰物生成</h2>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form label-width="120px" class="form">
          <el-form-item label="任务列表">
            <el-table :data="tasks" border size="small" style="width: 100%">
              <el-table-column prop="name" label="名称" width="120">
                <template #default="{ row }">
                  <el-input v-model="row.name" size="small" />
                </template>
              </el-table-column>
              <el-table-column prop="description" label="说明">
                <template #default="{ row }">
                  <el-input v-model="row.description" size="small" type="textarea" :rows="2" />
                </template>
              </el-table-column>
              <el-table-column prop="hint" label="提示">
                <template #default="{ row }">
                  <el-input v-model="row.hint" size="small" type="textarea" :rows="2" />
                </template>
              </el-table-column>
              <el-table-column prop="difficulty" label="难度" width="120">
                <template #default="{ row }">
                  <el-input v-model="row.difficulty" size="small" />
                </template>
              </el-table-column>
              <el-table-column prop="award" label="奖励">
                <template #default="{ row }">
                  <el-input v-model="row.award" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template #default="{ $index }">
                  <el-button type="danger" size="small" @click="removeTask($index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item>
            <el-button @click="addTask">添加任务</el-button>
            <el-button type="primary" :loading="loading" @click="generate">生成</el-button>
            <el-button @click="copyResult">复制结果</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="12">
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

interface Task {
  name: string
  description: string
  hint: string
  difficulty: string
  award: string
}

const tasks = reactive<Task[]>([
  { name: '新手任务', description: '', hint: '', difficulty: '简单', award: '' },
])

const loading = ref(false)
const result = ref('')

function addTask() {
  tasks.push({ name: '', description: '', hint: '', difficulty: '', award: '' })
}

function removeTask(index: number) {
  tasks.splice(index, 1)
}

function toEdnTasks(list: Task[]): string {
  const items = list.map(t => `{:name "${t.name}" :description "${t.description}" :hint "${t.hint}" :difficulty "${t.difficulty}" :award "${t.award}"}`)
  return '[' + items.join(' ') + ']'
}

async function generate() {
  const valid = tasks.filter(t => t.name)
  if (!valid.length) {
    ElMessage.warning('请填写至少一个任务')
    return
  }
  loading.value = true
  try {
    const res = await callClojure('generate-tasks', [
      '-t', toEdnTasks(valid),
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
  max-width: 800px;
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
