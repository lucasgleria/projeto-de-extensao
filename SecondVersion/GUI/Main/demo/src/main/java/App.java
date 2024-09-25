import javax.imageio.ImageIO;
import javax.swing.*;

// AIzaSyA5JS-OGg93WTcR86_UB7AgTLm6T6__N8Q
import java.awt.*;
import java.awt.image.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//
import java.io.*;
//
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTimeoutException;
//
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.List;

//
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
//
import interfaces.IClienteService;
import interfaces.IProdutoService;
//
import model.Cliente;
import model.Produto;
//
import thread.AdicionarClienteThread;
import thread.AdicionarProdutoThread;
import thread.DeletarClienteThread;
import thread.DeletarProdutoThread;
import thread.DeletarTodosClientesThread;
import thread.EditarClienteThread;
import thread.EditarProdutoThread;
//
import utils.TaskExecutorManager.TaskStatus;
import utils.TaskExecutorManager;


public class App extends JFrame {
    private final IClienteService clienteService;
    private final IProdutoService produtoService;
    private final TaskExecutorManager taskExecutorManager;

    public App(IClienteService clienteService, IProdutoService produtoService, TaskExecutorManager taskExecutorManager) {
        this.clienteService = clienteService;
        this.produtoService = produtoService;
        this.taskExecutorManager = taskExecutorManager;

        setTitle("Gestão de Clientes e Produtos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJanelaIcone("demo\\src\\main\\resources\\icons\\imagem1.jpg");
        
        // Define o layout do JFrame como BorderLayout para centralizar o painel de botões
        setLayout(new BorderLayout());

         // Painel superior com imagem e texto
        JPanel painelSuperior = new JPanel();
        painelSuperior.setLayout(new BorderLayout());

         // Adiciona a imagem
        JLabel labelImagem = new JLabel();
        labelImagem.setIcon(new ImageIcon("demo\\src\\main\\resources\\icons\\imagem1.jpg"));
        labelImagem.setHorizontalAlignment(SwingConstants.CENTER);
        painelSuperior.add(labelImagem, BorderLayout.CENTER);

         // Adiciona o texto de copyright
        JLabel labelCopyright = new JLabel("© 2024 lleria.\n MIT LICENSE.", SwingConstants.CENTER);
        painelSuperior.add(labelCopyright, BorderLayout.SOUTH);

         // Adiciona o painel superior ao topo da janela
        add(painelSuperior, BorderLayout.NORTH);
        // 

        // Cria um painel para os botões com GridLayout
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10)); // 0 linhas, 1 coluna
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margem ao redor dos botões

        // Botões
        JButton addButton = new JButton("Adicionar Cliente e Produtos");
        JButton listCButton = new JButton("Listar Clientes");
        JButton listPButton = new JButton("Listar Produtos");
        JButton editButton = new JButton("Editar Cliente/Produtos");
        JButton deleteButton = new JButton("Deletar Cliente");
        JButton deletePButton = new JButton("Deletar Produto");

         // Painel de rodapé com o texto de contato
        JPanel painelRodape = new JPanel();
        painelRodape.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        // Texto de contato com link mailto
        String textoContato = "<html>Contact <a href='mailto:lucasleria17@gmail.com?subject=Ol%C3%A1!%20acessei%20seu%20perfil%20pelo%20GitHub%20e%20gostaria%20de%20falar%20com%20voc%C3%AA!&body=_Escreva%20aqui%20sua%20mensagem_ target='_blank'>lucasleria17@gmail.com</a> for any maintenance.</html>";
        JLabel labelContato = new JLabel(textoContato);
        
        // Torna o link clicável
        labelContato.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        painelRodape.add(labelContato);

        // Adiciona o painel de rodapé na parte inferior
        add(painelRodape, BorderLayout.SOUTH);

