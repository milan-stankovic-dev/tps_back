CREATE OR REPLACE PROCEDURE select_person_by_id(selection_id BIGINT, INOUT result_person person)
AS $$
BEGIN
    SELECT *
    INTO result_person
    FROM person
    WHERE id = selection_id;
END;
$$ LANGUAGE plpgsql;