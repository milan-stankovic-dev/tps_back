create trigger calculate_age_update
before update on person
for each row
execute function calculate_age_in_months();