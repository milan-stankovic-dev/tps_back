create or replace procedure delete_person(person_delete_id BIGINT)
as $$
begin
    delete from person where id =person_delete_id;
end;
$$ language plpgsql;