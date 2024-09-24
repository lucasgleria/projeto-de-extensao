package thread;

import java.sql.SQLException;
import java.util.List;

import interfaces.IClienteService;
import interfaces.IProdutoService;
import model.Cliente;
import model.Produto;

public class ClienteListagemThread extends Thread {
    private IClienteService clienteService;
    private IProdutoService produtoService;

    public ClienteListagemThread(IClienteService clienteService, IProdutoService produtoService) {
        this.clienteService = clienteService;
        this.produtoService = produtoService;
    }

    @Override
    public void run() {
        try {
            List<Cliente> todosClientes = clienteService.listar();
            if (todosClientes.isEmpty()) {
                System.out.println("Nenhum cliente encontrado.");
            } else {
                System.out.println("Clientes disponíveis:");
                System.out.println("+-------------------+");
                for (Cliente cliente : todosClientes) {
                    System.out.println("");
                    System.out.println("Nome do Cliente = " + cliente.getNome().toUpperCase() + " | ID do Cliente = " + cliente.getId());

                    // Listar produtos associados a este cliente
                    List<Produto> produtos = produtoService.listarPorCliente(cliente.getId());
                    if (produtos.isEmpty()) {
                        System.out.println("Nenhum produto associado a este cliente.");
                    } else {
                        System.out.println("Produtos de " + cliente.getNome().toUpperCase() + ":");
                        for (Produto produto : produtos) {
                            System.out.println("*  ID do produto = " + produto.getId() + " | " + produto.getNome() + " | Preço = " + produto.getPreco());
                        }
                    }
                    System.out.println("");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar clientes: " + e.getMessage());
        }
    }
}
