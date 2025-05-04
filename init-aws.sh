#!/bin/bash
set -e

echo "Starting LocalStack initialization..."

# Create S3 bucket
echo "Creating S3 bucket..."
awslocal s3 mb s3://pfk-task-attachments || echo "S3 bucket already exists or creation failed"

# Verify SES email identity
echo "Verifying SES email identity..."
awslocal ses verify-email-identity --email-address noah@pfkdigital.co.uk || echo "Email identity verification failed or already verified"

echo "LocalStack initialization completed!"
