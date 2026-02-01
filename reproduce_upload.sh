#!/bin/bash

# Base URL
BASE_URL="http://localhost:8085"
COOKIE_FILE="cookies.txt"

# Create dummy file
echo "This is a test document for Dify Knowledge Base." > test_kb.txt

# 1. Register (Optional, in case user doesn't exist, but standard Spring Security usually doesn't expose register via FormLogin config directly, likely a Controller)
# We will try to LOGIN directly with 'admin/123456' which is common. If that fails, we might need to find a valid user.
# Wait, previous conversations didn't give me a user. I'll try to register using /api/user/register if it exists?
# The SecurityConfig allowed "/register". Let's see if there is an endpoint.
# Actually, I'll try to login as 'admin' '123456'.
# If that fails, I'll try to create a user via /api/user/register if implemented.

# Let's try to hit the login endpoint
echo "Attempting to login..."
curl -v -c $COOKIE_FILE -b $COOKIE_FILE -X POST "$BASE_URL/api/user/login" \
  -d "username=admin&password=Admin@2024" \
  -H "Content-Type: application/x-www-form-urlencoded"

echo -e "\n\nAttempting upload..."
# 2. Upload File
curl -v -c $COOKIE_FILE -b $COOKIE_FILE -X POST "$BASE_URL/api/knowledge-doc/upload" \
  -F "file=@test_kb.txt" \
  -F "category=test" \
  -F "documentType=txt"

echo -e "\n\nDone."
