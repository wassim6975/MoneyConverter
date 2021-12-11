package com.example.tp1_test;
// Author : wassim6975

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

// Creation d'une classe fille de SQLiteOpenHelper
public class SqlLite extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "test";
    private static final String KEY_TAUX = "Taux";
    private static final String KEY_NAME = "Name";
    private static final String TABLE = "Devises";



    public SqlLite(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating de la Table
    // Implementation de la fonction onCreate
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE+ "("
                + KEY_NAME + " STRING PRIMARY KEY," + KEY_TAUX + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    // Downgrading database
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //super.onDowngrade(db, oldVersion, newVersion);
        throw new SQLiteException("Can't downgrade database from version " +
                oldVersion + " to " + newVersion);
    }


    // To add a new devise
    public void addDevise(String deviseName, String taux) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, deviseName); // Contact Name
        values.put(KEY_TAUX, taux); // Contact Phone

        // Inserting Row
        db.insert(TABLE, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }


    // To get one devise
    public Cursor getData(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select Name from Devises where Name="+name+"", null );
        return res;
    }

    // To get the number of devises avaibles
    public int numberOfDevises(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE);
        return numRows;
    }

    // Tp get all the devise name
    public ArrayList<String> getAllNames() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select Name from Devises", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(KEY_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

    // To get all the devises in a list view
    // Récupération des valeurs (taux, devises) venant de la db
    public HashMap<String, String> getAllDevises() {
        HashMap<String,String> hashDevises = new HashMap<>();

        // Query
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Devises", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            hashDevises.put(res.getString(res.getColumnIndex(KEY_NAME)), res.getString(res.getColumnIndex(KEY_TAUX)));
            res.moveToNext();
        }

        return hashDevises;
    }

    // To update the devise-change
    public void updateDevise(String deviseName, String taux) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, deviseName);
        values.put(KEY_TAUX, taux);

        // updating devise-change
        db.update(TABLE, values, KEY_NAME + " = ?", new String[]{deviseName});
    }

    // To delete-remove a devise
    public void deleteDevise (String Name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Devises",
                "Name = ? ",
                new String[] { Name });
    }

    // To check if the devise already exist
    public boolean CheckExist(String deviseName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select Name from Devises WHERE Name = " + deviseName;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            //cursor.close();
            return false;
        }
        //cursor.close();
        return true;
    }

}
