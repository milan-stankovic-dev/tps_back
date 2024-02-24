create or replace view smederevci as
select *
from person
where city_birth_id = 5 OR city_residence_id = 5;