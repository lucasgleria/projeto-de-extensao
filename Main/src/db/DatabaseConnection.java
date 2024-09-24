package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/estoque"; // Nome do banco de dados incluído
    private static final String USER = "root";
    private static final String PASSWORD = "1234"; // Substitua pela senha do seu banco de dados

    // INFORMAÇÕES DA CLIENTE NÃO SERÃO COMPARTILHADAS. ESSA VERSÃO É UMA TEMPLATE.

    public static Connection getConnection() throws SQLException {
        try {
            // Certifique-se de que o driver JDBC está registrado
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC MySQL não encontrado", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

