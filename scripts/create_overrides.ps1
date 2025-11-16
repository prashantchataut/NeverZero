param()
$projectRes = 'app/src/main/res'
$libRes = 'app/libs/ui-release-fixed/res'
$localeSet = New-Object System.Collections.Generic.HashSet[string]

function Add-Locales {
    param($root)
    if (Test-Path $root) {
        Get-ChildItem -Path $root -Directory -Filter 'values*' | ForEach-Object {
            $null = $localeSet.Add($_.Name)
        }
    }
}

Add-Locales -root $projectRes
Add-Locales -root $libRes

if ($localeSet.Count -eq 0) {
    Write-Error 'No values directories found.'
    exit 1
}

$content = @'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="dashboard_section_tasks">Tasks</string>
</resources>
'@

$written = @()
foreach ($locale in ($localeSet | Sort-Object)) {
    $targetDir = Join-Path $projectRes $locale
    if (-not (Test-Path $targetDir)) {
        New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
    }
    $targetFile = Join-Path $targetDir 'strings_override.xml'
    Set-Content -Path $targetFile -Value $content -Encoding UTF8
    $written += $targetFile
}

Write-Output 'Locales detected:'
$localeSet | Sort-Object | ForEach-Object { Write-Output (" - " + $_) }
Write-Output ''
Write-Output 'Override files:'
$written | ForEach-Object { Write-Output (" - " + $_) }
