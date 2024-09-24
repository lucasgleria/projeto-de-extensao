package thread;

import interfaces.IProdutoService;

import java.sql.SQLException;

public class EditarProdutoThread extends Thread {
    private final int produtoId;
    private final String novoNome;
    private final double novoPreco;
    private final IProdutoService produtoService;

    public EditarProdutoThread(int produtoId, String novoNome, double novoPreco, IProdutoService produtoService) {
        this.produtoId = produtoId;
        this.novoNome = novoNome;
        this.novoPreco = novoPreco;
        this.produtoService = produtoService;
    }

    @Override
    public void run() {
        try {
            produtoService.atualizar(produtoId, novoNome, novoPreco);
            System.out.println("Produto atualizado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao editar o produto: " + e.getMessage());
        }
    }
}
