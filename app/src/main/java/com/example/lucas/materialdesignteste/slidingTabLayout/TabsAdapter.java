package com.example.lucas.materialdesignteste.slidingTabLayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.lucas.materialdesignteste.fragments.FragmentCabeleireiros;
import com.example.lucas.materialdesignteste.fragments.FragmentFuncionamento;
import com.example.lucas.materialdesignteste.fragments.FragmentServicos;

/**
 * Created by Lucas on 28/12/2016.
 */

public class TabsAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles;

    public TabsAdapter(FragmentManager fm, Context ctx, String[] titulos){
        super(fm);
        mContext = ctx;
        titles =  titulos;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
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
