CREATE ALIAS select_person_by_id AS $$
void selectPersonById(long selection_id, java.sql.ResultSet[] result) throws java.sql.SQLException {
    java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:test");
    try (java.sql.PreparedStatement ps = conn.prepareStatement("SELECT * FROM person WHERE id = ?");
         ResultSet rs = ps.executeQuery()) {
        ps.setLong(1, selection_id);
        result[0] = rs;
    }
}
$$;
