package model;

public class Produto {
    private int id;
    private String nome;
    private double preco;
    private int clienteId; // ID do cliente associado

    public Produto(int id, String nome, double preco, int clienteId) {
        this.id = id;
        this.nome = nome;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
}
