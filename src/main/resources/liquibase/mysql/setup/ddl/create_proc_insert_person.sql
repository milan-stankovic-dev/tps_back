create or replace procedure insert_person(p_first_name VARCHAR, p_last_name VARCHAR,
                                          p_height_in_cm INT, p_dob DATE, p_age_in_months INT,
                                          p_city_birth_id BIGINT, p_city_residence_id BIGINT)
as $$
begin
    insert into person
    (first_name, last_name, height_in_cm, dob,
     age_in_months, city_birth_id, city_residence_id)
     values (
        p_first_name,
        p_last_name,
        p_height_in_cm,
        p_dob,
        p_age_in_months,
        p_city_birth_id,
        p_city_residence_id
     );
end;
$$ language plpgsql;