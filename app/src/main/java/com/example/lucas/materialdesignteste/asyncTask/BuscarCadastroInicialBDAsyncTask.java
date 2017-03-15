package com.example.lucas.materialdesignteste.asyncTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.materialdesignteste.activitys.SplashScreenActivity;
import com.example.lucas.materialdesignteste.dao.CadastroInicialDAO;
import com.example.lucas.materialdesignteste.dao.model.CadastroInicial;

/**
 * Created by Lucas on 13/03/2017.
 */

public class BuscarCadastroInicialBDAsyncTask extends AsyncTask<String,Void,CadastroInicial> {
    private Context context;
    private CadastroInicialDAO cadastroInicialDAO;

    public BuscarCadastroInicialBDAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected CadastroInicial doInBackground(String... params) {
        if (this.cadastroInicialDAO == null){
            this.cadastroInicialDAO = new CadastroInicialDAO(this.context);
        }

        CadastroInicial cadastroInicial = this.cadastroInicialDAO.buscarCadastroInicialPorUID(params[0]);

        if (cadastroInicial == null){
            cadastroInicial = new CadastroInicial();
            cadastroInicial.setUid(params[0]);
            cadastroInicial.setNivelUsuario(1.0);

            long result = -1;
            while (!isCancelled() && result ==-1){
                result = this.cadastroInicialDAO.salvarCadastroInicial(cadastroInicial);
            }

            cadastroInicial.setNivelUsuario(null);

            if (isCancelled()){return null;}
        }

        return cadastroInicial;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (this.cadastroInicialDAO != null){
            this.cadastroInicialDAO.fechar();
            this.cadastroInicialDAO = null;
        }
        SplashScreenActivity.setCadastroInicialBD(null);
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
            if (SplashScreenActivity.isSplashScreenActivityAtiva()){
                Intent intentBrodcast = new Intent();
                intentBrodcast.setAction(SplashScreenActivity.getFilterDispararBrodcastErroBuscarCadastroInicialBdBdcloud());
                intentBrodcast.addCategory(Intent.CATEGORY_DEFAULT);
                this.context.sendBroadcast(intentBrodcast);
            }
        }else {
            if (SplashScreenActivity.isSplashScreenActivityAtiva()){
                SplashScreenActivity.setCadastroInicialBD(cadastroInicial);
                Intent intentBrodcast = new Intent();
                intentBrodcast.setAction(SplashScreenActivity.getFilterDispararBrodcastSplashScreen());
                intentBrodcast.addCategory(Intent.CATEGORY_DEFAULT);
                this.context.sendBroadcast(intentBrodcast);
            }
        }
        this.cadastroInicialDAO.fechar();
        this.cadastroInicialDAO = null;
    }
}
