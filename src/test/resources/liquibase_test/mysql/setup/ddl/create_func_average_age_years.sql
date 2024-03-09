CREATE ALIAS average_age_years AS $$
    double averageAgeYears() throws java.sql.SQLException {
        try (java.sql.PreparedStatement ps = java.sql.DriverManager.getConnection("jdbc:h2:mem:test").prepareStatement("SELECT AVG(age_in_months)/12 FROM person");
             java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            } else {
                return 0.0;
            }
        }
    }
$$;
