create or replace function average_age_years()
returns NUMERIC(5,2) as $$
declare
    result NUMERIC(5,2);
begin

    select avg(age_in_months)/12 into result
    from person;

    return result;
end;
$$ language plpgsql;