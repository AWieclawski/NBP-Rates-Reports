CREATE USER webapp WITH PASSWORD '123456';
CREATE USER testadmin WITH PASSWORD '123456';
CREATE SCHEMA IF NOT EXISTS nbprates AUTHORIZATION webapp;
CREATE SCHEMA IF NOT EXISTS nbptests AUTHORIZATION testadmin;
--
GRANT ALL ON ALL TABLES IN SCHEMA nbprates TO appadmin;
--
CREATE OR REPLACE FUNCTION public.concat_aph( p_string_one CHARACTER VARYING, p_string_two CHARACTER VARYING) 
RETURNS TEXT 
AS '  
DECLARE 
	v_result TEXT;
	aph_one CHARACTER VARYING;
	aph_two CHARACTER VARYING;
BEGIN 
	aph_one := (E''\''''||p_string_one)::TEXT;
	aph_two := (p_string_two||E''\'''')::TEXT;
	v_result := CONCAT(aph_one,aph_two);
RETURN v_result;
END; ' LANGUAGE plpgsql;
--
CREATE OR REPLACE FUNCTION public.copy_functions(source_schema character varying, target_schema character varying)
 RETURNS integer
AS '
DECLARE
    t_ex integer := 0;
    s_ex integer := 0;
    v_result record;
    src_def character varying;
    trg_def character varying;
    trg_function character varying;
BEGIN
    if (select 1 from pg_namespace where nspname = source_schema) THEN
        s_ex := 1;
    END IF;

    IF (s_ex = 0) THEN
        RETURN 0;
    END IF;

    if (select 1 from pg_namespace where nspname = target_schema) THEN
        t_ex := 1;
    ELSE
		RAISE NOTICE ''Target schema % not found! '', target_schema;
        RETURN 0;
    END IF;

    FOR v_result IN 
        SELECT pg_get_functiondef(oid) AS p_def, proname
        FROM pg_proc 
        WHERE pronamespace = source_schema::regnamespace
    LOOP
        trg_function := target_schema||''.''||v_result.proname;
        src_def := v_result.p_def; 
        trg_def := REPLACE(src_def, source_schema, target_schema);
        RAISE NOTICE ''appliying % '', trg_function;
        EXECUTE trg_def;
    END LOOP;
    return t_ex;
END;'  LANGUAGE plpgsql;  
--
CREATE OR REPLACE FUNCTION public.copy_schema(source_schema character varying, target_schema character varying, copy_data boolean)
 RETURNS integer
AS '
DECLARE
    t_ex integer := 0;
    s_ex integer := 0;
    src_table character varying;
    trg_table character varying;
    aph_exp character varying;
    l_sql character varying;
BEGIN
    if (select 1 from pg_namespace where nspname = source_schema) THEN
        s_ex := 1;
    END IF;

    IF (s_ex = 0) THEN
        RETURN 0;
    END IF;

    if (select 1 from pg_namespace where nspname = target_schema) THEN
        t_ex := 1;
    ELSE
		RAISE NOTICE ''Target schema % not found! '', target_schema;
        RETURN 0;
    END IF;

    FOR src_table IN 
        SELECT table_name 
        FROM information_schema.TABLES 
        WHERE table_schema = source_schema
    LOOP
        trg_table := target_schema||''.''||src_table;
        RAISE NOTICE ''appliying % '', trg_table;
		l_sql := ''CREATE TABLE '' || trg_table || '' (LIKE '' || source_schema || ''.'' || src_table || '' INCLUDING ALL)'';
		RAISE NOTICE ''appliying % '', l_sql;
        EXECUTE l_sql;
		l_sql := ''CREATE SEQUENCE '' || trg_table || ''_id_seq OWNED BY ''||trg_table || ''.id''; 
		RAISE NOTICE ''appliying % '', l_sql;
        EXECUTE l_sql;
        aph_exp := concat_aph(trg_table,''_id_seq'');
		l_sql := ''ALTER TABLE '' || trg_table || '' ALTER COLUMN id SET DEFAULT nextval(''||aph_exp||''::regclass)'';
		RAISE NOTICE ''appliying % '', l_sql;
        EXECUTE l_sql;
        IF (copy_data = true) THEN
			l_sql := ''INSERT INTO '' || trg_table || ''(SELECT * FROM '' || source_schema || ''.'' || src_table || '')''; 
			RAISE NOTICE ''appliying % '', l_sql;
    	    EXECUTE l_sql;
        END IF;
    END LOOP;
    RETURN t_ex;
END; 
'  LANGUAGE plpgsql;
--
CREATE OR REPLACE FUNCTION public.copy_views(source_schema character varying, target_schema character varying)
 RETURNS integer
AS '
DECLARE
    t_ex integer := 0;
    s_ex integer := 0;
    temprow record;
    trg_table character varying;
    trg_def character varying;
    l_sql character varying;
BEGIN
    if (select 1 from pg_namespace where nspname = source_schema) THEN
        s_ex := 1;
    END IF;

    IF (s_ex = 0) THEN
        RETURN 0;
    END IF;

    if (select 1 from pg_namespace where nspname = target_schema) THEN
        t_ex := 1;
    ELSE
		RAISE NOTICE ''Target schema % not found! '', target_schema;
        RETURN 0;
    END IF;

    FOR temprow IN 
        SELECT * 
        FROM information_schema.VIEWS 
        WHERE table_schema = source_schema
    LOOP
        trg_table := target_schema||''.''||temprow.table_name;
        trg_def := temprow.view_definition;
        RAISE NOTICE ''appliying % '', trg_table;
		l_sql := ''CREATE OR REPLACE VIEW '' || trg_table || '' AS '' || trg_def;
		RAISE NOTICE ''appliying % '', l_sql;
        EXECUTE l_sql;
    END LOOP;
    RETURN t_ex;
END; 
'  LANGUAGE plpgsql;
-- test
SELECT usename,usesuper,usecreatedb
FROM pg_catalog.pg_user
ORDER BY usename;
