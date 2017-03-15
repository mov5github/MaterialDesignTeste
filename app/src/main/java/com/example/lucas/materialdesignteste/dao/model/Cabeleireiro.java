package com.example.lucas.materialdesignteste.dao.model;

/**
 * Created by Lucas on 05/03/2017.
 */

public class Cabeleireiro {
    private Integer _id;
    private String nome;
    private Integer foto;
    private String codigoUnico;

    public Cabeleireiro() {
    }

    public Cabeleireiro(Integer _id, String nome, Integer foto, String codigoUnico) {
        this._id = _id;
        this.nome = nome;
        this.foto = foto;
        this.codigoUnico = codigoUnico;
    }

    //GETTERS AND SETTERS
    public Integer getFoto() {
        return foto;
    }
    public void setFoto(Integer foto) {
        this.foto = foto;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }
    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer get_id() {
        return _id;
    }
    public void set_id(Integer _id) {
        this._id = _id;
    }
}
