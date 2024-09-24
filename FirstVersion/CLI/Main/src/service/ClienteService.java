package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import interfaces.IClienteDAO;
import interfaces.IClienteService;
import model.Cliente;


public class ClienteService implements IClienteService {
    private final IClienteDAO clienteDAO;

    public ClienteService(IClienteDAO clienteDAO, Connection connection) {
        this.clienteDAO = clienteDAO;
    }

    @Override
    public int obterId() throws  SQLException {
        return clienteDAO.obterProximoIdCliente();
    }

    @Override
    public void salvar(Cliente cliente) throws SQLException {
        clienteDAO.adicionarCliente(cliente);
    }

    @Override
    public Cliente buscar(int id) throws SQLException {
        return clienteDAO.buscarCliente(id);
    }

    @Override
    public void atualizar(int id, String novoNome) throws SQLException {
        Cliente cliente = buscar(id);
        if (cliente != null) {
            cliente.setNome(novoNome);
            clienteDAO.atualizarNomeCliente(id, cliente);
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        clienteDAO.deletarCliente(id);
    }

    @Override
    public List<Cliente> listar() throws SQLException {
        return clienteDAO.listarClientes();
    }
}
