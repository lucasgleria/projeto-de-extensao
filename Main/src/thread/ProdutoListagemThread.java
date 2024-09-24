package thread;

import interfaces.IProdutoService;
import model.Produto;
import java.sql.SQLException;
import java.util.List;

public class ProdutoListagemThread extends Thread {
    private IProdutoService produtoService;

    public ProdutoListagemThread(IProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Override
    public void run() {
        try {
            List<Produto> todosProdutos = produtoService.listar();
            if (todosProdutos.isEmpty()) {
                System.out.println("Nenhum produto encontrado.");
            } else {
                System.out.println("Produtos disponíveis:");
                System.out.println("+--------------------+");
                for (Produto produto : todosProdutos) {
                    System.out.println("ID=" + produto.getId() + ", Nome=" + produto.getNome() + ", Preço=" + produto.getPreco() + ", Cliente ID=" + produto.getClienteId());
                    System.out.println("");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        }
    }
}
