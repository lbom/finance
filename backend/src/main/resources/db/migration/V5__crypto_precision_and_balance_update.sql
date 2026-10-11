ALTER TABLE person_balance ALTER COLUMN amount TYPE NUMERIC(28, 8);
ALTER TABLE person_transaction ALTER COLUMN amount TYPE NUMERIC(28, 8);
ALTER TABLE person_transaction_recurrent ALTER COLUMN amount TYPE NUMERIC(28, 8);
ALTER TABLE fx_rate ALTER COLUMN price TYPE NUMERIC(28, 8);