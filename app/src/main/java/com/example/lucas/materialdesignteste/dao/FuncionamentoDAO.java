package com.example.lucas.materialdesignteste.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.materialdesignteste.dao.model.Funcionamento;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 03/03/2017.
 */

public class FuncionamentoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private static final String SEGUNDA = "segunda";
    private static final String TERCA = "terca";
    private static final String QUARTA = "quarta";
    private static final String QUINTA = "quinta";
    private static final String SEXTA = "sexta";
    private static final String SABADO = "sabado";
    private static final String DOMINGO = "domingo";







    public FuncionamentoDAO(Context context){
        this.databaseHelper = new DatabaseHelper(context);
    }


    //ACESSOS
    public List<Funcionamento> listarFuncionamentos(){
        List<Funcionamento> funcionamentos = new ArrayList<Funcionamento>();
        Cursor cursor = getDatabase().query(DatabaseHelper.Funcionamento.TABELA,
                DatabaseHelper.Funcionamento.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            Funcionamento model = criarFuncionamento(cursor);
            funcionamentos.add(model);
        }
        cursor.close();
        return funcionamentos;
    }
    public List<Funcionamento> listarFuncionamentosCloud(){
        List<Funcionamento> funcionamentos = new ArrayList<Funcionamento>();
        Cursor cursor = getDatabase().query(DatabaseHelper.Funcionamento.TABELA_CLOUD,
                DatabaseHelper.Funcionamento.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            Funcionamento model = criarFuncionamento(cursor);
            funcionamentos.add(model);
        }
        cursor.close();
        return funcionamentos;
    }

    public long salvarFuncionamento(Funcionamento funcionamento){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Funcionamento.DIA, funcionamento.getDia());
        values.put(DatabaseHelper.Funcionamento.ABRE, funcionamento.getAbre());
        values.put(DatabaseHelper.Funcionamento.FECHA, funcionamento.getFecha());


        if(funcionamento.get_id() != null){
            return this.database.update(DatabaseHelper.Funcionamento.TABELA, values,
                    "_id = ?", new String[]{funcionamento.get_id().toString()});
        }else {
            return getDatabase().insert(DatabaseHelper.Funcionamento.TABELA, null, values);
        }
    }
    public long salvarFuncionamentoCloud(Funcionamento funcionamento){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Funcionamento.DIA, funcionamento.getDia());
        values.put(DatabaseHelper.Funcionamento.ABRE, funcionamento.getAbre());
        values.put(DatabaseHelper.Funcionamento.FECHA, funcionamento.getFecha());


        if(funcionamento.get_id() != null){
            return this.database.update(DatabaseHelper.Funcionamento.TABELA_CLOUD, values,
                    "_id = ?", new String[]{funcionamento.get_id().toString()});
        }else {
            return getDatabase().insert(DatabaseHelper.Funcionamento.TABELA_CLOUD, null, values);
        }
    }

    public boolean removerFuncionamentoPorId(int id){
        return getDatabase().delete(DatabaseHelper.Funcionamento.TABELA,
                "_id = ?", new String[]{Integer.toString(id)}) > 0;
    }
    public boolean removerFuncionamentoPorIdCloud(int id){
        return getDatabase().delete(DatabaseHelper.Funcionamento.TABELA_CLOUD,
                "_id = ?", new String[]{Integer.toString(id)}) > 0;
    }

    public boolean removerFuncionamentoPorDia(String dia){
        return getDatabase().delete(DatabaseHelper.Funcionamento.TABELA,
                "dia = ?", new String[]{dia}) > 0;
    }
    public boolean removerFuncionamentoPorDiaCloud(String dia){
        return getDatabase().delete(DatabaseHelper.Funcionamento.TABELA_CLOUD,
                "dia = ?", new String[]{dia}) > 0;
    }

    public Funcionamento buscarFuncionamentoPorId(int id){
        Cursor cursor = getDatabase().query(DatabaseHelper.Funcionamento.TABELA,
                DatabaseHelper.Funcionamento.COLUNAS, "_id = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToNext()){
            Funcionamento model = criarFuncionamento(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public Funcionamento buscarFuncionamentoPorIdCloud(int id){
        Cursor cursor = getDatabase().query(DatabaseHelper.Funcionamento.TABELA_CLOUD,
                DatabaseHelper.Funcionamento.COLUNAS, "_id = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToNext()){
            Funcionamento model = criarFuncionamento(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }

    public Funcionamento buscarFuncionamentoPorDia(String dia){
        Cursor cursor = getDatabase().query(DatabaseHelper.Funcionamento.TABELA,
                DatabaseHelper.Funcionamento.COLUNAS, "dia = ?", new String[]{dia}, null, null, null);

        if (cursor.moveToNext()){
            Funcionamento model = criarFuncionamento(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public Funcionamento buscarFuncionamentoPorDiaCloud(String dia){
        Cursor cursor = getDatabase().query(DatabaseHelper.Funcionamento.TABELA_CLOUD,
                DatabaseHelper.Funcionamento.COLUNAS, "dia = ?", new String[]{dia}, null, null, null);

        if (cursor.moveToNext()){
            Funcionamento model = criarFuncionamento(cursor);
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

    private Funcionamento criarFuncionamento(Cursor cursor){
        Funcionamento model = new Funcionamento(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Funcionamento._ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.Funcionamento.DIA)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.Funcionamento.ABRE)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.Funcionamento.FECHA))
        );
        return model;
    }

    //GETTERS
    public static String getTERCA() {
        return TERCA;
    }

    public static String getDOMINGO() {
        return DOMINGO;
    }

    public static String getSABADO() {
        return SABADO;
    }

    public static String getSEXTA() {
        return SEXTA;
    }

    public static String getQUINTA() {
        return QUINTA;
    }

    public static String getQUARTA() {
        return QUARTA;
    }

    public static String getSEGUNDA() {
        return SEGUNDA;
    }

}
