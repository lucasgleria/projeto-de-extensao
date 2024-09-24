package thread;

import java.sql.SQLException;
import java.util.List;

import interfaces.IClienteService;
import model.Cliente;

public class DeletarTodosClientesThread extends Thread {
    private final IClienteService clienteService;

    public DeletarTodosClientesThread(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public void run() {
        try {
            List<Cliente> todosClientesParaExcluir = clienteService.listar();
            for (Cliente cliente : todosClientesParaExcluir) {
                clienteService.deletar(cliente.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir todos os clientes: " + e.getMessage());
        }
    }
}
