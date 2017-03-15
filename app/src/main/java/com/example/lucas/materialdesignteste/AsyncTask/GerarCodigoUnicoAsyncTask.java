package com.example.lucas.materialdesignteste.asyncTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.materialdesignteste.activitys.ConfiguracaoInicialActivity;
import com.example.lucas.materialdesignteste.domain.User;
import com.example.lucas.materialdesignteste.domain.util.LibraryClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Lucas on 06/02/2017.
 */

public class GerarCodigoUnicoAsyncTask extends AsyncTask<Void,Void,Void> {
    private String REF = "com.example.lucas.materialdesignteste";
    private Activity activity;
    private User user;
    private int numCadastro;
    private int controlNumCadastro;


    private DatabaseReference firebaseControladorCodUnico;
    private ValueEventListener vELControladorCodUnico;

    private DatabaseReference.CompletionListener completionListenerReverterUsersCodUnico;
    private DatabaseReference.CompletionListener completionListenerControladorCodUnico;
    private DatabaseReference firebaseSaveControladorCodUnico;
    private DatabaseReference.CompletionListener completionListenerUsersCodUnico;
    private DatabaseReference firebaseSaveUsersCodUnico;
    private DatabaseReference.CompletionListener completionListenerNivelUsuario;
    private DatabaseReference firebaseSaveNivelUsuario;


    //CONTROLE
    private Boolean salvarRespondeu;
    private Boolean salvarCodUnicoOnline;
    private Boolean salvarNivelUsuarioOnline;
    private Boolean salvarInfoOnline;
    private Boolean codUnicoObtidoComSucesso;
    private Boolean reverterAtualizacaoUsersCodUnicoCompleta;




    public GerarCodigoUnicoAsyncTask(Activity activity, User user) {
        this.activity = activity;
        this.user = user;
        if (this.user.getId() != null && !this.user.getId().isEmpty()){
            this.REF = REF + this.user.getId();
        }
        this.numCadastro = 0;
        this.salvarRespondeu = false;
        this.salvarCodUnicoOnline = false;
        this.salvarNivelUsuarioOnline = false;
        this.salvarInfoOnline = false;
        this.codUnicoObtidoComSucesso = false;
        this.reverterAtualizacaoUsersCodUnicoCompleta = false;
    }

