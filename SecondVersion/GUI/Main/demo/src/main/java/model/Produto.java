package model;

public class Produto {
    private int id;
    private String nomeProduto;
    private double preco;
    private int clienteId; // ID do cliente associado
    private String nomeCliente; 

    public Produto(int id, String nomeProduto, double preco, int clienteId) {
        this.id = id;
        this.nomeProduto = nomeProduto;
        this.preco = preco;
        this.clienteId = clienteId;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }
}
