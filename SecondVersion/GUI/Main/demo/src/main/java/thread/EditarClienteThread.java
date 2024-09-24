package thread;

import interfaces.IClienteService;
import model.Cliente;

import java.sql.SQLException;

public class EditarClienteThread extends Thread {
    private final int clienteId;
    private final String novoNome;
    private final IClienteService clienteService;

    public EditarClienteThread(int clienteId, String novoNome, IClienteService clienteService) {
        this.clienteId = clienteId;
        this.novoNome = novoNome;
        this.clienteService = clienteService;
    }

    @Override
    public void run() {
        try {
            clienteService.atualizar(clienteId, novoNome);
            System.out.println("Cliente atualizado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao editar o cliente: " + e.getMessage());
        }
    }
}
