create trigger set_last_name_jovanovic_insert
before insert on person
for each row
execute function set_last_name_jovanovic_for_null();