package com.example.lucas.materialdesignteste.asyncTask.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.materialdesignteste.asyncTask.OperacaoDefinirTipoContaAsyncTask;
import com.example.lucas.materialdesignteste.asyncTask.OperacaoGerarCodUnicoAsyncTask;
import com.example.lucas.materialdesignteste.asyncTask.OperacaoRedefinirNivelUsuarioAsyncTask;
import com.example.lucas.materialdesignteste.asyncTask.OperacaoReverterTipoContaAsyncTask;
import com.example.lucas.materialdesignteste.asyncTask.OperacaoSalvarFuncionamentoSalaoAsyncTask;
import com.example.lucas.materialdesignteste.domain.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Lucas on 14/02/2017.
 */

public class ControladorAcessoFirebase2 {
    static final String OPERACAO_BUSCAR_INFORMACOES_INICIAIS = "OPERACAO_BUSCAR_INFORMACOES_INICIAIS";
    static final String OPERACAO_DEFINIR_TIPO_CONTA = "OPERACAO_DEFINIR_TIPO_CONTA";
    static final String OPERACAO_REVERTER_TIPO_CONTA = "OPERACAO_REVERTER_TIPO_CONTA";
    static final String OPERACAO_GERAR_COD_UNICO = "OPERACAO_GERAR_COD_UNICO";
    static final String OPERACAO_REDEFINIR_NIVEL_USUARIO = "OPERACAO_REDEFINIR_NIVEL_USUARIO";
    static final String OPERACAO_SALVAR_FUNCIONAMENTO_SALAO = "OPERACAO_SALVAR_FUNCIONAMENTO_SALAO";


    private String REF = "com.example.lucas.materialdesignteste";
    private String operacao;
    private Boolean run;
    private Activity activity;
    private User user;
    private FirebaseAuth mAuth;


    private ArrayList<String> operacoesAtivas;
    private ArrayList<String> operacoesFinalizadasComErro;
    private ArrayList<String> operacoesFinalizadasComSucesso;

    //ASYNCTASK
    private AsyncTask<Void,Void,Void> operacaoDefinirTipoContaAsyncTask;
    private AsyncTask<Void,Void,Void> operacaoReverterTipoContaAsyncTask;
    private AsyncTask<Void,Void,Void> operacaoGerarCodUnicoAsyncTask;
    private AsyncTask<Void,Void,Void> operacaoRedefinirNivelUsuarioAsyncTask;
    private AsyncTask<Void,Void,Void> operacaoBuscarInformacoesiniciaisAsyncTask;
    private AsyncTask<Void,Void,Void> operacaoSalvarFuncionamentoSalaoAsyncTask;



    public ControladorAcessoFirebase2(Activity activity, User user, String operacao) {
        this.operacao = operacao;
        this.run = false;
        this.operacoesAtivas = new ArrayList<String>();
        this.activity = activity;
        this.user = user;
        this.REF = this.REF + this.user.getId();
    }



    public void runInicial(){
        if (!run){
            this.run = true;
            this.mAuth = FirebaseAuth.getInstance();
            boolean  logado = verifyLogged();

            if (logado){
                switch (operacao){
                    case OPERACAO_BUSCAR_INFORMACOES_INICIAIS:
                        buscarInformacoesIniciais(operacao);
                        break;
                    case OPERACAO_DEFINIR_TIPO_CONTA:
                        salvarTipoUsuario(operacao);
                        break;
                    case OPERACAO_GERAR_COD_UNICO:
                        gerarCodUnico(operacao);
                        break;
                    case OPERACAO_REDEFINIR_NIVEL_USUARIO:
                        salvarNivelUsuario(operacao);
                        break;
                    case OPERACAO_SALVAR_FUNCIONAMENTO_SALAO:
                        salvarFuncionamentoSalão();
                        break;
                    default:
                        Log.i("script","ControladorAcessoFirebase2 Run() operaçao invalida");
                        //TODO implementar erro operacao invalida
                        break;
                }
            }else {
                //TODO implementar erro usuario nao logado
            }
        }
    }

