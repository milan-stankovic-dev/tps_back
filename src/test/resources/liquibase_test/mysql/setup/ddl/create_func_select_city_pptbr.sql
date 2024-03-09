CREATE ALIAS select_city_by_pptbr AS $$
void selectCityByPptbr(int pptbr_param, java.sql.ResultSet[] result) throws java.sql.SQLException {
    java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:test");
    try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT * FROM city WHERE pptbr = ?");
         java.sql.ResultSet rs = ps.executeQuery()) {
        ps.setInt(1, pptbr_param);
        result[0] = rs;
    }
}
$$;
