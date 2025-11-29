CREATE EXTENSION IF NOT EXISTS vector;

-- CREATE INDEX IF NOT EXISTS vector_store_embedding_idx 
--     ON vector_store USING hnsw (embedding vector_cosine_ops);

-- Verify extension
SELECT * FROM pg_extension WHERE extname = 'vector';