        // Define o tamanho preferido dos botões
        Dimension buttonSize = new Dimension(100, 30); // Largura 200, Altura 30
        addButton.setPreferredSize(buttonSize);
        listCButton.setPreferredSize(buttonSize);
        listPButton.setPreferredSize(buttonSize);
        editButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        deletePButton.setPreferredSize(buttonSize);

        // Aumenta a fonte do texto dos botões
        Font buttonFont = new Font("Arial", Font.PLAIN, 50); // Fonte Arial, tamanho 16
        addButton.setFont(buttonFont);
        listCButton.setFont(buttonFont);
        listPButton.setFont(buttonFont);
        editButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);
        deletePButton.setFont(buttonFont);

        // Adiciona os botões ao painel
        buttonPanel.add(addButton);
        buttonPanel.add(listCButton);
        buttonPanel.add(listPButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(deletePButton);

        // Adiciona o painel ao centro do frame
        add(buttonPanel, BorderLayout.CENTER);

        // Adiciona as ações aos botões
        addButton.addActionListener(e -> exibirOpcoesAdicionarCliente());
        listCButton.addActionListener(e -> listarClientes());
        listPButton.addActionListener(e -> listarProdutos());
        editButton.addActionListener(e -> editarClienteOuProdutos());
        deleteButton.addActionListener(e -> exibirOpcoesDeletarCliente());
        deletePButton.addActionListener(e -> deletarProduto());

        // Define o JFrame para iniciar em tela cheia
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        setVisible(true);
    }

    private void setJanelaIcone(String caminhoImagem) {
        try {
            // Carrega a imagem de ícone
            Image icon = ImageIO.read(new File(caminhoImagem));
            // Define o ícone da janela
            setIconImage(icon);
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem do ícone: " + e.getMessage());
        }
    }

    private void exibirOpcoesAdicionarCliente() {
        // Cria um novo JFrame para exibir as opções de adicionar cliente
        JFrame adicionarFrame = new JFrame("Adicionar Cliente");
        adicionarFrame.setSize(400, 200);
        adicionarFrame.setLayout(new GridLayout(2, 1, 10, 10));

        JButton manualButton = new JButton("Adicionar Manualmente");
        JButton planilhaButton = new JButton("Adicionar pela Planilha");

        manualButton.addActionListener(e -> {
            adicionarClienteManualmente();
            adicionarFrame.dispose();
        });

        planilhaButton.addActionListener(e -> {
            adicionarClientePorPlanilha();
            adicionarFrame.dispose();
        });

        adicionarFrame.add(manualButton);
        adicionarFrame.add(planilhaButton);

        adicionarFrame.setLocationRelativeTo(null); // Centraliza o JFrame
        adicionarFrame.setVisible(true);
    }

    private void adicionarClienteManualmente() {
        try {
            int clienteId = clienteService.obterId();
            if (clienteId <= 0) {
                throw new SQLException("ID negativo no banco, erro crítico");
            }

            // Caixa de diálogo para adicionar cliente
            JTextField nomeClienteField = new JTextField(15);
            JPanel panelCliente = new JPanel();
            panelCliente.add(new JLabel("Nome do Cliente:"));
            panelCliente.add(nomeClienteField);

            int result = JOptionPane.showConfirmDialog(this, panelCliente, "Adicionar Cliente", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String clienteNome = nomeClienteField.getText();
            Cliente cliente = new Cliente(clienteId, clienteNome);
            String clienteTaskId = UUID.randomUUID().toString();
            taskExecutorManager.submitTask(clienteTaskId, new AdicionarClienteThread(clienteService, cliente), 5000);
            monitorarStatusTarefa(clienteTaskId);

            boolean adicionarMaisProdutos = true;
            while (adicionarMaisProdutos) {
                int produtoId = produtoService.obterId();
                if (produtoId <= 0) {
                    throw new SQLException("ID negativo no banco, erro crítico");
                }

                // Caixa de diálogo para adicionar produto
                JTextField nomeProdutoField = new JTextField(15);
                JTextField precoProdutoField = new JTextField(5);
                JPanel panelProduto = new JPanel(new GridLayout(2, 2));
                panelProduto.add(new JLabel("Nome do Produto:"));
                panelProduto.add(nomeProdutoField);
                panelProduto.add(new JLabel("Preço do Produto:"));
                panelProduto.add(precoProdutoField);

                result = JOptionPane.showConfirmDialog(this, panelProduto, "Adicionar Produto", JOptionPane.OK_CANCEL_OPTION);
                if (result != JOptionPane.OK_OPTION) {
                    return;
                }

                String produtoNome = nomeProdutoField.getText();
                double produtoPreco;
                try {
                    produtoPreco = Double.parseDouble(precoProdutoField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Preço inválido, tente novamente.");
                    continue;
                }

                Produto produto = new Produto(produtoId, produtoNome, produtoPreco, clienteId);
                String produtoTaskId = UUID.randomUUID().toString();
                taskExecutorManager.submitTask(produtoTaskId, new AdicionarProdutoThread(produtoService, produto), 5000);
                monitorarStatusTarefa(produtoTaskId);

                int resposta = JOptionPane.showConfirmDialog(this, "Deseja adicionar mais produtos?", "Adicionar mais produtos", JOptionPane.YES_NO_OPTION);
                adicionarMaisProdutos = (resposta == JOptionPane.YES_OPTION);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Erro de integridade de dados: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLTimeoutException e) {
            JOptionPane.showMessageDialog(this, "Erro: Ocorreu um timeout ao tentar se conectar ao banco de dados. Tente novamente mais tarde.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLNonTransientConnectionException e) {
            JOptionPane.showMessageDialog(this, "Erro: Conexão ao banco de dados perdida. Verifique a conexão e tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar cliente ou produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarClientePorPlanilha() {
        String sheetUrl = "https://docs.google.com/spreadsheets/d/1XetNRGrViU-kiVqVY1aFmWPEps4hcTDBKaaOi4P_afI"; // URL da planilha
        LoadingDialog loadingDialog = new LoadingDialog(this); // Janela de carregamento
    
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Produto> produtos = processarPlanilhaDoGoogleSheets(sheetUrl);
    
                int totalProdutos = produtos.size();
                for (int i = 0; i < totalProdutos; i++) {
                    Produto produto = produtos.get(i);
    
                    int clienteId = clienteService.obterId();
                    Cliente cliente = new Cliente(clienteId, produto.getNomeCliente());
                    String clienteTaskId = UUID.randomUUID().toString();
                    taskExecutorManager.submitTask(clienteTaskId, new AdicionarClienteThread(clienteService, cliente), 5000);
                    monitorarStatusTarefa(clienteTaskId);
    
                    int produtoId = produtoService.obterId();
                    Produto produtoToAdd = new Produto(produtoId, produto.getNomeProduto(), produto.getPreco(), clienteId);
                    String produtoTaskId = UUID.randomUUID().toString();
                    taskExecutorManager.submitTask(produtoTaskId, new AdicionarProdutoThread(produtoService, produtoToAdd), 5000);
                    monitorarStatusTarefa(produtoTaskId);
    
                    publish((i + 1) * 100 / totalProdutos); // Atualiza o progresso
                }
    
                return null;
            }
    
            @Override
            protected void process(List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1);
                loadingDialog.setProgress(progress);
            }

            @Override
            protected void done() {
                try {
                    get(); // Verifica se houve exceções
                    loadingDialog.dispose();
                    JOptionPane.showMessageDialog(null, "Clientes e produtos adicionados com sucesso!");
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao adicionar clientes e produtos: " + e.getMessage());
                }
            }
        };
    
        worker.execute();
        loadingDialog.setVisible(true);
    }

private List<Produto> processarPlanilhaDoGoogleSheets(String sheetUrl) {
    List<Produto> produtos = new ArrayList<>();
    String apiKey = "#"; // Insira sua api do google sheets aqui 
    String sheetId = extrairIdDaURL(sheetUrl);
    String range = "Página1!A1:C100"; 
    
    String url = String.format("https://sheets.googleapis.com/v4/spreadsheets/%s/values/%s?key=%s", sheetId, range, apiKey);
    
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
        HttpGet request = new HttpGet(url);
        String response = EntityUtils.toString(httpClient.execute(request).getEntity());
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
    
        JsonArray values = jsonResponse.getAsJsonArray("values");
        if (values == null) {
            System.out.println("Nenhum dado encontrado na planilha.");
            return produtos; // Retorna lista vazia
        }
    
        for (int i = 1; i < values.size(); i++) { // Começa em 1 para ignorar o cabeçalho
            JsonArray row = values.get(i).getAsJsonArray();
            String nomeCliente = row.get(0).getAsString();
            String nomeProduto = row.get(1).getAsString();
            double precoProduto = row.get(2).getAsDouble();
    
            Produto produto = new Produto(0, nomeProduto, precoProduto, 0); // ID e clienteID temporários
            produto.setNomeCliente(nomeCliente); // Atribui o nome do cliente ao produto
            produtos.add(produto);
        }
        System.out.println("Dados extraídos da planilha com sucesso!");
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return produtos;
}
    
    private String extrairIdDaURL(String sheetUrl) {
        String[] partes = sheetUrl.split("/");
        for (int i = 0; i < partes.length; i++) {
            if (partes[i].equals("d")) {
                return partes[i + 1];
            }
        }
        throw new IllegalArgumentException("URL inválida do Google Sheets.");
    }

    private class LoadingDialog extends JDialog {
        private JProgressBar progressBar;
    
        public LoadingDialog(Frame owner) {
            super(owner, "Carregando", true);
            setLayout(new BorderLayout());
            setSize(300, 100);
            setLocationRelativeTo(owner);
    
            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            add(progressBar, BorderLayout.CENTER);
        }
    
        public void setProgress(int progress) {
            progressBar.setValue(progress);
        }
    }

    private void listarClientes() {
        // Cria um JDialog modal para a listagem dos clientes
        JDialog listagemDialog = new JDialog(this, "Listagem de Clientes", true);
        listagemDialog.setSize(500, 400);
        listagemDialog.setLayout(new BorderLayout());
    
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        listagemDialog.add(new JScrollPane(textArea), BorderLayout.CENTER);
    
        try {
            List<Cliente> todosClientes = clienteService.listar();
            if (todosClientes.isEmpty()) {
                textArea.append("Nenhum cliente encontrado.\n");
            } else {
                textArea.append("Clientes disponíveis:\n");
                textArea.append("+-------------------+\n\n");
                for (Cliente cliente : todosClientes) {
                    textArea.append("Nome do Cliente = " + cliente.getNome().toUpperCase() + " | ID do Cliente = " + cliente.getId() + "\n");
    
                    // Listar produtos associados a este cliente
                    List<Produto> produtos = produtoService.listarPorCliente(cliente.getId());
                    if (produtos.isEmpty()) {
                        textArea.append("Nenhum produto associado a este cliente.\n");
                    } else {
                        textArea.append("Produtos de " + cliente.getNome().toUpperCase() + ":\n");
                        for (Produto produto : produtos) {
                            textArea.append("* ID do produto = " + produto.getId() + " | " + produto.getNomeProduto() + " | Preço = " + produto.getPreco() + "\n");
                        }
                    }
                    textArea.append("\n");
                }
            }
        } catch (SQLException e) {
            textArea.append("Erro ao listar clientes: " + e.getMessage() + "\n");
        }
        
        // Exibe o JDialog de forma modal
        listagemDialog.setVisible(true);
    }
    

    private void listarProdutos() {
        // Cria um JDialog modal para a listagem dos produtos
        JDialog listagemDialog = new JDialog(this, "Listagem de Produtos", true);
        listagemDialog.setSize(500, 400);
        listagemDialog.setLayout(new BorderLayout());
    
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        listagemDialog.add(new JScrollPane(textArea), BorderLayout.CENTER);
    
        try {
            List<Produto> todosProdutos = produtoService.listar();
            if (todosProdutos.isEmpty()) {
                textArea.append("Nenhum produto encontrado.");
            } else {
                textArea.append("Produtos disponíveis: \n");
                textArea.append("+--------------------+\n\n");
                for (Produto produto : todosProdutos) {
                    textArea.append("ID do produto = " + produto.getId() + " | " +  "Cliente ID Relacionado = " + produto.getClienteId() + "\n");
                    textArea.append("Nome do Produto = " + produto.getNomeProduto() + " | " + "Preço do produto = " + "R$" + produto.getPreco() + "\n");
                    textArea.append("\n");
                }
            }
        } catch (SQLException e) {
            textArea.append("Erro ao listar produtos: " + e.getMessage());
        }
    
        // Exibe o JDialog de forma modal
        listagemDialog.setVisible(true);
    }
    

    private void editarClienteOuProdutos() {
        if (!verificarClientesEProdutosExistentes()) {
            JOptionPane.showConfirmDialog(this, "Nenhum cliente ou produto encontrado", "Não existem clientes ou produtos registrados", JOptionPane.OK_CANCEL_OPTION);
            return; 
        }
        // Cria uma nova janela para a escolha de edição
        JFrame escolhaFrame = new JFrame("Escolha a Edição");
        escolhaFrame.setSize(300, 150);
        escolhaFrame.setLayout(new FlowLayout());
        
        // Botão para editar cliente
        JButton editarClienteButton = new JButton("Editar Cliente");
        editarClienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!verificarClientesExistentes()) {
                    JOptionPane.showConfirmDialog(null, "Nenhum cliente encontrado", "Não existem clientes registrados", JOptionPane.OK_CANCEL_OPTION);
                    return; 
                }
                editarCliente();
                escolhaFrame.dispose(); // Fecha a janela de escolha após clicar
            }
        });
    
        // Botão para editar produtos
        JButton editarProdutoButton = new JButton("Editar Produtos");
        editarProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!verificarProdutosExistentes()) {
                    JOptionPane.showConfirmDialog(null, "Nenhum produto encontrado", "Não existem produtos registrados", JOptionPane.OK_CANCEL_OPTION);
                    return; 
                }
                editarProduto();
                escolhaFrame.dispose(); // Fecha a janela de escolha após clicar
            }
        });
    
        // Adiciona os botões à janela de escolha
        escolhaFrame.add(editarClienteButton);
        escolhaFrame.add(editarProdutoButton);
        escolhaFrame.setVisible(true);
    }
    
    private void editarCliente() {
        try {
            listarClientes(); // Lista os clientes antes de solicitar o ID para edição
            String clienteIdStr = JOptionPane.showInputDialog(this, "Digite o ID do cliente que deseja editar:");
            if (clienteIdStr == null || clienteIdStr.isEmpty()) {
                return;
            }
    
            int clienteId = Integer.parseInt(clienteIdStr);
            Cliente cliente = clienteService.buscar(clienteId);
            if (cliente == null) {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            String novoNome = JOptionPane.showInputDialog(this, "Digite o novo nome do cliente:", cliente.getNome());
            if (novoNome == null || novoNome.isEmpty()) {
                return;
            }
    
            String clienteTaskId = UUID.randomUUID().toString();
            taskExecutorManager.submitTask(clienteTaskId, new EditarClienteThread(clienteId, novoNome, clienteService), 5000);
            monitorarStatusTarefa(clienteTaskId);
    
            int resposta = JOptionPane.showConfirmDialog(this, "Deseja adicionar mais produtos ao cliente?", "Adicionar Produtos", JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                adicionarProdutosAClienteExistente(clienteId);
            }
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido, por favor, tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    private void editarProduto() {
        try {
            listarProdutos();
            String produtoIdStr = JOptionPane.showInputDialog(this, "Digite o ID do produto que deseja editar:");
            if (produtoIdStr == null || produtoIdStr.isEmpty()) {
                return;
            }
    
            int produtoId = Integer.parseInt(produtoIdStr);
            Produto produto = produtoService.buscar(produtoId);
            if (produto == null) {
                JOptionPane.showMessageDialog(this, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            String novoNomeProduto = JOptionPane.showInputDialog(this, "Digite o novo nome do produto:", produto.getNomeProduto());
            if (novoNomeProduto == null || novoNomeProduto.isEmpty()) {
                return;
            }
    
            String novoPrecoStr = JOptionPane.showInputDialog(this, "Digite o novo preço do produto:", produto.getPreco());
            double novoPreco = Double.parseDouble(novoPrecoStr);
    
            String produtoTaskId = UUID.randomUUID().toString();
            taskExecutorManager.submitTask(produtoTaskId, new EditarProdutoThread(produtoId, novoNomeProduto, novoPreco, produtoService), 5000);
            monitorarStatusTarefa(produtoTaskId);
    
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID ou preço inválido, por favor, tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void adicionarProdutosAClienteExistente(int clienteId) {
        try {
            boolean adicionarMaisProdutos = true;
            while (adicionarMaisProdutos) {
                int produtoId = produtoService.obterId();
                if (produtoId <= 0) {
                    throw new SQLException("ID negativo no banco, erro crítico");
                }

                JTextField nomeProdutoField = new JTextField(15);
                JTextField precoProdutoField = new JTextField(5);
                JPanel panelProduto = new JPanel(new GridLayout(2, 2));
                panelProduto.add(new JLabel("Nome do Produto:"));
                panelProduto.add(nomeProdutoField);
                panelProduto.add(new JLabel("Preço do Produto:"));
                panelProduto.add(precoProdutoField);

                int result = JOptionPane.showConfirmDialog(this, panelProduto, "Adicionar Produto", JOptionPane.OK_CANCEL_OPTION);
                if (result != JOptionPane.OK_OPTION) {
                    return;
                }

                String produtoNome = nomeProdutoField.getText();
                double produtoPreco = Double.parseDouble(precoProdutoField.getText());

                Produto produto = new Produto(produtoId, produtoNome, produtoPreco, clienteId);
                String produtoTaskId = UUID.randomUUID().toString();
                taskExecutorManager.submitTask(produtoTaskId, new AdicionarProdutoThread(produtoService, produto), 5000);
                monitorarStatusTarefa(produtoTaskId);

                int resposta = JOptionPane.showConfirmDialog(this, "Deseja adicionar mais produtos?", "Adicionar mais produtos", JOptionPane.YES_NO_OPTION);
                adicionarMaisProdutos = (resposta == JOptionPane.YES_OPTION);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exibirOpcoesDeletarCliente() {
        // Cria uma nova janela para escolher a opção de exclusão
        if (!verificarClientesExistentes()) {
            JOptionPane.showConfirmDialog(this, "Nenhum cliente encontrado", "Não existem clientes registrados", JOptionPane.OK_CANCEL_OPTION);
            return; 
        }
        JFrame escolhaFrame = new JFrame("Escolha a Exclusão");
        escolhaFrame.setSize(300, 150);
        escolhaFrame.setLayout(new FlowLayout());

        // Botão para deletar cliente por ID
        JButton excluirPorIdButton = new JButton("Excluir por ID");
        excluirPorIdButton.addActionListener(e -> {
            deletarCliente();
            escolhaFrame.dispose();
        });

        // Botão para deletar todos os clientes
        JButton excluirTodosButton = new JButton("Excluir Todos");
        excluirTodosButton.addActionListener(e -> {
            deletarTodosClientes();
            escolhaFrame.dispose();
        });

        // Adiciona os botões à janela de escolha
        escolhaFrame.add(excluirPorIdButton);
        escolhaFrame.add(excluirTodosButton);
        escolhaFrame.setVisible(true);
    }

    private void deletarCliente() {
        try {
            listarClientes();  // Exibe a lista de clientes para o usuário escolher
            String clienteIdStr = JOptionPane.showInputDialog(this, "Digite o ID do cliente que deseja deletar:");
            if (clienteIdStr == null || clienteIdStr.isEmpty()) {
                return;
            }

            int clienteId = Integer.parseInt(clienteIdStr);
            Cliente cliente = clienteService.buscar(clienteId);
            if (cliente == null) {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar o cliente " + cliente.getNome() + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (confirmacao != JOptionPane.YES_OPTION) {
                return;
            }

            // Executa a thread de exclusão do cliente
            String clienteTaskId = UUID.randomUUID().toString();
            taskExecutorManager.submitTask(clienteTaskId, new DeletarClienteThread(clienteId, clienteService), 5000);
            monitorarStatusTarefa(clienteTaskId);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido, por favor, tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarTodosClientes() {
        try {
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar todos os clientes?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (confirmacao != JOptionPane.YES_OPTION) {
                return;
            }

            // Executa a thread de exclusão de todos os clientes
            String clienteTaskId = UUID.randomUUID().toString();
            taskExecutorManager.submitTask(clienteTaskId, new DeletarTodosClientesThread(clienteService), 5000);
            monitorarStatusTarefa(clienteTaskId);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir todos os clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarProduto() {
        if (!verificarProdutosExistentes()) {
            JOptionPane.showConfirmDialog(this, "Nenhum produto encontrado", "Não existem produtos registrados", JOptionPane.OK_CANCEL_OPTION);
            return; 
        }
        try {
            listarProdutos();  // Exibe a lista de produtos para o usuário escolher
            String produtoIdStr = JOptionPane.showInputDialog(this, "Digite o ID do produto que deseja deletar:");
            if (produtoIdStr == null || produtoIdStr.isEmpty()) {
                return;
            }

            int produtoId = Integer.parseInt(produtoIdStr);
            Produto produto = produtoService.buscar(produtoId);
            if (produto == null) {
                JOptionPane.showMessageDialog(this, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar o produto " + produto.getNomeProduto() + "?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
            if (confirmacao != JOptionPane.YES_OPTION) {
                return;
            }

            // Executa a thread de exclusão do produto
            String produtoTaskId = UUID.randomUUID().toString();
            taskExecutorManager.submitTask(produtoTaskId, new DeletarProdutoThread(produtoId, produtoService), 5000);
            monitorarStatusTarefa(produtoTaskId);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido, por favor, tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void monitorarStatusTarefa(String taskId) {
        TaskStatus status = taskExecutorManager.getTaskStatus(taskId);
        while (status == TaskStatus.PENDING || status == TaskStatus.RUNNING) {
            System.out.println("Aguardando conclusão da tarefa " + taskId + "...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            status = taskExecutorManager.getTaskStatus(taskId);
        }
        System.out.println("Status da tarefa " + taskId + ": " + status);
    }

    // Método para verificar se há clientes
    private boolean verificarClientesExistentes() {
        try {
            List<Cliente> clientes = clienteService.obterTodos();
            return !clientes.isEmpty();
        } catch (SQLException e) {
            // Exibe uma mensagem de erro e retorna falso
            JOptionPane.showMessageDialog(null, "Erro ao verificar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Método para verificar se há produtos
    private boolean verificarProdutosExistentes() {
        try {
            List<Produto> produtos = produtoService.obterTodos();
            return !produtos.isEmpty();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao verificar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean verificarClientesEProdutosExistentes() {
        try {
            List<Cliente> clientes = clienteService.obterTodos();
            List<Produto> produtos = produtoService.obterTodos();
            return !produtos.isEmpty() || !clientes.isEmpty();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao verificar clientes e produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                AppConfig appConfig = new AppConfig();
                new App(appConfig.clienteService(), appConfig.produtoService(), appConfig.getTaskExecutorManager());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao inicializar a aplicação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
