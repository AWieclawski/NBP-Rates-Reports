--default schema: nbprates 
SET search_path = nbprates;
--insert PLN
INSERT INTO currencies(id,description,code)
VALUES(nextval('currencies_id_seq'::regclass),'Polski zloty','PLN') ON CONFLICT DO NOTHING;