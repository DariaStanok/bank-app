CREATE SCHEMA IF NOT EXISTS cash;
SET search_path TO cash;


CREATE TABLE IF NOT EXISTS cash_operation (
  id               VARCHAR(64)    PRIMARY KEY,
  type             VARCHAR(16)    NOT NULL,            
  account_id       BIGINT         NOT NULL,
  currency         VARCHAR(8)     NOT NULL,            
  amount           NUMERIC(19,4)  NOT NULL,
  status           VARCHAR(32)    NOT NULL,            
  idempotency_key  VARCHAR(128)   NOT NULL,
  created_at       TIMESTAMPTZ    NOT NULL 
  completed_at     TIMESTAMPTZ,
  new_balance      NUMERIC(19,4)
);


CREATE INDEX IF NOT EXISTS idx_cash_op_account_created
  ON cash_operation (account_id, created_at);

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM   pg_constraint
    WHERE  conname = 'uq_cash_op_idem_acc_type'
  ) THEN
    ALTER TABLE cash_operation
      ADD CONSTRAINT uq_cash_op_idem_acc_type
      UNIQUE (idempotency_key, account_id, type);
  END IF;
END$$;


CREATE TABLE IF NOT EXISTS balance_change (
  id           BIGSERIAL      PRIMARY KEY,
  operation_id VARCHAR(128)   NOT NULL,
  account_id   BIGINT         NOT NULL,
  currency     VARCHAR(8)     NOT NULL,               
  type         VARCHAR(16)    NOT NULL,              
  amount       NUMERIC(19,4)  NOT NULL,
  applied_at   TIMESTAMPTZ    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_change_account_applied
  ON balance_change (account_id, applied_at);

CREATE INDEX IF NOT EXISTS idx_change_operation
  ON balance_change (operation_id);
