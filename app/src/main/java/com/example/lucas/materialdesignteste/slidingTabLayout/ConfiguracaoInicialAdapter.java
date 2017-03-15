package com.example.lucas.materialdesignteste.slidingTabLayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.lucas.materialdesignteste.fragments.configuracaoInicial.FragmentCabeleireiros;
import com.example.lucas.materialdesignteste.fragments.configuracaoInicial.FragmentFuncionamento;
import com.example.lucas.materialdesignteste.fragments.configuracaoInicial.FragmentServicos;
import com.example.lucas.materialdesignteste.fragments.configuracaoInicial.FragmentTipoCadastro;

/**
 * Created by Lucas on 28/12/2016.
 */

public class ConfiguracaoInicialAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles;
    private String tipoUsuario;
    private Fragment[] mFragments;

    public ConfiguracaoInicialAdapter(FragmentManager fm, Context ctx, String[] titulos, String tipoUsuario){
        super(fm);
        mContext = ctx;
        titles =  titulos;
        if (tipoUsuario == null){
            this.tipoUsuario = "void";
        }else {
            this.tipoUsuario = tipoUsuario;
        }
        if (this.tipoUsuario.equals("salão")){
            mFragments = new Fragment[3];
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        if (tipoUsuario.equals("void") || tipoUsuario.equals("")){
            switch (position){
                case 0:
                    frag = new FragmentTipoCadastro();
                    break;
                default:
                    break;
            }
        }else if (tipoUsuario.equals("salão")){
            switch (position){
                case 0:
                    frag = new FragmentFuncionamento();
                    mFragments[position] = frag;
                    break;
                case 1:
                    frag = new FragmentServicos();
                    mFragments[position] = frag;
                    break;
                case 2:
                    frag = new FragmentCabeleireiros();
                    mFragments[position] = frag;
                    break;
                default:
                    break;
            }
        }

        if (frag != null){
            Bundle bundle = new Bundle();
            bundle.putInt("position",position);
            frag.setArguments(bundle);
        }

        return frag;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (titles[position]);
    }

    public Fragment getCurrentFragment(int position){
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = mFragments[position];
                break;
            case 1:
                fragment = mFragments[position];
                break;
            case 2:
                fragment = mFragments[position];
                break;
            default:
                break;
        }
        return fragment;
    }
}
