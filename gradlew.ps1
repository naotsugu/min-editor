$ENV:JAVA_TOOL_OPTIONS = '-Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8'
$batPath = Join-Path $PSScriptRoot "gradlew.bat"
cmd.exe /c "chcp 65001 > nul & `"$batPath`" $args"
