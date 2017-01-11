package com.example.lucas.materialdesignteste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by Lucas on 03/01/2017.
 */

public abstract class CommonActivity extends AppCompatActivity {
    protected String REF_SALAO = "com.example.lucas.materialdesignteste";
    protected AutoCompleteTextView email;
    protected EditText password;
    protected EditText passwordAgain;
    protected ProgressBar progressBar;

    protected void showSnackbar(String message ){
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
    protected void callLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    protected void callSignUpActivity(String tipoCadastro){
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra("tipoCadastro",tipoCadastro);
        startActivity(intent);
    }

    protected void callConfiguracaoIncialActivity(String etapa){
        Log.i("teste","callConfiguracaoIncialActivity() "+etapa);
        Intent intent = new Intent(this, TabsActivity.class);
        intent.putExtra("etapa",etapa);
        startActivity(intent);
        finish();
    }

    protected void callHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    //SHAREDPREFERENCES
    protected void saveSPRefBoolean(Context context, String key, Boolean value ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }

    protected Boolean getSPRefBoolean(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        Boolean value = sp.getBoolean(key, false);
        return( value );
    }

    protected void saveSPRefString(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    protected String getSPRefString(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(REF_SALAO, Context.MODE_PRIVATE);
        String value = sp.getString(key, "");
        return( value );
    }
}