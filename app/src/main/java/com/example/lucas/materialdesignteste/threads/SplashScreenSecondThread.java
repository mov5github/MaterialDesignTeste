package com.example.lucas.materialdesignteste.threads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lucas.materialdesignteste.LoginActivity;
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

public class SplashScreenSecondThread extends Thread implements GoogleApiClient.OnConnectionFailedListener{
    private String REF_SALAO;

    public Handler handlerSplashScreenSecondThread;
    private Activity activity;

    private ArrayList<String> etapasConfigResult;

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;

    //FIREBASE BUSCAS
    private DatabaseReference firebase1 = null;
    private ValueEventListener vEL1 = null;
    private DatabaseReference firebase2 = null;
    private ValueEventListener vEL2 = null;
    private DatabaseReference firebase3 = null;
    private ValueEventListener vEL3 = null;
    private DatabaseReference firebase4 = null;
    private ValueEventListener vEL4 = null;
    private DatabaseReference firebase5 = null;
    private ValueEventListener vEL5 = null;

    //CONTROLE
    private boolean verificacaoTipoUsuarioOnlineCompleta = false;
    private boolean verificacaoTipoUsuarioOnlineTimeOut = false;
    private boolean verificacaoTipoUsuarioOnlineIniciado = false;
    private boolean verificacaoCodUnicoOnlineAvulsaCompleta = false;
    private boolean verificacaoCodUnicoOnlineAvulsaTimeOut = false;
    private boolean verificacaoCodUnicoOnlineAvulsaIniciado = false;
    private boolean verificacaoEtapaConfigCompleta = false;
    private boolean verificacaoEtapaConfigOnlineIniciado = false;

    //CONTROLE 2

    //RUNNABLE
    private Runnable runnableVUL;
    private Runnable runnableNA;
    private Runnable runnableNA2;
    private Runnable runnableVTUOTO;
    private Runnable runnableVCUOATO;
    private Runnable runnableVECOTO;

    public SplashScreenSecondThread(Activity activity){
        this.activity = activity;
        if (activity instanceof SplashScreenActivity){
            //this.REF_SALAO = ((SplashScreenActivity)activity).getREF_SALAO();
        }
    }


    @Override
    public void run() {
        super.run();
        Looper.prepare();
        handlerSplashScreenSecondThread = new Handler();
        initUser();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();
        verificarUsuarioLogado();
        Looper.loop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FirebaseCrash
                .report(
                        new Exception(
                                connectionResult.getErrorCode()+": "+connectionResult.getErrorMessage()
                        )
                );
        if (activity instanceof SplashScreenActivity){
            ((SplashScreenActivity)activity).showSnackbar( connectionResult.getErrorMessage() );
        }
    }

    public void parar() {
        handlerSplashScreenSecondThread.post(new Runnable() {
            @Override
            public void run() {
                handlerSplashScreenSecondThread.removeCallbacksAndMessages(null);
                Looper.myLooper().quit();
            }
        });
    }

    //FIREBASE
    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("teste","getFirebaseAuthResultHandler()");

        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if( userFirebase == null ){
                    return;
                }

