create or replace function select_city_by_id(id_param BIGINT)
returns city as $$
declare
    result_city city;
begin
    select *
    into result_city
    from city
    where id = id_param;

    return result_city;
end;
$$ language plpgsql;