#!/bin/bash

# Create S3 bucket for attachments
awslocal s3 mb s3://pfk-task-attachments

# Make bucket public readable
awslocal s3api put-bucket-acl --bucket pfk-task-attachments --acl public-read

# Add bucket policy to allow public access
awslocal s3api put-bucket-policy --bucket pfk-task-attachments --policy '{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::pfk-task-attachments/*"
    }
  ]
}'

# Configure SES - verify email addresses
awslocal ses verify-email-identity --email-address noah@pfkdigital.co.uk

echo "AWS S3 and SES have been configured successfully!"