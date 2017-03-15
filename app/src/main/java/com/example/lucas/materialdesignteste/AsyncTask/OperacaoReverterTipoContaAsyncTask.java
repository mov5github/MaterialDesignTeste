package com.example.lucas.materialdesignteste.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.materialdesignteste.asyncTask.Interface.AcessoFirebase;
import com.example.lucas.materialdesignteste.activitys.ConfiguracaoInicialActivity;
import com.example.lucas.materialdesignteste.domain.User;
import com.example.lucas.materialdesignteste.domain.util.LibraryClass;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Lucas on 15/02/2017.
 */

public class OperacaoReverterTipoContaAsyncTask extends AsyncTask<Void,Void,Void> implements AcessoFirebase {
    private Activity mActivity;
    private User user;
    private boolean stop;
    private boolean removerTipoUsuario;
    private boolean removerEtapasConfig;
    private boolean removerFuncionamentoSalao;
    private boolean removerServicosSalao;
    private boolean removerCabeleireirosSalao;
    private ArrayList<String> etapasFinalizadas;

    private String TIPO_USUARIO;
    private String FUNCIONAMENTO_SALAO;
    private String SERVICOS_SALAO;
    private String CABELEIREIROS_SALAO;


    //FIREBASE
    private DatabaseReference firebaseSaveTipoUsuario;
    private DatabaseReference.CompletionListener completionListenerTipoUsuario;
    private DatabaseReference firebaseSaveEtapaFuncionamentoSalao;
    private DatabaseReference firebaseSaveEtapaServicosSalao;
    private DatabaseReference firebaseSaveEtapaCabeleireirosSalao;
    private DatabaseReference.CompletionListener completionListenerFuncionamentoSalao;
    private DatabaseReference.CompletionListener completionListenerServicosSalao;
    private DatabaseReference.CompletionListener completionListenerCabeleireirosSalao;


    public OperacaoReverterTipoContaAsyncTask(Activity activity,User user) {
        this.mActivity = activity;
        this.user = user;
        this.etapasFinalizadas = new ArrayList<String>();
        this.TIPO_USUARIO = "TIPO_USUARIO";
        this.FUNCIONAMENTO_SALAO = "FUNCIONAMENTO_SALAO";
        this.SERVICOS_SALAO = "SERVICOS_SALAO";
        this.CABELEIREIROS_SALAO = "CABELEIREIROS_SALAO";
    }

