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
 * Created by Lucas on 16/02/2017.
 */

public class OperacaoRedefinirNivelUsuarioAsyncTask extends AsyncTask<Void,Void,Void> implements AcessoFirebase {
    private String REF;
    private Activity mActivity;
    private User user;

    //CONTROLES
    private boolean stop;
    private int tempoDecorridoMili;
    private boolean saveNivelUsuario;
    private boolean dadosSalvosComSucesso;

    //FIREBASE
    private DatabaseReference firebaseSaveNivelUsuario;
    private DatabaseReference.CompletionListener completionListenerNivelUsuario;

    public OperacaoRedefinirNivelUsuarioAsyncTask(Activity mActivity, User user) {
        this.mActivity = mActivity;
        this.user = user;
        if (this.user.getId() != null && !this.user.getId().isEmpty()){
            this.REF = "com.example.lucas.materialdesignteste" + user.getId();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        initFirebaseEvents();
        initControles();

        do {
            if (isCancelled()){this.stop = true;return null;}

            if (this.saveNivelUsuario){
                this.saveNivelUsuario = false;
                saveDBNivelUsuario(completionListenerNivelUsuario);
            }

            if (!stop){
                int tempoAguardeMili = 250;
                Log.i("script","OperacaoRedefinirNivelUsuarioAsyncTask aguardando atualizacao dos dados");
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
                    saveSPRefString(this.mActivity.getApplicationContext(),"nivelUsuario",this.user.getNivelUsuario());
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("redefinirNivelUsuarioOK");
                }
            }else{
                if (this.mActivity instanceof ConfiguracaoInicialActivity){
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("redefinirNivelUsuarioError");
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
                saveSPRefString(this.mActivity.getApplicationContext(),"nivelUsuario",this.user.getNivelUsuario());
                ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("redefinirNivelUsuarioOK");
            }
        }else{
            if (this.mActivity instanceof ConfiguracaoInicialActivity){
                ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("redefinirNivelUsuarioError");
            }
        }
    }

    //INTERFACE
    @Override
    public void initFirebaseEvents() {
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
        this.firebaseSaveNivelUsuario = null;
    }
    @Override
    public void removerFirebaseEvents() {
        if (firebaseSaveNivelUsuario != null){
            Log.i("script","removerFirebaseEvents() remover listener firebaseSaveNivelUsuario");
            firebaseSaveNivelUsuario = null;
        }
        if (completionListenerNivelUsuario != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerNivelUsuario");
            completionListenerNivelUsuario = null;
        }
    }
    @Override
    public void initControles() {
        this.tempoDecorridoMili = 0;
        this.stop = false;
        this.saveNivelUsuario = true;
        this.dadosSalvosComSucesso = false;
    }

    //ACESSOS
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


    private void saveSPRefString(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

}
