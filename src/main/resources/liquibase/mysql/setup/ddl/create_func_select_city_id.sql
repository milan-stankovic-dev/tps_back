CREATE OR REPLACE FUNCTION select_city_by_id(id_param BIGINT)
RETURNS city AS $$
DECLARE
    result_city city;
BEGIN
    SELECT *
    INTO result_city
    FROM city
    WHERE id = id_param;

    RETURN result_city;
END;
$$ language plpgsql;