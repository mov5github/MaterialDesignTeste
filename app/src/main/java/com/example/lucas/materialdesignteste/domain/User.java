package com.example.lucas.materialdesignteste.domain;

import android.content.Context;

import com.example.lucas.materialdesignteste.domain.util.LibraryClass;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Lucas on 03/01/2017.
 */

public class User {
    public static String PROVIDER = "com.example.lucas.materialdesignteste.domain.User.PROVIDER";

    private String inicialConfig;

    private String id;
    private String name;
    private String email;
    private String password;
    private String newPassword;
    private String tipoUsuario;
    private String telefone1;
    private String telefone2;
    //cliente / responsavel
    private String rg;
    private String dataNascimento;
    private String sexo;
    //salão
    private String nomeSalao;
    private String publico;
    private String estado;
    private String cidade;
    private String rua;
    private String numEndereco;
    private String complementoEndereco;


    public User(){}


    public void saveDB( DatabaseReference.CompletionListener... completionListener ){
        DatabaseReference firebase = LibraryClass.getFirebase().child("users").child( getId() );

        if( completionListener.length == 0 ){
            firebase.setValue(this);
        }
        else{
            firebase.setValue(this, completionListener[0]);
        }
    }



    public boolean isSocialNetworkLogged( Context context ){
        String token = getProviderSP( context );
        return( token.contains("facebook") || token.contains("google") || token.contains("twitter") || token.contains("github") );
    }

    public void setNameIfNull(String name) {
        if( this.name == null ){
            this.name = name;
        }
    }

    public void setEmailIfNull(String email) {
        if( this.email == null ){
            this.email = email;
        }

    }

    public void setTipoUsuarioIfNull(String tipoUsuario) {
        if( this.tipoUsuario == null ){
            this.tipoUsuario = tipoUsuario;
        }

    }

    public void saveProviderSP(Context context, String token ){
        LibraryClass.saveSP( context, PROVIDER, token );
    }
    public String getProviderSP(Context context ){
        return( LibraryClass.getSP( context, PROVIDER) );
    }


    //Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getRg() {
        return rg;
    }
    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefone1() {
        return telefone1;
    }
    public void setTelefone1(String telefone1) {
        this.telefone1 = telefone1;
    }

    public String getTelefone2() {
        return telefone2;
    }
    public void setTelefone2(String telefone2) {
        this.telefone2 = telefone2;
    }


    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComplementoEndereco() {
        return complementoEndereco;
    }
    public void setComplementoEndereco(String complementoEndereco) {
        this.complementoEndereco = complementoEndereco;
    }

    public String getNumEndereco() {
        return numEndereco;
    }
    public void setNumEndereco(String numEndereco) {
        this.numEndereco = numEndereco;
    }

    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getPublico() {
        return publico;
    }
    public void setPublico(String publico) {
        this.publico = publico;
    }

    public String getNomeSalao() {
        return nomeSalao;
    }
    public void setNomeSalao(String nomeSalao) {
        this.nomeSalao = nomeSalao;
    }

    public String getRua() {
        return rua;
    }
    public void setRua(String rua) {
        this.rua = rua;
    }










}