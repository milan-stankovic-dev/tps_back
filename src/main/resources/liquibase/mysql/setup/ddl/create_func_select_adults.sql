create or replace function select_adults()
returns SETOF adults as $$
begin
    return QUERY select * from adults;
end;
$$ language plpgsql;