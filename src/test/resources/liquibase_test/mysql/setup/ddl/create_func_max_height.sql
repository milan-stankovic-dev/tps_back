CREATE ALIAS max_height AS $$
    int max_height() throws java.sql.SQLException {
        try (java.sql.PreparedStatement ps = java.sql.DriverManager.getConnection("jdbc:h2:mem:test").prepareStatement("SELECT MAX(height_in_cm) FROM person");
             java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }
$$;
