use serde::{Deserialize, Serialize};
use std::process::Command;

use crate::config::{load_config, save_config, AppConfig};

#[derive(Serialize, Deserialize, Debug)]
pub struct ClojureResult {
    pub success: bool,
    pub stdout: String,
    pub stderr: String,
}

#[tauri::command]
pub async fn call_clojure(cmd: String, args: Vec<String>) -> Result<ClojureResult, String> {
    let jar_path = std::env::current_exe()
        .ok()
        .and_then(|p| p.parent().map(|p| p.to_path_buf()))
        .unwrap_or_default()
        .join("../clojure/target/uberjar/gsein-war3-0.1.0-SNAPSHOT-standalone.jar");

    let jar_path_str = jar_path.to_string_lossy().to_string();

    let output = Command::new("java")
        .arg("-jar")
        .arg(&jar_path_str)
        .arg(&cmd)
        .args(&args)
        .output()
        .map_err(|e| format!("Failed to execute java: {}", e))?;

    let stdout = String::from_utf8_lossy(&output.stdout).to_string();
    let stderr = String::from_utf8_lossy(&output.stderr).to_string();
    let success = output.status.success();

    Ok(ClojureResult {
        success,
        stdout,
        stderr,
    })
}

#[tauri::command]
pub async fn get_config() -> Result<AppConfig, String> {
    load_config().map_err(|e| e.to_string())
}

#[tauri::command]
pub async fn set_config(config: AppConfig) -> Result<(), String> {
    save_config(&config).map_err(|e| e.to_string())
}
