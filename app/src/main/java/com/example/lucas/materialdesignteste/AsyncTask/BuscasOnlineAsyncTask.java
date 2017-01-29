package com.example.lucas.materialdesignteste.AsyncTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.lucas.materialdesignteste.SplashScreen2Activity;
import com.example.lucas.materialdesignteste.domain.User;
import com.example.lucas.materialdesignteste.domain.util.LibraryClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Lucas on 27/01/2017.
 */

public class BuscasOnlineAsyncTask extends android.os.AsyncTask {
    private Activity activity;
    private User user;

    //Controle
    private Boolean buscasOnlinesIniciaisCompletas;
    private Boolean buscasOnlinesSecundariasCompletas;
    private Boolean buscarNivelUsuarioOnlineFinalizado;
    private Boolean buscarTipoUsuarioOnlineFinalizado;
    private Boolean buscarCodUnicoOnlineFinalizado = null;
    private Boolean buscarEtapaConfigFuncionamentoOnlineFinalizado = null;
    private Boolean buscarEtapaConfigServicosOnlineFinalizado = null;
    private Boolean buscarEtapaConfigCabeleireirosOnlineFinalizado = null;

    //FIREBASE
    private DatabaseReference firebaseNivelUsuario = null;
    private ValueEventListener vELNivelUsuario = null;
    private DatabaseReference firebaseTipoUsuario = null;
    private ValueEventListener vELTipoUsuario = null;
    private DatabaseReference firebaseCodUnico = null;
    private ValueEventListener vELCodUnico = null;
    private DatabaseReference firebaseEtapaConfigFuncionamento = null;
    private ValueEventListener vELEtapaConfigFuncionamento = null;
    private DatabaseReference firebaseEtapaConfigServicos = null;
    private ValueEventListener vELEtapaConfigServicos = null;
    private DatabaseReference firebaseEtapaConfigCabeleireiros = null;
    private ValueEventListener vELEtapaConfigCabeleireiros = null;


