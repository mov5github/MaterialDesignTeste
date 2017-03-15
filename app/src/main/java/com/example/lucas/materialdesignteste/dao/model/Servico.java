package com.example.lucas.materialdesignteste.dao.model;

/**
 * Created by Lucas on 05/03/2017.
 */

public class Servico {
    private Integer _id;
    private String nome;
    private Integer icone;
    private Integer duracao;
    private Float preco;
    private String descricao;

    public Servico() {
    }

    public Servico(Integer _id, String nome, Integer icone, Integer duracao, Float preco, String descricao) {
        this._id = _id;
        this.nome = nome;
        this.icone = icone;
        this.duracao = duracao;
        this.preco = preco;
        this.descricao = descricao;
    }

    //GETTERS AND SETTERS
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Float getPreco() {
        return preco;
    }

    public void setPreco(Float preco) {
        this.preco = preco;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public Integer getIcone() {
        return icone;
    }

    public void setIcone(Integer icone) {
        this.icone = icone;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }
}
