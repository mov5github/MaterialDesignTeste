package com.example.lucas.materialdesignteste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Lucas on 03/01/2017.
 */

public abstract class CommonActivity extends AppCompatActivity {
    protected String REF = "com.example.lucas.materialdesignteste";
    protected AutoCompleteTextView email;
    protected EditText password;
    protected EditText passwordAgain;
    protected ProgressBar progressBar;

    public void showSnackbar(String message ){
        Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void showToast( String message ){
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }



    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

    abstract protected void initViews();

    abstract protected void initUser();

    protected Boolean emailIsValid(){
        if (email.getText().toString().isEmpty()){
            email.setError("Campo Obrigatório");
            email.requestFocus();
            return false;
        }else{
            return true;
        }
    }

    protected Boolean passwordIsvalid(){
        if (password.getText().length() < 5){
            password.setError("Campo minimo de 5 caracters");
            password.requestFocus();
            return false;
        }else{
            return true;
        }
    }

    protected Boolean passwordAgainIsvalid(){
        if (!(passwordAgain.getText().toString().equals(password.getText().toString()))){
            passwordAgain.setError("O password deve ser igual ao anterior");
            passwordAgain.requestFocus();
            return false;
        }else{
            return true;
        }
    }

    //CALL ACTIVITYS
    public void callLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void callSignUpActivity(){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void callConfiguracaoIncialActivity(){
        Log.i("teste","callConfiguracaoIncialActivity() ");
        Intent intent = new Intent(this, TabsActivity.class);
        startActivity(intent);
        finish();
    }

    public void callConfiguracaoIncialActivity2(Bundle bundle){
        Log.i("teste","callConfiguracaoIncialActivity() ");
        Intent intent = new Intent(this, TabsActivity.class);
        if (bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }

    public void callHomeActivity(Bundle bundle){
        Intent intent = new Intent(this, HomeActivity.class);
        if (bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }

    public void callErroBuscarOnlineActivity(Bundle bundle){
        Intent intent = new Intent(this, ErroBuscarOnlineActivity.class);
        if (bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }

    public void callSplashScreen2Activity(Bundle bundle){
        Intent intent = new Intent(this, SplashScreen2Activity.class);
        if (bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }



    //SHAREDPREFERENCES
    protected void saveSPRefBoolean(Context context, String key, Boolean value ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }

    protected Boolean getSPRefBoolean(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        Boolean value = sp.getBoolean(key, false);
        return( value );
    }

    protected void saveSPRefString(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    protected String getSPRefString(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(REF, Context.MODE_PRIVATE);
        String value = sp.getString(key, "");
        return( value );
    }

    //GETTERS
    public String getREF() {
        return REF;
    }
}