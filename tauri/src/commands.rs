use serde::{Deserialize, Serialize};
use std::process::Command;
use tauri::Manager;

use crate::config::{load_config, save_config, AppConfig};

#[derive(Serialize, Deserialize, Debug)]
pub struct ClojureResult {
    pub success: bool,
    pub stdout: String,
    pub stderr: String,
}

#[derive(Serialize, Deserialize, Debug)]
pub struct JavaCheckResult {
    pub installed: bool,
    pub version: Option<String>,
    pub error: Option<String>,
}

#[tauri::command]
pub async fn call_clojure(app: tauri::AppHandle, cmd: String, args: Vec<String>) -> Result<ClojureResult, String> {
    let jar_path = if let Ok(resource_dir) = app.path().resource_dir() {
        resource_dir.join("gsein-war3.jar")
    } else {
        std::env::current_exe()
            .ok()
            .and_then(|exe| exe.parent().map(|p| p.to_path_buf()))
            .map(|p| p.join("../../clojure/target/gsein-war3-0.1.0-SNAPSHOT-standalone.jar"))
            .ok_or_else(|| "Could not resolve jar path in dev or production".to_string())?
    };

    if !jar_path.exists() {
        return Err(format!("Clojure jar not found at: {}", jar_path.display()));
    }

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

#[tauri::command]
pub async fn check_java() -> Result<JavaCheckResult, String> {
    let output = Command::new("java")
        .arg("-version")
        .output();

    match output {
        Ok(out) => {
            let stderr = String::from_utf8_lossy(&out.stderr);
            // java -version writes to stderr
            let version_line = stderr.lines().next().unwrap_or("");
            let version = if version_line.contains("version") {
                version_line.split("\"").nth(1).map(|s| s.to_string())
            } else {
                None
            };

            Ok(JavaCheckResult {
                installed: true,
                version,
                error: None,
            })
        }
        Err(e) => {
            Ok(JavaCheckResult {
                installed: false,
                version: None,
                error: Some(format!("{}", e)),
            })
        }
    }
}
