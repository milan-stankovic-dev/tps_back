CREATE ALIAS select_persons AS $$
ResultSet selectPersons() throws java.sql.SQLException {
    java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:test");
    java.sql.Statement statement = conn.createStatement();
    return statement.executeQuery("SELECT * FROM person");
}
$$;
