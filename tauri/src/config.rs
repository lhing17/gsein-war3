use serde::{Deserialize, Serialize};
use std::fs;
use std::path::PathBuf;

#[derive(Serialize, Deserialize, Debug, Clone, Default)]
pub struct AppConfig {
    pub project_dir: Option<String>,
    pub workspace: Option<String>,
    pub out_dir: Option<String>,
    pub temp_dir: Option<String>,
}

fn config_path() -> Result<PathBuf, Box<dyn std::error::Error>> {
    let app_data = dirs::data_dir().ok_or("Could not find app data directory")?;
    let dir = app_data.join("gsein-war3");
    if !dir.exists() {
        fs::create_dir_all(&dir)?;
    }
    Ok(dir.join("config.json"))
}

pub fn load_config() -> Result<AppConfig, Box<dyn std::error::Error>> {
    let path = config_path()?;
    if !path.exists() {
        return Ok(AppConfig::default());
    }
    let content = fs::read_to_string(&path)?;
    let config: AppConfig = serde_json::from_str(&content)?;
    Ok(config)
}

pub fn save_config(config: &AppConfig) -> Result<(), Box<dyn std::error::Error>> {
    let path = config_path()?;
    let content = serde_json::to_string_pretty(config)?;
    fs::write(&path, content)?;
    Ok(())
}
