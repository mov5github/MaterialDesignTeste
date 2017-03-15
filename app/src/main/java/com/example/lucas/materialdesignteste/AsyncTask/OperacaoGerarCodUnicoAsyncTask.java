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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Lucas on 16/02/2017.
 */

public class OperacaoGerarCodUnicoAsyncTask extends AsyncTask<Void,Void,Void> implements AcessoFirebase{
    private String REF;
    private Activity mActivity;
    private User user;



    //FIREBASE
    private DatabaseReference firebaseControladorCodUnico;
    private ValueEventListener vELControladorCodUnico;
    private DatabaseReference.CompletionListener completionListenerControladorCodUnico;
    private DatabaseReference firebaseSaveControladorCodUnico;
    private DatabaseReference.CompletionListener completionListenerCodUnico;
    private DatabaseReference firebaseSaveCodUnico;
    private DatabaseReference firebaseSaveNivelUsuario;
    private DatabaseReference.CompletionListener completionListenerNivelUsuario;





    //CONTROLES
    private boolean stop;
    private int tempoDecorridoMili;
    private boolean resgatarControladorCodUnico;
    private boolean salvarControladorCodUnico;
    private Boolean salvarCodUnico;
    private Boolean salvarNivelUsuario;
    private Boolean dadosSalvosComSucesso;
    private int numCadastro;
    private int controlNumCadastro;


    private Boolean salvarRespondeu;


    private Boolean salvarInfoOnline;
    private Boolean codUnicoObtidoComSucesso;
    private Boolean reverterAtualizacaoUsersCodUnicoCompleta;


