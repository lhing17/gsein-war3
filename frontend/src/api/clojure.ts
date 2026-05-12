import { invoke } from '@tauri-apps/api/core'

export interface ClojureResult {
  success: boolean
  stdout: string
  stderr: string
}

export async function callClojure(cmd: string, args: string[]): Promise<ClojureResult> {
  return invoke('call_clojure', { cmd, args })
}