    public void stop(){
        if (operacoesAtivas.size() != 0){
            if (operacoesAtivas.contains(OPERACAO_BUSCAR_INFORMACOES_INICIAIS)){
                this.operacaoBuscarInformacoesiniciaisAsyncTask.cancel(true);
                this.operacoesAtivas.remove(OPERACAO_BUSCAR_INFORMACOES_INICIAIS);
            }
            if (operacoesAtivas.contains(OPERACAO_DEFINIR_TIPO_CONTA)){
                this.operacaoDefinirTipoContaAsyncTask.cancel(true);
                this.operacoesAtivas.remove(OPERACAO_DEFINIR_TIPO_CONTA);
                salvarTipoUsuario(OPERACAO_REVERTER_TIPO_CONTA);
            }
            if (operacoesAtivas.contains(OPERACAO_GERAR_COD_UNICO)){
                this.operacaoGerarCodUnicoAsyncTask.cancel(true);
                this.operacoesAtivas.remove(OPERACAO_GERAR_COD_UNICO);
                if (!getSPRefString(this.activity.getApplicationContext(),"codUnico").equals("")){
                    salvarNivelUsuario(OPERACAO_REDEFINIR_NIVEL_USUARIO);
                }
            }
            if (operacoesAtivas.contains(OPERACAO_SALVAR_FUNCIONAMENTO_SALAO)){
                this.operacaoSalvarFuncionamentoSalaoAsyncTask.cancel(true);
                this.operacoesAtivas.remove(OPERACAO_SALVAR_FUNCIONAMENTO_SALAO);
                //TODO
            }
        }else {
            //TODO encerrar normalmente
        }
    }

    public void iniciarOperacao(String operacao,User user){
        this.mAuth = FirebaseAuth.getInstance();
        boolean  logado = verifyLogged();
        if (logado){
            this.user = user;
            switch (operacao){
                case OPERACAO_BUSCAR_INFORMACOES_INICIAIS:
                    buscarInformacoesIniciais(operacao);
                    break;
                case OPERACAO_DEFINIR_TIPO_CONTA:
                    salvarTipoUsuario(operacao);
                    break;
                case OPERACAO_GERAR_COD_UNICO:
                    gerarCodUnico(operacao);
                    break;
                case OPERACAO_REDEFINIR_NIVEL_USUARIO:
                    salvarNivelUsuario(operacao);
                    break;
                case OPERACAO_SALVAR_FUNCIONAMENTO_SALAO:
                    salvarFuncionamentoSalão();
                    break;
                default:
                    break;
            }
        }else{
            //TODO implementar erro usuario nao logado
        }

    }


    private boolean verifyLogged(){
        Log.i("script","ControladorAcessoFirebase2 verifyLogged()");
        if( mAuth.getCurrentUser() != null ){
            Log.i("script","ControladorAcessoFirebase2 verifyLogged() mAuth.getCurrentUser() != null");
            if (mAuth.getCurrentUser().getUid() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                return  true;
            }else {
                Log.i("script","verifyLogged()  UID = null");
                return false;
            }
        }
        else{
            Log.i("script","ControladorAcessoFirebase2 verifyLogged() mAuth.getCurrentUser() == null");
            return  false;
        }
    }

    public void concluirOperacao(Boolean isSucess,String operacao){
        if (isSucess){
            if (this.operacoesFinalizadasComSucesso == null){
                this.operacoesFinalizadasComSucesso = new ArrayList<String>();
                this.operacoesFinalizadasComSucesso.add(operacao);
                this.operacoesAtivas.remove(operacao);
            }else {
                this.operacoesFinalizadasComSucesso.add(operacao);
                this.operacoesAtivas.remove(operacao);
            }
        }else {
            if (this.operacoesFinalizadasComErro == null){
                this.operacoesFinalizadasComErro = new ArrayList<String>();
                this.operacoesFinalizadasComErro.add(operacao);
                this.operacoesAtivas.remove(operacao);
            }else {
                this.operacoesFinalizadasComErro.add(operacao);
                this.operacoesAtivas.remove(operacao);
            }
            switch (operacao){
                case OPERACAO_DEFINIR_TIPO_CONTA:
                    salvarTipoUsuario(OPERACAO_REVERTER_TIPO_CONTA);
                    break;
                case OPERACAO_GERAR_COD_UNICO:
                    if (!getSPRefString(this.activity.getApplicationContext(),"codUnico").equals("")){
                        salvarNivelUsuario(OPERACAO_REDEFINIR_NIVEL_USUARIO);
                    }
                    break;
                case OPERACAO_SALVAR_FUNCIONAMENTO_SALAO:
                    //TODO
                    break;
                default:
                    break;
            }
        }
        this.operacoesAtivas.remove(operacao);
    }


    //METODOS DE ACESSO
    private void salvarTipoUsuario(String operacao) {
        switch (operacao){
            case OPERACAO_DEFINIR_TIPO_CONTA:
                if (this.operacaoDefinirTipoContaAsyncTask == null){
                    this.operacaoDefinirTipoContaAsyncTask = new OperacaoDefinirTipoContaAsyncTask(this.activity,this.user);
                    this.operacaoDefinirTipoContaAsyncTask.execute();
                    this.operacoesAtivas.add(operacao);
                }else {
                    this.operacaoDefinirTipoContaAsyncTask.execute();
                    this.operacoesAtivas.add(operacao);
                }
                break;
            case OPERACAO_REVERTER_TIPO_CONTA:
                if (this.operacaoReverterTipoContaAsyncTask == null){
                    this.operacaoReverterTipoContaAsyncTask = new OperacaoReverterTipoContaAsyncTask(this.activity,this.user);
                    this.operacaoReverterTipoContaAsyncTask.execute();
                    this.operacoesAtivas.add(operacao);
                }else {
                    this.operacaoReverterTipoContaAsyncTask.execute();
                    this.operacoesAtivas.add(operacao);
                }
                break;
            default:
                break;
        }
    }

