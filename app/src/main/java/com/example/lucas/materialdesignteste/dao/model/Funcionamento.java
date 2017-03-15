package com.example.lucas.materialdesignteste.dao.model;

/**
 * Created by Lucas on 03/03/2017.
 */

public class Funcionamento {
    private Integer _id;
    private String dia;
    private String abre;
    private String fecha;

    public Funcionamento() {

    }

    public Funcionamento(int _id, String dia, String abre, String fecha) {
        this._id = _id;
        this.dia = dia;
        this.abre = abre;
        this.fecha = fecha;
    }

    //GETTERS AND SETTERS
    public String getAbre() {
        return abre;
    }
    public void setAbre(String abre) {
        this.abre = abre;
    }

    public String getDia() {
        return dia;
    }
    public void setDia(String dia) {
        this.dia = dia;
    }

    public Integer get_id() {
        return _id;
    }
    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
