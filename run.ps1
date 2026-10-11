# Ищем JDK от IntelliJ IDEA или системный
$jdkBin = if (Test-Path "$env:USERPROFILE\.jdks\openjdk-26.0.2.1\bin\javac.exe") {
    "$env:USERPROFILE\.jdks\openjdk-26.0.2.1\bin"
} elseif ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\javac.exe")) {
    "$env:JAVA_HOME\bin"
} else {
    ""
}

$javacCmd = if ($jdkBin) { Join-Path $jdkBin "javac.exe" } else { "javac" }
$javaCmd  = if ($jdkBin) { Join-Path $jdkBin "java.exe" }  else { "java" }

# 1. Компилируем все .java файлы
$sources = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
& $javacCmd --enable-preview --release 26 -d out/production/netodology $sources

if ($LASTEXITCODE -ne 0) {
    Write-Error "Ошибка компиляции!"
    exit $LASTEXITCODE
}

# 2. Запускаем проект
& $javaCmd --enable-preview -cp out/production/netodology Main
