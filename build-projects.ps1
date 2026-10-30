# Run this every time you add or change something in the "projects" folder:
#   right-click this file > "Run with PowerShell"
# It reads every project folder and writes projects-data.js,
# which the portfolio uses to show your projects and their code.
#
# Each project folder can have a project.json like this (all fields optional):
#   { "title": "...", "description": "...", "tech": ["HTML","CSS"], "entry": "index.html", "order": 1 }

$root = $PSScriptRoot
$codeTypes = @(".html", ".htm", ".css", ".js", ".json", ".txt", ".md", ".java", ".sql", ".py")
$imageTypes = @(".png", ".jpg", ".jpeg", ".gif", ".webp", ".svg", ".avif")
$utf8 = New-Object Text.UTF8Encoding $false

$list = @()
foreach ($dir in Get-ChildItem "$root\projects" -Directory) {
  $meta = @{}
  if (Test-Path "$($dir.FullName)\project.json") {
    $meta = Get-Content "$($dir.FullName)\project.json" -Raw -Encoding UTF8 | ConvertFrom-Json
  }
  $files = @()
  foreach ($f in Get-ChildItem $dir.FullName -Recurse -File | Sort-Object FullName) {
    if ($f.Name -eq "project.json") { continue }
    $path = $f.FullName.Substring($dir.FullName.Length + 1).Replace("\", "/")
    $ext = $f.Extension.ToLower()
    if ($codeTypes -contains $ext) {
      $files += [ordered]@{ path = $path; kind = "code"; text = [IO.File]::ReadAllText($f.FullName, [Text.Encoding]::UTF8) }
    } elseif ($imageTypes -contains $ext) {
      $files += [ordered]@{ path = $path; kind = "image" }
    }
  }
  $html = @($files | Where-Object { $_.path -match '\.html?$' } | ForEach-Object { $_.path })
  $entry = if ($meta.entry) { $meta.entry } elseif ($html -contains "index.html") { "index.html" } else { $html | Select-Object -First 1 }
  $list += [ordered]@{
    folder      = $dir.Name
    title       = if ($meta.title) { $meta.title } else { $dir.Name }
    description = if ($meta.description) { $meta.description } else { "" }
    tech        = @($meta.tech | Where-Object { $_ })
    entry       = $entry
    order       = if ($meta.order) { [int]$meta.order } else { 999 }
    files       = $files
  }
}
$list = @($list | Sort-Object { $_.order }, { $_.title })

$json = ConvertTo-Json -InputObject $list -Depth 6 -Compress
[IO.File]::WriteAllText("$root\projects-data.js", "window.PROJECTS = $json;`n", $utf8)
Write-Host "projects-data.js updated: $($list.Count) projects"
