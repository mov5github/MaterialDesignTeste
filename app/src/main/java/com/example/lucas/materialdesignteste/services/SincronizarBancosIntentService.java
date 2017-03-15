package com.example.lucas.materialdesignteste.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.lucas.materialdesignteste.activitys.SplashScreenActivity;
import com.example.lucas.materialdesignteste.dao.CadastroInicialDAO;

/**
 * Created by Lucas on 12/03/2017.
 */

public class SincronizarBancosIntentService extends IntentService {
    private boolean ativo;
    private boolean stopAll;
    private Context mContext;
    private CadastroInicialDAO cadastroInicialDAO;

    public SincronizarBancosIntentService() {
        super("SincronizarBancosIntentService");
        this.ativo = true;
        this.stopAll = false;
        this.mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle b = intent.getExtras();
        if (b != null){
            int desligar = b.getInt("desligar");
            if (desligar == 1){
                this.stopAll = true;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (this.ativo && !this.stopAll){
            if (SplashScreenActivity.isSplashScreenActivityAtiva()){
                synchronized (SplashScreenActivity.getSplashScreenUiThread()){
                    try {
                        SplashScreenActivity.getSplashScreenUiThread().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (SplashScreenActivity.getCadastroInicialFirebase() != null){
                sincronizarBancos();
            }else {
                if (SplashScreenActivity.isSplashScreenActivityAtiva()){
                    Intent intentBrodcast = new Intent();
                    intentBrodcast.setAction(SplashScreenActivity.getFilterDispararBrodcastErroBuscarCadastroInicialFirebase());
                    intentBrodcast.addCategory(Intent.CATEGORY_DEFAULT);
                    this.mContext.sendBroadcast(intentBrodcast);
                }
            }

            this.ativo = false;
        }

        if (this.cadastroInicialDAO != null){
            this.cadastroInicialDAO.fechar();
            this.cadastroInicialDAO = null;
        }
        this.ativo = true;
    }

    //AUXILIARES
    private void sincronizarBancos(){
        long result = -1;
        if (SplashScreenActivity.isSplashScreenActivityAtiva()){
            if (SplashScreenActivity.getCadastroInicialFirebase().getVersao() > SplashScreenActivity.getCadastroInicialBDCloud().getVersao()){
                if (this.cadastroInicialDAO == null){
                    this.cadastroInicialDAO = new CadastroInicialDAO(this.mContext);
                }
                atualizarValoresBdCloud();
                while (result == -1 && !this.stopAll){
                    result = this.cadastroInicialDAO.salvarCadastroInicialCloud(SplashScreenActivity.getCadastroInicialBDCloud());
                }
            }
        }else {
            if (this.cadastroInicialDAO != null){
                this.cadastroInicialDAO.fechar();
                this.cadastroInicialDAO = null;
            }
            return;
        }

        if (SplashScreenActivity.isSplashScreenActivityAtiva()){
            if (SplashScreenActivity.getCadastroInicialBD().getVersao() > SplashScreenActivity.getCadastroInicialBDCloud().getVersao() ){
                //TODO salvar bd no firebase e atualizaxar bd cloud
                Intent intentBrodcast = new Intent();
                intentBrodcast.setAction(SplashScreenActivity.getFilterDispararBrodcastSalvarCadastroInicialFirebase());
                intentBrodcast.addCategory(Intent.CATEGORY_DEFAULT);
                this.mContext.sendBroadcast(intentBrodcast);

                synchronized (SplashScreenActivity.getSplashScreenUiThread()){
                    try {
                        SplashScreenActivity.getSplashScreenUiThread().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                ////TODO informar que cancos estao sincronizados
            }else{
                if (this.cadastroInicialDAO == null){
                    this.cadastroInicialDAO = new CadastroInicialDAO(this.mContext);
                }
                atualizarValoresBd();
                result = -1;
                while (result == -1 && !this.stopAll){
                    result = this.cadastroInicialDAO.salvarCadastroInicial(SplashScreenActivity.getCadastroInicialBD());
                }
                ////TODO informar que cancos estao sincronizados
            }
        }else {
            if (this.cadastroInicialDAO != null){
                this.cadastroInicialDAO.fechar();
                this.cadastroInicialDAO = null;
            }
        }
    }

    private void atualizarValoresBdCloud(){
        if (SplashScreenActivity.getCadastroInicialFirebase().getVersao() != null){
            SplashScreenActivity.getCadastroInicialBDCloud().setVersao(SplashScreenActivity.getCadastroInicialFirebase().getVersao());
        }
        if (SplashScreenActivity.getCadastroInicialFirebase().getDataModificalao() != null){
            SplashScreenActivity.getCadastroInicialBDCloud().setDataModificalao(SplashScreenActivity.getCadastroInicialFirebase().getDataModificalao());
        }
        if (SplashScreenActivity.getCadastroInicialFirebase().getNivelUsuario() != null){
            SplashScreenActivity.getCadastroInicialBDCloud().setNivelUsuario(SplashScreenActivity.getCadastroInicialFirebase().getNivelUsuario());
        }
        if (SplashScreenActivity.getCadastroInicialFirebase().getTipoUsuario() != null){
            SplashScreenActivity.getCadastroInicialBDCloud().setTipoUsuario(SplashScreenActivity.getCadastroInicialFirebase().getTipoUsuario());
        }
        if (SplashScreenActivity.getCadastroInicialFirebase().getCodigoUnico() != null){
            SplashScreenActivity.getCadastroInicialBDCloud().setCodigoUnico(SplashScreenActivity.getCadastroInicialFirebase().getCodigoUnico());
        }
    }

    private void atualizarValoresBd(){
        if (SplashScreenActivity.getCadastroInicialBDCloud().getVersao() != null){
            SplashScreenActivity.getCadastroInicialBD().setVersao(SplashScreenActivity.getCadastroInicialBDCloud().getVersao());
        }
        if (SplashScreenActivity.getCadastroInicialBDCloud().getDataModificalao() != null){
            SplashScreenActivity.getCadastroInicialBD().setDataModificalao(SplashScreenActivity.getCadastroInicialBDCloud().getDataModificalao());
        }
        if (SplashScreenActivity.getCadastroInicialBDCloud().getNivelUsuario() != null){
            SplashScreenActivity.getCadastroInicialBD().setNivelUsuario(SplashScreenActivity.getCadastroInicialBDCloud().getNivelUsuario());
        }
        if (SplashScreenActivity.getCadastroInicialBDCloud().getTipoUsuario() != null){
            SplashScreenActivity.getCadastroInicialBD().setTipoUsuario(SplashScreenActivity.getCadastroInicialBDCloud().getTipoUsuario());
        }
        if (SplashScreenActivity.getCadastroInicialBDCloud().getCodigoUnico() != null){
            SplashScreenActivity.getCadastroInicialBD().setCodigoUnico(SplashScreenActivity.getCadastroInicialBDCloud().getCodigoUnico());
        }
    }
}
