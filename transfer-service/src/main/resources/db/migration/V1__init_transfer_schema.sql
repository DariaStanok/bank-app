CREATE SCHEMA IF NOT EXISTS transfer;
SET search_path TO transfer;

CREATE TABLE IF NOT EXISTS transfers (
  id              BIGSERIAL PRIMARY KEY,
  operation_id    VARCHAR(128)  NOT NULL UNIQUE,
  from_account_id BIGINT        NOT NULL,
  to_account_id   BIGINT        NOT NULL,
  amount          NUMERIC(19,4) NOT NULL,
  currency        VARCHAR(8),
  rate            NUMERIC(19,8),
  debit_amount    NUMERIC(19,4),
  credit_amount   NUMERIC(19,4),
  status          VARCHAR(32),
  created_at      TIMESTAMPTZ   NOT NULL,
  updated_at      TIMESTAMPTZ   NULL
);

CREATE INDEX IF NOT EXISTS idx_transfers_status_created
  ON transfers (status, created_at);
