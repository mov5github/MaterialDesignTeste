package com.example.lucas.materialdesignteste.asyncTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.materialdesignteste.asyncTask.Interface.AcessoFirebase;
import com.example.lucas.materialdesignteste.activitys.ConfiguracaoInicialActivity;
import com.example.lucas.materialdesignteste.domain.User;
import com.example.lucas.materialdesignteste.domain.util.LibraryClass;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Lucas on 15/02/2017.
 */

public class OperacaoDefinirTipoContaAsyncTask extends AsyncTask<Void,Void,Void> implements AcessoFirebase {
    private String REF = "com.example.lucas.materialdesignteste";
    private Activity mActivity;
    private User user;

    //CONTROLES
    private boolean stop;
    private boolean saveTipoUsuario;
    private boolean saveNivelUsuario;
    private boolean saveEtapasConfig;
    private boolean saveEtapaFuncionamento;
    private boolean saveEtapaServicos;
    private boolean saveEtapaCabeleireiros;
    private boolean dadosSalvosComSucesso;
    private int tempoDecorridoMili;

    //FIREBASE
    private DatabaseReference firebaseSaveTipoUsuario;
    private DatabaseReference firebaseSaveNivelUsuario;
    private DatabaseReference firebaseSaveEtapaFuncionamentoSalao;
    private DatabaseReference firebaseSaveEtapaServicosSalao;
    private DatabaseReference firebaseSaveEtapaCabeleireirosSalao;
    private DatabaseReference.CompletionListener completionListenerTipoUsuario;
    private DatabaseReference.CompletionListener completionListenerNivelUsuario;
    private DatabaseReference.CompletionListener completionListenerFuncionamentoSalao;
    private DatabaseReference.CompletionListener completionListenerServicosSalao;
    private DatabaseReference.CompletionListener completionListenerCabeleireirosSalao;




    public OperacaoDefinirTipoContaAsyncTask(Activity activity, User user) {
        this.mActivity = activity;
        this.user = user;
        if (this.user.getId() != null && !this.user.getId().isEmpty()){
            this.REF = REF + user.getId();
        }
        this.user.setNivelUsuario("2");
        this.user.setEtapaFuncionamentoOK(false);
        this.user.setEtapaServicosOK(false);
        this.user.setEtapaCabeleireirosOK(false);
    }

