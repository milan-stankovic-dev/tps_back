CREATE ALIAS delete_person AS $$
void deletePerson(long person_delete_id) throws java.sql.SQLException {
    try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:test");
         java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM person WHERE id = ?")) {
        ps.setLong(1, person_delete_id);
        ps.executeUpdate();
    }
}
$$;
