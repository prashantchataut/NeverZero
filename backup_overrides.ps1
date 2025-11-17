Param()
Set-StrictMode -Version Latest

$base = Resolve-Path "app/src/main/res"
$backup = Join-Path (Get-Location) "backup_strings_override"

if (Test-Path $backup) {
    Remove-Item -Recurse -Force $backup
}

$files = Get-ChildItem -Path $base -Filter "strings_override.xml" -Recurse
foreach ($file in $files) {
    $relative = $file.FullName.Substring($base.Path.Length).TrimStart('\')
    $dest = Join-Path $backup $relative
    $destDir = Split-Path $dest
    if (!(Test-Path $destDir)) {
        New-Item -ItemType Directory -Force -Path $destDir | Out-Null
    }
    Move-Item -LiteralPath $file.FullName -Destination $dest
}

Write-Host ("Moved {0} override files to {1}" -f $files.Count, $backup)
