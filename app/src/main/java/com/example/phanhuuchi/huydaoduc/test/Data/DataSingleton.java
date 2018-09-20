package com.example.phanhuuchi.huydaoduc.test.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.phanhuuchi.huydaoduc.test.model.Word;

import java.util.ArrayList;
import java.util.List;


public class DataSingleton {
    //Truy vấn (design patterm)
    private static DataSingleton INSTANCE;
    private WordSqlite wordSqlite;

    private DataSingleton(Context context){
        wordSqlite = new WordSqlite(context);

    }

    public static DataSingleton getINSTANCE(Context context){
        if (INSTANCE == null){
            return new DataSingleton(context);
        }
        else {
            return INSTANCE;
        }
    }

    public List<Word> getWords(){
        List<Word> words = new ArrayList<>();

        String[] columns = {
                DBSQL.COLUMN_WORD_ID,
                DBSQL.COLUMN_WORD_TEN,
                DBSQL.COLUMN_WORD_MOTA,
                DBSQL.COLUMN_WORD_IMAGE
        };

        String selectId = DBSQL.COLUMN_WORD_ID + " = ?";
        String[] selectArr = {selectId};

        SQLiteDatabase database = wordSqlite.getReadableDatabase();

        Cursor cursor = database.query(DBSQL.WORD_TABLE,columns,selectId,selectArr,null,null,null);
        if (cursor!=null && cursor.getCount() > 0){
            while (cursor.moveToNext()){

                int wordId = cursor.getInt(cursor.getColumnIndexOrThrow(DBSQL.COLUMN_WORD_ID));
                String wordTen = cursor.getString(cursor.getColumnIndexOrThrow(DBSQL.COLUMN_WORD_TEN));
                String wordMota = cursor.getString(cursor.getColumnIndexOrThrow(DBSQL.COLUMN_WORD_MOTA));
                //byte[] wordImaga = cursor.getBlob(cursor.getColumnIndexOrThrow(DBSQL.COLUMN_WORD_IMAGE));

                Word word = new Word();
                word.setId(wordId);
                word.setTen(wordTen);
                word.setMota(wordMota);
                words.add(word);
            }
        }

        if (cursor != null){
            cursor.close();
        }

        database.close();

        return words;
    }

    //
    public  void insert (Word word){
        SQLiteDatabase database = wordSqlite.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBSQL.COLUMN_WORD_ID,word.getId());
        contentValues.put(DBSQL.COLUMN_WORD_TEN,word.getTen());
        contentValues.put(DBSQL.COLUMN_WORD_MOTA,word.getMota());
        //contentValues.put(DBSQL.COLUMN_WORD_IMAGE,word.getWordImage());

        database.insert(DBSQL.WORD_TABLE,null,contentValues);
        database.close();
    }

    public void update (Word word){
        SQLiteDatabase database = wordSqlite.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBSQL.COLUMN_WORD_ID,word.getId());
        contentValues.put(DBSQL.COLUMN_WORD_TEN,word.getTen());
        contentValues.put(DBSQL.COLUMN_WORD_MOTA,word.getMota());
        //contentValues.put(DBSQL.COLUMN_WORD_IMAGE,word.getImage());

        String select = DBSQL.COLUMN_WORD_ID + " = ?";
        int[] selectArr = {word.getId()};

        database.update(DBSQL.WORD_TABLE,contentValues,select,new String[] {String.valueOf(selectArr)});
        database.close();
    }

    public void delete (String wId){
        SQLiteDatabase database = wordSqlite.getWritableDatabase();

        String select = DBSQL.COLUMN_WORD_ID + " = ?";
        String[] selectArr = {wId};

        database.delete(DBSQL.WORD_TABLE,select,selectArr);
        database.close();

    }


}
