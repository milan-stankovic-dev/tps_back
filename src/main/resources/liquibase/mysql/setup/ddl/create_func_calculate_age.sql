create or replace function calculate_age_in_months()
returns trigger as $$
declare
    age_in_months int;
begin
    age_in_months := EXTRACT(year from age(CURRENT_DATE, new.dob)) * 12 +
     EXTRACT(month from age(CURRENT_DATE, new.dob));

    new.age_in_months = age_in_months;

    return new;
end;
$$ language plpgsql;