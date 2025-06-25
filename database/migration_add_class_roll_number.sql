-- Migration script to add class_roll_number column to lor_requests table
-- Run this script to update existing database

USE lor_management;

-- Add the class_roll_number column to lor_requests table
ALTER TABLE lor_requests 
ADD COLUMN class_roll_number VARCHAR(50) NOT NULL DEFAULT '' 
AFTER session;

-- Update the column to remove the default value constraint
ALTER TABLE lor_requests 
ALTER COLUMN class_roll_number DROP DEFAULT;

-- Verify the change
DESCRIBE lor_requests;