    private void gerarCodUnico(String operacao){
        switch (this.user.getTipoUsuario()){
            case "salão":
                if (!this.operacoesAtivas.contains(OPERACAO_GERAR_COD_UNICO)){
                    this.operacoesAtivas.add(OPERACAO_GERAR_COD_UNICO);
                    if (this.operacaoGerarCodUnicoAsyncTask == null){
                        this.operacaoGerarCodUnicoAsyncTask = new OperacaoGerarCodUnicoAsyncTask(this.activity,this.user);
                        this.operacaoGerarCodUnicoAsyncTask.execute();
                        this.operacoesAtivas.add(operacao);
                    }else{
                        this.operacaoGerarCodUnicoAsyncTask.execute();
                        this.operacoesAtivas.add(operacao);
                    }
                }
                break;
            case "cabeleireiro":
                if (!this.operacoesAtivas.contains(OPERACAO_GERAR_COD_UNICO)){
                    this.operacoesAtivas.add(OPERACAO_GERAR_COD_UNICO);
                    if (this.operacaoGerarCodUnicoAsyncTask == null){
                        this.operacaoGerarCodUnicoAsyncTask = new OperacaoGerarCodUnicoAsyncTask(this.activity,this.user);
                        this.operacaoGerarCodUnicoAsyncTask.execute();
                        this.operacoesAtivas.add(operacao);
                    }else{
                        this.operacaoGerarCodUnicoAsyncTask.execute();
                        this.operacoesAtivas.add(operacao);
                    }
                }
                break;
            default:
                Log.i("script","ControladorAcessoFirebase2 gerarCodUnico tipoUsuario invalido");
                break;
        }
    }

    private void salvarNivelUsuario(String operacao) {
        switch (operacao){
            case OPERACAO_REDEFINIR_NIVEL_USUARIO:
                this.user.setNivelUsuario("2.1");
                if (this.operacaoRedefinirNivelUsuarioAsyncTask == null){
                    this.operacaoRedefinirNivelUsuarioAsyncTask = new OperacaoRedefinirNivelUsuarioAsyncTask(this.activity,this.user);
                    this.operacaoRedefinirNivelUsuarioAsyncTask.execute();
                    this.operacoesAtivas.add(operacao);
                }else {
                    this.operacaoRedefinirNivelUsuarioAsyncTask.execute();
                    this.operacoesAtivas.add(operacao);
                }
                break;
            default:
                break;
        }
    }

    private void buscarInformacoesIniciais(String operacao){
        if (this.operacaoBuscarInformacoesiniciaisAsyncTask == null){
            this.operacaoBuscarInformacoesiniciaisAsyncTask.execute();
            this.operacoesAtivas.add(operacao);
        }else{
            this.operacaoBuscarInformacoesiniciaisAsyncTask.execute();
            this.operacoesAtivas.add(operacao);
        }
    }

    private void salvarFuncionamentoSalão(){
        if (this.operacaoSalvarFuncionamentoSalaoAsyncTask == null){
            this.operacaoSalvarFuncionamentoSalaoAsyncTask = new OperacaoSalvarFuncionamentoSalaoAsyncTask(this.activity,this.user);
            this.operacaoSalvarFuncionamentoSalaoAsyncTask.execute();
            this.operacoesAtivas.add(OPERACAO_SALVAR_FUNCIONAMENTO_SALAO);
        }else{
            this.operacaoBuscarInformacoesiniciaisAsyncTask.execute();
            this.operacoesAtivas.add(OPERACAO_SALVAR_FUNCIONAMENTO_SALAO);
        }
    }




    private String getSPRefString(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        String value = sp.getString(key, "");
        return( value );
    }


    //GETTERS
    public static String getOperacaoDefinirTipoConta() {
        return OPERACAO_DEFINIR_TIPO_CONTA;
    }

    public static String getOperacaoReverterTipoConta() {
        return OPERACAO_REVERTER_TIPO_CONTA;
    }

    public static String getOperacaoGerarCodUnico() {
        return OPERACAO_GERAR_COD_UNICO;
    }

    public static String getOperacaoBuscarInformacoesIniciais() {
        return OPERACAO_BUSCAR_INFORMACOES_INICIAIS;
    }

    public static String getOperacaoRedefinirNivelUsuario() {
        return OPERACAO_REDEFINIR_NIVEL_USUARIO;
    }

    public static String getOperacaoSalvarFuncionamentoSalao() {
        return OPERACAO_SALVAR_FUNCIONAMENTO_SALAO;
    }


}
