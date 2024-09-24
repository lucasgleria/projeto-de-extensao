import java.sql.Connection;
import java.sql.SQLException;

import dao.ClienteDAO;
import dao.ProdutoDAO;
import db.DatabaseConnection;
import interfaces.IClienteDAO;
import interfaces.IClienteService;
import interfaces.IProdutoDAO;
import interfaces.IProdutoService;
import service.ClienteService;
import service.ProdutoService;
import utils.TaskExecutorManager;

public class AppConfig {
    private Connection connection;
    private TaskExecutorManager taskExecutorManager;

    public AppConfig() throws SQLException {
        inicializarConexao();
        inicializarTaskExecutorManager();
    }

    private void inicializarConexao() throws SQLException {
        try {
            this.connection = DatabaseConnection.getConnection();
            if (this.connection == null || this.connection.isClosed()) {
                throw new SQLException("Conexão com o banco de dados falhou. Conexão é nula ou está fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao estabelecer conexão com o banco de dados: " + e.getMessage());
            throw new RuntimeException("Falha ao inicializar a configuração da aplicação devido a erro de conexão.", e);
        }
    }

    private void inicializarTaskExecutorManager() {
        try {
            this.taskExecutorManager = new TaskExecutorManager(10);
            if (this.taskExecutorManager == null) {
                throw new IllegalStateException("Falha ao inicializar o gerenciador de execução de tarefas.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao inicializar o gerenciador de tarefas: " + e.getMessage());
            throw new RuntimeException("Falha ao inicializar a configuração da aplicação devido a erro no gerenciador de tarefas.", e);
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("Conexão não foi inicializada corretamente.");
        }
        return connection;
    }

    public TaskExecutorManager getTaskExecutorManager() {
        if (taskExecutorManager == null) {
            throw new IllegalStateException("Gerenciador de tarefas não foi inicializado corretamente.");
        }
        return taskExecutorManager;
    }

    public IClienteDAO clienteDAO() {
        return new ClienteDAO(getConnection());
    }

    public IProdutoDAO produtoDAO() {
        return new ProdutoDAO(getConnection());
    }

    public IClienteService clienteService() {
        return new ClienteService(clienteDAO(), getConnection());
    }

    public IProdutoService produtoService() {
        return new ProdutoService(produtoDAO(), getConnection());
    }
}