    @Override
    protected Void doInBackground(Void... params) {
        initFirebaseEvents();
        initControles();

        do {
            if (isCancelled()){this.stop = true;return null;}

            if (saveTipoUsuario){
                this.saveTipoUsuario = false;
                saveDBTipoUsuario(this.completionListenerTipoUsuario);
            }
            if (saveNivelUsuario){
                this.saveNivelUsuario = false;
                saveDBNivelUsuario(this.completionListenerNivelUsuario);
            }
            if (saveEtapasConfig){
                if (this.saveEtapaFuncionamento){
                    this.saveEtapaFuncionamento = false;
                    saveDBFuncionamentoSalao(completionListenerFuncionamentoSalao);
                }
                if (this.saveEtapaServicos){
                    this.saveEtapaServicos = false;
                    saveDBServicosSalao(completionListenerServicosSalao);
                }
                if (this.saveEtapaCabeleireiros){
                    this.saveEtapaCabeleireiros = false;
                    saveDBCabeleireirosSalao(completionListenerCabeleireirosSalao);
                }
            }

            if (!stop){
                int tempoAguardeMili = 250;
                Log.i("script","OperacaoDefinirTipoContaAsyncTask aguardando atualizacao dos dados");
                synchronized (this){
                    try {
                        wait(tempoAguardeMili);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.tempoDecorridoMili += tempoAguardeMili;
                if (this.tempoDecorridoMili >= 7000){
                    this.stop = true;
                }
            }
        }while (!stop);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        removerFirebaseEvents();
        if (!isCancelled()){
            if (dadosSalvosComSucesso){
                if (this.mActivity instanceof ConfiguracaoInicialActivity){
                    saveSPRefString(this.mActivity.getApplicationContext(),"tipoUsuario",this.user.getTipoUsuario());
                    saveSPRefString(this.mActivity.getApplicationContext(),"nivelUsuario",this.user.getNivelUsuario());
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("tipoContaOK");
                }
            }else{
                if (this.mActivity instanceof ConfiguracaoInicialActivity){
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("tipoContaError");
                }
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        removerFirebaseEvents();
        if (dadosSalvosComSucesso){
            if (this.mActivity instanceof ConfiguracaoInicialActivity){
                saveSPRefString(this.mActivity.getApplicationContext(),"tipoUsuario",this.user.getTipoUsuario());
                saveSPRefString(this.mActivity.getApplicationContext(),"nivelUsuario",this.user.getNivelUsuario());
                ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("tipoContaOK");
                //TODO
            }
        }else{
            if (this.mActivity instanceof ConfiguracaoInicialActivity){
                ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("tipoContaError");
            }
        }
    }


    //INTERFACE
    @Override
    public void initFirebaseEvents() {
        Log.i("script","OperacaoDefinirTipoContaAsyncTask() initFirebaseEvents()");
        //atualiza no banco tipoUsuario
        this.completionListenerTipoUsuario = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nao foi possivel salvar tipoUsuario");
                    saveTipoUsuario = true;
                }else{
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask tipoUsuario salvo");
                    saveEtapasConfig = true;
                    switch (user.getTipoUsuario()){
                        case "salão":
                            saveEtapaFuncionamento = true;
                            break;
                        case "cliente":
                            //TODO
                            break;
                        case "cabeleireiro":
                            //TODO
                            break;
                    }
                }
            }
        };
        //atualiza no banco nivelUsuario
        this.completionListenerNivelUsuario = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nao foi possivel salvar nivelUsuario");
                    saveNivelUsuario = true;
                }else{
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nivelUsuario salvo");
                    stop = true;
                    dadosSalvosComSucesso = true;
                }
            }
        };
        //atualiza no banco etapa funcionamento salao
        this.completionListenerFuncionamentoSalao = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nao foi possivel salvar funcionamento salao");
                    saveEtapaFuncionamento = true;
                }else{
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask funcioanmento salao salvo");
                    saveEtapaServicos = true;
                }
            }
        };
        //atualiza no banco etapa servicos salao
        this.completionListenerServicosSalao = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nao foi possivel salvar servicos salao");
                    saveEtapaServicos = true;
                }else{
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask servicos salao salvo");
                    saveEtapaCabeleireiros = true;
                }
            }
        };
        //atualiza no banco etapa cabeleireiros salao
        this.completionListenerCabeleireirosSalao = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nao foi possivel salvar cabeleireiros salao");
                    saveEtapaCabeleireiros = true;
                }else{
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask cabeleireiros salao salvo");
                    saveNivelUsuario = true;
                }
            }
        };
        this.firebaseSaveNivelUsuario = null;
        this.firebaseSaveTipoUsuario = null;
        this.firebaseSaveEtapaFuncionamentoSalao = null;
        this.firebaseSaveEtapaServicosSalao = null;
        this.firebaseSaveEtapaCabeleireirosSalao = null;
    }

    @Override
    public void removerFirebaseEvents() {
        if (firebaseSaveTipoUsuario != null ){
            Log.i("script","removerFirebaseEvents() remover listener firebaseSaveTipoUsuario");
            firebaseSaveTipoUsuario = null;
        }
        if (completionListenerTipoUsuario != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerTipoUsuario");
            completionListenerTipoUsuario = null;
        }
        if (firebaseSaveNivelUsuario != null){
            Log.i("script","removerFirebaseEvents() remover listener firebaseSaveNivelUsuario");
            firebaseSaveNivelUsuario = null;
        }
        if (completionListenerNivelUsuario != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerNivelUsuario");
            completionListenerNivelUsuario = null;
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
        this.tempoDecorridoMili = 0;
        this.stop = false;
        this.saveTipoUsuario = true;
        this.saveNivelUsuario = false;
        this.dadosSalvosComSucesso = false;
        this.saveEtapasConfig = false;
        this.saveEtapaFuncionamento = false;
        this.saveEtapaServicos = false;
        this.saveEtapaCabeleireiros = false;

    }

    //METODOS
    private void saveDBNivelUsuario(DatabaseReference.CompletionListener... completionListener) {
        if (this.firebaseSaveNivelUsuario == null){
            this.firebaseSaveNivelUsuario = LibraryClass.getFirebase().child("users").child( user.getId() ).child("nivelUsuario");
        }

        if( completionListener.length == 0 ){
            this.firebaseSaveNivelUsuario.setValue(user.getNivelUsuario());
        }else{
            this.firebaseSaveNivelUsuario.setValue(user.getNivelUsuario(), completionListener[0]);
        }
    }

    private void saveDBTipoUsuario(DatabaseReference.CompletionListener... completionListener) {
        if (this.firebaseSaveTipoUsuario == null){
            this.firebaseSaveTipoUsuario = LibraryClass.getFirebase().child("users").child( user.getId() ).child("tipoUsuario");
        }

        if( completionListener.length == 0 ){
            this.firebaseSaveTipoUsuario.setValue(user.getTipoUsuario());
        }else{
            this.firebaseSaveTipoUsuario.setValue(user.getTipoUsuario(), completionListener[0]);
        }
    }

    private void saveDBFuncionamentoSalao(DatabaseReference.CompletionListener... completionListener) {
        if (this.firebaseSaveEtapaFuncionamentoSalao == null){
            this.firebaseSaveEtapaFuncionamentoSalao = LibraryClass.getFirebase().child("users").child(user.getId()).child("configurações").child("etapas").child("funcionamentoOK");
        }


        if( completionListener.length == 0 ){
            this.firebaseSaveEtapaFuncionamentoSalao.setValue(user.getEtapaFuncionamentoOK());
        }else{
            this.firebaseSaveEtapaFuncionamentoSalao.setValue(user.getEtapaFuncionamentoOK(), completionListener[0]);
        }
    }

    private void saveDBServicosSalao(DatabaseReference.CompletionListener... completionListener) {
        if (this.firebaseSaveEtapaServicosSalao == null){
            this.firebaseSaveEtapaServicosSalao = LibraryClass.getFirebase().child("users").child(user.getId()).child("configurações").child("etapas").child("servicosOK");
        }


        if( completionListener.length == 0 ){
            this.firebaseSaveEtapaServicosSalao.setValue(user.getEtapaServicosOK());
        }else{
            this.firebaseSaveEtapaServicosSalao.setValue(user.getEtapaServicosOK(), completionListener[0]);
        }
    }

    private void saveDBCabeleireirosSalao(DatabaseReference.CompletionListener... completionListener) {
        if (this.firebaseSaveEtapaCabeleireirosSalao == null){
            this.firebaseSaveEtapaCabeleireirosSalao = LibraryClass.getFirebase().child("users").child(user.getId()).child("configurações").child("etapas").child("cabeleireirosOK");
        }


        if( completionListener.length == 0 ){
            this.firebaseSaveEtapaCabeleireirosSalao.setValue(user.getEtapaCabeleireirosOK());
        }else{
            this.firebaseSaveEtapaCabeleireirosSalao.setValue(user.getEtapaCabeleireirosOK(), completionListener[0]);
        }
    }


    private void saveSPRefString(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }




}
