package thread;

import java.sql.SQLException;

import interfaces.IProdutoService;

public class DeletarProdutoThread extends Thread {
    private final int produtoId;
    private final IProdutoService produtoService;

    public DeletarProdutoThread(int produtoId, IProdutoService produtoService) {
        this.produtoId = produtoId;
        this.produtoService = produtoService;
    }

    @Override
    public void run() {
        try {
            produtoService.deletar(produtoId);
            System.out.println("Produto com ID " + produtoId + " deletado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao deletar o produto: " + e.getMessage());
        }
    }
}
