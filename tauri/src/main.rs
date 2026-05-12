mod commands;
mod config;

fn main() {
    tauri::Builder::default()
        .plugin(tauri_plugin_fs::init())
        .plugin(tauri_plugin_dialog::init())
        .invoke_handler(tauri::generate_handler![
            commands::call_clojure,
            commands::get_config,
            commands::set_config,
            commands::check_java,
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
