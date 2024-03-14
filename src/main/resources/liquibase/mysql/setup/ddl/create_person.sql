CREATE TABLE IF NOT EXISTS person (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(30) COLLATE "sr_RS.utf8"
    NOT NULL CHECK (LENGTH(first_name) BETWEEN 2 AND 30 AND first_name ~ '^[A-ZČĆŠŽĐ][a-zčćšžđ]{1,}$'),
    last_name VARCHAR(30) COLLATE "sr_RS.utf8"
    NOT NULL CHECK (LENGTH(last_name) BETWEEN 2 AND 30 AND last_name ~ '^[A-ZČĆŠŽĐ][a-zčćšžđ]{1,}$'),
    height_in_cm INT NOT NULL
    CHECK (height_in_cm between 70 and 260),
    dob DATE NOT NULL CHECK (dob BETWEEN '1950-01-01' AND '2005-12-31'),
    age_in_months INT CHECK (age_in_months BETWEEN 120 AND 1200),
    city_birth_id BIGSERIAL,
    city_residence_id BIGSERIAL,
    FOREIGN KEY (city_birth_id) REFERENCES city(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (city_residence_id) REFERENCES city(id) ON UPDATE SET NULL ON DELETE SET NULL
);