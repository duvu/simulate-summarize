#!/bin/bash

# Script to build and run the Enrichment Service demo

cd "$(dirname "$0")"
echo "Building the application..."
./mvnw clean package -DskipTests

echo "Running the demo..."
java -jar target/enrichment-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=demo
