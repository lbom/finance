SELECT setval('users_id_seq', COALESCE((SELECT MAX(id) FROM users), 1));
SELECT setval('currency_id_seq', COALESCE((SELECT MAX(id) FROM currency), 1));
SELECT setval('institution_id_seq', COALESCE((SELECT MAX(id) FROM institution), 1));
SELECT setval('person_id_seq', COALESCE((SELECT MAX(id) FROM person), 1));
SELECT setval('person_balance_id_seq', COALESCE((SELECT MAX(id) FROM person_balance), 1));
SELECT setval('person_transaction_id_seq', COALESCE((SELECT MAX(id) FROM person_transaction), 1));
SELECT setval('person_transaction_recurrent_id_seq', COALESCE((SELECT MAX(id) FROM person_transaction_recurrent), 1));
