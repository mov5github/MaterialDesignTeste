package com.example.lucas.materialdesignteste.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.materialdesignteste.activitys.SplashScreenActivity;
import com.example.lucas.materialdesignteste.asyncTask.Interface.AcessoFirebase;
import com.example.lucas.materialdesignteste.dao.CadastroInicialDAO;
import com.example.lucas.materialdesignteste.dao.DatabaseHelper;
import com.example.lucas.materialdesignteste.dao.VersaoDAO;
import com.example.lucas.materialdesignteste.dao.model.CadastroInicial;
import com.example.lucas.materialdesignteste.dao.model.Versao;
import com.example.lucas.materialdesignteste.domain.util.LibraryClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Lucas on 10/03/2017.
 */

public class SincronizarBancosAsyncTask extends AsyncTask<Void,Void,Void> implements AcessoFirebase {
    private Context context;
    private boolean novoUsuario;
    private String uid;
    private boolean stop;

    //DAO
    private CadastroInicialDAO cadastroInicialDAO;
    private VersaoDAO versaoDAO;

    //TABELAS
    private CadastroInicial cadastroInicial;
    private CadastroInicial cadastroInicialCloud;
    private CadastroInicial cadastroInicialFirebase;
    private Versao versao;
    private Versao versaoCloud;
    private Versao versaoFirebase;

    //CONTROLE
    private boolean verificarCadastroInicialFirebase;
    private boolean verificacaoCadastroInicialFirebaseConcluido;
    private boolean cadastroInicialSalvoFirebase;
    private boolean salvarCadastroInicialFirebase;
    private boolean cadastroInicialFirebaseEncontrado;

    //FIREBASE
    private DatabaseReference firebaseCadastroInicial;
    private DatabaseReference.CompletionListener completionListenerCadastroInicial;
    private ValueEventListener vELControladorCadastroInicial;





    public SincronizarBancosAsyncTask(Context context, boolean novoUsuario, String uid) {
        this.novoUsuario = novoUsuario;
        this.context = context;
        this.uid = uid;
    }

