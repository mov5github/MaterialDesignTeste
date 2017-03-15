package com.example.lucas.materialdesignteste.geral;

import com.example.lucas.materialdesignteste.dao.FuncionamentoDAO;
import com.example.lucas.materialdesignteste.dao.model.Funcionamento;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 24/02/2017.
 */

public class FuncionamentoSalao {
    private String abreSegunda;
    private String fechaSegunda;
    private String abreTerca;
    private String fechaTerca;
    private String abreQuarta;
    private String fechaQuarta;
    private String abreQuinta;
    private String fechaQuinta;
    private String abreSexta;
    private String fechaSexta;
    private String abreSabado;
    private String fechaSabado;
    private String abreDomingo;
    private String fechaDomingo;

    public List<Funcionamento> listaDeFuncionamento(){
        List<Funcionamento> funcionamentos = new ArrayList<Funcionamento>();
        Funcionamento funcionamento;

        if ((this.abreSegunda != null && !this.abreSegunda.isEmpty()) && (this.fechaSegunda != null && !this.fechaSegunda.isEmpty())){
            funcionamento = new Funcionamento();
            funcionamento.setDia(FuncionamentoDAO.getSEGUNDA());
            funcionamento.setAbre(this.abreSegunda);
            funcionamento.setFecha(this.fechaSegunda);
            funcionamentos.add(funcionamento);
        }
        if ((this.abreTerca != null && !this.abreTerca.isEmpty()) && (this.fechaTerca != null && !this.fechaTerca.isEmpty())){
            funcionamento = new Funcionamento();
            funcionamento.setDia(FuncionamentoDAO.getTERCA());
            funcionamento.setAbre(this.abreTerca);
            funcionamento.setFecha(this.fechaTerca);
            funcionamentos.add(funcionamento);
        }
        if ((this.abreQuarta != null && !this.abreQuarta.isEmpty()) && (this.fechaQuarta != null && !this.fechaQuarta.isEmpty())){
            funcionamento = new Funcionamento();
            funcionamento.setDia(FuncionamentoDAO.getQUARTA());
            funcionamento.setAbre(this.abreQuarta);
            funcionamento.setFecha(this.fechaQuarta);
            funcionamentos.add(funcionamento);
        }
        if ((this.abreQuinta != null && !this.abreQuinta.isEmpty()) && (this.fechaQuinta != null && !this.fechaQuinta.isEmpty())){
            funcionamento = new Funcionamento();
            funcionamento.setDia(FuncionamentoDAO.getQUINTA());
            funcionamento.setAbre(this.abreQuinta);
            funcionamento.setFecha(this.fechaQuinta);
            funcionamentos.add(funcionamento);
        }
        if ((this.abreSexta != null && !this.abreSexta.isEmpty()) && (this.fechaSexta != null && !this.fechaSexta.isEmpty())){
            funcionamento = new Funcionamento();
            funcionamento.setDia(FuncionamentoDAO.getSEXTA());
            funcionamento.setAbre(this.abreSexta);
            funcionamento.setFecha(this.fechaSexta);
            funcionamentos.add(funcionamento);
        }
        if ((this.abreSabado != null && !this.abreSabado.isEmpty()) && (this.fechaSabado != null && !this.fechaSabado.isEmpty())){
            funcionamento = new Funcionamento();
            funcionamento.setDia(FuncionamentoDAO.getSABADO());
            funcionamento.setAbre(this.abreSabado);
            funcionamento.setFecha(this.fechaSabado);
            funcionamentos.add(funcionamento);
        }
        if ((this.abreDomingo != null && !this.abreDomingo.isEmpty()) && (this.fechaDomingo != null && !this.fechaDomingo.isEmpty())){
            funcionamento = new Funcionamento();
            funcionamento.setDia(FuncionamentoDAO.getDOMINGO());
            funcionamento.setAbre(this.abreDomingo);
            funcionamento.setFecha(this.fechaDomingo);
            funcionamentos.add(funcionamento);
        }

        return funcionamentos;
    }


    //GETERS AND SETTERS
    public String getFechaTerca() {
        return fechaTerca;
    }
    public void setFechaTerca(String fechaTerca) {
        this.fechaTerca = fechaTerca;
    }

    public String getAbreSegunda() {
        return abreSegunda;
    }
    public void setAbreSegunda(String abreSegunda) {
        this.abreSegunda = abreSegunda;
    }

    public String getFechaSegunda() {
        return fechaSegunda;
    }
    public void setFechaSegunda(String fechaSegunda) {
        this.fechaSegunda = fechaSegunda;
    }

    public String getAbreTerca() {
        return abreTerca;
    }
    public void setAbreTerca(String abreTerca) {
        this.abreTerca = abreTerca;
    }

    public String getAbreQuarta() {
        return abreQuarta;
    }
    public void setAbreQuarta(String abreQuarta) {
        this.abreQuarta = abreQuarta;
    }

    public String getFechaQuarta() {
        return fechaQuarta;
    }
    public void setFechaQuarta(String fechaQuarta) {
        this.fechaQuarta = fechaQuarta;
    }

    public String getAbreSexta() {
        return abreSexta;
    }
    public void setAbreSexta(String abreSexta) {
        this.abreSexta = abreSexta;
    }

    public String getAbreQuinta() {
        return abreQuinta;
    }
    public void setAbreQuinta(String abreQuinta) {
        this.abreQuinta = abreQuinta;
    }

    public String getFechaQuinta() {
        return fechaQuinta;
    }
    public void setFechaQuinta(String fechaQuinta) {
        this.fechaQuinta = fechaQuinta;
    }

    public String getFechaSexta() {
        return fechaSexta;
    }
    public void setFechaSexta(String fechaSexta) {
        this.fechaSexta = fechaSexta;
    }

    public String getFechaSabado() {
        return fechaSabado;
    }
    public void setFechaSabado(String fechaSabado) {
        this.fechaSabado = fechaSabado;
    }

    public String getAbreSabado() {
        return abreSabado;
    }
    public void setAbreSabado(String abreSabado) {
        this.abreSabado = abreSabado;
    }

    public String getAbreDomingo() {
        return abreDomingo;
    }
    public void setAbreDomingo(String abreDomingo) {
        this.abreDomingo = abreDomingo;
    }

    public String getFechaDomingo() {
        return fechaDomingo;
    }
    public void setFechaDomingo(String fechaDomingo) {
        this.fechaDomingo = fechaDomingo;
    }
}
