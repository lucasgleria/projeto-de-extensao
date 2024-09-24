package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import interfaces.IClienteDAO;
import model.Cliente;

public class ClienteDAO implements IClienteDAO {
    private Connection connection;

    public ClienteDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int obterProximoIdCliente() throws SQLException {
        String sql = "SELECT MAX(id) AS max_id FROM clientes";
        try (PreparedStatement stmt = connection.prepareStatement(sql); 
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                if (maxId < 0) {
                    throw new SQLException("ID negativo no banco, erro crítico");
                }
                return maxId + 1;
            }
        }
        return 1;
    }

    @Override
    public void adicionarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (id, nome) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cliente.getId());
            stmt.setString(2, cliente.getNome());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar cliente: " + e.getMessage());
            throw e; // Propaga a exceção para que o chamador possa tratá-la se necessário
        }
    }

    @Override
    public Cliente buscarCliente(int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(rs.getInt("id"), rs.getString("nome"));
                }
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente: " + e.getMessage());
            throw e; // Propaga a exceção para que o chamador possa tratá-la se necessário
        }
    }
    
    @Override
    public void atualizarNomeCliente(int id, Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nome = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setInt(2, cliente.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
            throw e; // Propaga a exceção para que o chamador possa tratá-la se necessário
        }
    }

    @Override
    public void deletarCliente(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar cliente: " + e.getMessage());
            throw e; // Propaga a exceção para que o chamador possa tratá-la se necessário
        }
    }

    @Override
    public List<Cliente> listarClientes() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(new Cliente(rs.getInt("id"), rs.getString("nome")));
            }
        }
        return clientes;
    }

    @Override
public List<Cliente> obterTodos() throws SQLException {
    List<Cliente> clientes = new ArrayList<>();
    String sql = "SELECT * FROM clientes";
    try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            clientes.add(new Cliente(rs.getInt("id"), rs.getString("nome")));
        }
    } catch (SQLException e) {
        System.err.println("Erro ao listar todos os clientes: " + e.getMessage());
        throw e; // Propaga a exceção para que o chamador possa tratá-la se necessário
    }
    return clientes;
}

}
