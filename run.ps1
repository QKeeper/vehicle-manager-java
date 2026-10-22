$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

$jdkBin = if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
    "$env:JAVA_HOME\bin"
} else {
    ""
}

$javacCmd = if ($jdkBin) { Join-Path $jdkBin "javac.exe" } else { "javac" }
$javaCmd  = if ($jdkBin) { Join-Path $jdkBin "java.exe" }  else { "java" }

# 1. Компилируем все .java файлы
$sources = Get-ChildItem -LiteralPath 'src' -Filter '*.java' | ForEach-Object { $_.FullName }
$outputDir = Join-Path $PSScriptRoot 'out\production\netodology'
New-Item -ItemType Directory -Force -Path $outputDir | Out-Null
& $javacCmd --release 17 -encoding UTF-8 -d $outputDir $sources

if ($LASTEXITCODE -ne 0) {
    Write-Error "Ошибка компиляции!"
    exit $LASTEXITCODE
}

# 2. Запускаем проект
& $javaCmd -cp $outputDir Main
exit $LASTEXITCODE
