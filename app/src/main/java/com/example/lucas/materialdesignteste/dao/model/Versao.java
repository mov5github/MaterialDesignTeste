package com.example.lucas.materialdesignteste.dao.model;

/**
 * Created by Lucas on 03/03/2017.
 */

public class Versao {
    private Integer _id;
    private String identificacaoTabela;
    private Integer versao;
    private String dataModificacao;
    private String uid;

    public  Versao(){
    }

    public Versao(int _id, String identificacaoTabela, int versao, String dataModificacao, String uid) {
        this._id = _id;
        this.identificacaoTabela = identificacaoTabela;
        this.versao = versao;
        this.dataModificacao = dataModificacao;
        this.uid = uid;
    }

    //GETTERS AND ASETTERS
    public Integer get_id() {
        return _id;
    }
    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getIdentificacaoTabela() {
        return identificacaoTabela;
    }
    public void setIdentificacaoTabela(String identificacaoTabela) {
        this.identificacaoTabela = identificacaoTabela;
    }

    public Integer getVersao() {
        return versao;
    }
    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public String getDataModificacao() {
        return dataModificacao;
    }
    public void setDataModificacao(String dataModificacao) {
        this.dataModificacao = dataModificacao;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
}
