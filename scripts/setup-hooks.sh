#!/bin/bash
set -e

echo "Setting up Git hooks..."

# Configure Git to use .githooks directory
git config core.hooksPath .githooks

# Ensure hook files are executable
chmod +x .githooks/pre-commit .githooks/pre-push 2>/dev/null || true

echo "✅ Git hooks installed successfully!"
echo ""
echo "Hooks installed:"
echo "  • pre-commit: Code quality checks (spotless, checkstyle)"
echo "  • pre-push:   Unit tests"
echo ""
echo "Run this command to install: bash scripts/setup-hooks.sh"
