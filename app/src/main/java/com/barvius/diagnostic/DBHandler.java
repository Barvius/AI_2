package com.barvius.diagnostic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.barvius.diagnostic.entity.Diagnose;
import com.barvius.diagnostic.entity.Symptom;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {
    private static DBHandler instance = null;

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=on" );
        db.execSQL("CREATE TABLE IF NOT EXISTS symptom (" +
                "id INTEGER PRIMARY KEY,"+
                "name TEXT NOT NULL," +
                "hash TEXT NOT NULL," +
                "UNIQUE(hash)"+
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS diagnose (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "hash TEXT NOT NULL," +
                "UNIQUE(hash)"+
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS symptom_diagnose (" +
                "diagnose INTEGER NOT NULL," +
                "symptom INTEGER NOT NULL," +
                "confidence DOUBLE NOT NULL," +
                "FOREIGN KEY (diagnose) REFERENCES diagnose(id) ON DELETE CASCADE,"+
                "FOREIGN KEY (symptom) REFERENCES symptom(id) ON DELETE CASCADE,"+
                "UNIQUE(diagnose,symptom)"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS symptom");
//        db.execSQL("DROP TABLE IF EXISTS diagnose");
//        db.execSQL("DROP TABLE IF EXISTS symptom_diagnose");
        onCreate(db);
    }

    public long insertSymptom(Symptom symptom){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", symptom.getName());
        values.put("hash", DBHandler.HASH(symptom.getName()));
        long id = db.insert("symptom", null, values);
        db.close();
        return id;
    }

    public long insertDiagnoseName(Diagnose diagnose){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values;
        values = new ContentValues();
        values.put("name", diagnose.getName());
        values.put("hash", DBHandler.HASH(diagnose.getName()));
        long id = db.insert("diagnose", null, values);
        db.close();
        return id;
    }

    public void updateDiagnoseSymptoms(Diagnose diagnose){
        ContentValues values;
        long id = DBHandler.getInstance().getDiagnoseIdByName(diagnose.getName());
        SQLiteDatabase db = this.getWritableDatabase();
        if (id != -1){
            db.delete("symptom_diagnose", "diagnose = ?", new String[]{Long.toString(id)});
        }
        for (Symptom i:diagnose.getSymptoms()) {
            values = new ContentValues();
            values.put("diagnose", diagnose.getId());
            values.put("symptom", i.getId());
            values.put("confidence", i.getConfidence());
            db.insert("symptom_diagnose", null, values);
        }
        db.close();
    }

    public List<Symptom> selectSymptomNames(){
        List<Symptom> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id,name FROM symptom", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Symptom(cursor.getLong(0),cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<Diagnose> selectDiagnoseNames(){
        List<Diagnose> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id,name FROM diagnose", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Diagnose(cursor.getLong(0),cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }

    public List<Diagnose> selectDiagnoses(){
        List<Diagnose> diagnoseList = selectDiagnoseNames();
        for (Diagnose i: diagnoseList) {
            i.addSymptoms(selectDiagnoseById(i.getId()).getSymptoms());
        }
        return diagnoseList;
    }

    public Diagnose selectDiagnoseById(long id){
        Diagnose tmpDiagnose = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT symptom_diagnose.diagnose, diagnose.name, symptom_diagnose.symptom, symptom.name, symptom_diagnose.confidence " +
                "FROM symptom_diagnose, symptom, diagnose WHERE symptom_diagnose.diagnose = diagnose.id AND symptom_diagnose.symptom = symptom.id " +
                "AND symptom_diagnose.diagnose =" + id + " "+
                "GROUP BY symptom_diagnose.symptom", null);
        if (cursor.moveToFirst()) {
            do {
                if(tmpDiagnose == null){
                    tmpDiagnose = new Diagnose(cursor.getLong(0),cursor.getString(1));
                }
                if(tmpDiagnose != null){
                    tmpDiagnose.addSymptom(new Symptom(cursor.getLong(2),cursor.getString(3),cursor.getDouble(4)));
                }
            } while (cursor.moveToNext());
        }
        if (tmpDiagnose == null){
            cursor = db.rawQuery("SELECT id,name FROM diagnose WHERE id ="+id, null);
            if (cursor.moveToFirst()) {
                tmpDiagnose = new Diagnose(cursor.getLong(0),cursor.getString(1));
            }
        }
        db.close();
        return tmpDiagnose;
    }

    public long getDiagnoseIdByName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM diagnose WHERE hash = '"+DBHandler.HASH(name.toLowerCase())+"'", null);
        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }
        db.close();
        return id;
    }

    public void removeDiagnose(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("diagnose", "id = ?", new String[]{Long.toString(id)});
        db.delete("symptom_diagnose", "diagnose = ?", new String[]{Long.toString(id)});
        db.close();
    }

    public void removeSymptom(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("symptom", "id = ?", new String[]{Long.toString(id)});
        db.delete("symptom_diagnose", "symptom = ?", new String[]{Long.toString(id)});
        db.close();
    }

    public static DBHandler getInstance(){
        if(instance != null){
            return instance;
        }
        return null;
    }

    public static void init(Context context){
        instance = new DBHandler(context,"diagnostic",null,2);
    }

    public static String HASH(String text) {
        text = text.toLowerCase().trim();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
