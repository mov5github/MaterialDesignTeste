package com.example.lucas.materialdesignteste.threads;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.materialdesignteste.SplashScreen2Activity;
import com.example.lucas.materialdesignteste.SplashScreenActivity;
import com.example.lucas.materialdesignteste.domain.User;
import com.example.lucas.materialdesignteste.domain.util.LibraryClass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Lucas on 12/01/2017.
 */

public class SplashScreenSecondThread2 extends Thread implements GoogleApiClient.OnConnectionFailedListener{
    private Boolean stopThread = false;

    private String REF;
    private User user;

    private Handler handlerSplashScreenSecondThread;
    private Activity activity;

    //CONTROLE
    private Boolean buscasOnlineIniciada = false;
    private Boolean buscasOnlineConcluida = false;
    private Boolean buscarNivelUsuarioOnlineIniciado = false;
    private Boolean buscarNivelUsuarioOnlineFinalizado = false;
    private Boolean buscarTipoUsuarioOnlineIniciado = false;
    private Boolean buscarTipoUsuarioOnlineFinalizado = false;
    private Boolean buscarCodUnicoOnlineIniciado = false;
    private Boolean buscarCodUnicoOnlineFinalizado = false;
    private Boolean buscarEtapaConfigOnlineIniciado = false;
    private Boolean buscarEtapaConfigOnlineFinalizado = false;
    private Boolean buscarEtapaConfigFuncionamentoOnlineIniciado = false;
    private Boolean buscarEtapaConfigFuncionamentoOnlineFinalizado = false;
    private Boolean buscarEtapaConfigServicosOnlineIniciado = false;
    private Boolean buscarEtapaConfigServicosOnlineFinalizado = false;
    private Boolean buscarEtapaConfigCabeleireirosOnlineIniciado = false;
    private Boolean buscarEtapaConfigCabeleireirosOnlineFinalizado = false;

    //RUNNABLES
    private Runnable runnableIBO = null;
    private Runnable runnableBOTO = null;
    private Runnable runnableBNUOTO = null;
    private Runnable runnableBNUO = null;
    private Runnable runnableBTUO = null;
    private Runnable runnableBTUOTO = null;



    //FIREBASE BUSCAS
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



    public SplashScreenSecondThread2(Activity activity){
        this.activity = activity;
    }


    @Override
    public void run() {
        super.run();
        /*Looper.prepare();
        handlerSplashScreenSecondThread = new Handler();
        if (getActivity()!= null){
            this.REF = getActivity().getREF();
            Log.i("script","SplashScreenSecondThread2 run() REF"+this.REF);
            this.user = getActivity().getUser();
        }
        iniciarBuscasOnline();
        Looper.loop();*/
        while (!stopThread) {
            Looper.prepare();
            handlerSplashScreenSecondThread = new Handler();
            if (getActivity()!= null){
                this.REF = getActivity().getREF();
                Log.i("script","SplashScreenSecondThread2 run() REF"+this.REF);
                this.user = getActivity().getUser();
            }
            iniciarBuscasOnline();
            Looper.loop();
        }
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FirebaseCrash
                .report(
                        new Exception(
                                connectionResult.getErrorCode()+": "+connectionResult.getErrorMessage()
                        )
                );
        if (getActivity() != null){
            getActivity().showSnackbar( connectionResult.getErrorMessage() );
        }
    }

    public void killThread(){
        stopThread = true;
        Thread.currentThread().interrupt();
    }

