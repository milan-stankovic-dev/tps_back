CREATE VIEW smederevci AS
SELECT *
FROM person
WHERE city_birth_id = 5 OR city_residence_id = 5;
