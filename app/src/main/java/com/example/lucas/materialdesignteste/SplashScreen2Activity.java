package com.example.lucas.materialdesignteste;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lucas.materialdesignteste.domain.User;
import com.example.lucas.materialdesignteste.threads.SplashScreenSecondThread2;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

public class SplashScreen2Activity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener {
    //VIEWS
    private ImageView splashLogoMov5;
    private ImageView splashLogoSalao20;
    private TextView labelPoweredBy;
    private TextView labelCarregando;
    private ProgressBar progressBarSalao20;

    private Handler handlerUIThread;
    private Thread secondThread;

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;

    //CONTROLE
    private Boolean iniciarBuscarOnline;
    private Boolean splashIniciada;
    private Boolean splashCompleta;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","onCreate() SplashScreen2Activity");
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();
        initViews();
        initUser();
        initControles();
        initThreads();
        verifyLogged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","onStart() SplashScreen2Activity");
        if (iniciarBuscarOnline){
            iniciarBuscasOnline();
        }else{
            Log.i("script","onStart()  apenas splash");
            splashScreenInicial();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() SplashScreen2Activity");
        if( mAuthListener != null ){
            mAuth.removeAuthStateListener( mAuthListener );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","onDestroy() SplashScreen2Activity");
        handlerUIThread.removeCallbacksAndMessages(null);
        if (secondThread != null){
            if (secondThread.isAlive()){
                Log.i("script","onDestroy() destruir secondThread");
                //secondThread.interrupt();
                if(secondThread instanceof SplashScreenSecondThread2){
                    ((SplashScreenSecondThread2) secondThread).killThread();
                    if (secondThread.isAlive())
                        Log.i("script","onDestroy()  secondThread viva");
                    else
                        Log.i("script","onDestroy()  secondThread morta");
                }
            }
        }
        if( mAuthListener != null ){
            mAuth.removeAuthStateListener( mAuthListener );
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("noNull","noNull");
    }

    @Override
    protected void initViews() {
        splashLogoMov5 = (ImageView)findViewById(R.id.splash_logo_mov5);
        labelPoweredBy = (TextView) findViewById(R.id.label_powered_by);
        labelCarregando = (TextView) findViewById(R.id.label_carregando);
        splashLogoSalao20 = (ImageView) findViewById(R.id.splash_logo_salao20);
        progressBarSalao20 = (ProgressBar) findViewById(R.id.progress_splash_salao20);
    }

    @Override
    protected void initUser() {
        user = new User();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FirebaseCrash
                .report(
                        new Exception(
                                connectionResult.getErrorCode()+": "+connectionResult.getErrorMessage()
                        )
                );
        showSnackbar( connectionResult.getErrorMessage() );
    }

    private void initThreads(){
        secondThread = new SplashScreenSecondThread2(this);
        handlerUIThread = new Handler();
    }

    private void initControles() {
        this.iniciarBuscarOnline = false;
        this.splashIniciada = false;
        this.splashCompleta = false;
    }

    //FIREBASE
    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("script","getFirebaseAuthResultHandler()");

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
                    Log.i("script","getFirebaseAuthResultHandler() uid != null");
                    verificarNivelUsuario();
                }else{
                    Log.i("script","getFirebaseAuthResultHandler() uid == null");
                    verificarNivelUsuario();
                }

            }
        };
        return( callback );
    }

    private boolean isNameOk( User user, FirebaseUser firebaseUser ){
        return(
                user.getName() != null
                        || firebaseUser.getDisplayName() != null
        );
    }

    private void verifyLogged(){
        Log.i("script","verifyLogged()");
        if( mAuth.getCurrentUser() != null ){
            Log.i("script","verifyLogged() mAuth.getCurrentUser() != null");
            if (mAuth.getCurrentUser().getUid() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                Log.i("script","verifyLogged() set UID");
                user.setId(mAuth.getCurrentUser().getUid());
                verificarNivelUsuario();
            }else {
                Log.i("script","verifyLogged()  UID = null");
                verificarNivelUsuario();
            }
        }
        else{
            Log.i("script","verifyLogged() mAuth.getCurrentUser() == null");
            mAuth.addAuthStateListener( mAuthListener );
            callLoginActivity();
        }
    }

    private  void verificarNivelUsuario(){
        Log.i("script","verificarNivelUsuario() verificacao offline iniciada");
        if (user.getNivelUsuario() == null || user.getNivelUsuario().isEmpty()){
            Log.i("script","verificarNivelUsuario() nivelUsuario null verificar dados salvos no aparelho");
            if (user.getId() != null && !user.getId().isEmpty()){
                REF = REF + user.getId();
            }
            if (!getSPRefString(getApplicationContext(),"nivelUsuario").equals("")){
                Log.i("script","verificarNivelUsuario() nivelUsuario encontrado no aparelho");
                user.setNivelUsuario(getSPRefString(getApplicationContext(),"nivelUsuario"));
                direcionarUsuario();
            }else{
                Log.i("script","verificarNivelUsuario() nivelUsuario não encontrado no aparelho");
                iniciarBuscarOnline = true;
            }
        }else{
            Log.i("script","verificarNivelUsuario() nivelUsuario != null");
            direcionarUsuario();
        }
    }

    private void direcionarUsuario(){
        switch (user.getNivelUsuario()){
            //1 tipo usuario nao definido  2 confguracao basica incompleta  3 usuario basico
            case "1":
                //FRIST LOGIN encerra processo no onCreate e passa para onStart executa a splash screen e chama configuracaoInicialActivity
                break;
            case"2":
                verificarDadosCompletos();
                break;
            case"3":
                verificarDadosCompletos();
                break;
            default:
                Log.e("script","nivel de usuario invalido");
        }
    }

    private void verificarDadosCompletos(){
        Log.i("script","verificarDadosCompletos()");
        if (user.getTipoUsuario() != null && !user.getTipoUsuario().isEmpty()){
            verificarEtapaUsuario();
        }else{
            Log.i("script","verificarDadosCompletos() tipoUsuario == null");
            if (!getSPRefString(this.getApplicationContext(),"tipoUsuario").equals("")){
                Log.i("script","verificarDadosCompletos() tipoUsuario encontrado nos dados salvos no celular");
                user.setTipoUsuario(getSPRefString(this.getApplicationContext(),"tipoUsuario"));
                verificarEtapaUsuario();
            }else{
                //encerra processo no onCreate e passa para onStart executa iniciarBuscaOnline()
                iniciarBuscarOnline = true;
            }
        }
    }

    private void verificarEtapaUsuario(){
        //chamado apenas para nivelUsuario 2 ou 3
        Log.i("script","verificarEtapaUsuario()");
        switch (user.getTipoUsuario()){
            case "cliente":
                if (user.getNivelUsuario().equals("2")){
                    //TODO implementar verificacao etapas offline
                    //encerra processo no onCreate e passa para onStart executa iniciarBuscaOnline()
                    iniciarBuscarOnline = true;
                }else if (user.getNivelUsuario().equals("3")){
                    Bundle bundleUser = user.remodelUser();
                    callHomeActivity(bundleUser);
                }
                break;
            case "salão":
                if (user.getNivelUsuario().equals("2")){
                    ArrayList<String> arrayList = new ArrayList<String>();
                    if (user.getEtapaFuncionamentoOK() != null && user.getEtapaFuncionamentoOK()){
                        arrayList.add("funcionamentoOK");
                    } else if (getSPRefBoolean(getApplicationContext(),"funcionamentoOK")){
                        user.setEtapaFuncionamentoOK(true);
                        arrayList.add("funcionamentoOK");
                    }
                    if (user.getEtapaServicosOK() != null && user.getEtapaServicosOK()){
                        arrayList.add("servicosOK");
                    } else if (getSPRefBoolean(getApplicationContext(),"servicosOK")){
                        user.setEtapaServicosOK(true);
                        arrayList.add("servicosOK");
                    }
                    if (user.getEtapaCabeleireirosOK() != null && user.getEtapaCabeleireirosOK()){
                        arrayList.add("cabeleireirosOK");
                    } else if (getSPRefBoolean(getApplicationContext(),"cabeleireirosOK")){
                        user.setEtapaCabeleireirosOK(true);
                        arrayList.add("cabeleireirosOK");
                    }
                    if ((user.getCodUnico() == null || user.getCodUnico().isEmpty()) && !getSPRefString(getApplicationContext(),"codUnico").equals("")){
                        user.setCodUnico(getSPRefString(getApplicationContext(),"codUnico"));
                    }
                    if (arrayList.size() == 2 && user.getCodUnico() != null && !user.getCodUnico().isEmpty()){
                        Bundle bundleUser = user.remodelUser();
                        callConfiguracaoIncialActivity2(bundleUser);
                    }else {
                        //encerra processo no onCreate e passa para onStart executa iniciarBuscaOnline()
                        iniciarBuscarOnline = true;
                    }
                }else if (user.getNivelUsuario().equals("3")){
                    Bundle bundleUser = user.remodelUser();
                    callHomeActivity(bundleUser);
                }
                break;
            case "cabeleireiro":
                if (user.getNivelUsuario().equals("2")){
                    //TODO implementar verificacao etapas offline
                    if ((user.getCodUnico() == null || user.getCodUnico().isEmpty()) && !getSPRefString(getApplicationContext(),"codUnico").equals("")){
                        user.setCodUnico(getSPRefString(getApplicationContext(),"codUnico"));
                    }
                    //encerra processo no onCreate e passa para onStart executa iniciarBuscaOnline()
                    iniciarBuscarOnline = true;
                }else if (user.getNivelUsuario().equals("3")){
                    Bundle bundleUser = user.remodelUser();
                    callHomeActivity(bundleUser);
                }
                break;
            default:
                Log.e("script","tipo de usuario invalido");
        }
    }

    private void iniciarBuscasOnline(){
        Log.i("script"," iniciarBuscasOnline");
        if (!secondThread.isAlive()){
            secondThread.start();
        }
        if (!splashIniciada){
            splashScreenInicial();
        }
    }

    private void splashScreenInicial(){
        Log.i("script","splashScreenInicial() splashChamada");

        int tempoAnimacao = 2000;
        final AlphaAnimation animation1 = new AlphaAnimation(0.0f,1.0f);
        animation1.setDuration(tempoAnimacao);
        animation1.setFillAfter(true);

        final AlphaAnimation animation1Reverse = new AlphaAnimation(1.0f,0.0f);
        animation1Reverse.setDuration(tempoAnimacao);
        animation1Reverse.setFillAfter(true);

        splashLogoMov5.setVisibility(View.VISIBLE);
        splashLogoMov5.startAnimation(animation1);

        labelPoweredBy.setVisibility(View.VISIBLE);
        labelPoweredBy.startAnimation(animation1);


        handlerUIThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("script","splashScreenInicial() splash iniciada");
                splashLogoMov5.startAnimation(animation1Reverse);
                labelPoweredBy.startAnimation(animation1Reverse);
            }
        },(tempoAnimacao+250));

        handlerUIThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                splashLogoSalao20.setVisibility(View.VISIBLE);
                splashLogoSalao20.startAnimation(animation1);
            }
        },(4500));

        handlerUIThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("script","splashScreenInicial() splash completa");
                splashCompleta = true;
                if (!iniciarBuscarOnline){
                    //FRIST LOGIN
                    Bundle bundleUser = user.remodelUser();
                    callConfiguracaoIncialActivity2(bundleUser);
                }
            }
        },(7000));

        splashIniciada = true;

    }

    //GETTERS AND SETTERS
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getSplashCompleta() {
        return splashCompleta;
    }



}
