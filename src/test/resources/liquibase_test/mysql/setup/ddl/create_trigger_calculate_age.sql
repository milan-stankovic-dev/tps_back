CREATE TRIGGER calculate_age_insert
BEFORE INSERT ON person
FOR EACH ROW
SET NEW.age_in_months = (YEAR(CURRENT_DATE) - YEAR(new.dob)) * 12 + MONTH(CURRENT_DATE) - MONTH(new.dob);
