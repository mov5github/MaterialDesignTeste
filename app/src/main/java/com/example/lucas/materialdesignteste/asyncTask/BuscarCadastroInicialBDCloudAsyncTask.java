package com.example.lucas.materialdesignteste.asyncTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.lucas.materialdesignteste.activitys.SplashScreenActivity;
import com.example.lucas.materialdesignteste.dao.CadastroInicialDAO;
import com.example.lucas.materialdesignteste.dao.model.CadastroInicial;

/**
 * Created by Lucas on 14/03/2017.
 */

public class BuscarCadastroInicialBDCloudAsyncTask extends AsyncTask<CadastroInicial,Void,CadastroInicial> {
    private Context context;
    private CadastroInicialDAO cadastroInicialDAO;

    public BuscarCadastroInicialBDCloudAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected CadastroInicial doInBackground(CadastroInicial... params) {
        if (this.cadastroInicialDAO == null){
            this.cadastroInicialDAO = new CadastroInicialDAO(this.context);
        }

        if (params[0] == null){
            return params[0];
        }else {
            CadastroInicial cadastroInicialCloud = this.cadastroInicialDAO.buscarCadastroInicialPorUIDCloud(params[0].getUid());
            if (cadastroInicialCloud == null){
                long result = -1;
                while (!isCancelled() && result == -1){
                    result = this.cadastroInicialDAO.salvarCadastroInicialCloud(params[0]);
                }
                if (isCancelled()) {return null;}
                else {
                    return params[0];
                }
            }
            return cadastroInicialCloud;
        }


    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (this.cadastroInicialDAO != null){
            this.cadastroInicialDAO.fechar();
            this.cadastroInicialDAO = null;
        }
        SplashScreenActivity.setCadastroInicialBDCloud(null);
        if (SplashScreenActivity.isSplashScreenActivityAtiva()) {
            Intent intentBrodcast = new Intent();
            intentBrodcast.setAction(SplashScreenActivity.getFilterDispararBrodcastErroBuscarCadastroInicialBdBdcloud());
            intentBrodcast.addCategory(Intent.CATEGORY_DEFAULT);
            this.context.sendBroadcast(intentBrodcast);
        }
    }

    @Override
    protected void onPostExecute(CadastroInicial cadastroInicial) {
        super.onPostExecute(cadastroInicial);

        if (cadastroInicial == null){
            SplashScreenActivity.setCadastroInicialBDCloud(null);
            if (SplashScreenActivity.isSplashScreenActivityAtiva()) {
                Intent intentBrodcast = new Intent();
                intentBrodcast.setAction(SplashScreenActivity.getFilterDispararBrodcastErroBuscarCadastroInicialBdBdcloud());
                intentBrodcast.addCategory(Intent.CATEGORY_DEFAULT);
                this.context.sendBroadcast(intentBrodcast);
            }
        }else {
            if (SplashScreenActivity.isSplashScreenActivityAtiva()) {
                SplashScreenActivity.setCadastroInicialBDCloud(cadastroInicial);
                Intent intentBrodcast = new Intent();
                intentBrodcast.setAction(SplashScreenActivity.getFilterDispararBrodcastSincronizarBancos());
                intentBrodcast.addCategory(Intent.CATEGORY_DEFAULT);
                this.context.sendBroadcast(intentBrodcast);
            }
        }

        this.cadastroInicialDAO.fechar();
        this.cadastroInicialDAO = null;
    }
}
