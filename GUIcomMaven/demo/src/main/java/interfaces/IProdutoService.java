package interfaces;

import java.sql.SQLException;
import java.util.List;

import model.Cliente;
import model.Produto;

public interface IProdutoService {
    int obterId() throws SQLException;
    void salvar(Produto produto) throws SQLException;
    Produto buscar(int id) throws SQLException;
    void atualizar(int id, String novoNome, double novoPrecoProduto) throws SQLException;
    void deletar(int id) throws SQLException;
    void deletarPorCliente(int clienteId) throws SQLException;
    List<Produto> listar() throws SQLException;
    List<Produto> listarPorCliente(int clienteId) throws SQLException;
    List<Produto> obterTodos() throws SQLException;
}
