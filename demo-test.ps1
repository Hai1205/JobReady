# Demo Script - Test AI CV Builder Features (PowerShell)
# Make sure backend is running on localhost:8084

Write-Host "🚀 AI CV Builder - Demo Test Script" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$BASE_URL = "http://localhost:8084"
$USER_ID = "your-user-id-here"
$CV_ID = "your-cv-id-here"
$TOKEN = "your-auth-token-here"

# Helper functions
function Print-Test {
    param([string]$message)
    Write-Host "Testing: " -NoNewline -ForegroundColor Blue
    Write-Host $message
}

function Print-Success {
    param([string]$message)
    Write-Host "✓ " -NoNewline -ForegroundColor Green
    Write-Host $message
}

function Print-Error {
    param([string]$message)
    Write-Host "✗ " -NoNewline -ForegroundColor Red
    Write-Host $message
}

# Test 1: Health Check
Print-Test "Health Check"
try {
    $response = Invoke-RestMethod -Uri "$BASE_URL/cvs/health" -Method Get
    Print-Success "Service is running"
} catch {
    Print-Error "Service is not responding"
    exit 1
}
Write-Host ""

# Test 2: Import CV (commented out - requires file)
# Print-Test "Import CV File"
# $fileContent = Get-Content -Path "sample-cv.pdf" -Raw
# $form = @{
#     file = $fileContent
# }
# $headers = @{
#     "Authorization" = "Bearer $TOKEN"
# }
# Invoke-RestMethod -Uri "$BASE_URL/cvs/users/$USER_ID/import" -Method Post -Headers $headers -Form $form
# Print-Success "Import test completed"
# Write-Host ""

# Test 3: Analyze CV
Print-Test "Analyze CV"
try {
    $headers = @{
        "Authorization" = "Bearer $TOKEN"
        "Content-Type" = "application/json"
    }
    $response = Invoke-RestMethod -Uri "$BASE_URL/cvs/analyze/$CV_ID" -Method Post -Headers $headers
    Write-Host ($response | ConvertTo-Json -Depth 10)
    Print-Success "Analysis test completed"
} catch {
    Print-Error "Analysis failed: $_"
}
Write-Host ""

# Test 4: Improve CV Section
Print-Test "Improve CV Section"
try {
    $headers = @{
        "Authorization" = "Bearer $TOKEN"
        "Content-Type" = "application/json"
    }
    $body = @{
        section = "summary"
        content = "Experienced Java developer with Spring Boot knowledge"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/cvs/improve/$CV_ID" -Method Post -Headers $headers -Body $body
    Write-Host ($response | ConvertTo-Json -Depth 10)
    Print-Success "Improvement test completed"
} catch {
    Print-Error "Improvement failed: $_"
}
Write-Host ""

# Test 5: Analyze with Job Description
Print-Test "Analyze CV with Job Description"
try {
    $headers = @{
        "Authorization" = "Bearer $TOKEN"
        "Content-Type" = "application/json"
    }
    $body = @{
        jobDescription = "We are seeking a Senior Java Developer with 5+ years of experience in Spring Boot, microservices architecture, and cloud technologies. Must have strong knowledge of RESTful APIs, SQL databases, and modern CI/CD practices."
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/cvs/analyze-with-jd/$CV_ID" -Method Post -Headers $headers -Body $body
    Write-Host ($response | ConvertTo-Json -Depth 10)
    Print-Success "Job match analysis test completed"
} catch {
    Print-Error "Job match analysis failed: $_"
}
Write-Host ""

# Test 6: Get User CVs
Print-Test "Get User CVs"
try {
    $headers = @{
        "Authorization" = "Bearer $TOKEN"
    }
    $response = Invoke-RestMethod -Uri "$BASE_URL/cvs/user/$USER_ID" -Method Get -Headers $headers
    Write-Host ($response | ConvertTo-Json -Depth 10)
    Print-Success "Get CVs test completed"
} catch {
    Print-Error "Get CVs failed: $_"
}
Write-Host ""

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "✨ All tests completed!" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Notes:" -ForegroundColor Yellow
Write-Host "- Make sure to replace USER_ID, CV_ID, and TOKEN with actual values"
Write-Host "- For file import test, uncomment and provide a sample CV file"
Write-Host "- Check the responses to verify AI suggestions are returned"
Write-Host ""
