package com.example.lucas.materialdesignteste.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucas on 03/03/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String NOME_BD = "principal";
    private static final int VERSAO_BD = 1;

    public DatabaseHelper(Context context){
        super(context,NOME_BD,null,VERSAO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TABELA DE VERSOES
        db.execSQL("create table versoes(_id integer primary key autoincrement, "
                +"identificacaotabela text not null, versao integer not null, datamodificacao text not null, uid text not null)");


        //TABELA DE VERSOES CLOUD
        db.execSQL("create table versoescloud(_id integer primary key autoincrement, "
                +"identificacaotabela text not null, versao integer not null, datamodificacao text not null, uid text not null)");

        //TABELA DE FUNCIONAMENTO
        db.execSQL("create table funcionamento(_id integer primary key autoincrement, "
                +"dia text not null, abre text not null, fecha text not null)");

        //TABELA DE FUNCIONAMENTO CLOUD
        db.execSQL("create table funcionamentocloud(_id integer primary key autoincrement, "
                +"dia text not null, abre text not null, fecha text not null)");

        //TABELA DE SERVICO
        db.execSQL("create table servico(_id integer primary key autoincrement, "
                +"nome text not null, icone integer not null, duracao integer not null, preco real, descricao text not null)");

        //TABELA DE SERVICO CLOUD
        db.execSQL("create table servicocloud(_id integer primary key autoincrement, "
                +"nome text not null, icone integer not null, duracao integer not null, preco real, descricao text not null)");

        //TABELA DE CABELEIREIRO
        db.execSQL("create table cabeleireiro(_id integer primary key autoincrement, "
                +"nome text not null, foto integer not null, codigounico text not null)");

        //TABELA DE CABELEIREIRO CLOUD
        db.execSQL("create table cabeleireirocloud(_id integer primary key autoincrement, "
                +"nome text not null, foto integer not null, codigounico text not null)");

        //TABELA DE CADASTRO INICIAL
        db.execSQL("create table cadastroinicial(_id integer primary key autoincrement, "
                +"nivelusuario real not null, tipousuario text, codigounico integer, uid text not null, versao integer not null)");

        //TABELA DE CADASTRO INICIAL CLOUD
        db.execSQL("create table cadastroinicialcloud(_id integer primary key autoincrement, "
                +"nivelusuario real not null, tipousuario text, codigounico integer, uid text not null, versao integer not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static class Versoes{
        public static  final String TABELA = "versoes";
        public static  final String TABELA_CLOUD = "versoescloud";
        public static  final String _ID = "_id";
        public static  final String IDENTIFICACAO_TABELA = "identificacaotabela";
        public static  final String VERSAO = "versao";
        public static  final String DATA_MODIFICACAO = "datamodificacao";
        public static  final String UID = "uid";

        public static  final String[] COLUNAS = new String[]{_ID, IDENTIFICACAO_TABELA, VERSAO, DATA_MODIFICACAO, UID};
    }

    public static class Funcionamento{
        public static  final String TABELA = "funcionamento";
        public static  final String TABELA_CLOUD = "funcionamentocloud";
        public static  final String _ID = "_id";
        public static  final String DIA = "dia";
        public static  final String ABRE = "abre";
        public static  final String FECHA = "fecha";

        public static  final String[] COLUNAS = new String[]{_ID, DIA, ABRE, FECHA};

    }

    public static class Servico{
        public static  final String TABELA = "servico";
        public static  final String TABELA_CLOUD = "servicocloud";
        public static  final String _ID = "_id";
        public static  final String NOME = "nome";
        public static  final String ICONE = "icone";
        public static  final String DURACAO = "duracao";
        public static  final String PRECO = "preco";
        public static  final String DESCRICAO = "descricao";

        public static  final String[] COLUNAS = new String[]{_ID, NOME, ICONE, DURACAO, PRECO, DESCRICAO};

    }

    public static class Cabeleireiro{
        public static  final String TABELA = "cabeleireiro";
        public static  final String TABELA_CLOUD = "cabeleireirocloud";
        public static  final String _ID = "_id";
        public static  final String NOME = "nome";
        public static  final String FOTO = "foto";
        public static  final String CODIGO_UNICO = "codigounico";

        public static  final String[] COLUNAS = new String[]{_ID, NOME, FOTO, CODIGO_UNICO};

    }

    public static class CadastroInicial{
        public static  final String TABELA = "cadastroinicial";
        public static  final String TABELA_CLOUD = "cadastroinicialcloud";
        public static  final String _ID = "_id";
        public static  final String NIVEL_USUARIO = "nivelusuario";
        public static  final String TIPO_USUARIO = "tipousuario";
        public static  final String CODIGO_UNICO = "codigounico";
        public static  final String UID = "uid";
        public static  final String VERSAO = "versao";
        public static  final String DATA_MODIFICACAO = "datamodificacao";


        public static  final String[] COLUNAS = new String[]{_ID, NIVEL_USUARIO, TIPO_USUARIO, CODIGO_UNICO, UID, VERSAO, DATA_MODIFICACAO};

    }
}
