CREATE OR REPLACE FUNCTION select_person_by_id(selection_id BIGINT)
RETURNS person AS $$
DECLARE
    result_person person;
BEGIN
    SELECT *
    INTO result_person
    FROM person
    WHERE id = selection_id;

    RETURN result_person;
END;
$$ LANGUAGE plpgsql;
