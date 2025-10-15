#!/bin/bash

# Demo Script - Test AI CV Builder Features
# Make sure backend is running on localhost:8084

echo "🚀 AI CV Builder - Demo Test Script"
echo "===================================="
echo ""

# Configuration
BASE_URL="http://localhost:8084"
USER_ID="your-user-id-here"
CV_ID="your-cv-id-here"
TOKEN="your-auth-token-here"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Helper function
print_test() {
    echo -e "${BLUE}Testing:${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Test 1: Health Check
print_test "Health Check"
response=$(curl -s -w "\n%{http_code}" "${BASE_URL}/cvs/health")
http_code=$(echo "$response" | tail -n1)
if [ "$http_code" == "200" ]; then
    print_success "Service is running"
else
    print_error "Service is not responding"
    exit 1
fi
echo ""

# Test 2: Import CV (commented out - requires file)
# print_test "Import CV File"
# curl -X POST "${BASE_URL}/cvs/users/${USER_ID}/import" \
#   -H "Authorization: Bearer ${TOKEN}" \
#   -F "file=@sample-cv.pdf"
# print_success "Import test completed"
# echo ""

# Test 3: Analyze CV
print_test "Analyze CV"
curl -X POST "${BASE_URL}/cvs/analyze/${CV_ID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"
print_success "Analysis test completed"
echo ""

# Test 4: Improve CV Section
print_test "Improve CV Section"
curl -X POST "${BASE_URL}/cvs/improve/${CV_ID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "section": "summary",
    "content": "Experienced Java developer with Spring Boot knowledge"
  }'
print_success "Improvement test completed"
echo ""

# Test 5: Analyze with Job Description
print_test "Analyze CV with Job Description"
curl -X POST "${BASE_URL}/cvs/analyze-with-jd/${CV_ID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "jobDescription": "We are seeking a Senior Java Developer with 5+ years of experience in Spring Boot, microservices architecture, and cloud technologies. Must have strong knowledge of RESTful APIs, SQL databases, and modern CI/CD practices."
  }'
print_success "Job match analysis test completed"
echo ""

# Test 6: Get User CVs
print_test "Get User CVs"
curl -X GET "${BASE_URL}/cvs/user/${USER_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
print_success "Get CVs test completed"
echo ""

echo "===================================="
echo "✨ All tests completed!"
echo ""
echo "📝 Notes:"
echo "- Make sure to replace USER_ID, CV_ID, and TOKEN with actual values"
echo "- For file import test, uncomment and provide a sample CV file"
echo "- Check the responses to verify AI suggestions are returned"
echo ""
