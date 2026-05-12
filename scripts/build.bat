@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo [build] ==========================================
echo [build] gsein-war3 Desktop Build Script
echo [build] ==========================================

:: Detect project root (parent of scripts directory)
set "PROJECT_ROOT=%~dp0.."
cd /d "%PROJECT_ROOT%"
set "PROJECT_ROOT=%CD%"

echo [build] Project root: %PROJECT_ROOT%

:: ====== Environment Checks ======

echo [build] Checking prerequisites...

:: Check Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [build] ERROR: Java is not installed or not in PATH.
    echo [build] Please install Java 8+ from https://adoptium.net/
    exit /b 1
)
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set "JAVA_VERSION=%%g"
    set "JAVA_VERSION=!JAVA_VERSION:"=!"
)
echo [build] Java version: %JAVA_VERSION%

:: Check Node.js
call node --version >nul 2>&1
if errorlevel 1 (
    echo [build] ERROR: Node.js is not installed or not in PATH.
    echo [build] Please install Node.js 18+ from https://nodejs.org/
    exit /b 1
)
for /f "tokens=*" %%g in ('node --version') do set "NODE_VERSION=%%g"
echo [build] Node.js version: %NODE_VERSION%

:: Check npm
call npm --version >nul 2>&1
if errorlevel 1 (
    echo [build] ERROR: npm is not installed or not in PATH.
    exit /b 1
)

:: Check Rust / cargo
cargo --version >nul 2>&1
if errorlevel 1 (
    echo [build] ERROR: Rust / Cargo is not installed or not in PATH.
    echo [build] Please install Rust from https://rustup.rs/
    exit /b 1
)
for /f "tokens=*" %%g in ('cargo --version') do set "CARGO_VERSION=%%g"
echo [build] Cargo version: %CARGO_VERSION%

:: ====== Leiningen Setup ======

echo [build] Checking Leiningen...
set "LEIN_PATH=%PROJECT_ROOT%\clojure\.lein\lein.bat"
set "LEIN_DIR=%PROJECT_ROOT%\clojure\.lein"

if exist "%LEIN_PATH%" (
    echo [build] Using local Leiningen: %LEIN_PATH%
    set "LEIN_CMD=%LEIN_PATH%"
) else (
    :: Check global lein
    lein version >nul 2>&1
    if errorlevel 1 (
        echo [build] Leiningen not found. Downloading...
        if not exist "%LEIN_DIR%" mkdir "%LEIN_DIR%"
        powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein.bat' -OutFile '%LEIN_PATH%'" >nul 2>&1
        if errorlevel 1 (
            echo [build] ERROR: Failed to download lein.bat. Please install Leiningen manually.
            echo [build] Visit: https://leiningen.org/
            exit /b 1
        )
        :: Download lein jar as well
        powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein.ps1' -OutFile '%LEIN_DIR%\lein.ps1'" >nul 2>&1
        echo [build] Leiningen downloaded. First run may take a while to bootstrap...
    ) else (
        echo [build] Using global Leiningen
        set "LEIN_CMD=lein"
    )
)

:: ====== Step 1: Build Clojure Uberjar ======

echo [build] ==========================================
echo [build] Step 1/4: Building Clojure uberjar...
echo [build] ==========================================

cd /d "%PROJECT_ROOT%\clojure"

if not exist "%PROJECT_ROOT%\clojure\project.clj" (
    echo [build] ERROR: project.clj not found in clojure/ directory
    exit /b 1
)

call %LEIN_CMD% uberjar
if errorlevel 1 (
    echo [build] ERROR: lein uberjar failed
    exit /b 1
)

:: Verify jar exists
if not exist "%PROJECT_ROOT%\clojure\target\gsein-war3-0.1.0-SNAPSHOT-standalone.jar" (
    echo [build] ERROR: Uberjar not found at expected path:
    echo [build]   clojure\target\gsein-war3-0.1.0-SNAPSHOT-standalone.jar
    exit /b 1
)

echo [build] Clojure uberjar built successfully.

:: ====== Step 2: Install Frontend Dependencies ======

echo [build] ==========================================
echo [build] Step 2/4: Installing frontend dependencies...
echo [build] ==========================================

cd /d "%PROJECT_ROOT%\frontend"

call npm install
if errorlevel 1 (
    echo [build] ERROR: npm install failed
    exit /b 1
)

echo [build] Frontend dependencies installed.

:: ====== Step 3: Build Frontend ======

echo [build] ==========================================
echo [build] Step 3/4: Building frontend production bundle...
echo [build] ==========================================

cd /d "%PROJECT_ROOT%\frontend"

call npm run build
if errorlevel 1 (
    echo [build] ERROR: npm run build failed
    exit /b 1
)

echo [build] Frontend bundle built.

:: ====== Step 4: Build Tauri Desktop App ======

echo [build] ==========================================
echo [build] Step 4/4: Building Tauri desktop app...
echo [build] ==========================================

cd /d "%PROJECT_ROOT%\tauri"

:: Check if --debug flag is passed
set "TAURI_ARGS="
if "%1"=="--debug" (
    echo [build] Debug mode enabled
    set "TAURI_ARGS=--debug"
)

call npm run tauri build %TAURI_ARGS%
if errorlevel 1 (
    echo [build] ERROR: tauri build failed
    exit /b 1
)

:: ====== Done ======

echo [build] ==========================================
echo [build] Build completed successfully!
echo [build] ==========================================

:: Report output locations
if exist "%PROJECT_ROOT%\tauri\src-tauri\target\release\bundle\msi" (
    echo [build] MSI installer location:
    dir /b "%PROJECT_ROOT%\tauri\src-tauri\target\release\bundle\msi\*.msi" 2>nul
)

if exist "%PROJECT_ROOT%\tauri\target\release\bundle\msi" (
    echo [build] MSI installer location:
    dir /b "%PROJECT_ROOT%\tauri\target\release\bundle\msi\*.msi" 2>nul
)

endlocal
