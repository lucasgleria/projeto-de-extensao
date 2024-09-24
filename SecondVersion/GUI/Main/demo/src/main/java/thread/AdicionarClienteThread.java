package thread;

import interfaces.IClienteService;
import model.Cliente;

public class AdicionarClienteThread extends Thread {
    private final IClienteService clienteService;
    private final Cliente cliente;

    public AdicionarClienteThread(IClienteService clienteService, Cliente cliente) {
        this.clienteService = clienteService;
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            clienteService.salvar(cliente);
        } catch (Exception e) {
            System.err.println("Erro ao adicionar cliente na thread: " + e.getMessage());
        }
    }
}