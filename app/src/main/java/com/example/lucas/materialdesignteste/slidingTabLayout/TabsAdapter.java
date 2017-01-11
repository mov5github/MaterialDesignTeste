package com.example.lucas.materialdesignteste.slidingTabLayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.lucas.materialdesignteste.fragments.configuracaoInicial.FragmentCabeleireiros;
import com.example.lucas.materialdesignteste.fragments.signUp2.FragmentCadastroCabeleireiro;
import com.example.lucas.materialdesignteste.fragments.signUp2.FragmentCadastroCliente;
import com.example.lucas.materialdesignteste.fragments.signUp2.FragmentCadastroSalao;
import com.example.lucas.materialdesignteste.fragments.configuracaoInicial.FragmentFuncionamento;
import com.example.lucas.materialdesignteste.fragments.configuracaoInicial.FragmentServicos;

/**
 * Created by Lucas on 28/12/2016.
 */

public class TabsAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles;
    private String mActivityName;

    public TabsAdapter(FragmentManager fm, Context ctx, String[] titulos, String activityName){
        super(fm);
        mContext = ctx;
        titles =  titulos;
        mActivityName = activityName;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        if (mActivityName.equals("TabsActivity")){
            switch (position){
                case 0:
                    frag = new FragmentFuncionamento();
                    break;
                case 1:
                    frag = new FragmentServicos();
                    break;
                case 2:
                    frag = new FragmentCabeleireiros();
                    break;
                default:
                    break;
            }
        }else if (mActivityName.equals("SignUp2Activity")){
            switch (position){
                case 0:
                    frag = new FragmentCadastroCliente();
                    break;
                case 1:
                    frag = new FragmentCadastroSalao();
                    break;
                case 2:
                    frag = new FragmentCadastroCabeleireiro();
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
}
