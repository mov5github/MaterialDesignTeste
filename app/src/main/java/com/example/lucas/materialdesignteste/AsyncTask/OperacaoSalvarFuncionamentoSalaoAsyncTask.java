package com.example.lucas.materialdesignteste.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.materialdesignteste.asyncTask.Interface.AcessoFirebase;
import com.example.lucas.materialdesignteste.domain.User;

/**
 * Created by Lucas on 24/02/2017.
 */

public class OperacaoSalvarFuncionamentoSalaoAsyncTask extends AsyncTask<Void,Void,Void> implements AcessoFirebase{
    private Activity mActivity;
    private User user;
    private boolean stop;


    public OperacaoSalvarFuncionamentoSalaoAsyncTask(Activity mActivity, User user) {
        this.mActivity = mActivity;
        this.user = user;
    }

    @Override
    protected Void doInBackground(Void... params) {

        do {
            if (isCancelled()){return null;}

            //TODO

            if (!stop){
                int tempoAguardeMili = 250;
                Log.i("script","OperacaoSalvarFuncionamentoSalaoAsyncTask aguardando atualizacao dos dados");
                synchronized (this){
                    try {
                        wait(tempoAguardeMili);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }while (!stop);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    //INTERFACE
    @Override
    public void initFirebaseEvents() {

    }

    @Override
    public void removerFirebaseEvents() {

    }

    @Override
    public void initControles() {
        this.stop = false;
    }
}
