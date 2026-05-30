#!/bin/bash

echo "=== Test 1: Register avec nouvel email ==="
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"bob@example.com","password":"Pass123!","firstName":"Bob","lastName":"Johnson"}' \
  -s
echo -e "\n\n=== Test 2: Login ==="
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"bob@example.com","password":"Pass123!"}' \
  -s
echo -e "\n\n=== Done ==="
