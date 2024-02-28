create or replace function select_persons()
returns SETOF person as $$
begin
    return QUERY select * from person;
end;
$$ language plpgsql;