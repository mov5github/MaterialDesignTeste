package com.example.lucas.materialdesignteste.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.materialdesignteste.dao.model.Cabeleireiro;
import com.example.lucas.materialdesignteste.dao.model.CadastroInicial;
import com.example.lucas.materialdesignteste.dao.model.Funcionamento;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lucas on 05/03/2017.
 */

public class CadastroInicialDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public CadastroInicialDAO(Context context){
        this.databaseHelper = new DatabaseHelper(context);
    }

    //ACESSOS
    public List<CadastroInicial> listarCadastrosIniciais(){
        List<CadastroInicial> cadastrosIniciais = new ArrayList<CadastroInicial>();
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroInicial.TABELA,
                DatabaseHelper.CadastroInicial.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            CadastroInicial model = criarCadastroInicial(cursor);
            cadastrosIniciais.add(model);
        }
        cursor.close();
        return cadastrosIniciais;
    }
    public List<CadastroInicial> listarCadastrosIniciaisCloud(){
        List<CadastroInicial> cadastrosIniciais = new ArrayList<CadastroInicial>();
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroInicial.TABELA_CLOUD,
                DatabaseHelper.CadastroInicial.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            CadastroInicial model = criarCadastroInicial(cursor);
            cadastrosIniciais.add(model);
        }
        cursor.close();
        return cadastrosIniciais;
    }

    public long salvarCadastroInicial(CadastroInicial cadastroInicial){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CadastroInicial.NIVEL_USUARIO, cadastroInicial.getNivelUsuario());
        if (cadastroInicial.getTipoUsuario() != null && !cadastroInicial.getTipoUsuario().isEmpty()){
            values.put(DatabaseHelper.CadastroInicial.TIPO_USUARIO, cadastroInicial.getTipoUsuario());
        }

        if (cadastroInicial.getCodigoUnico() != null && !cadastroInicial.getCodigoUnico().toString().isEmpty()){
            values.put(DatabaseHelper.CadastroInicial.CODIGO_UNICO, cadastroInicial.getCodigoUnico());
        }
        values.put(DatabaseHelper.CadastroInicial.UID, cadastroInicial.getUid());

        if (cadastroInicial.getVersao() == null){
            cadastroInicial.setVersao(1);
        }else {
            cadastroInicial.setVersao(cadastroInicial.getVersao()+1);
        }
        values.put(DatabaseHelper.CadastroInicial.VERSAO, cadastroInicial.getVersao());

        cadastroInicial.setDataModificalao(getDateTime());
        values.put(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO, cadastroInicial.getDataModificalao());

        if(cadastroInicial.get_id() != null){
            long retorno = this.database.update(DatabaseHelper.CadastroInicial.TABELA, values,
                    "_id = ?", new String[]{cadastroInicial.get_id().toString()});
            if (retorno < 1){
                return -1;
            }else {
                return retorno;
            }
        }else {
            return getDatabase().insert(DatabaseHelper.CadastroInicial.TABELA, null, values);
        }
    }
    public long salvarCadastroInicialCloud(CadastroInicial cadastroInicial){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CadastroInicial.NIVEL_USUARIO, cadastroInicial.getNivelUsuario());
        if (cadastroInicial.getTipoUsuario() != null && !cadastroInicial.getTipoUsuario().isEmpty()){
            values.put(DatabaseHelper.CadastroInicial.TIPO_USUARIO, cadastroInicial.getTipoUsuario());
        }

        if (cadastroInicial.getCodigoUnico() != null && !cadastroInicial.getCodigoUnico().toString().isEmpty()){
            values.put(DatabaseHelper.CadastroInicial.CODIGO_UNICO, cadastroInicial.getCodigoUnico());
        }
        values.put(DatabaseHelper.CadastroInicial.UID, cadastroInicial.getUid());

        if (cadastroInicial.getVersao() == null){
            cadastroInicial.setVersao(1);
        }else {
            cadastroInicial.setVersao(cadastroInicial.getVersao()+1);
        }
        values.put(DatabaseHelper.CadastroInicial.VERSAO, cadastroInicial.getVersao());

        cadastroInicial.setDataModificalao(getDateTime());
        values.put(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO, cadastroInicial.getDataModificalao());

        if(cadastroInicial.get_id() != null){
            long retorno = this.database.update(DatabaseHelper.CadastroInicial.TABELA_CLOUD, values,
                    "_id = ?", new String[]{cadastroInicial.get_id().toString()});
            if (retorno < 1){
                return -1;
            }else {
                return retorno;
            }
        }else {
            return getDatabase().insert(DatabaseHelper.CadastroInicial.TABELA_CLOUD, null, values);
        }
    }

    public boolean removerCadastroInicialPorId(int id){
        return getDatabase().delete(DatabaseHelper.CadastroInicial.TABELA,
                "_id = ?", new String[]{Integer.toString(id)}) > 0;
    }
    public boolean removerCadastroInicialPorIdCloud(int id){
        return getDatabase().delete(DatabaseHelper.CadastroInicial.TABELA_CLOUD,
                "_id = ?", new String[]{Integer.toString(id)}) > 0;
    }

    public boolean removerCadastroInicialPorUID(String uid){
        return getDatabase().delete(DatabaseHelper.CadastroInicial.TABELA,
                "uid = ?", new String[]{uid}) > 0;
    }
    public boolean removerCadastroInicialPorUIDCloud(String uid){
        return getDatabase().delete(DatabaseHelper.CadastroInicial.TABELA_CLOUD,
                "dia = ?", new String[]{uid}) > 0;
    }

    public CadastroInicial buscarCadastroInicialPorId(int id){
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroInicial.TABELA,
                DatabaseHelper.CadastroInicial.COLUNAS, "_id = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToNext()){
            CadastroInicial model = criarCadastroInicial(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public CadastroInicial buscarCadastroInicialPorIdCloud(int id){
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroInicial.TABELA_CLOUD,
                DatabaseHelper.CadastroInicial.COLUNAS, "_id = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToNext()){
            CadastroInicial model = criarCadastroInicial(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }

    public CadastroInicial buscarCadastroInicialPorUID(String uid){
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroInicial.TABELA,
                DatabaseHelper.CadastroInicial.COLUNAS, "uid = ?", new String[]{uid}, null, null, null);

        if (cursor.moveToNext()){
            CadastroInicial model = criarCadastroInicial(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public CadastroInicial buscarCadastroInicialPorUIDCloud(String uid){
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroInicial.TABELA_CLOUD,
                DatabaseHelper.CadastroInicial.COLUNAS, "uid = ?", new String[]{uid}, null, null, null);

        if (cursor.moveToNext()){
            CadastroInicial model = criarCadastroInicial(cursor);
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

    private CadastroInicial criarCadastroInicial(Cursor cursor){
        CadastroInicial model = new CadastroInicial(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroInicial._ID)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.CadastroInicial.NIVEL_USUARIO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroInicial.TIPO_USUARIO)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroInicial.CODIGO_UNICO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroInicial.UID)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroInicial.VERSAO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO))
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
