package com.example.sannotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class DBHelper extends SQLiteOpenHelper {
    Context context;
    public DBHelper(Context context) {
        super(context, "Notes.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table notes(id integer primary key autoincrement, title TEXT, note TEXT, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists notes");
        onCreate(db);
    }

    public void insertData(String title, String note, String date){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",title);
        values.put("note",note);
        values.put("date",date);
        long result = database.insert("notes",null,values);
        if(result == -1){
            Toast.makeText(context,"Not Inserted",Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(context, "Inserted", Toast.LENGTH_SHORT).show();
        }
    }
    public void updateData(String title_id, String title, String note,String date){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",title);
        values.put("note",note);
        values.put("date",date);
        long result = database.update("notes",values,"title=?",new String[]{title_id});
        if(result == -1){
            Toast.makeText(context,"Not Updated",Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteNote(String title_id){
        SQLiteDatabase database = this.getWritableDatabase();
        long result = database.delete("notes","title=?" ,new String[]{title_id});
        if(result == -1){
            Toast.makeText(context, "Failed To Delete.", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(context, "Deleted Successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAll(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from notes");
    }

    public Cursor getdata(){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("select * from notes",null);

    }
}
