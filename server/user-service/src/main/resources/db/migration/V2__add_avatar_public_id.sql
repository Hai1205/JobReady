-- Migration: Add avatarPublicId column to users table
-- This column stores Cloudinary public ID for avatar deletion

ALTER TABLE users 
ADD COLUMN avatar_public_id VARCHAR(255) NULL 
COMMENT 'Cloudinary public ID for avatar management';

-- Add index for faster lookups (optional but recommended)
CREATE INDEX idx_avatar_public_id ON users(avatar_public_id);
