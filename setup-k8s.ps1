param(
    [string]$MinikubeProfile = "gymhub-k8s",
    [int]$CPUs = 4,
    [int]$MemoryMB = 6144,
    [string]$DiskSize = "30g",
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

function Invoke-Checked {
    param(
        [Parameter(Mandatory = $true)][string]$Exe,
        [Parameter()][string[]]$Args = @()
    )

    & $Exe @Args
    if ($LASTEXITCODE -ne 0) {
        throw "Command failed ($LASTEXITCODE): $Exe $($Args -join ' ')"
    }
}

function Invoke-Retry {
    param(
        [Parameter(Mandatory = $true)][string]$Label,
        [Parameter(Mandatory = $true)][scriptblock]$Action,
        [int]$MaxAttempts = 10,
        [int]$SleepSeconds = 5
    )

    for ($i = 1; $i -le $MaxAttempts; $i++) {
        try {
            & $Action
            return
        } catch {
            if ($i -eq $MaxAttempts) {
                throw "Failed after $MaxAttempts attempts: $Label. Last error: $($_.Exception.Message)"
            }
            Start-Sleep -Seconds $SleepSeconds
        }
    }
}

Write-Host "GymHub - Kubernetes Setup" -ForegroundColor Cyan
Write-Host "========================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Step 1: Starting Minikube profile '$MinikubeProfile'..." -ForegroundColor Yellow
Invoke-Checked "minikube" @(
    "-p", $MinikubeProfile,
    "start",
    "--driver=docker",
    "--cpus=$CPUs",
    "--memory=$MemoryMB",
    "--disk-size=$DiskSize",
    "--wait=all",
    "--wait-timeout=180s"
)

Write-Host ""
Write-Host "Ensuring kubectl context is set to Minikube profile '$MinikubeProfile'..." -ForegroundColor Yellow
Invoke-Checked "kubectl" @("config", "use-context", $MinikubeProfile)

Write-Host ""
Write-Host "Step 2: Enabling ingress addon..." -ForegroundColor Yellow
Invoke-Retry -Label "enable ingress addon" -MaxAttempts 5 -SleepSeconds 5 -Action {
    Invoke-Checked "minikube" @("-p", $MinikubeProfile, "addons", "enable", "ingress")
}

Write-Host "Waiting for ingress controller deployment to be available..." -ForegroundColor Cyan
Invoke-Retry -Label "ingress readiness" -MaxAttempts 30 -SleepSeconds 5 -Action {
    Invoke-Checked "kubectl" @("wait", "--for=condition=available", "deployment", "ingress-nginx-controller", "-n", "ingress-nginx", "--timeout=10s")
}

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

if (-not $SkipBuild) {
    Write-Host ""
    Write-Host "Step 3: Building images using 'minikube image build'..." -ForegroundColor Yellow

    $images = @(
        @{ name = "eureka-server";  dockerfile = "eureka-server/dockerfile" },
        @{ name = "api-gateway";   dockerfile = "api-gateway/dockerfile" },
        @{ name = "members";       dockerfile = "members/dockerfile" },
        @{ name = "trainers";      dockerfile = "trainers/dockerfile" },
        @{ name = "schedule";      dockerfile = "schedule/dockerfile" },
        @{ name = "workout";       dockerfile = "workout/dockerfile" },
        @{ name = "notifications"; dockerfile = "notifications/dockerfile" },
        @{ name = "report";        dockerfile = "report/dockerfile" }
    )

    foreach ($img in $images) {
        $tag = "gymhub/$($img.name):latest"
        $dfRel = $img.dockerfile

        if (-not (Test-Path -LiteralPath (Join-Path $projectRoot $dfRel))) {
            throw "Dockerfile not found: $(Join-Path $projectRoot $dfRel)"
        }

        Write-Host "Building $tag..." -ForegroundColor Cyan
        Invoke-Checked "minikube" @(
            "-p", $MinikubeProfile,
            "image", "build",
            "-t", $tag,
            "-f", $dfRel,
            $projectRoot
        )
    }
} else {
    Write-Host ""
    Write-Host "Step 3: Skipping build (SkipBuild=true)." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Step 4: Applying Kubernetes manifests (strict order)..." -ForegroundColor Yellow

$k8sDir = Join-Path $projectRoot "k8s"
if (-not (Test-Path -LiteralPath $k8sDir)) {
    throw "k8s directory not found: $k8sDir"
}

Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "namespace.yaml"))

Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "postgres-secret.yaml"), "-n", "gymhub")
Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "rabbitmq-secret.yaml"), "-n", "gymhub")

Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "rabbitmq-pvc.yaml"), "-n", "gymhub")
Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "rabbitmq-deployment.yaml"), "-n", "gymhub")

Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "postgres-databases.yaml"), "-n", "gymhub")

Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "eureka-deployment.yaml"), "-n", "gymhub")
Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "api-gateway.yaml"), "-n", "gymhub")

Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "members-deployment.yaml"), "-n", "gymhub")
Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "trainers-deployment.yaml"), "-n", "gymhub")
Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "schedule-deployment.yaml"), "-n", "gymhub")
Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "workout-deployment.yaml"), "-n", "gymhub")
Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "report-deployment.yaml"), "-n", "gymhub")
Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "notifications-deployment.yaml"), "-n", "gymhub")

Invoke-Checked "kubectl" @("apply", "-f", (Join-Path $k8sDir "ingress.yaml"), "-n", "gymhub")

Write-Host ""
Write-Host "Step 5: Waiting for deployments to become Available..." -ForegroundColor Yellow
Invoke-Checked "kubectl" @("wait", "--for=condition=available", "deployment", "--all", "-n", "gymhub", "--timeout=420s")

Write-Host ""
Write-Host "Setup completed." -ForegroundColor Green
Write-Host "Namespace: gymhub" -ForegroundColor Green
