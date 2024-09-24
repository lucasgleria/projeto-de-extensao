// Cliente.java
package model;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    private int id;
    private String nomeCliente;
    private List<Produto> produtos;

    public Cliente(int id, String nomeCliente) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.produtos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nomeCliente;
    }

    public void setNome(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
    }

    public void removerProduto(Produto produto) {
        produtos.remove(produto);
    }
}
