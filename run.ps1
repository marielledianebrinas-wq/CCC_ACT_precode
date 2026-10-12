$ErrorActionPreference = "Stop"

$javaHome = "C:\Users\Admin\.jdks\openjdk-25.0.1\bin"
$out = Join-Path $PSScriptRoot "out"

New-Item -ItemType Directory -Force $out | Out-Null

& "$javaHome\javac.exe" -d $out (Get-ChildItem -Recurse "$PSScriptRoot\src\main\java","$PSScriptRoot\src\test\java" -Filter *.java).FullName
& "$javaHome\java.exe" -cp $out tests.BusinessLogicTest
& "$javaHome\java.exe" -cp $out app.App
