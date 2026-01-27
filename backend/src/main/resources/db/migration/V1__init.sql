CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS currency (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(255),
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS institution (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    symbol VARCHAR(255),
    country VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS symbol (
    id BIGSERIAL PRIMARY KEY,
    institution_id BIGINT NOT NULL,
    symbol VARCHAR(255),
    "group" VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS fx_pair (
    id BIGSERIAL PRIMARY KEY,
    base_currency_id BIGINT NOT NULL,
    quote_currency_id BIGINT NOT NULL,
    symbol VARCHAR(255),
    type VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS fx_rate (
    pair_id BIGINT PRIMARY KEY,
    price NUMERIC(19, 2),
    candle_date DATE,
    candle_type VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS person (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS person_balance (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL,
    institution_id BIGINT NOT NULL,
    currency_id BIGINT,
    amount NUMERIC(19, 2),
    type VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS person_transaction (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL,
    balance_id BIGINT NOT NULL,
    date DATE NOT NULL,
    details TEXT,
    type VARCHAR(255),
    spending_type VARCHAR(255),
    profit_type VARCHAR(255),
    amount NUMERIC(19, 2)
);

CREATE TABLE IF NOT EXISTS person_transaction_recurrent (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL,
    balance_id BIGINT NOT NULL,
    period_days INTEGER,
    amount NUMERIC(19, 2),
    last_run_date TIMESTAMP,
    name VARCHAR(255),
    is_active BOOLEAN NOT NULL,
    type VARCHAR(255),
    spending_type VARCHAR(255),
    profit_type VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS trade (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    reason TEXT,
    profit NUMERIC(19, 4),
    type TEXT,
    amount NUMERIC(19, 4),
    symbol_id INTEGER,
    institution_id INTEGER
);

CREATE TABLE IF NOT EXISTS investment (
    id BIGSERIAL PRIMARY KEY,
    person_id BIGINT NOT NULL,
    amount NUMERIC(19, 4),
    end_date TIMESTAMP,
    reason TEXT,
    profit NUMERIC(19, 4),
    start_date TIMESTAMP,
    type TEXT,
    symbol_id INTEGER,
    institution_id INTEGER
);

CREATE TABLE IF NOT EXISTS business (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name TEXT,
    idea TEXT,
    expense NUMERIC(10, 2),
    revenue NUMERIC(10, 2)
);

CREATE TABLE IF NOT EXISTS business_transaction (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name TEXT,
    amount NUMERIC(10, 2),
    type VARCHAR(255)
);
