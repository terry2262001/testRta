package com.example.testrta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.testrta.Model.Data;

import java.util.Calendar;

public class DbHelper {
    String dbName ="dbImportedDataManagement";
    String tblImported = "tblImportedData";

    public static final String COL_INSTANCE_ID= "instanceid";
    public static final String COL_PATH= "path";

    Context mContext;
    public DbHelper(Context mContext) {
        this.mContext = mContext;
    }
    public SQLiteDatabase openDB(){
        return mContext.openOrCreateDatabase(dbName,Context.MODE_PRIVATE,null);
    }
    public void closeDB(SQLiteDatabase db ){db.close();    }


    public void createTableImportedData(){
        SQLiteDatabase db = openDB();
        String sql = "create table if not exists " + tblImported + "(" + "" +
                "instanceid TEXT primary key,"
                +"path TEXT )";
        db.execSQL(sql);
        closeDB(db);
    }
    public long insertImportedData(Data data){
        SQLiteDatabase db = openDB();


        Cursor cursor = db.rawQuery("SELECT * FROM tblImportedData WHERE instanceid = ?", new String[]{data.getInstanceid()});
        System.out.println(cursor+"tho1");

        if (cursor.moveToFirst()){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_INSTANCE_ID,data.getInstanceid()+" " + Calendar.getInstance().getTime());
            contentValues.put(COL_PATH,data.getPath());
            int tmp = db.update(tblImported,contentValues, "instanceid = ?", new String[]{data.getInstanceid()});
            db.close();
            return tmp;
        }else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_INSTANCE_ID,data.getInstanceid());
            contentValues.put(COL_PATH,data.getPath());
            long tmp  = db.insert(tblImported, null,contentValues);
            db.close();
            return tmp;


        }
//        contentValues.put(COL_INSTANCE_ID,data.getInstanceid());
//        contentValues.put(COL_PATH,data.getPath());
//        long tmp  = db.insert(tblImported, null,contentValues);
//        db.close();
//        return tmp;
    }





    }
