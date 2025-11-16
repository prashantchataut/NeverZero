param()
$pattern = "dashboard_section_tasks"
$root = Join-Path $env:USERPROFILE ".gradle\caches"
Get-ChildItem -Path $root -Recurse -Filter "values*.xml" -File -ErrorAction SilentlyContinue | ForEach-Object {
    $path = $_.FullName
    try {
        if (Select-String -Path $path -Pattern $pattern -SimpleMatch -Quiet) {
            Write-Output $path
        }
    } catch {
        Write-Output ("ERROR: " + $path + " -> " + $_.Exception.Message)
    }
}
