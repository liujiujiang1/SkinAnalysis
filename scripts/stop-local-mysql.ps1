$ErrorActionPreference = "SilentlyContinue"

$mysql = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqladmin.exe"

if (Test-Path $mysql) {
    & $mysql -h127.0.0.1 -P3308 -uroot -proot --protocol=tcp shutdown
}

$listener = Get-NetTCPConnection -LocalPort 3308 -ErrorAction SilentlyContinue |
    Where-Object { $_.State -eq "Listen" } |
    Select-Object -First 1

if ($listener) {
    Stop-Process -Id $listener.OwningProcess -Force
}
