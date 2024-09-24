import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTimeoutException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.UUID;

import interfaces.IClienteService;
import interfaces.IProdutoService;
import model.Cliente;
import model.Produto;
import thread.AdicionarClienteThread;
import thread.AdicionarProdutoThread;
import thread.ClienteListagemThread;
import thread.DeletarClienteThread;
import thread.DeletarTodosClientesThread;
import thread.ProdutoListagemThread;
import utils.TaskExecutorManager;
import utils.TaskExecutorManager.TaskStatus;

public class Main {
    private final IClienteService clienteService;
    private final IProdutoService produtoService;
    private final TaskExecutorManager taskExecutorManager;

    public Main(IClienteService clienteService, IProdutoService produtoService, TaskExecutorManager taskExecutorManager) {
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.taskExecutorManager = taskExecutorManager;
    }

    public static void main(String[] args) {
        try {
            AppConfig appConfig = new AppConfig(); // De preferência, trate erros com exceções.
            Main mainApp = new Main(appConfig.clienteService(), appConfig.produtoService(), appConfig.getTaskExecutorManager());
            mainApp.run();  // Mantém a lógica de execução fora do construtor.
        } catch (SQLException e) {
            System.err.println("Erro ao configurar a aplicação: " + e.getMessage());
        }
    }

