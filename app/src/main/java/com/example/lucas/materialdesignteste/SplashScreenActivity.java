package com.example.lucas.materialdesignteste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.example.lucas.materialdesignteste.domain.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

public class SplashScreenActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener  {
    private String REF_SALAO = "com.example.lucas.materialdesignteste";
    private Boolean splashIniciada = false;
    private Boolean splashCompleta = false;
    private ImageView splashLogoMov5;
    private ImageView splashLogoSalao20;
    private TextView labelPoweredBy;
    private TextView labelCarregando;
    ProgressBar progressBarSalao20;

    private Handler handlerSplashScreenCompleta= new Handler();
    private Handler handlerAnimationSplashSalao20= new Handler();
    private Handler handlerReverseAnimationSplashMov5= new Handler();
    private Handler handlerTempoMaximoVerificacao = new Handler();
    private Handler handlerVerifyLogged = new Handler();
    private Thread threadVerificarUsuarioLogado;


    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("teste","onCreate()");
        setContentView(R.layout.activity_splash_screen);

        initViews();


    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("teste","onStart()");

        verificarUsuarioLogado();

        if (!splashIniciada){
            splashScreenInicial();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadVerificarUsuarioLogado.interrupt();
        handlerTempoMaximoVerificacao.removeCallbacksAndMessages(null);
        handlerVerifyLogged.removeCallbacksAndMessages(null);
        handlerAnimationSplashSalao20.removeCallbacksAndMessages(null);
        handlerSplashScreenCompleta.removeCallbacksAndMessages(null);
        handlerReverseAnimationSplashMov5.removeCallbacksAndMessages(null);

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

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
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
                    //callConfiguracaoInicialActivity(userFirebase.getUid());
                    //verificarTipousuario(userFirebase.getUid());
                    /*if (configInicialIsOk(userFirebase.getUid())){
                        callHomeActivity();
                    }*/
                    Log.i("teste","getFirebaseAuthResultHandler() uid != null");
                    nextActivity(userFirebase.getUid());


                }else{
                    //callConfiguracaoInicialActivity("");
                    //verificarTipousuario(null);
                    /*if (configInicialIsOk(null)){
                        callHomeActivity();
                    }*/
                    Log.i("teste","getFirebaseAuthResultHandler() uid == null");
                    nextActivity(null);

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

    private void splashScreenInicial(){
        Log.i("teste","splashScreenInicial() rodadando");

        int tempoAnimacao = 3000;
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


        handlerReverseAnimationSplashMov5.postDelayed(new Runnable() {
            @Override
            public void run() {
                splashLogoMov5.startAnimation(animation1Reverse);
                labelPoweredBy.startAnimation(animation1Reverse);
            }
        },(tempoAnimacao+250));

        handlerAnimationSplashSalao20.postDelayed(new Runnable() {
            @Override
            public void run() {
                splashLogoSalao20.setVisibility(View.VISIBLE);
                splashLogoSalao20.startAnimation(animation1);
            }
        },(6500));

        handlerSplashScreenCompleta.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("teste","splashScreenInicial() completa");
                splashCompleta = true;
            }
        },(10000));


        splashIniciada = true;

    }



    private void verificarUsuarioLogado() {
        Log.i("teste","verificarUsuarioLogado()");
        final int maxTempoVerificacao = 20000;
        threadVerificarUsuarioLogado = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("teste","threadVerificarUsuarioLogado run()");
                mAuth = FirebaseAuth.getInstance();
                mAuthListener = getFirebaseAuthResultHandler();

                handlerVerifyLogged.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("teste","verificarUsuarioLogado() handlerVerifyLogged run()");
                        verifyLogged();
                    }
                },1000);

                handlerTempoMaximoVerificacao.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("teste","verificarUsuarioLogado() handlerTempoMaximoVerificacao run()");
                        callLoginActivity();
                    }
                },maxTempoVerificacao);

            }
        });
        threadVerificarUsuarioLogado.start();

    }

    private void verifyLogged(){
        if( mAuth.getCurrentUser() != null ){
            if (mAuth.getCurrentUser().getUid() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                nextActivity(mAuth.getCurrentUser().getUid());
            }else nextActivity(null);
        }
        else{
            mAuth.addAuthStateListener( mAuthListener );
        }
    }

    private void nextActivity(String uid) {
        Log.i("teste","nextActivity()");
        if (uid != null){
            REF_SALAO = REF_SALAO+uid;
            if (verificaEtapaConfig(verificarTipousuario()) != null){
                Log.i("teste","nextActivity() aguardando splash completa");
                while (!splashCompleta){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("teste","nextActivity() splash completa");
                switch (verificaEtapaConfig(verificarTipousuario())){
                    case "funcionamento":
                        callConfiguracaoIncialActivity("funcionamento");
                        break;
                    case "servicos":
                        callConfiguracaoIncialActivity("servicos");
                        break;
                    case "cabeleireiros":
                        callConfiguracaoIncialActivity("cabeleireiros");
                        break;
                    case "salaoCompleto":
                        callHomeActivity();
                        break;
                    default:
                        break;
                }
            }
        }else {
            //TODO implementar uid == null
        }
    }


    private String verificarTipousuario() {
        Log.i("teste","verificarTipousuario()");
        if (getSPRefString(getApplicationContext(),"tipousuario").equals("salao") || getSPRefString(getApplicationContext(),"tipousuario").equals("cliente") || getSPRefString(getApplicationContext(),"tipousuario").equals("cabeleireiro")){
            return getSPRefString(getApplicationContext(),"tipousuario");
        }else {
            return verificarTipoUsuarioOnline();
        }
    }

    private String verificarTipoUsuarioOnline() {
        //TODO implementar verificaçao tipoUsuario online
        return "online";
    }


    private String verificaEtapaConfig(String tipoUsuario) {
        Log.i("teste","verificaEtapaConfig()");
        if (tipoUsuario != null){
            if (tipoUsuario.equals("salao")){
                if (getSPRefBoolean(getApplicationContext(),"funcionamento") || getSPRefBoolean(getApplicationContext(),"servicos") || getSPRefBoolean(getApplicationContext(),"cabeleireiros")){
                    if (getSPRefBoolean(getApplicationContext(),"funcionamento")){
                        if (getSPRefBoolean(getApplicationContext(),"servicos")){
                            if (getSPRefBoolean(getApplicationContext(),"cabeleireiros")){
                                return "salaoCompleto";
                            }else{
                                return "cabeleireiros";
                            }
                        }else {
                            return "servicos";
                        }
                    }else {
                        return "funcionamento";
                    }
                }else {
                    return verificaEtapaConfigOnline(tipoUsuario);
                }

            }else if (tipoUsuario.equals("cliente")){
                //TODO implementar verificaçao etapa cliente
                return "cliente";

            }else if (tipoUsuario.equals("cabeleireiro")){
                //TODO implementar verificaçao etapa cabeleireiro
                return "cabeleireiro";
            }else {
                return "usuarioInvalido";
            }

        }else {
            //TODO implementar tipoUsuario recebendo null
            return null;
        }

    }

    private String verificaEtapaConfigOnline(String tipoUsuario) {
        Log.i("teste","verificaEtapaConfigOnline()");
        //TODO implementar verificaçao etapa ONLINE
        return "novo";
    }





    //SHAREDPREFERENCES
    private void saveSPRefBoolean(Context context, String key, Boolean value ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }

    private Boolean getSPRefBoolean(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        Boolean value = sp.getBoolean(key, false);
        return( value );
    }

    private void saveSPRefString(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    private String getSPRefString(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        String value = sp.getString(key, "");
        return( value );
    }


    //CALL ACTIVITYS
    private void callLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void callConfiguracaoIncialActivity(String etapa){
        Intent intent = new Intent(this, TabsActivity.class);
        intent.putExtra("etapa",etapa);
        startActivity(intent);
        finish();
    }

    private void callHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void tabs(View view) {
        callLoginActivity();
    }
}
