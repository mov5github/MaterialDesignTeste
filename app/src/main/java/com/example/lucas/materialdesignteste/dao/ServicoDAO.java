package com.example.lucas.materialdesignteste.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.materialdesignteste.dao.model.Servico;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 05/03/2017.
 */

public class ServicoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public ServicoDAO(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    //ACESSOS
    public List<Servico> listarServicos(){
        List<Servico> servicos = new ArrayList<Servico>();
        Cursor cursor = getDatabase().query(DatabaseHelper.Servico.TABELA,
                DatabaseHelper.Servico.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            Servico model = criarServico(cursor);
            servicos.add(model);
        }
        cursor.close();
        return servicos;
    }
    public List<Servico> listarServicosCloud(){
        List<Servico> servicos = new ArrayList<Servico>();
        Cursor cursor = getDatabase().query(DatabaseHelper.Servico.TABELA_CLOUD,
                DatabaseHelper.Servico.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            Servico model = criarServico(cursor);
            servicos.add(model);
        }
        cursor.close();
        return servicos;
    }

    public long salvarServico(Servico servico){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Servico.NOME, servico.getNome());
        values.put(DatabaseHelper.Servico.ICONE, servico.getIcone());
        values.put(DatabaseHelper.Servico.DURACAO, servico.getDuracao());
        values.put(DatabaseHelper.Servico.PRECO, servico.getPreco());
        values.put(DatabaseHelper.Servico.DESCRICAO, servico.getDescricao());


        if(servico.get_id() != null){
            return this.database.update(DatabaseHelper.Servico.TABELA, values,
                    "_id = ?", new String[]{servico.get_id().toString()});
        }else {
            return getDatabase().insert(DatabaseHelper.Servico.TABELA, null, values);
        }
    }
    public long salvarServicoCloud(Servico servico){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.Servico.NOME, servico.getNome());
        values.put(DatabaseHelper.Servico.ICONE, servico.getIcone());
        values.put(DatabaseHelper.Servico.DURACAO, servico.getDuracao());
        values.put(DatabaseHelper.Servico.PRECO, servico.getPreco());
        values.put(DatabaseHelper.Servico.DESCRICAO, servico.getDescricao());


        if(servico.get_id() != null){
            return this.database.update(DatabaseHelper.Servico.TABELA_CLOUD, values,
                    "_id = ?", new String[]{servico.get_id().toString()});
        }else {
            return getDatabase().insert(DatabaseHelper.Servico.TABELA_CLOUD, null, values);
        }
    }

    public boolean removerServicoPorId(int id){
        return getDatabase().delete(DatabaseHelper.Servico.TABELA,
                "_id = ?", new String[]{Integer.toString(id)}) > 0;
    }
    public boolean removerServicoPorIdCloud(int id){
        return getDatabase().delete(DatabaseHelper.Servico.TABELA_CLOUD,
                "_id = ?", new String[]{Integer.toString(id)}) > 0;
    }

    public boolean removerServicoPorNome(String nome){
        return getDatabase().delete(DatabaseHelper.Servico.TABELA,
                "nome = ?", new String[]{nome}) > 0;
    }
    public boolean removerServicoPorNomeCloud(String nome){
        return getDatabase().delete(DatabaseHelper.Servico.TABELA_CLOUD,
                "nome = ?", new String[]{nome}) > 0;
    }

    public Servico buscarServicoPorId(int id){
        Cursor cursor = getDatabase().query(DatabaseHelper.Servico.TABELA,
                DatabaseHelper.Servico.COLUNAS, "_id = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToNext()){
            Servico model = criarServico(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public Servico buscarServicoPorIdCloud(int id){
        Cursor cursor = getDatabase().query(DatabaseHelper.Servico.TABELA_CLOUD,
                DatabaseHelper.Servico.COLUNAS, "_id = ?", new String[]{Integer.toString(id)}, null, null, null);

        if (cursor.moveToNext()){
            Servico model = criarServico(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }

    public Servico buscarServicoPorNome(String nome){
        Cursor cursor = getDatabase().query(DatabaseHelper.Servico.TABELA,
                DatabaseHelper.Servico.COLUNAS, "nome = ?", new String[]{nome}, null, null, null);

        if (cursor.moveToNext()){
            Servico model = criarServico(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public Servico buscarServicoPorNomeCloud(String nome){
        Cursor cursor = getDatabase().query(DatabaseHelper.Servico.TABELA_CLOUD,
                DatabaseHelper.Servico.COLUNAS, "dia = ?", new String[]{nome}, null, null, null);

        if (cursor.moveToNext()){
            Servico model = criarServico(cursor);
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

    private Servico criarServico(Cursor cursor){
        Servico model = new Servico(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Servico._ID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.Servico.NOME)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Servico.ICONE)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.Servico.DURACAO)),
                cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.Servico.PRECO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.Servico.DESCRICAO))
        );
        return model;
    }
}
