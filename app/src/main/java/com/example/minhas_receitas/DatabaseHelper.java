package com.example.minhas_receitas;
// DatabaseHelper.java
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MinhasReceitasDB";
    private static final int DATABASE_VERSION = 1;

    // Tabelas
    private static final String TABLE_USERS = "users";
    private static final String TABLE_RECEITAS = "receitas";

    // Colunas da tabela users
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    // private static final String COLUMN_USER_NOME = "nome"; // Removida a coluna nome

    // Colunas da tabela receitas
    private static final String COLUMN_RECEITA_ID = "id";
    private static final String COLUMN_RECEITA_USER_EMAIL = "user_email";
    private static final String COLUMN_RECEITA_TITULO = "titulo";
    private static final String COLUMN_RECEITA_IMAGEM = "imagem";
    private static final String COLUMN_RECEITA_INGREDIENTES = "ingredientes";
    private static final String COLUMN_RECEITA_MODO_PREPARO = "modo_preparo";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criar tabela users
        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_EMAIL + " TEXT PRIMARY KEY,"
                + COLUMN_USER_PASSWORD + " TEXT" + ")"; // Removida a coluna nome
        db.execSQL(CREATE_TABLE_USERS);

        // Criar tabela receitas
        String CREATE_TABLE_RECEITAS = "CREATE TABLE " + TABLE_RECEITAS + "("
                + COLUMN_RECEITA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_RECEITA_USER_EMAIL + " TEXT,"
                + COLUMN_RECEITA_TITULO + " TEXT,"
                + COLUMN_RECEITA_IMAGEM + " BLOB,"
                + COLUMN_RECEITA_INGREDIENTES + " TEXT," // Armazenar como String separada por vírgulas
                + COLUMN_RECEITA_MODO_PREPARO + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_RECEITA_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_EMAIL + ")" + ")";
        db.execSQL(CREATE_TABLE_RECEITAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Atualizar o banco de dados (ex: quando mudar a versão do esquema)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEITAS);
        onCreate(db);
    }

    // Métodos para a tabela users

    public boolean insertUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_EMAIL, email);
        contentValues.put(COLUMN_USER_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean updatePassword(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_PASSWORD, password);
        int rowsAffected = db.update(TABLE_USERS, contentValues, COLUMN_USER_EMAIL + " = ?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    // Métodos para a tabela receitas

    public boolean insertReceita(String userEmail, String titulo, byte[] imagem, List<String> ingredientes, String modoPreparo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RECEITA_USER_EMAIL, userEmail);
        contentValues.put(COLUMN_RECEITA_TITULO, titulo);
        contentValues.put(COLUMN_RECEITA_IMAGEM, imagem);
        contentValues.put(COLUMN_RECEITA_INGREDIENTES, joinStrings(ingredientes)); // Converter lista para String
        contentValues.put(COLUMN_RECEITA_MODO_PREPARO, modoPreparo);
        long result = db.insert(TABLE_RECEITAS, null, contentValues);
        db.close();
        return result != -1;
    }

    public List<Receita> getUserRecipes(String userEmail) {
        List<Receita> receitas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_RECEITA_ID, COLUMN_RECEITA_USER_EMAIL, COLUMN_RECEITA_TITULO, COLUMN_RECEITA_IMAGEM, COLUMN_RECEITA_INGREDIENTES, COLUMN_RECEITA_MODO_PREPARO};
        String selection = COLUMN_RECEITA_USER_EMAIL + " = ?";
        String[] selectionArgs = new String[]{userEmail};
        Cursor cursor = db.query(
                TABLE_RECEITAS, // A tabela a ser consultada
                columns,        // As colunas a retornar (null para todas)
                selection,   // As colunas para a cláusula WHERE
                selectionArgs, // Os valores para a cláusula WHERE
                null,        // Agrupar as linhas
                null,        // Filtrar por grupos
                null         // A ordem de classificação
        );

        // Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECEITAS + " WHERE " + COLUMN_RECEITA_USER_EMAIL + " = ?", new String[]{userEmail});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Receita receita = new Receita();
                receita.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_USER_EMAIL)));
                receita.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_ID)));
                receita.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_TITULO)));
                receita.setImagem(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_IMAGEM)));
                receita.setIngredientes(splitString(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_INGREDIENTES)))); // Converter String para lista
                receita.setModoPreparo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_MODO_PREPARO)));
                receitas.add(receita);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return receitas;
    }

    public Receita getReceita(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RECEITAS + " WHERE " + COLUMN_RECEITA_ID + " = ?", new String[]{String.valueOf(id)});
        Receita receita = null;

        if (cursor.moveToFirst()) {
            receita = new Receita();
            receita.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_ID)));
            receita.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_USER_EMAIL)));
            receita.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_TITULO)));
            receita.setImagem(cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_IMAGEM)));
            receita.setIngredientes(splitString(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_INGREDIENTES))));
            receita.setModoPreparo(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECEITA_MODO_PREPARO)));
        }

        cursor.close();
        db.close();
        return receita;
    }

    public boolean updateReceita(int id, String titulo, byte[] imagem, List<String> ingredientes, String modoPreparo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_RECEITA_TITULO, titulo);
        contentValues.put(COLUMN_RECEITA_IMAGEM, imagem);
        contentValues.put(COLUMN_RECEITA_INGREDIENTES, joinStrings(ingredientes));
        contentValues.put(COLUMN_RECEITA_MODO_PREPARO, modoPreparo);
        int rowsAffected = db.update(TABLE_RECEITAS, contentValues, COLUMN_RECEITA_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean deleteReceita(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_RECEITAS, COLUMN_RECEITA_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    // Métodos utilitários para converter List<String> para String e vice-versa
    private String joinStrings(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(","); // Separador (pode ser outro caractere)
            }
        }
        return sb.toString();
    }

    private List<String> splitString(String str) {
        return new ArrayList<>(Arrays.asList(str.split(",")));
    }
}