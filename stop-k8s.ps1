param(
    [string]$MinikubeProfile = "gymhub-k8s"
)

$ErrorActionPreference = "Stop"

function Invoke-Soft {
    param(
        [Parameter(Mandatory = $true)][string]$Exe,
        [Parameter()][string[]]$Args = @()
    )

    try {
        & $Exe @Args | Out-Null
    } catch {
        # ignore
    }
}

Write-Host "GymHub - Kubernetes Cleanup" -ForegroundColor Cyan
Write-Host "============================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Ensuring kubectl context is set to Minikube profile '$MinikubeProfile'..." -ForegroundColor Yellow
Invoke-Soft "kubectl" @("config", "use-context", $MinikubeProfile)

Write-Host "Deleting namespace gymhub (non-blocking)..." -ForegroundColor Yellow
Invoke-Soft "kubectl" @("delete", "namespace", "gymhub", "--wait=false")

Write-Host "Stopping Minikube profile '$MinikubeProfile'..." -ForegroundColor Yellow
Invoke-Soft "minikube" @("-p", $MinikubeProfile, "stop")

Write-Host "Deleting Minikube profile '$MinikubeProfile'..." -ForegroundColor Yellow
Invoke-Soft "minikube" @("-p", $MinikubeProfile, "delete")

Write-Host ""
Write-Host "Cleanup completed." -ForegroundColor Green
Write-Host "To restart:" -ForegroundColor Green
Write-Host "  .\setup-k8s.ps1" -ForegroundColor Green
