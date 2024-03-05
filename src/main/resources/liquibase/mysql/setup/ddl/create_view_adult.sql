create or replace view adults as
select *
from person
where age_in_months >= 216;