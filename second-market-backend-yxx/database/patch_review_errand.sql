ALTER TABLE review MODIFY COLUMN order_id BIGINT NULL;
ALTER TABLE review ADD COLUMN campus_service_id BIGINT NULL;
CREATE INDEX idx_review_campus_service_id ON review(campus_service_id);
