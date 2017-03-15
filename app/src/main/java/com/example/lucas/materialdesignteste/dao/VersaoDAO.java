package com.example.lucas.materialdesignteste.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.materialdesignteste.dao.model.Versao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lucas on 03/03/2017.
 */

public class VersaoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public VersaoDAO(Context context){
        this.databaseHelper = new DatabaseHelper(context);
    }




    //ACESSOS
    public List<Versao> listarVersoes(){
        List<Versao> versoes = new ArrayList<Versao>();
        Cursor cursor = getDatabase().query(DatabaseHelper.Versoes.TABELA,
                DatabaseHelper.Versoes.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            Versao model = criarVersao(cursor);
            versoes.add(model);
        }
        cursor.close();
        return versoes;
    }
    public List<Versao> listarVersoesCloud(){
        List<Versao> versoes = new ArrayList<Versao>();
        Cursor cursor = getDatabase().query(DatabaseHelper.Versoes.TABELA_CLOUD,
                DatabaseHelper.Versoes.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            Versao model = criarVersao(cursor);
            versoes.add(model);
        }
        cursor.close();
        return versoes;
    }

    public long salvarVersao(Versao versao){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Versoes.IDENTIFICACAO_TABELA, versao.getIdentificacaoTabela());
        values.put(DatabaseHelper.Versoes.UID, versao.getUid());
        if (versao.getVersao() == null){
            values.put(DatabaseHelper.Versoes.VERSAO, 1);
        }else {
            values.put(DatabaseHelper.Versoes.VERSAO, versao.getVersao());
        }
        if (versao.getDataModificacao() == null){
            values.put(DatabaseHelper.Versoes.DATA_MODIFICACAO, getDateTime());
        }

        if(versao.get_id() != null){
            return this.database.update(DatabaseHelper.Versoes.TABELA, values,
                    "_id = ?", new String[]{versao.get_id().toString()});
        }else {
            return getDatabase().insert(DatabaseHelper.Versoes.TABELA, null, values);
        }
    }
    public long salvarVersaoCloud(Versao versao){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Versoes.IDENTIFICACAO_TABELA, versao.getIdentificacaoTabela());
        values.put(DatabaseHelper.Versoes.UID, versao.getUid());
        if (versao.getVersao() == null){
            values.put(DatabaseHelper.Versoes.VERSAO, 1);
        }else {
            values.put(DatabaseHelper.Versoes.VERSAO, versao.getVersao());
        }
        if (versao.getDataModificacao() == null){
            values.put(DatabaseHelper.Versoes.DATA_MODIFICACAO, getDateTime());
        }
        if(versao.get_id() != null){
            return this.database.update(DatabaseHelper.Versoes.TABELA_CLOUD, values,
                    "_id = ?", new String[]{versao.get_id().toString()});
        }else {
            return getDatabase().insert(DatabaseHelper.Versoes.TABELA_CLOUD, null, values);
        }
    }

    public boolean removerVersaoPorId(int id){
        return getDatabase().delete(DatabaseHelper.Versoes.TABELA,
                "_id = ?", new String[]{Integer.toString(id)}) > 0;
    }
    public boolean removerVersaoPorIdCloud(int id){
        return getDatabase().delete(DatabaseHelper.Versoes.TABELA_CLOUD,
                "_id = ?", new String[]{Integer.toString(id)}) > 0;
    }

    public boolean removerVersaoPorTabela(String tabela){
        return getDatabase().delete(DatabaseHelper.Versoes.TABELA,
                "identificacaotabela = ?", new String[]{tabela}) > 0;
    }
    public boolean removerVersaoPorTabelaCloud(String tabela){
        return getDatabase().delete(DatabaseHelper.Versoes.TABELA_CLOUD,
                "identificacaotabela = ?", new String[]{tabela}) > 0;
    }

    public Versao buscarVersaoPorId(int id){
        Cursor cursor = getDatabase().query(DatabaseHelper.Versoes.TABELA,
                DatabaseHelper.Versoes.COLUNAS, "_id = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToNext()){
            Versao model = criarVersao(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public Versao buscarVersaoPorIdCloud(int id){
        Cursor cursor = getDatabase().query(DatabaseHelper.Versoes.TABELA_CLOUD,
                DatabaseHelper.Versoes.COLUNAS, "_id = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToNext()){
            Versao model = criarVersao(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }

    public Versao buscarVersaoPorTabela(String tabela){
        Cursor cursor = getDatabase().query(DatabaseHelper.Versoes.TABELA,
                DatabaseHelper.Versoes.COLUNAS, "identificacaotabela = ?", new String[]{tabela}, null, null, null);

        if (cursor.moveToNext()){
            Versao model = criarVersao(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public Versao buscarVersaoPorTabelaCloud(String tabela){
        Cursor cursor = getDatabase().query(DatabaseHelper.Versoes.TABELA_CLOUD,
                DatabaseHelper.Versoes.COLUNAS, "identificacaotabela = ?", new String[]{tabela}, null, null, null);

        if (cursor.moveToNext()){
            Versao model = criarVersao(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }

    public Versao buscarVersaoPorUID(String uid){
        Cursor cursor = getDatabase().query(DatabaseHelper.Versoes.TABELA,
                DatabaseHelper.Versoes.COLUNAS, "uid = ?", new String[]{uid}, null, null, null);

        if (cursor.moveToNext()){
            Versao model = criarVersao(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public Versao buscarVersaoPorUIDCloud(String uid){
        Cursor cursor = getDatabase().query(DatabaseHelper.Versoes.TABELA_CLOUD,
                DatabaseHelper.Versoes.COLUNAS, "uid = ?", new String[]{uid}, null, null, null);

        if (cursor.moveToNext()){
            Versao model = criarVersao(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }


    //AUXILIARES
    private SQLiteDatabase getDatabase(){
        if (this.database == null){
            this.database = this.databaseHelper.getWritableDatabase();
        }
        return this.database;
    }

    public void fechar(){
        this.databaseHelper.close();
        this.database = null;
    }

    private Versao criarVersao(Cursor cursor){
        Versao model = new Versao(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Versoes._ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.Versoes.IDENTIFICACAO_TABELA)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Versoes.VERSAO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.Versoes.DATA_MODIFICACAO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.Versoes.UID))
        );
        return model;
    }

    //UTILIDADE
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
