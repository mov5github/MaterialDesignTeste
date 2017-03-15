package com.example.lucas.materialdesignteste.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.materialdesignteste.dao.model.Cabeleireiro;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 05/03/2017.
 */

public class CabeleireiroDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public CabeleireiroDAO(Context context){
        this.databaseHelper = new DatabaseHelper(context);
    }

    //ACESSOS
    public List<Cabeleireiro> listarCabeleireiro(){
        List<Cabeleireiro> cabeleireiros = new ArrayList<Cabeleireiro>();
        Cursor cursor = getDatabase().query(DatabaseHelper.Cabeleireiro.TABELA,
                DatabaseHelper.Cabeleireiro.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            Cabeleireiro model = criarCabeleireiro(cursor);
            cabeleireiros.add(model);
        }
        cursor.close();
        return cabeleireiros;
    }
    public List<Cabeleireiro> listarCabeleireiroCloud(){
        List<Cabeleireiro> cabeleireiros = new ArrayList<Cabeleireiro>();
        Cursor cursor = getDatabase().query(DatabaseHelper.Cabeleireiro.TABELA_CLOUD,
                DatabaseHelper.Cabeleireiro.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            Cabeleireiro model = criarCabeleireiro(cursor);
            cabeleireiros.add(model);
        }
        cursor.close();
        return cabeleireiros;
    }

    public long salvarCabeleireiro(Cabeleireiro cabeleireiro){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Cabeleireiro.NOME, cabeleireiro.getNome());
        values.put(DatabaseHelper.Cabeleireiro.FOTO, cabeleireiro.getFoto());
        values.put(DatabaseHelper.Cabeleireiro.CODIGO_UNICO, cabeleireiro.getCodigoUnico());


        if(cabeleireiro.get_id() != null){
            return this.database.update(DatabaseHelper.Cabeleireiro.TABELA, values,
                    "_id = ?", new String[]{cabeleireiro.get_id().toString()});
        }else {
            return getDatabase().insert(DatabaseHelper.Cabeleireiro.TABELA, null, values);
        }
    }
    public long salvarCabeleireiroCloud(Cabeleireiro cabeleireiro){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Cabeleireiro.NOME, cabeleireiro.getNome());
        values.put(DatabaseHelper.Cabeleireiro.FOTO, cabeleireiro.getFoto());
        values.put(DatabaseHelper.Cabeleireiro.CODIGO_UNICO, cabeleireiro.getCodigoUnico());


        if(cabeleireiro.get_id() != null){
            return this.database.update(DatabaseHelper.Cabeleireiro.TABELA_CLOUD, values,
                    "_id = ?", new String[]{cabeleireiro.get_id().toString()});
        }else {
            return getDatabase().insert(DatabaseHelper.Cabeleireiro.TABELA_CLOUD, null, values);
        }
    }

    public boolean removerCabeleireiroPorId(int id){
        return getDatabase().delete(DatabaseHelper.Cabeleireiro.TABELA,
                "_id = ?", new String[]{Integer.toString(id)}) > 0;
    }
    public boolean removerCabeleireiroPorIdCloud(int id){
        return getDatabase().delete(DatabaseHelper.Cabeleireiro.TABELA_CLOUD,
                "_id = ?", new String[]{Integer.toString(id)}) > 0;
    }

    public boolean removerCabeleireiroPorNome(String nome){
        return getDatabase().delete(DatabaseHelper.Cabeleireiro.TABELA,
                "dia = ?", new String[]{nome}) > 0;
    }
    public boolean removerCabeleireiroPorNomeCloud(String nome){
        return getDatabase().delete(DatabaseHelper.Cabeleireiro.TABELA_CLOUD,
                "dia = ?", new String[]{nome}) > 0;
    }

    public Cabeleireiro buscarCabeleireiroPorId(int id){
        Cursor cursor = getDatabase().query(DatabaseHelper.Cabeleireiro.TABELA,
                DatabaseHelper.Cabeleireiro.COLUNAS, "_id = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToNext()){
            Cabeleireiro model = criarCabeleireiro(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public Cabeleireiro buscarCabeleireiroPorIdCloud(int id){
        Cursor cursor = getDatabase().query(DatabaseHelper.Cabeleireiro.TABELA_CLOUD,
                DatabaseHelper.Cabeleireiro.COLUNAS, "_id = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToNext()){
            Cabeleireiro model = criarCabeleireiro(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }

    public Cabeleireiro buscarCabeleireiroPorNome(String nome){
        Cursor cursor = getDatabase().query(DatabaseHelper.Cabeleireiro.TABELA,
                DatabaseHelper.Cabeleireiro.COLUNAS, "dia = ?", new String[]{nome}, null, null, null);

        if (cursor.moveToNext()){
            Cabeleireiro model = criarCabeleireiro(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public Cabeleireiro buscarCabeleireiroPorNomeCloud(String nome){
        Cursor cursor = getDatabase().query(DatabaseHelper.Cabeleireiro.TABELA_CLOUD,
                DatabaseHelper.Cabeleireiro.COLUNAS, "dia = ?", new String[]{nome}, null, null, null);

        if (cursor.moveToNext()){
            Cabeleireiro model = criarCabeleireiro(cursor);
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

    private Cabeleireiro criarCabeleireiro(Cursor cursor){
        Cabeleireiro model = new Cabeleireiro(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Cabeleireiro._ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.Cabeleireiro.NOME)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Cabeleireiro.FOTO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.Cabeleireiro.CODIGO_UNICO))
        );
        return model;
    }
}
