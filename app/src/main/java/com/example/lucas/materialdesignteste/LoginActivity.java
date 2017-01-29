package com.example.lucas.materialdesignteste;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.lucas.materialdesignteste.domain.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

public class LoginActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener  {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private User user;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","onCreate() LOGIN");

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();


        initViews();
        initUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("script","onResume() LOGIN");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            mToolbar.setElevation(4 * this.getResources().getDisplayMetrics().density);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","onStart() LOGIN");
        if( mAuth.getCurrentUser() != null ){
            Bundle bundle = user.remodelUser();
            callSplashScreen2Activity(bundle);
        }
        else{
            mAuth.addAuthStateListener( mAuthListener );
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() LOGIN");

        if( mAuthListener != null ){
            mAuth.removeAuthStateListener( mAuthListener );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","onDestroy() LOGIN");
        if( mAuthListener != null ){
            mAuth.removeAuthStateListener( mAuthListener );
        }
    }

    @Override
    protected void initViews() {
        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar_login);
        mToolbar.setTitle("LOGIN");
        mToolbar.setSubtitle("tela de login");
        mToolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(mToolbar);

        //FLOATING ACTION BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_login);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "script", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                //callSignUpActivity("salao");
                if (validaFormulario()){
                    FirebaseCrash.log("LoginActivity:clickListener:button:sendLoginData()");
                    openProgressBar();
                    initUser();
                    verifyLogin();
                }
            }
        });

        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
    }

    @Override
    protected void initUser() {
        user = new User();
        user.setEmail( email.getText().toString() );
        user.setPassword( password.getText().toString() );
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
        Log.i("script","getFirebaseAuthResultHandler() login ");

        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i("script","getFirebaseAuthResultHandler() onAuthStateChanged login");

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if( userFirebase == null ){
                    Log.i("script","getFirebaseAuthResultHandler() userFirebase == null login");
                    return;
                }

                if( user.getId() == null
                        && isNameOk( user, userFirebase ) ){
                    Log.i("script","getFirebaseAuthResultHandler() set user login");
                    user.setId( userFirebase.getUid() );
                    user.setNameIfNull( userFirebase.getDisplayName() );
                    user.setEmailIfNull( userFirebase.getEmail() );
                    user.saveDB();
                }
                if (userFirebase.getUid()!= null && !userFirebase.getUid().isEmpty()){
                    Log.i("script","getFirebaseAuthResultHandler() uid != null uid = " +userFirebase.getUid()+" login");
                    Bundle bundle = user.remodelUser();
                    callSplashScreen2Activity(bundle);
                }else{
                    Log.i("script","getFirebaseAuthResultHandler() uid == null login");
                    Bundle bundle = user.remodelUser();
                    callSplashScreen2Activity(bundle);
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



    private void verifyLogin(){
        Log.i("script","verifyLogin()");
        FirebaseCrash.log("LoginActivity:verifyLogin()");
        user.saveProviderSP( LoginActivity.this, "" );
        mAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        )
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("script","verifyLogin() onComplete");

                        if( !task.isSuccessful() ){
                            Log.i("script","verifyLogin() onComplete !task.isSuccessful()");
                            showSnackbar("Login falhou");
                            closeProgressBar();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("script","verifyLogin() onFailure");

                FirebaseCrash.report( e );
            }
        });
    }

    private Boolean validaFormulario(){
        if (emailIsValid() && passwordIsvalid()){
            return true;
        }else{
            return false;
        }
    }


    //TEXT LINK
    public void callSignUp(View view) {
        Log.i("script","callSignUp()");
        callSignUpActivity();
    }

    //OLD
    /*private void nextActivity(final String uid) {
        Log.i("teste","nextActivity()");
        mThreadNextActivity = new Thread(new Runnable() {
            @Override
            public void run() {
                if (uid != null){
                    Log.i("teste","nextActivity() uid != null");
                    REF_SALAO = REF_SALAO+uid;
                    if (verificaEtapaConfig(verificarTipousuario()) != null){
                        switch (verificaEtapaConfig(verificarTipousuario())){
                            case "funcionamento":
                                Log.i("teste","nextActivity() funcionamento");
                                callConfiguracaoIncialActivity("funcionamento",null);
                                break;
                            case "servicos":
                                Log.i("teste","nextActivity() servicos");
                                callConfiguracaoIncialActivity("servicos",null);
                                break;
                            case "cabeleireiros":
                                Log.i("teste","nextActivity() cabeleireiro");
                                callConfiguracaoIncialActivity("cabeleireiros",null);
                                break;
                            case "salaoCompleto":
                                Log.i("teste","nextActivity() salao completo");
                                callHomeActivity();
                                break;
                            case "usuarioInvalido":
                                Log.i("teste","nextActivity() usuarioInvalido");
                                //TODO implementar tela nextActivity com tipo usuario n identificado
                            default:
                                break;
                        }
                    }
                }else {
                    //TODO implementar uid == null
                    Log.i("teste","nextActivity() uid == null");

                }
            }
        });
        mThreadNextActivity.start();

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
    }*/
}