    public OperacaoGerarCodUnicoAsyncTask(Activity mActivity, User user) {
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

            if(this.resgatarControladorCodUnico){
                if (this.user.getTipoUsuario() != null && this.user.getTipoUsuario().equals("salão")){
                    this.resgatarControladorCodUnico = false;
                    resgataDBControladorCodigoSalao();
                }else if (this.user.getTipoUsuario() != null && this.user.getTipoUsuario().equals("cabeleireiro")){
                    this.resgatarControladorCodUnico = false;
                    resgataDBControladorCodigoCabeleireiro();
                }else{
                    Log.i("script","OperacaoGerarCodUnicoAsyncTask tipo usuario invalido");
                    this.stop = true;
                }
            }
            if (this.salvarControladorCodUnico){
                this.salvarControladorCodUnico = false;
                saveDBControladorCodUnico();
            }
            if (this.salvarCodUnico){
                this.salvarCodUnico = false;
                saveDBCodigoUnico(this.completionListenerCodUnico);
            }
            if (this.salvarNivelUsuario){
                this.user.setNivelUsuario("2.1");
                this.salvarNivelUsuario = false;
                saveDBNivelUsuario(completionListenerNivelUsuario);
            }


            if (!stop){
                int tempoAguardeMili = 250;
                Log.i("script","OperacaoGerarCodUnicoAsyncTask aguardando atualizacao dos dados");
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
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("codUnicoOK");
                }
            }else{
                if (this.mActivity instanceof ConfiguracaoInicialActivity){
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("codUnicoError");
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
                if (ConfiguracaoInicialActivity.isConfiguracaoInicialActivityAtiva()){
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("codUnicoOK");
                }
            }
        }else{
            if (this.mActivity instanceof ConfiguracaoInicialActivity){
                if (ConfiguracaoInicialActivity.isConfiguracaoInicialActivityAtiva()){
                    ((ConfiguracaoInicialActivity)mActivity).addTarefaUiHandler("codUnicoError");
                }
            }
        }
    }

    //INTERFACE
    @Override
    public void initFirebaseEvents() {
        if (this.user.getTipoUsuario() != null && this.user.getTipoUsuario().equals("salão")){
            vELControladorCodUnico = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(int.class) == null) {
                        Log.i("script", "resgataDBControladorCodigoSalao() datasnapshot == null");
                        resgatarControladorCodUnico = true;
                    } else {
                        Log.i("script", "resgataDBControladorCodigoSalao() datasnapshot != null ");
                        numCadastro = Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                        salvarControladorCodUnico = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("script", "resgataDBControladorCodigoSalao() datasnapshot onCancelled ");
                }
            };
        }else if (this.user.getTipoUsuario() != null && this.user.getTipoUsuario().equals("cabeleireiro")){
            vELControladorCodUnico = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(int.class) == null) {
                        Log.i("script", "resgataDBControladorCodigoCabeleireiro() datasnapshot == null");
                        resgatarControladorCodUnico = true;
                    } else {
                        Log.i("script", "resgataDBControladorCodigoCabeleireiro() datasnapshot != null ");
                        numCadastro = Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                        salvarControladorCodUnico = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("script", "resgataDBControladorCodigoCabeleireiro() datasnapshot onCancelled ");
                }
            };
        }
        completionListenerControladorCodUnico = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoGerarCodUnicoAsyncTask nao foi possivel salvar o controladorCodUnico");
                    resgatarControladorCodUnico = true;
                }else {
                    Log.i("script","OperacaoGerarCodUnicoAsyncTask  controladorCodUnico salvo; controlNumCadastro é unico");
                    salvarCodUnico = true;
                }
            }
        };
        completionListenerCodUnico = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoGerarCodUnicoAsyncTask nao foi possivel salvar o CodUnico");
                    salvarCodUnico = true;
                }else {
                    Log.i("script","OperacaoGerarCodUnicoAsyncTask  CodUnico salvo");
                    salvarNivelUsuario = true;
                    saveSPRefString(mActivity.getApplication(),"codUnico",String.valueOf(controlNumCadastro));
                    user.setCodUnico(String.valueOf(controlNumCadastro));
                }
            }
        };
        //atualiza no banco nivelUsuario
        this.completionListenerNivelUsuario = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nao foi possivel salvar nivelUsuario");
                    salvarNivelUsuario = true;
                }else{
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nivelUsuario salvo");
                    stop = true;
                    dadosSalvosComSucesso = true;
                    saveSPRefString(mActivity.getApplication(),"nivelUsuario",user.getNivelUsuario());
                }
            }
        };
    }
    @Override
    public void removerFirebaseEvents() {
        if (this.firebaseControladorCodUnico != null){
            this.firebaseControladorCodUnico.removeEventListener(vELControladorCodUnico);
        }
        this.firebaseControladorCodUnico = null;
        this.vELControladorCodUnico = null;
        this.firebaseControladorCodUnico = null;
        this.completionListenerControladorCodUnico = null;
        this.firebaseSaveCodUnico = null;
        this.completionListenerCodUnico = null;
        this.firebaseSaveNivelUsuario = null;
        this.completionListenerNivelUsuario = null;
    }
    @Override
    public void initControles() {
        this.tempoDecorridoMili = 0;
        this.stop = false;
        this.resgatarControladorCodUnico = true;
        this.numCadastro = 0;
        this.controlNumCadastro = 0;
        this.salvarCodUnico = false;
        this.salvarNivelUsuario = false;
        this.dadosSalvosComSucesso = false;

    }


    //ACESSOS
    private void resgataDBControladorCodigoSalao(){
        Log.i("script","resgataDBControladorCodigoSalao()");
        firebaseControladorCodUnico = LibraryClass.getFirebase().child("RegrasDeNegocio").child("ControladorCodigoSalão");
        firebaseControladorCodUnico.removeEventListener(vELControladorCodUnico);
        firebaseControladorCodUnico.addValueEventListener(vELControladorCodUnico);
    }

    private void resgataDBControladorCodigoCabeleireiro(){
        Log.i("script","resgataDBControladorCodigoCabeleireiro()");
        firebaseControladorCodUnico = LibraryClass.getFirebase().child("RegrasDeNegocio").child("ControladorCodigoCabeleireiro");
        firebaseControladorCodUnico.removeEventListener(vELControladorCodUnico);
        firebaseControladorCodUnico.addValueEventListener(vELControladorCodUnico);
    }

    private void saveDBControladorCodUnico(){
        Log.i("script","saveDBControladorCodUnico()");
        this.firebaseControladorCodUnico.removeEventListener(vELControladorCodUnico);
        this.controlNumCadastro = this.numCadastro;
        if (user.getTipoUsuario() != null && user.getTipoUsuario().equals("salão")){
            saveDBControladorCodigoSalao(completionListenerControladorCodUnico);
        }else if(user.getTipoUsuario() != null && user.getTipoUsuario().equals("cabeleireiro")){
            saveDBControladorCodigoCabeleireiro(completionListenerControladorCodUnico);
        }else {
            Log.i("script","saveDBControladorCodUnico() tipo usuario invalido");
        }
    }
    private void saveDBControladorCodigoSalao( DatabaseReference.CompletionListener... completionListener ){
        Log.i("script","saveDBControladorCodigoSalao()");
        firebaseSaveControladorCodUnico = LibraryClass.getFirebase().child("RegrasDeNegocio").child("ControladorCodigoSalão");
        if( completionListener.length == 0 ){
            firebaseSaveControladorCodUnico.setValue(controlNumCadastro+1);
        }
        else{
            firebaseSaveControladorCodUnico.setValue(controlNumCadastro+1, completionListener[0]);
        }
    }
    private void saveDBControladorCodigoCabeleireiro( DatabaseReference.CompletionListener... completionListener ){
        Log.i("firebase","saveDBControladorCodigoCabeleireiro()");
        firebaseSaveControladorCodUnico = LibraryClass.getFirebase().child("RegrasDeNegocio").child("ControladorCodigoCabeleireiro");
        if( completionListener.length == 0 ){
            firebaseSaveControladorCodUnico.setValue(controlNumCadastro+1);
        }
        else{
            firebaseSaveControladorCodUnico.setValue(controlNumCadastro+1, completionListener[0]);
        }
    }

    private void saveDBCodigoUnico( DatabaseReference.CompletionListener... completionListener ){
        Log.i("script","saveDBCodigoUnico()");
        firebaseSaveCodUnico = LibraryClass.getFirebase().child("users").child(user.getId()).child("CodUnico");
        if( completionListener.length == 0 ){
            firebaseSaveCodUnico.setValue(controlNumCadastro);
        }
        else{
            firebaseSaveCodUnico.setValue(controlNumCadastro, completionListener[0]);
        }
    }

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
