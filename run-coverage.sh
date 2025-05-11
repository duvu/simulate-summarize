#!/bin/bash

# Run tests with JaCoCo coverage
echo "Running tests with JaCoCo code coverage..."
./mvnw clean test jacoco:report

# Check if report was generated
if [ -f "target/site/jacoco/index.html" ]; then
    echo "JaCoCo report generated successfully at: target/site/jacoco/index.html"
    
    # Print coverage summary if available
    echo ""
    echo "Coverage Summary:"
    echo "================="
    # Extract coverage percentage from the index.html file
    INSTRUCTION_COVERAGE=$(grep -o 'Total.*class="bar"' target/site/jacoco/index.html | head -1 | grep -o '[0-9]\+\.[0-9]\+%' | head -1)
    BRANCH_COVERAGE=$(grep -o 'Total.*class="bar"' target/site/jacoco/index.html | head -2 | tail -1 | grep -o '[0-9]\+\.[0-9]\+%' | head -1)
    
    echo "Instruction Coverage: $INSTRUCTION_COVERAGE"
    echo "Branch Coverage: $BRANCH_COVERAGE"
    echo ""
    echo "For detailed results, open the HTML report in your browser."
else
    echo "Error: JaCoCo report was not generated."
    exit 1
fi
