package interfaces;

import java.sql.SQLException;
import java.util.List;

import model.Cliente;

public interface IClienteService {
    int obterId() throws SQLException;
    void salvar(Cliente cliente) throws SQLException;
    Cliente buscar(int id) throws SQLException;
    void atualizar(int id, String novoNome) throws SQLException;
    void deletar(int id) throws SQLException;
    List<Cliente> listar() throws SQLException;
}
