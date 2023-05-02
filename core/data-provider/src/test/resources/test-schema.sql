SELECT public.copy_schema('nbprates','nbptests',FALSE);
SELECT public.copy_views('nbprates','nbptests');
SELECT public.copy_functions('nbprates','nbptests')