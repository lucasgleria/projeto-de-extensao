package thread;

import java.sql.SQLException; // Adicione esta linha

import interfaces.IClienteService;

// Nova classe ExcluirClienteThread
public class DeletarClienteThread extends Thread {
    private final int clienteId;
    private final IClienteService clienteService;

    public DeletarClienteThread(int clienteId, IClienteService clienteService) {
        this.clienteId = clienteId;
        this.clienteService = clienteService;
    }

    @Override
    public void run() {
        try {
            clienteService.deletar(clienteId);
        } catch (SQLException e) {
            System.err.println("Erro ao excluir cliente na thread: " + e.getMessage());
        }
    }
}
