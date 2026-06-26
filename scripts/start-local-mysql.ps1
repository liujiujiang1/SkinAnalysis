$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$mysqlHome = "C:\Program Files\MySQL\MySQL Server 8.0"
$mysqld = Join-Path $mysqlHome "bin\mysqld.exe"
$mysql = Join-Path $mysqlHome "bin\mysql.exe"
$dataDir = Join-Path $repoRoot ".local-run\mysql-data"
$logDir = Join-Path $repoRoot ".local-run\logs"
$pidFile = Join-Path $dataDir "skinanalysis-mysql.pid"
$configFile = Join-Path $repoRoot ".local-run\mysql.ini"
$initSql = Join-Path $repoRoot "mysql\init\init.sql"

if (!(Test-Path $mysqld)) {
    throw "mysqld.exe not found at $mysqld. Please install MySQL Server 8.x or update this script."
}

New-Item -ItemType Directory -Force -Path $logDir | Out-Null

$mysqlHomeForIni = $mysqlHome.Replace("\", "/")
$dataDirForIni = $dataDir.Replace("\", "/")
$pidFileForIni = $pidFile.Replace("\", "/")
@"
[mysqld]
basedir="$mysqlHomeForIni"
datadir="$dataDirForIni"
port=3308
bind-address=127.0.0.1
pid-file="$pidFileForIni"
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
lower_case_table_names=1
default-authentication-plugin=mysql_native_password
"@ | Set-Content -Encoding ASCII $configFile

if (!(Test-Path $dataDir)) {
    New-Item -ItemType Directory -Force -Path $dataDir | Out-Null
    & $mysqld "--defaults-file=$configFile" --initialize-insecure
}

$existing = Get-NetTCPConnection -LocalPort 3308 -ErrorAction SilentlyContinue |
    Where-Object { $_.State -eq "Listen" } |
    Select-Object -First 1

if (!$existing) {
    Start-Process -FilePath $mysqld `
        -ArgumentList @(
            "--defaults-file=$configFile",
            "--console"
        ) `
        -RedirectStandardOutput (Join-Path $logDir "mysql.out.log") `
        -RedirectStandardError (Join-Path $logDir "mysql.err.log") `
        -WindowStyle Hidden | Out-Null
}

$ready = $false
$passwordArg = $null
for ($i = 0; $i -lt 30; $i++) {
    try {
        & $mysql --host=127.0.0.1 --port=3308 --user=root --protocol=tcp -e "SELECT 1" | Out-Null
        $ready = $true
        $passwordArg = $null
        break
    } catch {
        try {
            & $mysql --host=127.0.0.1 --port=3308 --user=root --password=root --protocol=tcp -e "SELECT 1" | Out-Null
            $ready = $true
            $passwordArg = "--password=root"
            break
        } catch {
            Start-Sleep -Seconds 1
        }
    }
}

if (!$ready) {
    throw "Local MySQL did not become ready. Check .local-run/logs/mysql.err.log."
}

$commonArgs = @("--host=127.0.0.1", "--port=3308", "--user=root", "--protocol=tcp", "--default-character-set=utf8mb4")
if ($passwordArg) {
    $commonArgs += $passwordArg
}

Get-Content -Raw -Encoding UTF8 $initSql | & $mysql @commonArgs --force

if (!$passwordArg) {
    & $mysql @commonArgs -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root'; FLUSH PRIVILEGES;"
}

Write-Host "Local MySQL is ready at 127.0.0.1:3308, database SkinAnalysis, user root, password root."
