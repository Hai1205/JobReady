-- Kích hoạt pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Table này sẽ được tạo tự động bởi Spring AI VectorStore
-- Nhưng bạn có thể tạo thủ công nếu cần:

-- CREATE TABLE IF NOT EXISTS vector_store (
--     id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
--     content text,
--     metadata json,
--     embedding vector(1536)
-- );

-- CREATE INDEX IF NOT EXISTS vector_store_embedding_idx 
--     ON vector_store USING hnsw (embedding vector_cosine_ops);

-- Verify extension
SELECT * FROM pg_extension WHERE extname = 'vector';
