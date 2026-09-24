$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$localDirectory = Join-Path $projectRoot '.local'
$configFile = Join-Path $localDirectory 'application.properties'
if (Test-Path -LiteralPath $configFile) {
    Write-Output 'Local configuration already exists; no secrets changed.'
    exit 0
}
$keyBytes = New-Object byte[] 32
$random = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try { $random.GetBytes($keyBytes) } finally { $random.Dispose() }
New-Item -ItemType Directory -Path $localDirectory -Force | Out-Null
$contents = 'JWT_SECRET=' + [Convert]::ToBase64String($keyBytes) + [Environment]::NewLine
[IO.File]::WriteAllText($configFile, $contents, (New-Object System.Text.UTF8Encoding($false)))
Write-Output 'Generated a new local JWT key in .local/application.properties (ignored by Git).'
