create trigger calculate_age_insert
before insert on person
for each row
execute function calculate_age_in_months();