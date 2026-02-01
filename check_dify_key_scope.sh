#!/bin/bash

API_KEY="app-WD8cecIhJqqDV4iDyVbAFXR2"
DATASET_ID="65638590-a5e1-4d25-adbd-14615561e434"
BASE_URL="http://localhost/v1"

echo "========================================================"
echo "TEST 1: Verifying Key works for APP APIs (e.g. Parameters)"
echo "Expected: 200 OK (if this is a valid App Key)"
echo "--------------------------------------------------------"
curl -s -o /dev/null -w "%{http_code}" -X GET "${BASE_URL}/parameters" \
  -H "Authorization: Bearer ${API_KEY}"
echo ""
echo "Note: If 404, endpoint might not exist. If 200, Key is valid for App."
echo ""

echo "========================================================"
echo "TEST 2: Verifying Key works for DATASET APIs"
echo "Expected: 200 OK (if this is a Dataset Key)"
echo "Actual Issue: 401/403 (if this is an App Key)"
echo "--------------------------------------------------------"
# We simulate a file upload or just a simple GET if possible. 
# Dataset API usually doesn't have a simple GET without ID, let's try to access the dataset document list or similar if possible, 
# or just retry the upload which we know fails.
# Trying a lighter weight endpoint: GET /datasets (System API) or just the upload endpoint.
# Since we know upload fails, let's stick to that to reproduce exact failure.

# Create a dummy file
echo "test content" > verification_dummy.txt

curl -v -X POST "${BASE_URL}/datasets/${DATASET_ID}/document/create_by_file" \
  -H "Authorization: Bearer ${API_KEY}" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@verification_dummy.txt" \
  -F "data={\"indexing_technique\":\"high_quality\",\"process_rule\":{\"mode\":\"automatic\"}}"

rm verification_dummy.txt
echo ""
echo "========================================================"
