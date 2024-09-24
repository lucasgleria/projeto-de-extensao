package thread;

import java.sql.SQLException; // Adicione esta linha

import interfaces.IProdutoService;
import model.Produto;

public class AdicionarProdutoThread extends Thread {
    private IProdutoService produtoService;
    private Produto produto;

    public AdicionarProdutoThread(IProdutoService produtoService, Produto produto) {
        this.produtoService = produtoService;
        this.produto = produto;
    }

    @Override
    public void run() {
        try {
            produtoService.salvar(produto);
        } catch (SQLException e) { 
            System.err.println("Erro ao adicionar produto na thread: " + e.getMessage());
        }
    }
}
