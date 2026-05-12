@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo [build] Starting gsein-war3 build...

:: Step 1: Build Clojure uberjar
echo [build] Building Clojure uberjar...
cd /d "%~dp0..\clojure"
call lein uberjar
if errorlevel 1 (
    echo [build] ERROR: lein uberjar failed
    exit /b 1
)

:: Step 2: Build Tauri app
echo [build] Building Tauri desktop app...
cd /d "%~dp0..\frontend"
call npm install
call npm run tauri build
if errorlevel 1 (
    echo [build] ERROR: tauri build failed
    exit /b 1
)

echo [build] All done!
endlocal