    @Override
    protected Void doInBackground(Void... params) {
        initFirebaseEvents();

        do {
            if (isCancelled())
                return null;
            if (salvarInfoOnline){
                if(salvarCodUnicoOnline){
                    saveDBCodigoUnico(completionListenerUsersCodUnico);
                    while (!salvarRespondeu){
                        Log.i("script","GerarCodigoUnicoAsyncTask aguardando salvar CodUnico");
                        int tempoAguardeMili = 250;
                        synchronized (this){
                            try {
                                wait(tempoAguardeMili);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (salvarNivelUsuarioOnline){
                    salvarRespondeu = false;
                    user.setNivelUsuario("2.1");
                    saveDBNivelUsuario(completionListenerNivelUsuario);
                    while (!salvarRespondeu){
                        Log.i("script","GerarCodigoUnicoAsyncTask aguardando salvar NivelUsuario");
                        int tempoAguardeMili = 250;
                        synchronized (this){
                            try {
                                wait(tempoAguardeMili);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }else{
                if (numCadastro == 0){
                    Log.i("script","GerarCodigoUnicoAsyncTask aguardando resgastar controladorCodUnico");
                    int tempoAguardeMili = 250;
                    synchronized (this){
                        try {
                            wait(tempoAguardeMili);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    controlNumCadastro = numCadastro;
                    salvarControladorCodUnico();
                    while (!salvarRespondeu){
                        Log.i("script","GerarCodigoUnicoAsyncTask aguardando salvar controladorCodUnico");
                        int tempoAguardeMili = 250;
                        synchronized (this){
                            try {
                                wait(tempoAguardeMili);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }while (!codUnicoObtidoComSucesso);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        removerFirebaseEvents();
        if (!isCancelled()){
            if(codUnicoObtidoComSucesso){
                Log.i("script","onPostExecute codUnico foi obtido com sucesso");
                if (getActivity() != null){
                    getActivity().addTarefaUiHandler("codUnico");
                }
            }else {
                Log.i("script","onPostExecute codUnico nao foi obtido com sucesso");
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        while (!reverterAtualizacaoUsersCodUnicoCompleta){
            if (!codUnicoObtidoComSucesso && salvarNivelUsuarioOnline){
                reverterSaveDBCodigoUnico(this.completionListenerReverterUsersCodUnico);
                while (!salvarRespondeu){
                    Log.i("script","GerarCodigoUnicoAsyncTask aguardando salvar CodUnico");
                    int tempoAguardeMili = 250;
                    synchronized (this){
                        try {
                            wait(tempoAguardeMili);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else {
                reverterAtualizacaoUsersCodUnicoCompleta = true;
            }
        }
        removerFirebaseEvents();
    }

    private void initFirebaseEvents(){
        completionListenerControladorCodUnico = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","nao foi possivel salvar o controladorCodUnico");
                    salvarRespondeu = true;
                }else {
                    Log.i("script","GerarCodigoUnicoAsyncTask  controladorCodUnico salvo; controlNumCadastro é unico");
                    salvarRespondeu = true;
                    salvarInfoOnline = true;
                    salvarCodUnicoOnline = true;
                }
            }
        };
        completionListenerUsersCodUnico = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","nao foi possivel salvar o CodUnico");
                    salvarRespondeu = true;
                }else {
                    Log.i("script","GerarCodigoUnicoAsyncTask  CodUnico salvo");
                    salvarRespondeu = true;
                    salvarCodUnicoOnline = false;
                    salvarNivelUsuarioOnline = true;
                    saveSPRefString(getActivity().getApplication(),"codUnico",String.valueOf(controlNumCadastro));
                }
            }
        };
        //atualiza no banco nivelUsuario
        this.completionListenerNivelUsuario = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","AtualizarUsersOnlineAsyncTask nao foi possivel salvar nivelUsuario");
                    salvarRespondeu = true;
                }else{
                    Log.i("script","AtualizarUsersOnlineAsyncTask nivelUsuario salvo");
                    saveSPRefString(getActivity().getApplicationContext(),"nivelUsuario",user.getNivelUsuario());
                    salvarRespondeu = true;
                    codUnicoObtidoComSucesso = true;
                    salvarNivelUsuarioOnline = false;
                }
            }
        };
        //reverte no banco a atualizacao tipoUsuario
        this.completionListenerReverterUsersCodUnico = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","AtualizarUsersOnlineAsyncTask nao foi possivel reverter as atualizacoes nivelUsuario");
                    salvarRespondeu = true;
                }else{
                    Log.i("script","AtualizarUsersOnlineAsyncTask reverter as atualizacoes nivelUsuario com sucesso");
                    saveSPRefString(getActivity().getApplicationContext(),"codUnico","");
                    salvarRespondeu = true;
                    reverterAtualizacaoUsersCodUnicoCompleta = true;
                }
            }
        };
        if (user.getTipoUsuario() != null && user.getTipoUsuario().equals("salão")){
            if (numCadastro == 0){
                resgataDBControladorCodigoSalao();
            }
        }else if(user.getTipoUsuario() != null && user.getTipoUsuario().equals("cabeleireiro")){
            if (numCadastro == 0){
                resgataDBControladorCodigoCabeleireiro();
            }
        }else {
            Log.i("script","tipo usuario invalido");
        }
    }

    private void removerFirebaseEvents(){
        if (firebaseControladorCodUnico != null && vELControladorCodUnico != null){
            Log.i("script","removerFirebaseEvents() remover VEL controladorCodUnico");
            firebaseControladorCodUnico.removeEventListener(vELControladorCodUnico);
            firebaseControladorCodUnico = null;
            vELControladorCodUnico = null;
        }
        if (firebaseSaveControladorCodUnico != null ){
            Log.i("script","removerFirebaseEvents() remover listener firebaseSaveControladorCodUnico");
            firebaseSaveControladorCodUnico = null;
        }
        if (completionListenerControladorCodUnico != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerControladorCodUnico");
            completionListenerControladorCodUnico = null;
        }
        if (firebaseSaveUsersCodUnico != null ){
            Log.i("script","removerFirebaseEvents() remover listener firebaseSaveUsersCodUnico");
            firebaseSaveUsersCodUnico = null;
        }
        if (completionListenerUsersCodUnico != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerUsersCodUnico");
            completionListenerUsersCodUnico = null;
        }
        if (firebaseSaveControladorCodUnico != null){
            Log.i("script","removerFirebaseEvents() remover listener controladorCodUnico");
            firebaseSaveControladorCodUnico = null;
        }
        if (completionListenerControladorCodUnico != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerControladorCodUnico");
            completionListenerControladorCodUnico = null;
        }
        if (firebaseSaveNivelUsuario != null){
            Log.i("script","removerFirebaseEvents() remover listener firebaseSaveNivelUsuario");
            firebaseSaveNivelUsuario = null;
        }
        if (completionListenerNivelUsuario != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerNivelUsuario");
            completionListenerNivelUsuario = null;
        }
        if (completionListenerReverterUsersCodUnico != null){
            Log.i("script","removerFirebaseEvents() remover listener completionListenerReverterUsersCodUnico");
            completionListenerReverterUsersCodUnico = null;
        }
    }

    //FIREBASE
    private void resgataDBControladorCodigoSalao(){
        Log.i("firebase","resgataDBControladorCodigoSalao()");
        firebaseControladorCodUnico = LibraryClass.getFirebase().child("RegrasDeNegocio").child("ControladorCodigoSalão");
        vELControladorCodUnico = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(int.class) == null) {
                    Log.i("script", "resgataDBControladorCodigoSalao() datasnapshot == null");
                } else {
                    Log.i("script", "resgataDBControladorCodigoSalao() datasnapshot != null ");
                    numCadastro = Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("script", "resgataDBControladorCodigoSalao() datasnapshot onCancelled ");
            }
        };
        firebaseControladorCodUnico.addValueEventListener(vELControladorCodUnico);
    }
    private void resgataDBControladorCodigoCabeleireiro(){
        Log.i("firebase","resgataDBControladorCodigoCabeleireiro()");
        firebaseControladorCodUnico = LibraryClass.getFirebase().child("RegrasDeNegocio").child("ControladorCodigoCabeleireiro");
        vELControladorCodUnico = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(int.class) == null) {
                    Log.i("script", "resgataDBControladorCodigoCabeleireiro() datasnapshot == null");
                } else {
                    Log.i("script", "resgataDBControladorCodigoCabeleireiro() datasnapshot != null ");
                    numCadastro = Integer.valueOf(String.valueOf(dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("script", "resgataDBControladorCodigoCabeleireiro() datasnapshot onCancelled ");
            }
        };
        firebaseControladorCodUnico.addValueEventListener(vELControladorCodUnico);
    }

    private void  salvarControladorCodUnico(){
        if (user.getTipoUsuario() != null && user.getTipoUsuario().equals("salão")){
            saveDBControladorCodigoSalao(completionListenerControladorCodUnico);
        }else if(user.getTipoUsuario() != null && user.getTipoUsuario().equals("cabeleireiro")){
           saveDBControladorCodigoCabeleireiro(completionListenerControladorCodUnico);
        }else {
            Log.i("script","tipo usuario invalido");
        }
    }
    private void saveDBControladorCodigoSalao( DatabaseReference.CompletionListener... completionListener ){
        Log.i("script","saveDBControladorCodigoSalao()");
        salvarRespondeu = false;
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
        salvarRespondeu = false;
        firebaseSaveControladorCodUnico = LibraryClass.getFirebase().child("RegrasDeNegocio").child("ControladorCodigoCabeleireiro");
        if( completionListener.length == 0 ){
            firebaseSaveControladorCodUnico.setValue(controlNumCadastro+1);
        }
        else{
            firebaseSaveControladorCodUnico.setValue(controlNumCadastro+1, completionListener[0]);
        }
    }

    private void saveDBCodigoUnico( DatabaseReference.CompletionListener... completionListener ){
        Log.i("script","saveDBControladorCodigoSalao()");
        salvarRespondeu = false;
        firebaseSaveUsersCodUnico = LibraryClass.getFirebase().child("users").child(user.getId()).child("CodUnico");
        if( completionListener.length == 0 ){
            firebaseSaveUsersCodUnico.setValue(controlNumCadastro);
        }
        else{
            firebaseSaveUsersCodUnico.setValue(controlNumCadastro, completionListener[0]);
        }
    }
    private void reverterSaveDBCodigoUnico( DatabaseReference.CompletionListener... completionListener ){
        Log.i("script","reverterSaveDBCodigoUnico()");
        salvarRespondeu = false;
        firebaseSaveUsersCodUnico = LibraryClass.getFirebase().child("users").child(user.getId()).child("CodUnico");
        if( completionListener.length == 0 ){
            firebaseSaveUsersCodUnico.removeValue();
        }
        else{
            firebaseSaveUsersCodUnico.removeValue(completionListener[0]);
        }
    }


    private void saveDBNivelUsuario( DatabaseReference.CompletionListener... completionListener ){
        Log.i("script","saveDBNivelUsuario()");
        firebaseSaveUsersCodUnico = LibraryClass.getFirebase().child("users").child(user.getId()).child("nivelUsuario");
        if( completionListener.length == 0 ){
            firebaseSaveUsersCodUnico.setValue("2.1");
        }
        else{
            firebaseSaveUsersCodUnico.setValue("2.1", completionListener[0]);
        }
    }




    private ConfiguracaoInicialActivity getActivity(){
        ConfiguracaoInicialActivity configuracaoInicialActivity2 = null;
        if (this.activity instanceof ConfiguracaoInicialActivity){
            configuracaoInicialActivity2 = (ConfiguracaoInicialActivity) this.activity;
        }
        return configuracaoInicialActivity2;
    }

    private void saveSPRefString(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }


}