    @Override
    protected Void doInBackground(Void... params) {
        initFirebaseEvents();
        initControles();
        long retorno;

        do {
            if (isCancelled()) return null;

            if (this.cadastroInicialDAO == null){
                this.cadastroInicialDAO = new CadastroInicialDAO(this.context);
            }
            if (this.versaoDAO == null){
                this.versaoDAO = new VersaoDAO(this.context);
            }

            this.cadastroInicial = this.cadastroInicialDAO.buscarCadastroInicialPorUID(this.uid);
            this.cadastroInicialCloud = this.cadastroInicialDAO.buscarCadastroInicialPorUIDCloud(this.uid);
            this.versao = this.versaoDAO.buscarVersaoPorUID(this.uid);
            this.versaoCloud = this.versaoDAO.buscarVersaoPorUIDCloud(this.uid);

            this.verificarCadastroInicialFirebase = true;
            do {//VERIFICA NA NUVEM SE O USUARIO JA EXISTE
                if (isCancelled()) return null;

                if (this.verificarCadastroInicialFirebase){
                    verificarCadastroInicialFirebase();
                }
            }while (!this.verificacaoCadastroInicialFirebaseConcluido);
            this.firebaseCadastroInicial.removeEventListener(this.vELControladorCadastroInicial);

            if (this.cadastroInicialFirebaseEncontrado){//CADASTRO INICIAL ENCONTRADO FIREBASE
                //TODO
            }else {//CADASTRO INICIAL NAO ENCONTRADO FIREBASE
                //TODO
            }


        }while (!stop);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        removerFirebaseEvents();
        removerDAOs();
        if (!isCancelled()){
            //TODO
           // SplashScreenActivity.setNovoUsuarioInserido(true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        //TODO
        removerFirebaseEvents();
        removerDAOs();
    }

    //INTERFACE
    @Override
    public void initFirebaseEvents() {
        //atualiza no firebase cadastro inicial
        this.completionListenerCadastroInicial = new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask nao foi possivel salvar cadastroInicial");
                    cadastroInicialSalvoFirebase = false;
                    salvarCadastroInicialFirebase = true;
                }else{
                    Log.i("script","OperacaoDefinirTipoContaAsyncTask cadastroInicial salvo");
                    cadastroInicialSalvoFirebase = true;
                    salvarCadastroInicialFirebase = false;
                }
            }
        };

        //busca no firebase o cadastro inicial
        this.vELControladorCadastroInicial = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Map.class) == null) {
                    Log.i("script", "vELControladorCadastroInicial datasnapshot == null usuario nao existe");
                    verificacaoCadastroInicialFirebaseConcluido = true;
                } else {
                    Log.i("script", "vELControladorCadastroInicial datasnapshot != null usuario existe");
                    cadastroInicialFirebaseEncontrado = true;
                    verificacaoCadastroInicialFirebaseConcluido = true;

                    Map map = dataSnapshot.getValue(Map.class);
                    if (map.containsKey(DatabaseHelper.CadastroInicial.NIVEL_USUARIO)){
                        cadastroInicialFirebase.setNivelUsuario(Double.valueOf((String) map.get(DatabaseHelper.CadastroInicial.NIVEL_USUARIO)));
                    }
                    if (map.containsKey(DatabaseHelper.CadastroInicial.TIPO_USUARIO)){
                        cadastroInicialFirebase.setTipoUsuario(String.valueOf(map.get(DatabaseHelper.CadastroInicial.TIPO_USUARIO)));
                    }
                    if (map.containsKey(DatabaseHelper.CadastroInicial.CODIGO_UNICO)){
                        cadastroInicialFirebase.setCodigoUnico(Integer.valueOf((String) map.get(DatabaseHelper.CadastroInicial.CODIGO_UNICO)));
                    }
                    if (map.containsKey(DatabaseHelper.Versoes.VERSAO)){
                        versaoFirebase.setVersao(Integer.valueOf((String) map.get(DatabaseHelper.Versoes.VERSAO)));
                        versaoFirebase.setIdentificacaoTabela(DatabaseHelper.CadastroInicial.TABELA);
                    }
                    if (map.containsKey(DatabaseHelper.Versoes.DATA_MODIFICACAO)){
                        versaoFirebase.setDataModificacao(String.valueOf(map.get(DatabaseHelper.Versoes.DATA_MODIFICACAO)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("script", "vELControladorCadastroInicial datasnapshot onCancelled usuario nao verificado");
                verificarCadastroInicialFirebase = true;
            }
        };

        this.firebaseCadastroInicial = null;
    }

    @Override
    public void removerFirebaseEvents() {
        //TODO
    }

    @Override
    public void initControles() {
        this.stop = false;
        this.verificarCadastroInicialFirebase = false;
        this.verificacaoCadastroInicialFirebaseConcluido = false;
        this.cadastroInicialFirebaseEncontrado = false;
        this.cadastroInicialSalvoFirebase = false;
        this.salvarCadastroInicialFirebase = false;

    }

    private void removerDAOs(){
        if (this.cadastroInicialDAO != null){
            this.cadastroInicialDAO.fechar();
            this.cadastroInicialDAO = null;
        }
        if (this.versaoDAO != null){
            this.versaoDAO.fechar();
            this.versaoDAO = null;
        }
    }

    //METODOS ACESSO
    private void verificarCadastroInicialFirebase(){
        this.verificarCadastroInicialFirebase = false;
        if (this.firebaseCadastroInicial == null){
            this.firebaseCadastroInicial = LibraryClass.getFirebase().child("users").child( this.uid ).child("CadastroInicial");
        }else {
            this.firebaseCadastroInicial.removeEventListener(this.vELControladorCadastroInicial);
        }
        this.firebaseCadastroInicial.addValueEventListener(this.vELControladorCadastroInicial);

    }
}