    public BuscasOnlineAsyncTask(Activity activity, User user) {
        this.buscasOnlinesIniciaisCompletas = false;
        this.activity = activity;
        this.user = user;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        int tempoMaximoVerificacaoMilisegundos = 10000;
        int tempoDecorridoVerificacaoMilisegundos = 0;
        do {
            realizarBuscasOnlineIniniais();
            if (!buscasOnlinesIniciaisCompletas){
                int tempoAguardeMili = 250;
                try {
                    wait(tempoAguardeMili);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tempoDecorridoVerificacaoMilisegundos =+ tempoAguardeMili ;
            }
        }while (!buscasOnlinesIniciaisCompletas && tempoDecorridoVerificacaoMilisegundos < tempoMaximoVerificacaoMilisegundos);
        do {
            if (tempoDecorridoVerificacaoMilisegundos >= tempoMaximoVerificacaoMilisegundos){
                buscasOnlinesSecundariasCompletas = true;
            }else{
                realizarBuscasOnlineSecundarias();
                int tempoAguardeMili = 250;
                try {
                    wait(tempoAguardeMili);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tempoDecorridoVerificacaoMilisegundos =+ tempoAguardeMili ;
            }
        }while (!buscasOnlinesSecundariasCompletas);
        while (!getActivity().getSplashCompleta()){
            Log.i("script","aguardando splashScren terminar");
            int tempoAguardeMili = 250;
            try {
                wait(tempoAguardeMili);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        direcionarUsuario();
        //TODO direcionar usuario para tela correta

    }

    private void realizarBuscasOnlineIniniais() {
        if (buscasOnlinesIniciaisCompletas == null){
            //verifica a nescessidade de iniciar buscas Onlines iniciais
            if ((user.getNivelUsuario() != null && !user.getNivelUsuario().isEmpty()) && (user.getTipoUsuario() != null && !user.getTipoUsuario().isEmpty())){
                buscasOnlinesIniciaisCompletas = true;
                return;
            }else {
                buscasOnlinesIniciaisCompletas = false;
            }
        }
        //realiza busca por nivelUsuario
        if (user.getNivelUsuario() == null || user.getNivelUsuario().isEmpty()){
            if (buscarNivelUsuarioOnlineFinalizado == null) {
                buscarNivelUsuarioOnlineFinalizado = false;
                buscarNivelUsuarioOnline();
            }
        }
        //realiza busca por nivelUsuario
        if ((user.getNivelUsuario() != null || !user.getNivelUsuario().isEmpty()) && (user.getTipoUsuario() == null || user.getTipoUsuario().isEmpty())){
            if (buscarTipoUsuarioOnlineFinalizado == null) {
                buscarTipoUsuarioOnlineFinalizado = false;
                buscarTipoUsuarioOnline();
            }
        }
        //verifica se as buscas acabaram
        if (buscarNivelUsuarioOnlineFinalizado && buscarTipoUsuarioOnlineFinalizado){
            buscasOnlinesIniciaisCompletas = true;
        }
    }

    private void buscarNivelUsuarioOnline(){
        if (vELNivelUsuario == null){
            Log.i("script","vELNivelUsuario == null ");
            vELNivelUsuario = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(String.class) != null){
                        Log.i("script","buscarNivelUsuarioOnline() data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                        user.setNivelUsuario(String.valueOf(dataSnapshot.getValue()));
                        saveSPRefString(getActivity().getApplicationContext(),"nivelUsuario",String.valueOf(dataSnapshot.getValue()));
                        if (user.getNivelUsuario().equals("1")){
                            buscarTipoUsuarioOnlineFinalizado = true;
                        }
                        buscarNivelUsuarioOnlineFinalizado = true;
                    }else{
                        Log.i("script","buscarNivelUsuarioOnline() data dataSnapshot == Null");
                        buscarNivelUsuarioOnlineFinalizado = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("script","buscarNivelUsuarioOnline() onCancelled ");
                    buscarNivelUsuarioOnlineFinalizado = true;
                }
            };
        }
        if (firebaseNivelUsuario == null){
            Log.i("script","firebaseNivelUsuario == null "+user.getId());
            firebaseNivelUsuario = LibraryClass.getFirebase().child("users").child(user.getId()).child("nivelUsuario");
        }
        firebaseNivelUsuario.addValueEventListener(vELNivelUsuario);
    }

    private void buscarTipoUsuarioOnline(){
        if (vELTipoUsuario == null){
            vELTipoUsuario = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(String.class) != null){
                        Log.i("script","buscarTipoUsuarioOnline() data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                        user.setNivelUsuario(String.valueOf(dataSnapshot.getValue()));
                        saveSPRefString(getActivity().getApplicationContext(),"nivelUsuario",String.valueOf(dataSnapshot.getValue()));
                        buscarTipoUsuarioOnlineFinalizado = true;
                    }else{
                        Log.i("script","buscarTipoUsuarioOnline() data dataSnapshot == Null");
                        buscarTipoUsuarioOnlineFinalizado = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("script","buscarTipoUsuarioOnline() onCancelled ");
                    buscarTipoUsuarioOnlineFinalizado = true;
                }
            };
        }
        if (firebaseTipoUsuario == null){
            firebaseTipoUsuario = LibraryClass.getFirebase().child("users").child(user.getId()).child("tipoUsuario");
        }
        firebaseTipoUsuario.addValueEventListener(vELTipoUsuario);
    }

    private void realizarBuscasOnlineSecundarias() {
        if (buscasOnlinesSecundariasCompletas == null){
            buscasOnlinesSecundariasCompletas = false;
        }
        if (!buscasOnlinesSecundariasCompletas){
            if (user.getNivelUsuario() != null && !user.getNivelUsuario().isEmpty()){
                if (user.getNivelUsuario().equals("1")){
                    buscasOnlinesSecundariasCompletas = true;
                }else if (user.getNivelUsuario().equals("2") || user.getNivelUsuario().equals("3")){
                    if (user.getTipoUsuario() == null || user.getTipoUsuario().isEmpty()){
                        buscasOnlinesSecundariasCompletas = true;
                    }else {
                        if (user.getTipoUsuario().equals("cliente")){
                            //TODO implementar verificaçao de etapaConfig para nivel == "2"
                            buscasOnlinesSecundariasCompletas = true;
                        }else if (user.getTipoUsuario().equals("salão")){
                            if (user.getCodUnico() == null || user.getCodUnico().isEmpty()){ //realiza busca por CodUnico
                                if (buscarCodUnicoOnlineFinalizado == null) {
                                    buscarCodUnicoOnlineFinalizado = false;
                                    buscarCodUnicoOnline();
                                }
                            }else {//realiza busca por etapasConfig
                                //busca por etapa de funcionamento
                                if (buscarEtapaConfigFuncionamentoOnlineFinalizado == null) {
                                    buscarEtapaConfigFuncionamentoOnlineFinalizado = false;
                                    buscarEtapaConfigFuncionamentoOnline();
                                }
                                //busca por etapa de servicos
                                if (buscarEtapaConfigServicosOnlineFinalizado == null) {
                                    buscarEtapaConfigServicosOnlineFinalizado = false;
                                    buscarEtapaConfigServicosOnline();
                                }
                                //busca por etapa de cabeleireiros
                                if (buscarEtapaConfigCabeleireirosOnlineFinalizado == null) {
                                    buscarEtapaConfigCabeleireirosOnlineFinalizado = false;
                                    buscarEtapaConfigCabeleireirosOnline();
                                }
                                //verifica se buscas por etapasConfig estao completas
                                if (buscarEtapaConfigFuncionamentoOnlineFinalizado && buscarEtapaConfigServicosOnlineFinalizado && buscarEtapaConfigCabeleireirosOnlineFinalizado){
                                    buscasOnlinesSecundariasCompletas = true;
                                }
                            }
                        }
                        else if (user.getTipoUsuario().equals("cabeleireiro")){
                            if (user.getCodUnico() == null || user.getCodUnico().isEmpty()){
                                if (buscarCodUnicoOnlineFinalizado == null) {
                                    buscarCodUnicoOnlineFinalizado = false;
                                    buscarCodUnicoOnline();
                                }
                            }else {
                                //TODO  busca por etapasConfig e verifica se buscas por etapasConfig estao completas
                            }
                        }
                    }
                }
            }else {
                buscasOnlinesSecundariasCompletas = true;
            }
        }

    }

    private void buscarCodUnicoOnline(){
        if (vELCodUnico == null){
            vELCodUnico = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(String.class) != null){
                        Log.i("script","buscarCodUnicoOnline() data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                        user.setCodUnico(String.valueOf(dataSnapshot.getValue()));
                        saveSPRefString(getActivity().getApplicationContext(),"codUnico",String.valueOf(dataSnapshot.getValue()));
                        buscarCodUnicoOnlineFinalizado = true;
                    }else{
                        Log.i("script","buscarCodUnicoOnline() data dataSnapshot == Null");
                        buscarCodUnicoOnlineFinalizado = true;
                        buscasOnlinesSecundariasCompletas = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("script","buscarCodUnicoOnline() onCancelled ");
                    buscarCodUnicoOnlineFinalizado = true;
                    buscasOnlinesSecundariasCompletas = true;
                }
            };
        }
        if (firebaseCodUnico == null){
            firebaseCodUnico = LibraryClass.getFirebase().child("users").child(user.getId()).child("codUnico");
        }
        firebaseCodUnico.addValueEventListener(vELCodUnico);
    }

    private void buscarEtapaConfigFuncionamentoOnline(){
        if (user.getEtapaFuncionamentoOK() == null || !user.getEtapaFuncionamentoOK()){
            if (vELEtapaConfigFuncionamento == null){
                vELEtapaConfigFuncionamento = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Boolean.class) != null){
                            Log.i("script","buscarEtapaConfigOnline() funcionamento salao data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                            user.setEtapaFuncionamentoOK((Boolean) dataSnapshot.getValue());
                            saveSPRefBoolean(getActivity().getApplicationContext(),"funcionamentoOK",(Boolean) dataSnapshot.getValue());
                            buscarEtapaConfigFuncionamentoOnlineFinalizado = true;
                        }else{
                            Log.i("script","buscarEtapaConfigOnline() funcionamento data dataSnapshot == Null");
                            buscarEtapaConfigFuncionamentoOnlineFinalizado = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","buscarEtapaConfigOnline() funcionamento onCancelled ");
                        buscarEtapaConfigFuncionamentoOnlineFinalizado = true;
                    }
                };
            }
            if (firebaseEtapaConfigFuncionamento == null){
                firebaseEtapaConfigFuncionamento = LibraryClass.getFirebase().child("salões").child(user.getCodUnico()).child("configurações").child("etapas").child("funcionamentoOK");
            }
            firebaseEtapaConfigFuncionamento.addValueEventListener(vELEtapaConfigFuncionamento);
        }
    }

    private void buscarEtapaConfigServicosOnline(){
        if (user.getEtapaServicosOK() == null || !user.getEtapaServicosOK()){
            if (vELEtapaConfigServicos == null){
                vELEtapaConfigServicos = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Boolean.class) != null){
                            Log.i("script","buscarEtapaConfigOnline() Servicos salao data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                            user.setEtapaServicosOK((Boolean) dataSnapshot.getValue());
                            saveSPRefBoolean(getActivity().getApplicationContext(),"servicosOK",(Boolean) dataSnapshot.getValue());
                            buscarEtapaConfigServicosOnlineFinalizado = true;
                        }else{
                            Log.i("script","buscarEtapaConfigOnline() Servicos data dataSnapshot == Null");
                            buscarEtapaConfigServicosOnlineFinalizado = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","buscarEtapaConfigOnline() Servicos onCancelled ");
                        buscarEtapaConfigServicosOnlineFinalizado = true;
                    }
                };
            }
            if (firebaseEtapaConfigServicos == null){
                firebaseEtapaConfigServicos = LibraryClass.getFirebase().child("salões").child(user.getCodUnico()).child("configurações").child("etapas").child("servicosOK");
            }
            firebaseEtapaConfigServicos.addValueEventListener(vELEtapaConfigServicos);
        }
    }

    private void buscarEtapaConfigCabeleireirosOnline(){
        if (user.getEtapaCabeleireirosOK() == null || !user.getEtapaCabeleireirosOK()){
            if (vELEtapaConfigCabeleireiros == null){
                vELEtapaConfigCabeleireiros = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Boolean.class) != null){
                            Log.i("script","buscarEtapaConfigOnline() Cabeleireiros salao data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                            user.setEtapaCabeleireirosOK((Boolean) dataSnapshot.getValue());
                            saveSPRefBoolean(getActivity().getApplicationContext(),"cabeleireirosOK",(Boolean) dataSnapshot.getValue());
                            buscarEtapaConfigCabeleireirosOnlineFinalizado = true;
                        }else{
                            Log.i("script","buscarEtapaConfigOnline() Servicos data dataSnapshot == Null");
                            buscarEtapaConfigCabeleireirosOnlineFinalizado = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","buscarEtapaConfigOnline() Cabeleireiros onCancelled ");
                        buscarEtapaConfigCabeleireirosOnlineFinalizado = true;
                    }
                };
            }
            if (firebaseEtapaConfigCabeleireiros == null){
                firebaseEtapaConfigCabeleireiros = LibraryClass.getFirebase().child("salões").child(user.getCodUnico()).child("configurações").child("etapas").child("cabeleireirosOK");
            }
            firebaseEtapaConfigCabeleireiros.addValueEventListener(vELEtapaConfigCabeleireiros);
        }

    }

    private void direcionarUsuario(){
        if (user.getNivelUsuario() == null || user.getNivelUsuario().isEmpty()){
            Log.i("script","direcionarUsuario() nivelUsuario == null call erroAoBuscar");
            Bundle bundle = user.remodelUser();
            getActivity().callErroBuscarOnlineActivity(bundle);
        }else {
            if (user.getNivelUsuario().equals("1")){
                Log.i("script","direcionarUsuario() nivelUsuario == 1 callConfigInicial");
                Bundle bundle = user.remodelUser();
                getActivity().callConfiguracaoIncialActivity2(bundle);
            }else if (user.getNivelUsuario().equals("2") || user.getNivelUsuario().equals("3")){
                if (user.getTipoUsuario() == null || user.getTipoUsuario().isEmpty()){
                    Log.i("script","direcionarUsuario() tipoUsuario == null call erroAoBuscar");
                    Bundle bundle = user.remodelUser();
                    getActivity().callErroBuscarOnlineActivity(bundle);
                }else if (user.getTipoUsuario().equals("cliente")){
                    if (user.getNivelUsuario().equals("2")){
                        //TODO implementar direcionar cliente
                    }else if (user.getTipoUsuario().equals("3")){
                        //TODO implementar direcionar cliente
                    }
                }else if (user.getTipoUsuario().equals("salão") || user.getTipoUsuario().equals("cabeleireiro")){
                    if (user.getNivelUsuario().equals("2")){
                        if (user.getCodUnico() == null || user.getCodUnico().isEmpty()){
                            Log.i("script","direcionarUsuario() CodUnico == null call erroAoBuscar");
                            Bundle bundle = user.remodelUser();
                            getActivity().callErroBuscarOnlineActivity(bundle);
                        }else {
                            Log.i("script","direcionarUsuario() callConfigInicial");
                            Bundle bundle = user.remodelUser();
                            getActivity().callConfiguracaoIncialActivity2(bundle);
                        }
                    }else if (user.getNivelUsuario().equals("3")){
                        if (user.getCodUnico() == null || user.getCodUnico().isEmpty()){
                            Log.i("script","direcionarUsuario() CodUnico == null call erroAoBuscar");
                            Bundle bundle = user.remodelUser();
                            getActivity().callErroBuscarOnlineActivity(bundle);
                        }else{
                            Log.i("script","direcionarUsuario() nivelUsuario == 3 callHome");
                            Bundle bundle = user.remodelUser();
                            getActivity().callHomeActivity(bundle);
                        }
                    }
                }else {
                    Log.i("script","direcionarUsuario() tipoUsuario invalido call erroAoBuscar");
                    Bundle bundle = user.remodelUser();
                    getActivity().callErroBuscarOnlineActivity(bundle);
                }
            }else{
                Log.i("script","direcionarUsuario() nivelUsuario invalido call erroAoBuscar");
                Bundle bundle = user.remodelUser();
                getActivity().callErroBuscarOnlineActivity(bundle);
            }
        }
    }


    public SplashScreen2Activity getActivity() {
        SplashScreen2Activity activity = null;
        if (this.activity instanceof SplashScreen2Activity){
            activity = (SplashScreen2Activity)this.activity;
        }
        return activity;
    }

    //SHAREDPREFERENCES
    private void saveSPRefString(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    private void saveSPRefBoolean(Context context, String key, Boolean value ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }

    private String getSPRefString(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        String value = sp.getString(key, "");
        return( value );
    }

}
