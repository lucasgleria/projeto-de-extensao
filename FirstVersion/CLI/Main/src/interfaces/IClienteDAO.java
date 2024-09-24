package interfaces;

import model.Cliente;
import java.sql.SQLException;
import java.util.List;

public interface IClienteDAO {
    int obterProximoIdCliente() throws SQLException;
    void adicionarCliente(Cliente cliente) throws SQLException;
    Cliente buscarCliente(int id) throws SQLException;
    void atualizarNomeCliente(int id, Cliente cliente) throws SQLException;
    void deletarCliente(int id) throws SQLException;
    List<Cliente> listarClientes() throws SQLException;
}
