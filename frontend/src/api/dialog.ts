import { open } from '@tauri-apps/plugin-dialog'

export async function pickFile(options?: { multiple?: boolean; filters?: { name: string; extensions: string[] }[] }): Promise<string | null> {
  const result = await open({ ...options, multiple: false })
  return result as string | null
}

export async function pickFiles(options?: { filters?: { name: string; extensions: string[] }[] }): Promise<string[] | null> {
  const result = await open({ ...options, multiple: true })
  return result as string[] | null
}

export async function pickDirectory(): Promise<string | null> {
  const result = await open({ directory: true, multiple: false })
  return result as string | null
}
