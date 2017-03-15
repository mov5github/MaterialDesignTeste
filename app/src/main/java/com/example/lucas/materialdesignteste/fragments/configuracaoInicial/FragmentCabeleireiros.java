package com.example.lucas.materialdesignteste.fragments.configuracaoInicial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.materialdesignteste.R;

/**
 * Created by Lucas on 28/12/2016.
 */
public class FragmentCabeleireiros extends Fragment {
    private static String titulo = "Cabeleireiros";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cabeleireiros,container,false);
        return view;
    }

    public static String getTitulo() {
        return titulo;
    }

    public boolean preenchimentoIsValid(){
        return false;
    }
}
