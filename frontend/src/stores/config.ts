import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getConfig, setConfig, type AppConfig } from '@/api/tauri'

export const useConfigStore = defineStore('config', () => {
  const config = ref<AppConfig>({})

  async function load() {
    config.value = await getConfig()
  }

  async function save(newConfig: AppConfig) {
    await setConfig(newConfig)
    config.value = newConfig
  }

  return { config, load, save }
})