    private void iniciarBuscasOnline() {
        //maximo tempo de verificacao
        if (runnableBOTO == null){
            runnableBOTO = new Runnable() {
                @Override
                public void run() {
                    buscasOnlineConcluida = true;
                    removerVEL();
                    Log.i("script","chamar direcionar usuario  runnableBOTO");
                    direcionarUsuario();
                }
            };
            handlerSplashScreenSecondThread.postDelayed(runnableBOTO,(11000));
        }
        if (runnableIBO == null){
            runnableIBO = new Runnable() {
                @Override
                public void run() {

                //verificacao nivel usuario
                if (user.getNivelUsuario() == null || user.getNivelUsuario().isEmpty()){
                    buscarNivelUsuarioOnline();
                }
                //verificacao tipo usuario
                if (user.getTipoUsuario() == null || user.getTipoUsuario().isEmpty()){
                    buscarTipoUsuarioOnline();
                }
                //verificacao codUnico
                if (user.getCodUnico() == null || user.getCodUnico().isEmpty()){
                    if (user.getTipoUsuario() != null && !user.getTipoUsuario().isEmpty() && user.getNivelUsuario() != null && !user.getNivelUsuario().isEmpty()){
                        switch (user.getNivelUsuario()){
                            case "2":
                                if (user.getTipoUsuario().equals("salão") || user.getTipoUsuario().equals("cabeleireiro")){
                                    buscarCodUnicoOnline();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                //verificacao etapa configuracao
                if (user.getTipoUsuario() != null && !user.getTipoUsuario().isEmpty() && user.getNivelUsuario() != null && !user.getNivelUsuario().isEmpty()){
                    switch (user.getNivelUsuario()){
                        case "2":
                            buscarEtapaConfigOnline();
                            break;
                        default:
                            break;
                    }
                }
                //verifica se as buscas estao completas e direciona o usuario
                if (buscasOnlineIniciada){
                    Log.i("script","chamar direcionar usuario  runnableIBO");
                    direcionarUsuario();
                }
                }
            };
            buscasOnlineIniciada = true;
            handlerSplashScreenSecondThread.post(runnableIBO);
        }


    }

    private void direcionarUsuario(){
        if (user.getNivelUsuario() != null && !user.getNivelUsuario().isEmpty()){
            Log.i("script","direcionarUsuario() nivelUsuario != null");
            if (user.getNivelUsuario().equals("1")){
                Log.i("script","direcionarUsuario() nivelUsuario == 1 callConfigInicial");
                Bundle bundle = user.remodelUser();
                getActivity().callConfiguracaoIncialActivity2(bundle);
            }else if ((user.getNivelUsuario().equals("2") || user.getNivelUsuario().equals("3")) && (user.getTipoUsuario() != null && !user.getTipoUsuario().isEmpty()) && (user.getCodUnico() != null && !user.getCodUnico().isEmpty()) ){
                Log.i("script","direcionarUsuario() tipoUsuario != null codUnico != null");
                if (user.getNivelUsuario().equals("2")){
                    Log.i("script","direcionarUsuario() nivelUsuario == 2");
                    if (buscarEtapaConfigOnlineFinalizado){
                        Log.i("script","direcionarUsuario() etapaConfigOk callConfigInicial");
                        Bundle bundle = user.remodelUser();
                        getActivity().callConfiguracaoIncialActivity2(bundle);
                    }
                }else if (user.getNivelUsuario().equals("3")){
                    Log.i("script","direcionarUsuario() nivelUsuario == 3 callHome");
                    Bundle bundle = user.remodelUser();
                    getActivity().callHomeActivity(bundle);
                }
            }
        }
        //verifica se as etapas foram concluidas porem sem receber dados
        if (buscasOnlineConcluida){
            Log.i("script","direcionarUsuario() BOTO call erroAoBuscar");
            Bundle bundle = user.remodelUser();
            getActivity().callErroBuscarOnlineActivity(bundle);
        } else if (buscarNivelUsuarioOnlineIniciado && buscarNivelUsuarioOnlineFinalizado && (user.getNivelUsuario() == null || user.getNivelUsuario().isEmpty())){
            Log.i("script","direcionarUsuario() buscarNivelUsuarioOnline concluido nivelUsuario == null call erroAoBuscar");
            Bundle bundle = user.remodelUser();
            getActivity().callErroBuscarOnlineActivity(bundle);
        }else if (buscarTipoUsuarioOnlineIniciado && buscarTipoUsuarioOnlineFinalizado && (user.getTipoUsuario() == null || user.getTipoUsuario().isEmpty()) && (user.getNivelUsuario() != null && !user.getNivelUsuario().isEmpty() && (user.getNivelUsuario().equals("2") || user.getNivelUsuario().equals("3")))){
            Log.i("script","direcionarUsuario() buscarTipoUsuarioOnline concluido tipoUsuario == null call erroAoBuscar");
            Bundle bundle = user.remodelUser();
            getActivity().callErroBuscarOnlineActivity(bundle);
        }else if (buscarCodUnicoOnlineIniciado && buscarCodUnicoOnlineFinalizado && (user.getCodUnico() == null || user.getCodUnico().isEmpty())){
            Log.i("script","direcionarUsuario() buscarCodUnicoOnline concluido CodUnico == null call erroAoBuscar");
            Bundle bundle = user.remodelUser();
            getActivity().callErroBuscarOnlineActivity(bundle);
        }


    }


    private void buscarEtapaConfigOnline(){
        if (!buscarEtapaConfigOnlineIniciado){
            if (user.getTipoUsuario().equals("salão")){
                if (user.getCodUnico() != null && !user.getCodUnico().isEmpty()){
                    // verificacao etapa funcinamento
                    if (user.getEtapaFuncionamentoOK() == null || !user.getEtapaFuncionamentoOK()){
                        if (!buscarEtapaConfigFuncionamentoOnlineIniciado){
                            if (vELEtapaConfigFuncionamento == null){
                                vELEtapaConfigFuncionamento = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue(Boolean.class) != null){
                                            Log.i("script","buscarEtapaConfigOnline() funcionamento salao data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                                            user.setEtapaFuncionamentoOK((Boolean) dataSnapshot.getValue());
                                            saveSPRefBoolean(getActivity().getApplicationContext(),"funcionamentoOK",(Boolean) dataSnapshot.getValue());
                                            buscarEtapaConfigFuncionamentoOnlineFinalizado = true;
                                            if (buscarEtapaConfigFuncionamentoOnlineFinalizado && buscarEtapaConfigServicosOnlineFinalizado && buscarEtapaConfigCabeleireirosOnlineFinalizado){
                                                buscarEtapaConfigOnlineFinalizado = true;
                                            }
                                        }else{
                                            Log.i("script","buscarEtapaConfigOnline() funcionamento data dataSnapshot == Null");
                                            buscarEtapaConfigFuncionamentoOnlineFinalizado = true;
                                            if (buscarEtapaConfigFuncionamentoOnlineFinalizado && buscarEtapaConfigServicosOnlineFinalizado && buscarEtapaConfigCabeleireirosOnlineFinalizado){
                                                buscarEtapaConfigOnlineFinalizado = true;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.i("script","buscarEtapaConfigOnline() funcionamento onCancelled ");
                                        buscarEtapaConfigFuncionamentoOnlineFinalizado = true;
                                        if (buscarEtapaConfigFuncionamentoOnlineFinalizado && buscarEtapaConfigServicosOnlineFinalizado && buscarEtapaConfigCabeleireirosOnlineFinalizado){
                                            buscarEtapaConfigOnlineFinalizado = true;
                                        }
                                    }
                                };
                            }
                            if (firebaseEtapaConfigFuncionamento == null){
                                firebaseEtapaConfigFuncionamento = LibraryClass.getFirebase().child("salões").child(user.getCodUnico()).child("configurações").child("etapas").child("funcionamentoOK");
                            }
                            firebaseEtapaConfigFuncionamento.addValueEventListener(vELEtapaConfigFuncionamento);
                            buscarEtapaConfigFuncionamentoOnlineIniciado = true;
                        }
                    }
                    // verificacao etapa servicos
                    if (user.getEtapaServicosOK() == null || !user.getEtapaServicosOK()){
                        if (!buscarEtapaConfigServicosOnlineIniciado){
                            if (vELEtapaConfigServicos == null){
                                vELEtapaConfigServicos = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue(Boolean.class) != null){
                                            Log.i("script","buscarEtapaConfigOnline() Servicos salao data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                                            user.setEtapaServicosOK((Boolean) dataSnapshot.getValue());
                                            saveSPRefBoolean(getActivity().getApplicationContext(),"servicosOK",(Boolean) dataSnapshot.getValue());
                                            buscarEtapaConfigServicosOnlineFinalizado = true;
                                            if (buscarEtapaConfigFuncionamentoOnlineFinalizado && buscarEtapaConfigServicosOnlineFinalizado && buscarEtapaConfigCabeleireirosOnlineFinalizado){
                                                buscarEtapaConfigOnlineFinalizado = true;
                                            }
                                        }else{
                                            Log.i("script","buscarEtapaConfigOnline() Servicos data dataSnapshot == Null");
                                            buscarEtapaConfigServicosOnlineFinalizado = true;
                                            if (buscarEtapaConfigFuncionamentoOnlineFinalizado && buscarEtapaConfigServicosOnlineFinalizado && buscarEtapaConfigCabeleireirosOnlineFinalizado){
                                                buscarEtapaConfigOnlineFinalizado = true;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.i("script","buscarEtapaConfigOnline() Servicos onCancelled ");
                                        buscarEtapaConfigServicosOnlineFinalizado = true;
                                        if (buscarEtapaConfigFuncionamentoOnlineFinalizado && buscarEtapaConfigServicosOnlineFinalizado && buscarEtapaConfigCabeleireirosOnlineFinalizado){
                                            buscarEtapaConfigOnlineFinalizado = true;
                                        }
                                    }
                                };
                            }
                            if (firebaseEtapaConfigServicos == null){
                                firebaseEtapaConfigServicos = LibraryClass.getFirebase().child("salões").child(user.getCodUnico()).child("configurações").child("etapas").child("servicosOK");
                            }
                            firebaseEtapaConfigServicos.addValueEventListener(vELEtapaConfigServicos);
                            buscarEtapaConfigServicosOnlineIniciado = true;
                        }
                    }
                    // verificacao etapa cabeleireiros
                    if (user.getEtapaCabeleireirosOK() == null || !user.getEtapaCabeleireirosOK()){
                        if (!buscarEtapaConfigCabeleireirosOnlineIniciado){
                            if (vELEtapaConfigCabeleireiros == null){
                                vELEtapaConfigCabeleireiros = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue(Boolean.class) != null){
                                            Log.i("script","buscarEtapaConfigOnline() Cabeleireiros salao data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                                            user.setEtapaCabeleireirosOK((Boolean) dataSnapshot.getValue());
                                            saveSPRefBoolean(getActivity().getApplicationContext(),"cabeleireirosOK",(Boolean) dataSnapshot.getValue());
                                            buscarEtapaConfigCabeleireirosOnlineFinalizado = true;
                                            if (buscarEtapaConfigFuncionamentoOnlineFinalizado && buscarEtapaConfigServicosOnlineFinalizado && buscarEtapaConfigCabeleireirosOnlineFinalizado){
                                                buscarEtapaConfigOnlineFinalizado = true;
                                            }
                                        }else{
                                            Log.i("script","buscarEtapaConfigOnline() Servicos data dataSnapshot == Null");
                                            buscarEtapaConfigCabeleireirosOnlineFinalizado = true;
                                            if (buscarEtapaConfigFuncionamentoOnlineFinalizado && buscarEtapaConfigServicosOnlineFinalizado && buscarEtapaConfigCabeleireirosOnlineFinalizado){
                                                buscarEtapaConfigOnlineFinalizado = true;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.i("script","buscarEtapaConfigOnline() Cabeleireiros onCancelled ");
                                        buscarEtapaConfigCabeleireirosOnlineFinalizado = true;
                                        if (buscarEtapaConfigFuncionamentoOnlineFinalizado && buscarEtapaConfigServicosOnlineFinalizado && buscarEtapaConfigCabeleireirosOnlineFinalizado){
                                            buscarEtapaConfigOnlineFinalizado = true;
                                        }
                                    }
                                };
                            }
                            if (firebaseEtapaConfigCabeleireiros == null){
                                firebaseEtapaConfigCabeleireiros = LibraryClass.getFirebase().child("salões").child(user.getCodUnico()).child("configurações").child("etapas").child("cabeleireirosOK");
                            }
                            firebaseEtapaConfigCabeleireiros.addValueEventListener(vELEtapaConfigCabeleireiros);
                            buscarEtapaConfigCabeleireirosOnlineIniciado = true;
                        }
                    }
                    buscarEtapaConfigOnlineIniciado = true;
                }
            }else if (user.getTipoUsuario().equals("cabeleireiro")){
                if (user.getCodUnico() != null && !user.getCodUnico().isEmpty()){
                    //TODO implementar buscar etapa online cabeleirerio
                    buscarEtapaConfigOnlineIniciado = true;
                }
            }else if (user.getTipoUsuario().equals("cliente")){
                //TODO implementar buscar etapa online cliente
                buscarEtapaConfigOnlineIniciado = true;
            }
        }

    }

    private void buscarCodUnicoOnline(){
        if (!buscarCodUnicoOnlineIniciado){
            if (vELCodUnico == null){
                vELCodUnico = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(String.class) != null){
                            Log.i("script","buscarCodUnicoOnline() data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                            user.setCodUnico(String.valueOf(dataSnapshot.getValue()));
                            saveSPRefString(getActivity().getApplicationContext(),"codUnico",String.valueOf(dataSnapshot.getValue()));
                            buscarCodUnicoOnlineFinalizado = true;
                            handlerSplashScreenSecondThread.post(runnableIBO);
                        }else{
                            Log.i("script","buscarCodUnicoOnline() data dataSnapshot == Null");
                            buscarCodUnicoOnlineFinalizado = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","buscarCodUnicoOnline() onCancelled ");
                        buscarCodUnicoOnlineFinalizado = true;
                    }
                };
            }
            if (firebaseCodUnico == null){
                firebaseCodUnico = LibraryClass.getFirebase().child("users").child(user.getId()).child("codUnico");
            }
            firebaseCodUnico.addValueEventListener(vELCodUnico);
            buscarCodUnicoOnlineIniciado = true;
        }

    }

    private void buscarNivelUsuarioOnline(){
        if (!buscarNivelUsuarioOnlineIniciado){
            if (vELNivelUsuario == null){
                Log.i("script","vELNivelUsuario == null ");
                vELNivelUsuario = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(String.class) != null){
                            Log.i("script","buscarNivelUsuarioOnline() data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                            user.setNivelUsuario(String.valueOf(dataSnapshot.getValue()));
                            saveSPRefString(getActivity().getApplicationContext(),"nivelUsuario",String.valueOf(dataSnapshot.getValue()));
                            buscarNivelUsuarioOnlineFinalizado = true;
                            handlerSplashScreenSecondThread.post(runnableIBO);
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
            buscarNivelUsuarioOnlineIniciado = true;
        }

    }

    private void buscarTipoUsuarioOnline(){
        if (!buscarTipoUsuarioOnlineIniciado){
            if (vELTipoUsuario == null){
                vELTipoUsuario = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(String.class) != null){
                            Log.i("script","buscarTipoUsuarioOnline() data dataSnapshot "+String.valueOf(dataSnapshot.getValue()));
                            user.setNivelUsuario(String.valueOf(dataSnapshot.getValue()));
                            saveSPRefString(getActivity().getApplicationContext(),"nivelUsuario",String.valueOf(dataSnapshot.getValue()));
                            buscarTipoUsuarioOnlineFinalizado = true;
                            handlerSplashScreenSecondThread.post(runnableIBO);
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
            buscarTipoUsuarioOnlineIniciado = true;
        }

    }

    public void removerVEL(){
        if(vELNivelUsuario != null && firebaseNivelUsuario != null){
            firebaseNivelUsuario.removeEventListener(vELNivelUsuario);
            firebaseNivelUsuario = null;
            vELNivelUsuario = null;
        }
        if(vELTipoUsuario != null && firebaseTipoUsuario != null){
            firebaseTipoUsuario.removeEventListener(vELTipoUsuario);
            firebaseTipoUsuario = null;
            vELTipoUsuario = null;
        }
        if(vELCodUnico != null && firebaseCodUnico != null){
            firebaseCodUnico.removeEventListener(vELCodUnico);
            firebaseCodUnico = null;
            vELCodUnico = null;
        }
        if(vELEtapaConfigFuncionamento != null && firebaseEtapaConfigFuncionamento != null){
            firebaseEtapaConfigFuncionamento.removeEventListener(vELEtapaConfigFuncionamento);
            firebaseEtapaConfigFuncionamento = null;
            vELEtapaConfigFuncionamento = null;
        }
        if(vELEtapaConfigServicos != null && firebaseEtapaConfigServicos != null){
            firebaseEtapaConfigServicos.removeEventListener(vELEtapaConfigServicos);
            firebaseEtapaConfigServicos = null;
            vELEtapaConfigServicos = null;
        }
        if(vELEtapaConfigCabeleireiros != null && firebaseEtapaConfigCabeleireiros != null){
            firebaseEtapaConfigCabeleireiros.removeEventListener(vELEtapaConfigCabeleireiros);
            firebaseEtapaConfigCabeleireiros = null;
            vELEtapaConfigCabeleireiros = null;
        }
    }







    /// OLD
    /*private void iniciarBuscasOnline() {
        if (runnableIBO == null){
            runnableIBO = new Runnable() {
                @Override
                public void run() {
                    //verificacao nivel usuario
                    if(!buscarNivelUsuarioOnlineIniciado){
                        buscarNivelUsuarioOnline();
                    }
                    //verificacao tipo usuario
                    if (user.getNivelUsuario()!= null && !user.getNivelUsuario().isEmpty()){
                        if (user.getTipoUsuario() != null && !user.getTipoUsuario().isEmpty()){
                            // TODO implementar direcionar usuario tela correta
                        }else{
                            if (!getSPRefString(activity.getApplicationContext(),"tipoUsuario").equals("")){
                                user.setTipoUsuario(getSPRefString(activity.getApplicationContext(),"tipoUsuario"));
                                verificarEtapaUsuario();
                            }else{
                                if (!buscarTipoUsuarioOnlineIniciado){
                                    buscarTipoUsuarioOnline();
                                }
                            }
                        }
                    }else{
                        //TODO nivel usuario null apos verificacao online direcionar usuario para tela reconectar informaçoes
                    }
                    //verificacao etapa usuario
                    if (user.getNivelUsuario()!= null && !user.getNivelUsuario().isEmpty() && user.getTipoUsuario() != null && !user.getTipoUsuario().isEmpty()){
                        verificarEtapaUsuario();
                    }else{
                        //TODO nivel ou tipo null apos verificacao online direcionar usuario para tela reconectar informaçoes
                    }

                }
            };
            handlerSplashScreenSecondThread.post(runnableIBO);
        }

    }*/

    /*private void buscarNivelUsuarioOnline(){
        buscarNivelUsuarioOnlineIniciado = true;
        if (runnableBNUOTO == null){
            runnableBNUOTO = new Runnable() {
                @Override
                public void run() {
                    //TODO liberar tarefa em aguarde
                }
            };
            handlerSplashScreenSecondThread.postDelayed(runnableBNUOTO,(4500));
        }
        if (runnableBNUO == null){
            runnableBNUO = new Runnable() {
                @Override
                public void run() {
                    firebaseNivelUsuario = LibraryClass.getFirebase().child("users").child(user.getId()).child("nivelUsuario");
                    vELNivelUsuario = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(String.class) == null){
                                Log.i("teste","dataSnapshot nivelUsuario == null");
                                //TODO liberar tarefa em aguarde
                            }else {
                                Log.i("teste","dataSnapshot nivelUsuario != null");
                                user.setNivelUsuario(String.valueOf(dataSnapshot.getValue()));
                                saveSPRefString(activity.getApplicationContext(),"nivelUsuario",user.getNivelUsuario());
                                //TODO liberar tarefa em aguarde
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("teste","nivelUsuario ValueEventListener  onCancelled");
                            //TODO liberar tarefa em aguarde
                        }
                    };
                    firebaseNivelUsuario.addValueEventListener(vELNivelUsuario);
                }
            };
            handlerSplashScreenSecondThread.post(runnableBNUO);
        }
        //TODO fazer tarefa atual aguardar
    }*/

   /* private void buscarTipoUsuarioOnline(){
        buscarTipoUsuarioOnlineIniciado = true;
        if (runnableBTUOTO == null){
            runnableBTUOTO = new Runnable() {
                @Override
                public void run() {
                    //TODO liberar tarefa em aguarde
                }
            };
            handlerSplashScreenSecondThread.postDelayed(runnableBTUOTO,(4500));
        }
        if (runnableBTUO == null){
            runnableBTUO = new Runnable() {
                @Override
                public void run() {
                    firebaseTipoUsuario = LibraryClass.getFirebase().child("users").child(user.getId()).child("tipoUsuario");
                    vELTipoUsuario = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(String.class) == null){
                                Log.i("teste","dataSnapshot nivelUsuario == null");
                                //TODO liberar tarefa em aguarde
                            }else {
                                Log.i("teste","dataSnapshot nivelUsuario != null");
                                user.setTipoUsuario(String.valueOf(dataSnapshot.getValue()));
                                saveSPRefString(activity.getApplicationContext(),"tipoUsuario",user.getTipoUsuario());
                                //TODO liberar tarefa em aguarde
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("teste","nivelUsuario ValueEventListener  onCancelled");
                            //TODO liberar tarefa em aguarde
                        }
                    };
                    firebaseTipoUsuario.addValueEventListener(vELTipoUsuario);
                }
            };
            handlerSplashScreenSecondThread.post(runnableBTUO);
        }
        //TODO fazer tarefa atual aguardar
    }*/

    /*private void verificarEtapaUsuario(){
        switch (user.getTipoUsuario()){
            case "cliente":
                //TODO implementar verificar estapa cliente
                break;
            case "salão":
                if (user.getNivelUsuario().equals("1")){
                    Bundle bundleUser = user.remodelUser();
                    getActivity().callConfiguracaoIncialActivity2(bundleUser);
                }else if (user.getNivelUsuario().equals("2")){
                    verifica
                }else if (user.getNivelUsuario().equals("3")){
                    getActivity().callHomeActivity();
                }else{
                    Log.e("script","verificarEtapaUsuario() tipoUsuario salao, nivel usuario invalido");
                }
                break;
            case "cabeleireiro":
                //TODO implementar verificar estapa cabeleireiro
                break;
            default:
                Log.e("script","verificarEtapaUsuario() tipoUsuario defaut");
                break;
        }
    }*/



    //GETTERS
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
