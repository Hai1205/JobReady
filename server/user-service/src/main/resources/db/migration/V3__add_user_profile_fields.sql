-- Add new profile fields to users table
ALTER TABLE users
ADD COLUMN phone VARCHAR(20),
ADD COLUMN location VARCHAR(255),
ADD COLUMN birth VARCHAR(10),
ADD COLUMN summary TEXT;
