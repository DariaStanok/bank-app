CREATE SCHEMA IF NOT EXISTS accounts;

CREATE TABLE IF NOT EXISTS accounts.user_account (
  id                  BIGSERIAL PRIMARY KEY,
  username            VARCHAR(64)  NOT NULL,
  password_hash       VARCHAR(255) NOT NULL,
  enabled             BOOLEAN      NOT NULL,
  first_name          VARCHAR(64),
  last_name           VARCHAR(64),
  email               VARCHAR(255),
  birth_date          DATE,
  created_at          TIMESTAMPTZ  NOT NULL,
  current_account_id  BIGINT       NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_user_account_username
  ON accounts.user_account (username);

CREATE INDEX IF NOT EXISTS idx_user_created
  ON accounts.user_account (created_at);

CREATE TABLE IF NOT EXISTS accounts.account (
  id                   BIGSERIAL PRIMARY KEY,
  user_id              BIGINT        NOT NULL,
  external_account_id  VARCHAR(64),
  currency             VARCHAR(8)    NOT NULL,
  balance              NUMERIC(19,2) NOT NULL DEFAULT 0,
  created_at           TIMESTAMPTZ   NOT NULL,
  CONSTRAINT fk_account_user
    FOREIGN KEY (user_id) REFERENCES accounts.user_account(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_account_user_created
  ON accounts.account (user_id, created_at);

CREATE UNIQUE INDEX IF NOT EXISTS ux_account_user_currency
  ON accounts.account (user_id, currency);