    @Override
    protected Void doInBackground(Void... params) {
        initFirebaseEvents();
        initControles();

        do {
            if (isCancelled()){this.stop = true;return null;}

            if (this.removerTipoUsuario){
                this.removerTipoUsuario = false;
                removerBDTipoUsuario(this.completionListenerTipoUsuario);
            }

            if (this.removerEtapasConfig){
                if (this.removerFuncionamentoSalao){
                    this.removerFuncionamentoSalao = false;
                    removerDBFuncionamentoSalao(this.completionListenerFuncionamentoSalao);
                }
                if (this.removerServicosSalao){
                    this.removerServicosSalao = false;
                    removerDBServicosSalao(this.completionListenerServicosSalao);
                }
                if (this.removerCabeleireirosSalao){
                    this.removerCabeleireirosSalao = false;
                    removerDBCabeleireirosSalao(this.completionListenerCabeleireirosSalao);
                }
            }

            if (!stop){
                int tempoAguardeMili = 250;
                Log.i("script","OperacaoReverterTipoContaAsyncTask aguardando remoçao dos dados");
                synchronized (this){
                    try {
                        wait(tempoAguardeMili);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            verificarEtapasCompletas();
        }while (!stop);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        removerFirebaseEvents();
        if (!isCancelled()){
            if (mActivity instanceof ConfiguracaoInicialActivity){
                if (ConfiguracaoInicialActivity.isConfiguracaoInicialActivityAtiva()){
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("reverterTipoContaOK");
                }
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        removerFirebaseEvents();
        if (stop){
            if (mActivity instanceof ConfiguracaoInicialActivity){
                if (ConfiguracaoInicialActivity.isConfiguracaoInicialActivityAtiva()){
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("reverterTipoContaOK");
                }
            }
        }else {
            if (mActivity instanceof ConfiguracaoInicialActivity){
                if (ConfiguracaoInicialActivity.isConfiguracaoInicialActivityAtiva()){
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("reverterTipoContaError");
                }
            }
        }
    }


    //INTERFACE
    @Override
    public void initFirebaseEvents() {
        this.completionListenerTipoUsuario = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoReverterTipoContaAsyncTask nao foi possivel remover tipoUsuario");
                    removerTipoUsuario = true;
                }else{
                    Log.i("script","OperacaoReverterTipoContaAsyncTask tipoUsuario removido");
                    etapasFinalizadas.add(TIPO_USUARIO);
                }
            }
        };
        //atualiza no banco etapa funcionamento salao
        this.completionListenerFuncionamentoSalao = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nao foi possivel remover funcionamento salao");
                    removerFuncionamentoSalao = true;
                }else{
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask funcioanmento salao removido");
                    etapasFinalizadas.add(FUNCIONAMENTO_SALAO);
                }
            }
        };
        //atualiza no banco etapa servicos salao
        this.completionListenerServicosSalao = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nao foi possivel salvar servicos salao");
                    removerServicosSalao = true;
                }else{
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask servicos salao salvo");
                    etapasFinalizadas.add(SERVICOS_SALAO);
                }
            }
        };
        //atualiza no banco etapa cabeleireiros salao
        this.completionListenerCabeleireirosSalao = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nao foi possivel salvar cabeleireiros salao");
                    removerCabeleireirosSalao = true;
                }else{
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask cabeleireiros salao salvo");
                    etapasFinalizadas.add(CABELEIREIROS_SALAO);
                }
            }
        };
    }

    @Override
    public void removerFirebaseEvents() {
        if (this.firebaseSaveTipoUsuario != null ){
            Log.i("script","removerFirebaseEvents() remover listener firebaseSaveTipoUsuario");
            this.firebaseSaveTipoUsuario = null;
        }
        if (this.completionListenerTipoUsuario != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerTipoUsuario");
            this.completionListenerTipoUsuario = null;
        }
        if (firebaseSaveEtapaFuncionamentoSalao != null){
            Log.i("script","removerFirebaseEvents() remover listener firebaseSaveEtapaFuncionamentoSalao");
            firebaseSaveEtapaFuncionamentoSalao = null;
        }
        if (completionListenerFuncionamentoSalao != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerFuncionamentoSalao");
            completionListenerFuncionamentoSalao = null;
        }
        if (firebaseSaveEtapaServicosSalao != null){
            Log.i("script","removerFirebaseEvents() remover listener firebaseSaveEtapaServicosSalao");
            firebaseSaveEtapaServicosSalao = null;
        }
        if (completionListenerServicosSalao != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerServicosSalao");
            completionListenerServicosSalao = null;
        }
        if (firebaseSaveEtapaCabeleireirosSalao != null){
            Log.i("script","removerFirebaseEvents() remover listener firebaseSaveEtapaCabeleireirosSalao");
            firebaseSaveEtapaCabeleireirosSalao = null;
        }
        if (completionListenerCabeleireirosSalao != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerCabeleireirosSalao");
            completionListenerCabeleireirosSalao = null;
        }
    }

    @Override
    public void initControles() {
        this.stop = false;
        this.removerTipoUsuario = true;
        this.removerEtapasConfig = true;
        this.removerFuncionamentoSalao = true;
        this.removerServicosSalao = true;
        this.removerCabeleireirosSalao = true;
    }

    private void verificarEtapasCompletas(){
        if (this.etapasFinalizadas.contains(this.TIPO_USUARIO) && this.etapasFinalizadas.contains(this.FUNCIONAMENTO_SALAO) && this.etapasFinalizadas.contains(this.SERVICOS_SALAO) && this.etapasFinalizadas.contains(this.CABELEIREIROS_SALAO)){
            this.stop = true;
        }
    }

    //ACESSO
    private void removerBDTipoUsuario(DatabaseReference.CompletionListener...completionListener){
        if (this.firebaseSaveTipoUsuario == null){
            this.firebaseSaveTipoUsuario = LibraryClass.getFirebase().child("users").child( user.getId() ).child("tipoUsuario");
        }

        if( completionListener.length == 0 ){
            this.firebaseSaveTipoUsuario.removeValue();
        }else{
            this.firebaseSaveTipoUsuario.removeValue(completionListener[0]);
        }
    }

    private void removerDBFuncionamentoSalao(DatabaseReference.CompletionListener... completionListener) {
        if (this.firebaseSaveEtapaFuncionamentoSalao == null){
            this.firebaseSaveEtapaFuncionamentoSalao = LibraryClass.getFirebase().child("users").child(user.getId()).child("configurações").child("etapas").child("funcionamentoOK");
        }

        if( completionListener.length == 0 ){
            this.firebaseSaveEtapaFuncionamentoSalao.removeValue();
        }else{
            this.firebaseSaveEtapaFuncionamentoSalao.removeValue(completionListener[0]);
        }
    }

    private void removerDBServicosSalao(DatabaseReference.CompletionListener... completionListener) {
        if (this.firebaseSaveEtapaServicosSalao == null){
            this.firebaseSaveEtapaServicosSalao = LibraryClass.getFirebase().child("users").child(user.getId()).child("configurações").child("etapas").child("servicosOK");
        }

        if( completionListener.length == 0 ){
            this.firebaseSaveEtapaServicosSalao.removeValue();
        }else{
            this.firebaseSaveEtapaServicosSalao.removeValue(completionListener[0]);
        }
    }

    private void removerDBCabeleireirosSalao(DatabaseReference.CompletionListener... completionListener) {
        if (this.firebaseSaveEtapaCabeleireirosSalao == null){
            this.firebaseSaveEtapaCabeleireirosSalao = LibraryClass.getFirebase().child("users").child(user.getId()).child("configurações").child("etapas").child("cabeleireirosOK");
        }

        if( completionListener.length == 0 ){
            this.firebaseSaveEtapaCabeleireirosSalao.removeValue();
        }else{
            this.firebaseSaveEtapaCabeleireirosSalao.removeValue(completionListener[0]);
        }
    }
}
