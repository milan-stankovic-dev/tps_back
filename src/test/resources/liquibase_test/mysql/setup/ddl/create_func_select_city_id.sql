CREATE ALIAS select_city_by_id AS $$
void selectCityById(long id_param, java.sql.ResultSet[] result) throws java.sql.SQLException {
    java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:test");
    try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT * FROM city WHERE id = ?");
         java.sql.ResultSet rs = ps.executeQuery()) {
        ps.setLong(1, id_param);
        result[0] = rs;
    }
}
$$;
