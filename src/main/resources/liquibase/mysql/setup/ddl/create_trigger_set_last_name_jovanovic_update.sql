create trigger set_last_name_jovanovic_update
before update on person
for each row
execute function set_last_name_jovanovic_for_null();