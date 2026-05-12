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
    // Try multiple candidate paths for robustness across dev and production
    let candidates: Vec<std::path::PathBuf> = vec![
        // Production: resource dir next to exe
        app.path().resource_dir().ok().map(|d| d.join("gsein-war3.jar")),
        // Dev: relative to exe (target/debug/)
        std::env::current_exe()
            .ok()
            .and_then(|exe| exe.parent().map(|p| p.join("gsein-war3.jar"))),
        // Dev: relative to exe parent (target/)
        std::env::current_exe()
            .ok()
            .and_then(|exe| exe.parent().and_then(|p| p.parent()).map(|p| p.join("gsein-war3.jar"))),
        // Dev: fallback using exe -> project root -> clojure/target
        std::env::current_exe()
            .ok()
            .and_then(|exe| exe.parent().map(|p| p.to_path_buf()))
            .map(|p| p.join("../../../clojure/target/gsein-war3-0.1.0-SNAPSHOT-standalone.jar")),
    ]
    .into_iter()
    .flatten()
    .collect();

    let jar_path = candidates
        .iter()
        .find(|p| p.exists())
        .cloned()
        .ok_or_else(|| {
            let tried = candidates.iter().map(|p| p.display().to_string()).collect::<Vec<_>>().join(", ");
            format!("Clojure jar not found. Tried: {}", tried)
        })?;

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
