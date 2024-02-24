CREATE TABLE IF NOT EXISTS person (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(30) COLLATE "sr_RS.utf8"
    NOT NULL CHECK (LENGTH(first_name) BETWEEN 2 AND 30),
    last_name VARCHAR(30) COLLATE "sr_RS.utf8"
    CHECK (LENGTH(last_name) BETWEEN 2 AND 30),
    height_in_cm INT NOT NULL
    CHECK (height_in_cm between 70 and 260),
    dob DATE NOT NULL CHECK (dob BETWEEN '1950-01-01' AND '2005-12-31'),
    age_in_months INT,
    city_birth_id BIGSERIAL,
    city_residence_id BIGSERIAL,
    FOREIGN KEY (city_birth_id) REFERENCES city(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    FOREIGN KEY (city_residence_id) REFERENCES city(id) ON UPDATE RESTRICT ON DELETE RESTRICT
);