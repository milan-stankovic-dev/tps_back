create or replace procedure update_person(p_id BIGINT, p_first_name VARCHAR, p_last_name VARCHAR,
                                          p_height_in_cm INT, p_dob DATE, p_age_in_months INT,
                                          p_city_birth_id BIGINT, p_city_residence_id BIGINT)
as $$
begin
    update person set
        first_name = p_first_name,
        last_name = p_last_name,
        height_in_cm = p_height_in_cm,
        dob = p_dob,
        age_in_months = p_age_in_months,
        city_birth_id = p_city_birth_id,
        city_residence_id = p_city_residence_id
    where
        id = p_id;
end;
$$ language plpgsql;