BEGIN;

DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'review') THEN
    EXECUTE 'ALTER TABLE review ALTER COLUMN order_id DROP NOT NULL';
    EXECUTE 'ALTER TABLE review ADD COLUMN IF NOT EXISTS campus_service_id BIGINT';
    EXECUTE 'CREATE INDEX IF NOT EXISTS idx_review_campus_service_id ON review(campus_service_id)';
  END IF;
END;
$$;

COMMIT;