                if( user.getId() == null
                        && isNameOk( user, userFirebase ) ){

                    user.setId( userFirebase.getUid() );
                    user.setNameIfNull( userFirebase.getDisplayName() );
                    user.setEmailIfNull( userFirebase.getEmail() );
                    user.saveDB();
                }
                if (userFirebase.getUid()!= null && !userFirebase.getUid().isEmpty()){
                    Log.i("teste","getFirebaseAuthResultHandler() uid != null");
                    nextActivity();
                }else{
                    Log.i("teste","getFirebaseAuthResultHandler() uid == null");
                    nextActivity();

                }

            }
        };
        return( callback );
    }

    private void initUser() {
        user = new User();
    }

    private boolean isNameOk( User user, FirebaseUser firebaseUser ){
        return(
                user.getName() != null
                        || firebaseUser.getDisplayName() != null
        );
    }

    //METODOS EXECUTAVEIS ALL TREADS

    //METODOS BASE
    private void verificarUsuarioLogado() {
        Log.i("teste","verificarUsuarioLogado()");
        final int maxTempoVerificacao = 11000;
        runnableVUL = new Runnable() {
            @Override
            public void run() {
                Log.i("teste","verificarUsuarioLogado secondThread runnable principal run()");
                verifyLogged();
            }
        };
        handlerSplashScreenSecondThread.post(runnableVUL);
    }

    private void verifyLogged(){
        Log.i("teste","verifyLogged()");
        if( mAuth.getCurrentUser() != null ){
            Log.i("teste","verifyLogged() mAuth.getCurrentUser() != null");
            if (mAuth.getCurrentUser().getUid() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                user.setId(mAuth.getCurrentUser().getUid());
                nextActivity();
            }else {
                nextActivity();
            }
        }
        else{
            Log.i("teste","verifyLogged() mAuth.getCurrentUser() == null");
            mAuth.addAuthStateListener( mAuthListener );
            callLoginActivity();
        }
    }

    private void verificarTipousuario() {
        Log.i("teste","verificarTipousuario()");
        if (user.getTipoUsuario() == null || user.getTipoUsuario().isEmpty()){
            if (getSPRefString(activity.getApplicationContext(),"tipousuario").equals("salão") || getSPRefString(activity.getApplicationContext(),"tipousuario").equals("cliente") || getSPRefString(activity.getApplicationContext(),"tipousuario").equals("cabeleireiro")){
                user.setTipoUsuario(getSPRefString(activity.getApplicationContext(),"tipousuario"));
            }else{
                verificarTipoUsuarioOnline();
            }
        }

    }

    private void verificarTipoUsuarioOnline() {
        Log.i("teste","verificarTipoUsuarioOnline()");
        Boolean controlRetryNextActivity = false;
        if (!verificacaoTipoUsuarioOnlineTimeOut){
            if (!verificacaoTipoUsuarioOnlineIniciado){
                if (user.getCodUnico() == null || user.getCodUnico().isEmpty()){
                    if (!getSPRefString(activity.getApplicationContext(),"codUnico").equals("")){
                        user.setCodUnico(getSPRefString(activity.getApplicationContext(),"codUnico"));
                    }else{
                        if (firebase2 == null){
                            firebase2 = LibraryClass.getFirebase().child("users").child(user.getId()).child("codUnico");
                            vEL2= new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue(String.class) == null){
                                        Log.i("teste","dataSnapshot codUnico == null");
                                        user.setCodUnico("stop");
                                        if (user.getTipoUsuario() != null && !user.getTipoUsuario().isEmpty()) {
                                            verificacaoTipoUsuarioOnlineCompleta = true;
                                        }
                                    }else {
                                        Log.i("teste","dataSnapshot codUnico != null");
                                        user.setCodUnico(String.valueOf(dataSnapshot.getValue()));
                                        saveSPRefString(activity.getApplicationContext(),"codUnico",String.valueOf(dataSnapshot.getValue()));
                                        if (user.getTipoUsuario() != null && !user.getTipoUsuario().isEmpty()) {
                                            verificacaoTipoUsuarioOnlineCompleta = true;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.i("teste","verificarTipoUsuarioOnline() codUnico ValueEventListener  onCancelled");
                                    user.setCodUnico("stop");
                                    if (user.getTipoUsuario() != null && !user.getTipoUsuario().isEmpty()) {
                                        verificacaoTipoUsuarioOnlineCompleta = true;
                                    }
                                }
                            };
                            firebase2.addValueEventListener(vEL2);
                        }
                    }
                }
                if (firebase1 == null){
                    firebase1 = LibraryClass.getFirebase().child("users").child(user.getId()).child("tipoUsuario");
                    vEL1 = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(String.class) == null){
                                Log.i("teste","dataSnapshot tipoUsuario == null");
                                user.setTipoUsuario("stop");
                                if (user.getCodUnico() != null && !user.getCodUnico().isEmpty()) {
                                    verificacaoTipoUsuarioOnlineCompleta = true;
                                }
                            }else {
                                Log.i("teste","dataSnapshot tipoUsuario != null");
                                user.setTipoUsuario(String.valueOf(dataSnapshot.getValue()));
                                saveSPRefString(activity.getApplicationContext(),"tipoUsuario",user.getTipoUsuario());
                                if (user.getCodUnico() != null && !user.getCodUnico().isEmpty()) {
                                    verificacaoTipoUsuarioOnlineCompleta = true;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("teste","verificarTipoUsuarioOnline() tipoUsuario ValueEventListener  onCancelled");
                            user.setTipoUsuario("stop");
                            if (user.getCodUnico() != null && !user.getCodUnico().isEmpty()) {
                                verificacaoTipoUsuarioOnlineCompleta = true;
                            }
                        }
                    };
                    firebase1.addValueEventListener(vEL1);
                }
                if (runnableVTUOTO == null){
                    runnableVTUOTO = new Runnable() {
                        @Override
                        public void run() {
                            Log.i("teste","verificarTipoUsuarioOnline() tempo esgotado run()");
                            if (user.getTipoUsuario() == null || user.getTipoUsuario().isEmpty()){
                                user.setTipoUsuario("stop");
                            }
                            if (user.getCodUnico() == null || user.getCodUnico().isEmpty()){
                                user.setCodUnico("stop");
                            }
                            if (vEL1 != null)
                                firebase1.removeEventListener(vEL1);
                            if (vEL2 != null )
                                firebase2.removeEventListener(vEL2);

                            verificacaoTipoUsuarioOnlineTimeOut = true;
                            verificacaoTipoUsuarioOnlineCompleta = true;

                            runnableVTUOTO = null;
                        }
                    };
                    handlerSplashScreenSecondThread.postDelayed(runnableVTUOTO,4500);
                }
                if (runnableNA == null){
                    runnableNA = new Runnable() {
                        @Override
                        public void run() {
                            nextActivity();
                        }
                    };
                    handlerSplashScreenSecondThread.post(runnableNA);
                }
                if (!verificacaoTipoUsuarioOnlineIniciado){
                    verificacaoTipoUsuarioOnlineIniciado = true;
                }
                if (runnableVUL != null){
                    handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableVUL);
                    controlRetryNextActivity = true;
                    runnableVUL = null;
                }
            }
            if (!controlRetryNextActivity){
                handlerSplashScreenSecondThread.post(runnableNA);
            }
        }else{
            Log.i("teste","verificarTipoUsuarioOnline() online ja ocorreu e nao encontrou");
            //TODO implementar verificacao online ja ocorreu e nao encontrou
        }
    }

    private String verificaEtapaConfig() {
        Log.i("teste","verificaEtapaConfig()");
        String retorno = "etapaInvalida";
        if (user.getTipoUsuario().equals("salão")){
            Log.i("teste","verificaEtapaConfig() tipoUsuario salao");
            if (getSPRefBoolean(activity.getApplicationContext(), "funcionamento") || getSPRefBoolean(activity.getApplicationContext(), "serviços") || getSPRefBoolean(activity.getApplicationContext(), "cabeleireiros")) {
                if (getSPRefBoolean(activity.getApplicationContext(), "funcionamento")) {
                    if (getSPRefBoolean(activity.getApplicationContext(), "serviços")) {
                        if (getSPRefBoolean(activity.getApplicationContext(), "cabeleireiros")) {
                            retorno = "salaoCompleto";
                        } else {
                            retorno = "cabeleireiros";
                        }
                    } else {
                        retorno = "serviços";
                    }
                } else {
                    retorno = "funcionamento";
                }
            } else {
                retorno = "online";
            }
        }else if (user.getTipoUsuario().equals("cliente")){
            //TODO implementar verificaçao etapa cliente
            Log.i("teste","verificaEtapaConfig() tipoUsuario cliente");
            retorno = "cliente";

        }else if (user.getTipoUsuario().equals("cabeleireiro")){
            //TODO implementar verificaçao etapa cabeleireiro
            Log.i("teste","verificaEtapaConfig() tipoUsuario cabeleireiro");
            retorno = "cabeleireiro";
        }
        return retorno;
    }

    private void verificaEtapaConfigOnline() {
        Log.i("teste","verificaEtapaConfigOnline()");
        verificacaoEtapaConfigOnlineIniciado = true;

        etapasConfigResult= new ArrayList<String>();
        runnableVECOTO = new Runnable() {
            @Override
            public void run() {
                Log.i("teste","verificaEtapaConfigOnline() tempo esgotado");
                etapasConfigResult.add("stop");
            }
        };
        handlerSplashScreenSecondThread.postDelayed(runnableVECOTO,(4500));

        if (user.getTipoUsuario().equals("salão")){
            Log.i("teste","verificaEtapaConfigOnline() salão");
            if (firebase3 == null)
                firebase3 = LibraryClass.getFirebase().child("salões").child(user.getCodUnico()).child("configFuncionamentoOk");
            if (firebase4 == null)
                firebase4 = LibraryClass.getFirebase().child("salões").child(user.getCodUnico()).child("configServiçosOk");
            if (firebase5 == null)
                firebase5 = LibraryClass.getFirebase().child("salões").child(user.getCodUnico()).child("configCabeleireirosOk");

            if (vEL3 == null) {
                vEL3 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Boolean.class) == null) {
                            Log.i("teste", "verificaEtapaConfigOnline() salão dataSnapshot configFuncionamentoOk null ");
                            etapasConfigResult.add("notFuncionamento");
                        } else {
                            if (dataSnapshot.getValue(Boolean.class)) {
                                Log.i("teste", "verificaEtapaConfigOnline() salão dataSnapshot configFuncionamentoOk true ");
                                etapasConfigResult.add("funcionamento");
                                saveSPRefBoolean(activity.getApplicationContext(), "funcionamento", true);
                            } else {
                                Log.i("teste", "verificaEtapaConfigOnline() salão dataSnapshot configFuncionamentoOk false ");
                                etapasConfigResult.add("notFuncionamento");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("teste", "verificaEtapaConfigOnline() salão ValueEventListener onCancelled ");
                        etapasConfigResult.add("cancelledFuncionamento");
                    }
                };
                firebase3.addValueEventListener(vEL3);
            }
            if (vEL4 == null) {
                vEL4 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Boolean.class) == null) {
                            Log.i("teste", "verificaEtapaConfigOnline() serviços dataSnapshot configServiçosOk null ");
                            etapasConfigResult.add("notServiços");
                        } else {
                            if (dataSnapshot.getValue(Boolean.class)) {
                                Log.i("teste", "verificaEtapaConfigOnline() serviços dataSnapshot configServiçosOk true ");
                                etapasConfigResult.add("serviços");
                                saveSPRefBoolean(activity.getApplicationContext(), "serviços", true);
                            } else {
                                Log.i("teste", "verificaEtapaConfigOnline() serviços dataSnapshot configServiçosOk false ");
                                etapasConfigResult.add("notServiços");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("teste", "verificaEtapaConfigOnline() serviços ValueEventListener onCancelled ");
                        etapasConfigResult.add("cancelledServiços");
                    }
                };
                firebase4.addValueEventListener(vEL4);
            }
            if (vEL5 == null) {
                vEL5 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue(Boolean.class) == null) {
                            Log.i("teste", "verificaEtapaConfigOnline() cabeleireiros dataSnapshot configCabeleireirosOk null ");
                            etapasConfigResult.add("notCabeleireiros");
                        } else {
                            if (dataSnapshot.getValue(Boolean.class)) {
                                Log.i("teste", "verificaEtapaConfigOnline() cabeleireiros dataSnapshot configCabeleireirosOk true ");
                                etapasConfigResult.add("cabeleireiros");
                                saveSPRefBoolean(activity.getApplicationContext(), "cabeleireiros", true);
                            } else {
                                Log.i("teste", "verificaEtapaConfigOnline() cabeleireiros dataSnapshot configCabeleireirosOk false ");
                                etapasConfigResult.add("notCabeleireiros");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("teste", "verificaEtapaConfigOnline() cabeleireiros ValueEventListener onCancelled ");
                        etapasConfigResult.add("cancelledCabeleireiros");
                        if (vEL5 != null) {
                            firebase5.removeEventListener(vEL5);
                        }
                    }
                };
                firebase5.addValueEventListener(vEL5);
            }

        }else if (user.getTipoUsuario().equals("cabeleireiro")) {
            //TODO implementar verificaçao etapa ONLINE cabeleireiro
            Log.i("teste", "verificaEtapaConfigOnline() tipo usuario cabeleireiro ");

        }else if(user.getTipoUsuario().equals("cliente")){
            //TODO implementar verificaçao etapa ONLINE cliente
            Log.i("teste", "verificaEtapaConfigOnline() tipo usuario cliente");

        }else{
            //TODO implementar usuario invalido
            Log.i("teste", "verificaEtapaConfigOnline() tipo usuario invalido ");
        }

    }

    //CALL ACTIVITYS
    private void callLoginActivity() {
        if (activity instanceof SplashScreenActivity){
            while (!((SplashScreenActivity)activity).getSplashCompleta()){
                Log.i("teste","nextActivity() aguardando splash completa");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ((SplashScreenActivity)activity).callLoginActivity();
        }
    }

    private void nextActivity() {
        Log.i("teste","nextActivity()");
        if (user.getId() != null && !user.getId().isEmpty()){
            Log.i("teste","nextActivity() uid != null");
            REF_SALAO = REF_SALAO+user.getId();

            if (user.getTipoUsuario() == null || user.getTipoUsuario().isEmpty()){
                verificarTipousuario();
            }else if (user.getTipoUsuario().equals("stop")){
                //TODO implementar verificaçao online completada com tipoUsuario "stop"
                if (runnableVTUOTO != null){
                    handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableVTUOTO);
                    runnableVTUOTO = null;
                }
            }else if (!verificacaoEtapaConfigOnlineIniciado){
                if (!verificacaoTipoUsuarioOnlineIniciado) {
                    if (user.getCodUnico() == null || user.getCodUnico().isEmpty()) {
                        if (!getSPRefString(activity.getApplicationContext(),"codunico").equals("")) {
                            user.setCodUnico(getSPRefString(activity.getApplicationContext(),"codunico"));
                        }else {
                            if (!verificacaoCodUnicoOnlineAvulsaCompleta) {
                                if (!verificacaoCodUnicoOnlineAvulsaIniciado) {
                                    if (firebase2 == null){
                                        firebase2 = LibraryClass.getFirebase().child("users").child(user.getId()).child("codUnico");
                                        vEL2= new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue(String.class) == null){
                                                    Log.i("teste","dataSnapshot codUnico == null");
                                                    user.setCodUnico("stop");
                                                    verificacaoCodUnicoOnlineAvulsaCompleta = true;
                                                    if (runnableVCUOATO != null) {
                                                        handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableVCUOATO);
                                                        runnableVCUOATO = null;
                                                    }
                                                }else {
                                                    Log.i("teste","dataSnapshot codUnico != null");
                                                    user.setCodUnico(String.valueOf(dataSnapshot.getValue()));
                                                    saveSPRefString(activity.getApplicationContext(),"codUnico",String.valueOf(dataSnapshot.getValue()));
                                                    verificacaoCodUnicoOnlineAvulsaCompleta = true;
                                                    if (runnableVCUOATO != null) {
                                                        handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableVCUOATO);
                                                        runnableVCUOATO = null;
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.i("teste","verificarTipoUsuarioOnline() codUnico ValueEventListener  onCancelled");
                                                user.setCodUnico("stop");
                                                verificacaoCodUnicoOnlineAvulsaCompleta = true;
                                                if (runnableVCUOATO != null) {
                                                    handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableVCUOATO);
                                                    runnableVCUOATO = null;
                                                }
                                            }
                                        };
                                        firebase2.addValueEventListener(vEL2);
                                    }
                                    if (runnableVCUOATO == null){
                                        runnableVCUOATO = new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.i("teste","verificarCodUnicoOnlineAvulsoTimeOut() tempo esgotado run()");
                                                if (user.getCodUnico() == null || user.getCodUnico().isEmpty()){
                                                    user.setCodUnico("stop");
                                                }
                                                if (vEL2 != null )
                                                    firebase2.removeEventListener(vEL2);

                                                verificacaoCodUnicoOnlineAvulsaTimeOut = true;
                                                verificacaoCodUnicoOnlineAvulsaCompleta = true;

                                                runnableVCUOATO = null;
                                            }
                                        };
                                        handlerSplashScreenSecondThread.postDelayed(runnableVTUOTO,4500);
                                    }
                                    verificacaoCodUnicoOnlineAvulsaIniciado = true;
                                }
                            }else {
                                //TODO implementar  verificacaoCodUnicoOnlineAvulsaCompleta porem nao encontrol cod unico
                                Log.i("teste","verificacaoCodUnicoOnlineAvulsaCompleta() codUnico nao encomntrado");
                                firebase2.removeEventListener(vEL2);
                                if (runnableVCUOATO != null) {
                                    handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableVCUOATO);
                                }
                            }
                        }
                    }else if (user.getCodUnico() != null && user.getCodUnico().equals("stop")) {
                        //TODO implementar codUnico == "stop"
                        Log.i("teste","verificacaoCodUnicoOnlineAvulsaCompleta() codUnico == stop");
                        if (runnableVCUOATO != null) {
                            if (vEL2 != null )
                                firebase2.removeEventListener(vEL2);
                            handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableVCUOATO);
                            runnableVCUOATO = null;
                        }
                        if (runnableNA != null) {
                            handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableNA);
                            runnableNA = null;
                        }else if (runnableNA2 != null) {
                            handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableNA2);
                            runnableNA2 = null;
                        }
                    }else {
                        Log.i("teste","tipo usuario cod unico OK");
                        if (runnableVCUOATO != null) {
                            if (vEL2 != null )
                                firebase2.removeEventListener(vEL2);
                            handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableVCUOATO);
                            runnableVCUOATO = null;
                        }
                        if (!verificacaoEtapaConfigOnlineIniciado) {
                            String etapaConfig = verificaEtapaConfig();

                            if (etapaConfig.equals("online")) {
                                verificaEtapaConfigOnline();
                            }else {
                                if (activity instanceof SplashScreenActivity){
                                    switch (etapaConfig){
                                        case "funcionamento":
                                            //((SplashScreenActivity)activity).callConfiguracaoIncialActivity("funcionamento",etapasConfigResult);
                                            break;
                                        case "serviços":
                                           // ((SplashScreenActivity)activity).callConfiguracaoIncialActivity("servicos",etapasConfigResult);
                                            break;
                                        case "cabeleireiros":
                                           // ((SplashScreenActivity)activity).callConfiguracaoIncialActivity("cabeleireiros",etapasConfigResult);
                                            break;
                                        case "salaoCompleto":
                                           // ((SplashScreenActivity)activity).callHomeActivity();
                                            break;
                                        default:
                                            //TODO implementar tela login com etapa config n identificado
                                            Log.i("teste","nextActivity() verificaEtapaConfig etapaConfig default");
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }else {
                    Log.i("teste","verificacao tipoUsuarioOnline iniciado tipo usuario OK");
                    if (verificacaoTipoUsuarioOnlineCompleta) {
                        if (!verificacaoTipoUsuarioOnlineTimeOut) {
                            firebase1.removeEventListener(vEL1);
                            firebase2.removeEventListener(vEL2);
                            vEL1 = null;
                            vEL2 = null;
                        }
                        if (runnableVTUOTO != null){
                            handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableVTUOTO);
                            runnableVTUOTO = null;
                        }
                        if (user.getCodUnico() != null && !user.getCodUnico().isEmpty() && !user.getCodUnico().equals("stop")) {
                            if (!verificacaoEtapaConfigOnlineIniciado) {
                                String etapaConfig = verificaEtapaConfig();

                                if (etapaConfig.equals("online")) {
                                    verificaEtapaConfigOnline();
                                }else {
                                    if (activity instanceof SplashScreenActivity){
                                        switch (etapaConfig){
                                            case "funcionamento":
                                               // ((SplashScreenActivity)activity).callConfiguracaoIncialActivity("funcionamento",etapasConfigResult);
                                                break;
                                            case "serviços":
                                                //((SplashScreenActivity)activity).callConfiguracaoIncialActivity("servicos",etapasConfigResult);
                                                break;
                                            case "cabeleireiros":
                                               //((SplashScreenActivity)activity).callConfiguracaoIncialActivity("cabeleireiros",etapasConfigResult);
                                                break;
                                            case "salaoCompleto":
                                               // ((SplashScreenActivity)activity).callHomeActivity();
                                                break;
                                            default:
                                                //TODO implementar tela login com etapa config n identificado
                                                Log.i("teste","nextActivity() verificaEtapaConfig etapaConfig default");
                                                break;
                                        }
                                    }
                                }
                            }
                        }else {
                            //TODO implementar verificacao online completa porem codUnico invalido
                            Log.i("teste","nextActivity() verificacao online completa porem codUnico invalido");
                        }
                    }else {
                        if (runnableNA != null) {
                            Log.i("teste","nextActivity() runnableNA to runnableNA2");
                            if (runnableNA2 == null){
                                runnableNA2 = new Runnable() {
                                    @Override
                                    public void run() {
                                        runnableNA = null;
                                        nextActivity();
                                    }
                                };
                                handlerSplashScreenSecondThread.post(runnableNA2);
                            }
                            handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableNA);
                        }else if (runnableNA2 != null) {
                            Log.i("teste","nextActivity() runnableNA to runnableNA2");
                            if (runnableNA == null){
                                runnableNA = new Runnable() {
                                    @Override
                                    public void run() {
                                        runnableNA2 = null;
                                        nextActivity();
                                    }
                                };
                                handlerSplashScreenSecondThread.post(runnableNA);
                            }
                            handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableNA2);
                        }
                    }
                }


            }else {
                Log.i("teste","tipoUsuario codUnico ok verificaEtapaConfigOnline iniciada");
                if (!etapasConfigResult.contains("stop") && !( (etapasConfigResult.contains("funcionamento")||etapasConfigResult.contains("notFuncionamento")||etapasConfigResult.contains("cancelledFuncionamento")) && (etapasConfigResult.contains("serviços")||etapasConfigResult.contains("notServiços")||etapasConfigResult.contains("cancelledServiços")) && (etapasConfigResult.contains("cabeleireiros")||etapasConfigResult.contains("notCabeleireiros")||etapasConfigResult.contains("cancelledCabeleireiros")) ) ) {
                    Log.i("teste","verificaEtapaConfigOnline() aguardando");
                    if (runnableNA != null) {
                        Log.i("teste","nextActivity() runnableNA to runnableNA2");
                        if (runnableNA2 == null){
                            runnableNA2 = new Runnable() {
                                @Override
                                public void run() {
                                    runnableNA = null;
                                    nextActivity();
                                }
                            };
                            handlerSplashScreenSecondThread.post(runnableNA2);
                        }
                        handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableNA);
                    }else if (runnableNA2 != null) {
                        Log.i("teste","nextActivity() runnableNA to runnableNA2");
                        if (runnableNA == null){
                            runnableNA = new Runnable() {
                                @Override
                                public void run() {
                                    runnableNA2 = null;
                                    nextActivity();
                                }
                            };
                            handlerSplashScreenSecondThread.post(runnableNA);
                        }
                        handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableNA2);
                    }
                }else {
                    Log.i("teste","verificaEtapaConfigOnline() concluida");
                    if (runnableVECOTO != null) {
                        handlerSplashScreenSecondThread.removeCallbacksAndMessages(runnableVECOTO);
                        runnableVECOTO = null;
                    }
                    if (vEL3 != null) {
                        firebase3.removeEventListener(vEL3);
                        vEL3 = null;
                        firebase3 = null;
                    }
                    if (vEL4 != null) {
                        firebase4.removeEventListener(vEL4);
                        vEL4 = null;
                        firebase4 = null;
                    }
                    if (vEL5 != null) {
                        firebase5.removeEventListener(vEL5);
                        vEL5 = null;
                        firebase5 = null;
                    }
                    if (getSPRefBoolean(activity.getApplicationContext(), "funcionamento") || getSPRefBoolean(activity.getApplicationContext(), "serviços") || getSPRefBoolean(activity.getApplicationContext(), "cabeleireiros")) {
                        if (getSPRefBoolean(activity.getApplicationContext(), "funcionamento")) {
                            if (getSPRefBoolean(activity.getApplicationContext(), "serviços")) {
                                if (getSPRefBoolean(activity.getApplicationContext(), "cabeleireiros")) {
                                    Log.i("teste","nextActivity() salao completo");
                                  //  ((SplashScreenActivity)activity).callHomeActivity();
                                } else {
                                    Log.i("teste","nextActivity() etapa cabeleireiros");
                                    //((SplashScreenActivity)activity).callConfiguracaoIncialActivity("cabeleireiros",etapasConfigResult);
                                }
                            } else {
                                Log.i("teste","nextActivity() etapa serviços");
                                //((SplashScreenActivity)activity).callConfiguracaoIncialActivity("serviços",etapasConfigResult);
                            }
                        } else {
                            Log.i("teste","nextActivity() etapa funcionamento");
                            //((SplashScreenActivity)activity).callConfiguracaoIncialActivity("funcionamento",etapasConfigResult);
                        }
                    }else {
                        Log.i("teste","nextActivity() nao identificado etapa Funcionamento");
                        //((SplashScreenActivity)activity).callConfiguracaoIncialActivity("etapaNãoIdentificada",etapasConfigResult);
                    }
                }

            }
            }else {
                //TODO implementar verificaEtapaConfig(verificarTipousuario(uid)) == null
                Log.i("teste","nextActivity() verificaEtapaConfig(verificarTipousuario(uid)) == null");
            }
    }

    //SHAREDPREFERENCES
    private String getSPRefString(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        String value = sp.getString(key, "");
        return( value );
    }

    private void saveSPRefString(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    private Boolean getSPRefBoolean(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        Boolean value = sp.getBoolean(key, false);
        return( value );
    }

    private void saveSPRefBoolean(Context context, String key, Boolean value ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }



}
