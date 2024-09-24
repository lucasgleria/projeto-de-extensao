package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import interfaces.IProdutoDAO;
import interfaces.IProdutoService;
import model.Produto;

public class ProdutoService implements IProdutoService {
    private final IProdutoDAO produtoDAO;

    public ProdutoService(IProdutoDAO produtoDAO, Connection connection) {
        this.produtoDAO = produtoDAO;
    }
    
    @Override
    public int obterId() throws SQLException {
        return produtoDAO.obterProximoIdProduto();
    } 

    @Override
    public void salvar(Produto produto) throws SQLException {
        produtoDAO.adicionarProduto(produto);
    }
    
    @Override
    public Produto buscar(int id) throws SQLException {
        return produtoDAO.buscarProduto(id);
    }
    
    @Override
    public void atualizar(int id, String novoNome, double novoPreco) throws SQLException {
        Produto produto = buscar(id);
        if (produto != null) {
            produto.setNome(novoNome); 
            produto.setPreco(novoPreco);
            produtoDAO.atualizarNomeEPrecoProduto(id, produto, novoPreco);
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        produtoDAO.deletarProduto(id);
    }

    @Override
    public void deletarPorCliente(int clienteId) throws SQLException {
        produtoDAO.deletarProdutosPorCliente(clienteId);
    }
    @Override
    public List<Produto> listar() throws SQLException {
        return produtoDAO.listarProdutos();
    }

    @Override
    public List<Produto> listarPorCliente(int clienteId) throws SQLException {
        return produtoDAO.listarProdutosPorCliente(clienteId);
    }
}
