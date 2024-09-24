package interfaces;

import java.sql.SQLException;
import java.util.List;
import model.Produto;

public interface IProdutoDAO {
    int obterProximoIdProduto() throws SQLException;
    void adicionarProduto(Produto produto) throws SQLException;
    Produto buscarProduto(int id) throws SQLException;
    void atualizarNomeEPrecoProduto(int id, Produto produto, double preco) throws SQLException;
    void deletarProduto(int id) throws SQLException;
    void deletarProdutosPorCliente(int clienteId) throws SQLException;
    List<Produto> listarProdutos() throws SQLException;
    List<Produto> listarProdutosPorCliente(int clienteId) throws SQLException;
}
