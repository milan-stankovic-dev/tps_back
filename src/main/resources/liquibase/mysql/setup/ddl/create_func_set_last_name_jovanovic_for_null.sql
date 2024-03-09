create or replace function set_last_name_jovanovic_for_null()
returns trigger as $$
begin
    IF new.last_name IS NULL
    THEN new.last_name := 'Jovanović';
    END IF;

    return new;
end;
$$ language plpgsql;