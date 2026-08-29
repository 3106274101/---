# Backup drill for Windows: dump MySQL if available, always copy H2 + uploads.
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$out = Join-Path (Join-Path $root "backups") $stamp
New-Item -ItemType Directory -Force -Path $out | Out-Null

$mysqldump = Get-Command mysqldump -ErrorAction SilentlyContinue
if ($mysqldump) {
  $hostName = if ($env:MYSQL_HOST) { $env:MYSQL_HOST } else { "127.0.0.1" }
  $port = if ($env:MYSQL_PORT) { $env:MYSQL_PORT } else { "3306" }
  $user = if ($env:MYSQL_USER) { $env:MYSQL_USER } else { "tradehub" }
  $pass = if ($env:MYSQL_PASSWORD) { $env:MYSQL_PASSWORD } else { "tradehub" }
  $db = if ($env:MYSQL_DB) { $env:MYSQL_DB } else { "tradehub" }
  & mysqldump -h $hostName -P $port -u $user "-p$pass" $db | Out-File -Encoding utf8 (Join-Path $out "tradehub.sql")
  Write-Host "mysql dump -> $out\tradehub.sql"
} else {
  Write-Host "mysqldump not found, skip SQL dump"
}

$h2 = Join-Path $root "services\tradehub-api\data"
if (Test-Path $h2) {
  Copy-Item -Recurse $h2 (Join-Path $out "h2-data")
}
$uploads = Join-Path $root "services\tradehub-api\uploads"
if (Test-Path $uploads) {
  Copy-Item -Recurse $uploads (Join-Path $out "uploads")
}

Write-Host "backup complete: $out"
Write-Host "Restore MySQL: mysql -u tradehub -ptradehub tradehub < backups\$stamp\tradehub.sql"
