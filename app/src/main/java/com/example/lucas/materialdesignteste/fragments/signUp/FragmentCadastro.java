package com.example.lucas.materialdesignteste.fragments.signUp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.lucas.materialdesignteste.R;

/**
 * Created by Lucas on 10/01/2017.
 */

public class FragmentCadastro extends Fragment{
    private AutoCompleteTextView email;
    private EditText password;
    private EditText passwordAgain;
    private String tipoCadastro;
    private Spinner spinnerTipoCadastro;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("teste","onCreate() fragment cadastro");
        Bundle bundle = getArguments();
        tipoCadastro = bundle.getString("tipo");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        email = (AutoCompleteTextView) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        passwordAgain = (EditText) view.findViewById(R.id.password_again);
        spinnerTipoCadastro = (Spinner) view.findViewById(R.id.spinner_tipo_cadastro);
        if (tipoCadastro.equals("salao")){
            spinnerTipoCadastro.setSelection(0);
        }else if (tipoCadastro.equals("cliente")){
            spinnerTipoCadastro.setSelection(1);
        }else if (tipoCadastro.equals("cabeleireiro")){
            spinnerTipoCadastro.setSelection(2);
        }
    }

    //GETTERS
    public EditText getPassword() {
        return password;
    }

    public EditText getPasswordAgain() {
        return passwordAgain;
    }

    public AutoCompleteTextView getEmail() {
        return email;
    }

    public Spinner getSpinnerTipoCadastro() {
        return spinnerTipoCadastro;
    }
}
