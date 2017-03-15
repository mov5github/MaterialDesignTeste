package com.example.lucas.materialdesignteste.asyncTask.asyncTaskAuxiliares;

import android.os.AsyncTask;

import com.example.lucas.materialdesignteste.dao.model.CadastroInicial;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Lucas on 12/03/2017.
 */

public class BucarCadastroInicialFirebaseAsyncTask extends AsyncTask<CadastroInicial,Void,String> {
    //FIREBASE
    private DatabaseReference firebaseSaveCadastroInicial;
    private DatabaseReference.CompletionListener completionListenerCadastroInicial;
    private ValueEventListener vELCadastroInicial;


    @Override
    protected String doInBackground(CadastroInicial... params) {
        return null;
    }
}
