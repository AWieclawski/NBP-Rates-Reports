--default schema: nbprates 
SET search_path = nbprates;
--packages
CREATE SEQUENCE IF NOT EXISTS packages_id_seq 
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1;
CREATE TABLE IF NOT EXISTS packages(
id BIGINT DEFAULT nextval('packages_id_seq'::regclass) NOT NULL,
json_data TEXT,
url VARCHAR(512),
end_point VARCHAR(128),
created_at TIMESTAMP DEFAULT now(),
processed BOOLEAN NOT NULL DEFAULT FALSE, 
converted BOOLEAN NOT NULL DEFAULT FALSE, 
PRIMARY KEY(id)
);
--
GRANT SELECT, UPDATE, DELETE, INSERT ON TABLE packages TO webapp;
GRANT SELECT, UPDATE ON TABLE packages_id_seq TO webapp;
--currencies
CREATE SEQUENCE IF NOT EXISTS currencies_id_seq 
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1;
CREATE TABLE IF NOT EXISTS currencies(
id BIGINT DEFAULT nextval('currencies_id_seq'::regclass) NOT NULL,
description VARCHAR(255),
code VARCHAR(3) UNIQUE,
created_at TIMESTAMP DEFAULT now(),
PRIMARY KEY(id)
);
--
GRANT SELECT, UPDATE, DELETE, INSERT ON TABLE currencies TO webapp;
GRANT SELECT, UPDATE ON TABLE currencies_id_seq TO webapp;
--rates
CREATE SEQUENCE IF NOT EXISTS rates_id_seq 
INCREMENT 1
MINVALUE 1
MAXVALUE 9223372036854775807
START 1;
CREATE TABLE IF NOT EXISTS rates(
id BIGINT DEFAULT nextval('rates_id_seq'::regclass) NOT NULL,
disc VARCHAR(1),
rate NUMERIC(16,4),
rate_inv NUMERIC(16,4),
bid NUMERIC(16,4),
ask NUMERIC(16,4),
trading_date TIMESTAMP NULL,
published TIMESTAMP NOT NULL, 
nbp_table VARCHAR(32) NOT NULL,
created_at TIMESTAMP DEFAULT now(),
currency_id BIGINT,
PRIMARY KEY(id),
CONSTRAINT fk_currency_rates
	FOREIGN KEY(currency_id) 
	REFERENCES currencies(id)
);
--
GRANT SELECT, UPDATE, DELETE, INSERT ON TABLE rates TO webapp;
GRANT SELECT, UPDATE ON TABLE rates_id_seq TO webapp;
--test
SELECT table_schema,table_name
FROM information_schema.tables
WHERE table_schema NOT LIKE ALL (ARRAY['pg_catalog','information_schema']) 
ORDER BY table_schema,table_name;
