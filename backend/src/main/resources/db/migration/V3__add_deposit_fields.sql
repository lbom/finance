ALTER TABLE person_balance
    ADD COLUMN IF NOT EXISTS interest_rate NUMERIC(10, 4),
    ADD COLUMN IF NOT EXISTS last_profit_date DATE;

ALTER TABLE person_transaction_recurrent
    ADD COLUMN IF NOT EXISTS interest_rate NUMERIC(10, 4);
