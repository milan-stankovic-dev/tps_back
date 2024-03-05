create or replace function max_height()
returns INT as $$
declare
    query_result INT;
begin
    select max(height_in_cm) into query_result
    from person;

    return query_result;
end;
$$ language plpgsql;

