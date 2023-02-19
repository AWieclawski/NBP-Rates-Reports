--insert PLN
INSERT INTO currencies(id,description,code)
VALUES(nextval('currencies_id_seq'::regclass),'Polski zloty','PLN') ON CONFLICT DO NOTHING;