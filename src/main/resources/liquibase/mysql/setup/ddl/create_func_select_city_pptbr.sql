create or replace function select_city_by_pptbr(pptbr_param INT)
returns city as $$
declare
    result_city city;
begin
    select *
    into result_city
    from city
    where pptbr = pptbr_param;

    return result_city;
end;
$$ language plpgsql;