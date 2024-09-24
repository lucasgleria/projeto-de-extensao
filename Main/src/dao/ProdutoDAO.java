package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import interfaces.IProdutoDAO;
import model.Produto;

public class ProdutoDAO implements IProdutoDAO {
    private Connection connection;

    public ProdutoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int obterProximoIdProduto() throws SQLException {
        String sql = "SELECT MAX(id) AS max_id FROM produtos";
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
        // Se não houver nenhum produto, retorna 1
        return 1;
    }

    @Override
    public void adicionarProduto(Produto produto) throws SQLException {
        String sql = "INSERT INTO Produtos (nome, preco, cliente_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getClienteId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar produto: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Produto buscarProduto(int id) throws SQLException {
        String sql = "SELECT * FROM Produtos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco"),
                        rs.getInt("cliente_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto: " + e.getMessage());
            throw e;
        }
        return null;
    }

    @Override
    public void atualizarNomeEPrecoProduto(int id, Produto produto, double preco) throws SQLException {
        String sql = "UPDATE Produtos SET nome = ?, preco = ?, cliente_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getClienteId());
            stmt.setInt(4, produto.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar produto: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deletarProduto(int id) throws SQLException {
        String sql = "DELETE FROM Produtos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar produto: " + e.getMessage());
            throw e; // Propaga a exceção para que o chamador possa tratá-la se necessário
        }
    }

    @Override
    public void deletarProdutosPorCliente(int clienteId) throws SQLException {
        String sql = "DELETE FROM Produtos WHERE cliente_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar produtos do cliente: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Produto> listarProdutos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                produtos.add(new Produto(rs.getInt("id"), rs.getString("nome"), rs.getDouble("preco"), rs.getInt("cliente_id")));
            }
        }
        return produtos;
    }

    @Override
    public List<Produto> listarProdutosPorCliente(int clienteId) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE cliente_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(new Produto(rs.getInt("id"), rs.getString("nome"), rs.getDouble("preco"), rs.getInt("cliente_id")));
                }
            }
        }
        return produtos;
    }
}
