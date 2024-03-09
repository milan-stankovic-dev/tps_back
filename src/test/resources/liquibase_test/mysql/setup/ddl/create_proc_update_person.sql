CREATE ALIAS update_person AS $$
void updatePerson(long p_id, String p_first_name, String p_last_name,
                  int p_height_in_cm, Date p_dob, int p_age_in_months,
                  long p_city_birth_id, long p_city_residence_id) throws java.sql.SQLException {
    try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:test");
         java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE person SET "
                 + "first_name = ?, "
                 + "last_name = ?, "
                 + "height_in_cm = ?, "
                 + "dob = ?, "
                 + "age_in_months = ?, "
                 + "city_birth_id = ?, "
                 + "city_residence_id = ? "
                 + "WHERE id = ?")) {
        ps.setString(1, p_first_name);
        ps.setString(2, p_last_name);
        ps.setInt(3, p_height_in_cm);
        ps.setDate(4, p_dob);
        ps.setInt(5, p_age_in_months);
        ps.setLong(6, p_city_birth_id);
        ps.setLong(7, p_city_residence_id);
        ps.setLong(8, p_id);

        ps.executeUpdate();
    }
}
$$;
