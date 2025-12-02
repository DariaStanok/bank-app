CREATE SCHEMA IF NOT EXISTS notifications;
SET search_path TO notifications;

CREATE TABLE IF NOT EXISTS notifications (
  id           BIGSERIAL PRIMARY KEY,
  event        VARCHAR(64)    NOT NULL,         
  message      TEXT           NOT NULL,
  operation_id VARCHAR(128),  
  user_id      BIGINT,         
  created_at   TIMESTAMPTZ    NOT NULL

  CONSTRAINT chk_user_or_operation
    CHECK (operation_id IS NOT NULL OR user_id IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_createdat
  ON notifications (user_id, created_at);

CREATE INDEX IF NOT EXISTS idx_notifications_operation
  ON notifications (operation_id);
