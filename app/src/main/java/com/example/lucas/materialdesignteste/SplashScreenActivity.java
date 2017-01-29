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
import com.example.lucas.materialdesignteste.domain.util.LibraryClass;
import com.example.lucas.materialdesignteste.threads.SplashScreenSecondThread;
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

public class SplashScreenActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener  {
    //VIEWS
    private ImageView splashLogoMov5;
    private ImageView splashLogoSalao20;
    private TextView labelPoweredBy;
    private TextView labelCarregando;
    private ProgressBar progressBarSalao20;

    //CONTROLE
    private Boolean splashIniciada = false;
    private Boolean splashCompleta = false;
    private Boolean verificarUsuarioLogadoIniciado = false;
    private Boolean limiteVerificacaoUsuarioLogadoIniciado = false;

    /*private Handler handlerSplashScreenCompleta= new Handler();
    private Handler handlerAnimationSplashSalao20= new Handler();
    private Handler handlerReverseAnimationSplashMov5= new Handler();
    private Handler handlerTempoMaximoVerificacao = new Handler();
    private Thread threadVerificarUsuarioLogado;
    private Handler handlerStopVerifyTipoUsuarioOnline = new Handler();*/

    private Handler handlerUIThread;
    private Thread secondThread;
    private Thread threadAnimacaoSplash;

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;

    private String codUnico = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("teste","onCreate()");
        setContentView(R.layout.activity_splash_screen);

        //mAuth = FirebaseAuth.getInstance();
       // mAuthListener = getFirebaseAuthResultHandler();
        initViews();
        //initUser();
        initThreads();


    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("teste","onStart()");

        /*if (!verificarUsuarioLogadoIniciado){
            verificarUsuarioLogado();
        }*/

        if (!secondThread.isAlive()){
            Log.i("teste","onStart() secondthread iniciada");
            secondThread.start();
        }

        if (!splashIniciada){
            Log.i("teste","onStart() splashscreen iniciada");
            splashScreenInicial();
        }

        if (!limiteVerificacaoUsuarioLogadoIniciado){
            Log.i("teste","onStart() timer limite verificacao iniciada");
            limiteVerificacaoUsuarioLogado();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( mAuthListener != null ){
            mAuth.removeAuthStateListener( mAuthListener );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*threadVerificarUsuarioLogado.interrupt();
        handlerTempoMaximoVerificacao.removeCallbacksAndMessages(null);
        handlerAnimationSplashSalao20.removeCallbacksAndMessages(null);
        handlerSplashScreenCompleta.removeCallbacksAndMessages(null);
        handlerReverseAnimationSplashMov5.removeCallbacksAndMessages(null);*/
        handlerUIThread.removeCallbacksAndMessages(null);
        if (secondThread != null){
            if (secondThread.isAlive()){
                secondThread.interrupt();
            }
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
        secondThread = new SplashScreenSecondThread(this);
        handlerUIThread = new Handler();
    }



    private void splashScreenInicial(){
        Log.i("teste","splashScreenInicial() rodadando");

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
                Log.i("teste","splashScreenInicial() completa");
                splashCompleta = true;
            }
        },(7000));

        splashIniciada = true;

    }

    private void limiteVerificacaoUsuarioLogado(){
        final int maxTempoVerificacao = 11000;
        handlerUIThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("teste","limiteVerificacaoUsuarioLogado  runnable maxTempoVerifiacaçao run()");
                callLoginActivity();
            }
        },(maxTempoVerificacao));
    }


    public void tabs(View view) {
        callLoginActivity();

    }

    //GETTERS
    public Boolean getSplashCompleta() {
        return splashCompleta;
    }


}
