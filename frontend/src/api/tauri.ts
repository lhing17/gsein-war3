import { invoke } from '@tauri-apps/api/core'

export interface AppConfig {
  project_dir?: string
  workspace?: string
  out_dir?: string
  temp_dir?: string
}

export interface JavaCheckResult {
  installed: boolean
  version?: string
  error?: string
}

export async function getConfig(): Promise<AppConfig> {
  return invoke('get_config')
}

export async function setConfig(config: AppConfig): Promise<void> {
  return invoke('set_config', { config })
}

export async function checkJava(): Promise<JavaCheckResult> {
  return invoke('check_java')
}
