package com.example.lucas.materialdesignteste;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.lucas.materialdesignteste.domain.util.LibraryClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ErroBuscarOnlineActivity extends CommonActivity {
    private DatabaseReference firebaseNivelUsuario = null;
    private ValueEventListener vELNivelUsuario = null;
    private String nivelUsuario = "erro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erro_buscar_online);

    }

    public void recarregarInformacoesNuvem(View view) {
        showToast("desculpe não foi possivel conectar-se a nuvem");
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initUser() {

    }
}
