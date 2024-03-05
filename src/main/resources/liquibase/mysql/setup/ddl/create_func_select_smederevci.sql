create or replace function select_smederevci()
returns SETOF smederevci as $$
begin
    return QUERY select * from smederevci;
end;
$$ language plpgsql;