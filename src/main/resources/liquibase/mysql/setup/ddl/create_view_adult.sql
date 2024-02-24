create or replace view punoletni as
select *
from person
where age_in_months >= 216;