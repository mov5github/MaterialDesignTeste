package com.example.lucas.materialdesignteste.fragments.signUp2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lucas.materialdesignteste.R;

/**
 * Created by Lucas on 05/01/2017.
 */

public class FragmentCadastroSalao extends Fragment {
    private AutoCompleteTextView name;
    private AutoCompleteTextView email;
    private EditText password;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro_salao,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        /*name = (AutoCompleteTextView) view.findViewById(R.id.name_frag_salao);
        email = (AutoCompleteTextView) view.findViewById(R.id.email_frag_salao);
        password = (EditText) view.findViewById(R.id.password_frag_salao);*/
    }


    public static String getTitulo() {
        String titulo = "Salão";
        return titulo;
    }

    public AutoCompleteTextView getEmail() {
        return email;
    }

    public EditText getPassword() {
        return password;
    }

    public AutoCompleteTextView getName() {
        return name;
    }
}
