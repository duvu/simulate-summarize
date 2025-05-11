#!/bin/bash

# Script to run all tests for the Enrichment Service

cd "$(dirname "$0")"
echo "Running all tests..."
./mvnw test