    private void run() {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;
    
        while (continuar) {
            try {
                exibirMenu();
                int opcao = scanner.nextInt();
                scanner.nextLine();  // Consumir a nova linha
    
                switch (opcao) {
                    case 1:
                        adicionarClienteEProdutos(scanner);
                        break;
                    case 2:
                        editarClienteOuProduto(scanner);
                        break;
                    case 3:
                        executarClienteListagemThread();
                        break;
                    case 4:
                        excluirCliente(scanner);
                        break;
                    case 5:
                        excluirTodosOsClientes();
                        break;
                    case 6:
                        adicionarProdutosAClienteExistente(scanner); 
                        break;
                    case 7:
                        deletarProdutosDeCliente(scanner);
                        break;
                    case 8:
                        System.out.println("Saindo...");
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, insira um número correspondente a uma das opções.");
                scanner.nextLine(); // Limpar buffer do scanner
            } catch (Exception e) {
                System.err.println("Erro na execução: " + e.getMessage());
            }
        }
        
        taskExecutorManager.shutdown();
        scanner.close();
    }
    

    private void exibirMenu() {
        System.out.println("\n+-------------------------------------------+");
        System.out.println("| Escolha uma opção:                        |");
        System.out.println("| 1. Adicionar Cliente e Produtos           |");
        System.out.println("| 2. Editar Cliente e Produto               |");
        System.out.println("| 3. Listar Clientes e Produtos             |");
        System.out.println("| 4. Excluir Cliente                        |");
        System.out.println("| 5. Excluir Todos os Clientes              |");
        System.out.println("| 6. Adicionar Produtos a Cliente Existente |");
        System.out.println("| 7. Deletar Produtos de Cliente Existente  |"); 
        System.out.println("| 8. Sair                                   |"); 
        System.out.println("+-------------------------------------------+\n");
    }
    

    private void adicionarClienteEProdutos(Scanner scanner) {
        try {
            int clienteId = clienteService.obterId();
            if (clienteId <= 0) {
                throw new SQLException("ID negativo no banco, erro crítico");
            }

            System.out.println("ID do Cliente será: " + clienteId+ "\n");
            System.out.print("Digite o Nome do Cliente: ");
            String clienteNome = scanner.nextLine();

            Cliente cliente = new Cliente(clienteId, clienteNome);
            String clienteTaskId = UUID.randomUUID().toString(); // Gerar ID único para a tarefa
            taskExecutorManager.submitTask(clienteTaskId, new AdicionarClienteThread(clienteService, cliente), 5000); // Timeout de 5 segundos

            monitorarStatusTarefa(clienteTaskId); // Monitorar o status da tarefa

            boolean adicionarMaisProdutos = true;

            while (adicionarMaisProdutos) {
                int produtoId = produtoService.obterId();
                if (produtoId <= 0) {
                    throw new SQLException("ID negativo no banco, erro crítico");
                }

                System.out.println("ID do Produto será: " + produtoId + "\n");
                System.out.print("Digite o Nome do Produto: ");
                String produtoNome = scanner.nextLine();
                System.out.print("Digite o Preço do Produto: ");
                double produtoPreco = scanner.nextDouble();
                scanner.nextLine();

                Produto produto = new Produto(produtoId, produtoNome, produtoPreco, clienteId);
                String produtoTaskId = UUID.randomUUID().toString(); // Gerar ID único para a tarefa
                taskExecutorManager.submitTask(produtoTaskId, new AdicionarProdutoThread(produtoService, produto), 5000); // Timeout de 5 segundos

                monitorarStatusTarefa(produtoTaskId); // Monitorar o status da tarefa

                System.out.print("Todos os produtos foram adicionados? (sim/não): ");
                String resposta = scanner.nextLine().trim().toLowerCase();
                adicionarMaisProdutos = resposta.startsWith("n");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Erro de integridade de dados: " + e.getMessage());
        } catch (SQLTimeoutException e) {
            System.err.println("Erro: Ocorreu um timeout ao tentar se conectar ao banco de dados. Tente novamente mais tarde.");
        } catch (SQLNonTransientConnectionException e) {
            System.err.println("Erro: Conexão ao banco de dados perdida. Por favor, verifique a conexão e tente novamente.");
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar cliente ou produto: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar buffer do scanner
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    private void editarClienteOuProduto(Scanner scanner) {
        try {
            System.out.println("O que você deseja editar:");
            System.out.println("1. Editar Cliente");
            System.out.println("2. Editar Produto");

            int opcaoEdicao = scanner.nextInt();
            scanner.nextLine();  // Consumir a nova linha

            switch (opcaoEdicao) {
                case 1:
                    executarClienteListagemThread();
                    System.out.print("Digite o ID do cliente a ser editado: ");
                    int idClienteEditar = scanner.nextInt();
                    scanner.nextLine();  // Consumir a nova linha

                    Cliente cliente = clienteService.buscar(idClienteEditar);
                    if (cliente != null) {
                        System.out.print("Digite o novo nome para o cliente: ");
                        String novoNomeCliente = scanner.nextLine();
                        clienteService.atualizar(idClienteEditar, novoNomeCliente);
                        System.out.println("Nome do cliente atualizado com sucesso.");
                    } else {
                        System.out.println("Cliente não encontrado.");
                    }
                    break;

                case 2:
                    executarProdutoListagemThread();
                    System.out.print("Digite o ID do produto a ser editado: ");
                    int idProdutoEditar = scanner.nextInt();
                    scanner.nextLine();  // Consumir a nova linha

                    Produto produto = produtoService.buscar(idProdutoEditar);
                    if (produto != null) {
                        System.out.print("Digite o novo nome para o produto: ");
                        String novoNomeProduto = scanner.nextLine();
                        System.out.print("Digite o novo preço para o produto: ");
                        double novoPrecoProduto = scanner.nextDouble();
                        scanner.nextLine();  // Consumir a nova linha

                        produtoService.atualizar(idProdutoEditar, novoNomeProduto, novoPrecoProduto);
                        System.out.println("Produto atualizado com sucesso.");
                    } else {
                        System.out.println("Produto não encontrado.");
                    }
                    break;

                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Erro de integridade de dados: " + e.getMessage());
        } catch (SQLTimeoutException e) {
            System.err.println("Erro: Ocorreu um timeout ao tentar se conectar ao banco de dados.");
        } catch (SQLException e) {
            System.err.println("Erro ao editar cliente ou produto: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar buffer do scanner
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    private void excluirCliente(Scanner scanner) {
        try {
            executarClienteListagemThread();  // Listar clientes antes de deletar
            System.out.print("Digite o ID do cliente a ser excluído: ");
            int clienteId = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha

            if (clienteId <= 0) {
                System.out.println("ID inválido. O ID deve ser um número positivo.");
                return;
            }

            Cliente cliente = clienteService.buscar(clienteId);
            if (cliente == null) {
                System.out.println("Cliente não encontrado. Por favor, insira um ID válido.");
            } else {
                String clienteTaskId = UUID.randomUUID().toString(); // Gerar ID único para a tarefa
                taskExecutorManager.submitTask(clienteTaskId, new DeletarClienteThread(clienteId, clienteService), 5000);
                
                
                monitorarStatusTarefa(clienteTaskId); // Monitorar o status da tarefa
                
                System.out.println("Cliente excluído com sucesso.");
            }
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Por favor, insira um número inteiro para o ID do cliente.");
            scanner.nextLine(); // Limpar buffer do scanner
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao deletar cliente: " + e.getMessage());
        }
    }

    private void excluirTodosOsClientes() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Tem certeza de que deseja deletar todos os clientes? (sim/não): ");
            String confirmacao = scanner.nextLine().trim().toLowerCase();

            if (confirmacao.startsWith("s")) {
                String clienteTaskId = UUID.randomUUID().toString(); // Gerar ID único para a tarefa
                taskExecutorManager.submitTask(clienteTaskId, new DeletarTodosClientesThread(clienteService), 5000);

                
                monitorarStatusTarefa(clienteTaskId); // Monitorar o status da tarefa

                System.out.println("Todos os clientes foram excluídos com sucesso.");
            } else {
                System.out.println("Operação de exclusão cancelada.");
            }
        } catch (Exception e) {
            System.err.println("Erro inesperado ao tentar deletar todos os clientes: " + e.getMessage());
        }
    }

    private void adicionarProdutosAClienteExistente(Scanner scanner) {
        try {
            executarClienteListagemThread(); // Listar clientes existentes
            System.out.print("Digite o ID do cliente ao qual deseja adicionar produtos: ");
            int clienteId = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha
    
            Cliente cliente = clienteService.buscar(clienteId); // Verificar se o cliente existe
            if (cliente == null) {
                System.out.println("Cliente não encontrado. Por favor, insira um ID válido.");
                return;
            }
    
            boolean adicionarMaisProdutos = true;
    
            while (adicionarMaisProdutos) {
                int produtoId = produtoService.obterId();
                if (produtoId <= 0) {
                    throw new SQLException("ID negativo no banco, erro crítico");
                }
    
                System.out.println("ID do Produto será: " + produtoId + "\n");
                System.out.print("Digite o Nome do Produto: ");
                String produtoNome = scanner.nextLine();
                System.out.print("Digite o Preço do Produto: ");
                double produtoPreco = scanner.nextDouble();
                scanner.nextLine(); // Consumir nova linha
    
                Produto produto = new Produto(produtoId, produtoNome, produtoPreco, clienteId);
                String produtoTaskId = UUID.randomUUID().toString(); // Gerar ID único para a tarefa
                taskExecutorManager.submitTask(produtoTaskId, new AdicionarProdutoThread(produtoService, produto), 5000); // Timeout de 5 segundos
    
                monitorarStatusTarefa(produtoTaskId); // Monitorar o status da tarefa
    
                System.out.print("Deseja adicionar mais produtos ao cliente? (sim/não): ");
                String resposta = scanner.nextLine().trim().toLowerCase();
                adicionarMaisProdutos = resposta.startsWith("s");
            }
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar buffer do scanner
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar produtos ao cliente: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao adicionar produtos: " + e.getMessage());
        }
    }

    private void deletarProdutosDeCliente(Scanner scanner) {
        try {
            executarClienteListagemThread(); // Listar clientes existentes
            System.out.print("Digite o ID do cliente do qual deseja deletar produtos: ");
            int clienteId = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha
    
            Cliente cliente = clienteService.buscar(clienteId); // Verificar se o cliente existe
            if (cliente == null) {
                System.out.println("Cliente não encontrado. Por favor, insira um ID válido.");
                return;
            }
    
            executarProdutoListagemThread(); // Listar produtos existentes
    
            System.out.print("Digite o ID do produto que deseja deletar: ");
            int produtoId = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha
    
            Produto produto = produtoService.buscar(produtoId); // Verificar se o produto existe
            if (produto == null || produto.getClienteId() != clienteId) {
                System.out.println("Produto não encontrado para este cliente. Por favor, insira um ID válido.");
                return;
            }
    
            String produtoTaskId = UUID.randomUUID().toString(); // Gerar ID único para a tarefa
            taskExecutorManager.submitTask(produtoTaskId, () -> {
                try {
                    produtoService.deletar(produtoId);
                    System.out.println("Produto excluído com sucesso.");
                } catch (SQLException e) {
                    System.err.println("Erro ao deletar produto: " + e.getMessage());
                }
            }, 5000); // Timeout de 5 segundos
    
            monitorarStatusTarefa(produtoTaskId); // Monitorar o status da tarefa
    
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar buffer do scanner
        } catch (SQLException e) {
            System.err.println("Erro ao deletar produtos do cliente: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao deletar produtos: " + e.getMessage());
        }
    }
    

    private void executarClienteListagemThread() {
        Thread clienteListagemThread = new ClienteListagemThread(clienteService, produtoService);
        // clienteListagemThread.start();
        
        String clienteTaskId = UUID.randomUUID().toString(); // Gerar ID único para a tarefa
        taskExecutorManager.submitTask(clienteTaskId, new ClienteListagemThread(clienteService, produtoService), 5000);

        
        try {
            monitorarStatusTarefa(clienteTaskId); // Monitorar o status da tarefa
            clienteListagemThread.join();  // Aguarda a conclusão da thread
        } catch (InterruptedException e) {
            System.err.println("Erro ao executar a thread de listagem de clientes: " + e.getMessage());
            Thread.currentThread().interrupt(); // Preserva o status de interrupção da thread atual
        } catch (Exception e) {
            System.err.println("Erro inesperado ao listar clientes: " + e.getMessage());
        }
    }

    private void executarProdutoListagemThread() {
        Thread produtoListagemThread = new ProdutoListagemThread(produtoService);
        // produtoListagemThread.start();

        String clienteTaskId = UUID.randomUUID().toString(); // Gerar ID único para a tarefa
        taskExecutorManager.submitTask(clienteTaskId, new ProdutoListagemThread(produtoService), 5000);

        
        try {
            monitorarStatusTarefa(clienteTaskId); // Monitorar o status da tarefa
            produtoListagemThread.join();  // Aguarda a conclusão da thread
        } catch (InterruptedException e) {
            System.err.println("Erro ao executar a thread de listagem de produtos: " + e.getMessage());
            Thread.currentThread().interrupt(); // Preserva o status de interrupção da thread atual
        } catch (Exception e) {
            System.err.println("Erro inesperado ao listar produtos: " + e.getMessage());
        }
    }

    // Novo método para monitorar o status das tarefas
    private void monitorarStatusTarefa(String taskId) {
        TaskStatus status = taskExecutorManager.getTaskStatus(taskId);
        while (status == TaskStatus.PENDING || status == TaskStatus.RUNNING) {
            System.out.println("Aguardando conclusão da tarefa " + taskId + "...");
            try {
                Thread.sleep(1000); // Espera de 1 segundo antes de verificar novamente
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            status = taskExecutorManager.getTaskStatus(taskId);
        }
        System.out.println("Status da tarefa " + taskId + ": " + status);
    }
}
