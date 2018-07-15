package com.thebrokegamers.kolkatanavigator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by g0ldfighter on 10/6/2015.
 */
public class UserDatabaseAdapter {

    private DatabaseHelper databaseHelper;
    private static final int TOTAL_FAV_ELEMENTS = 20;

    public UserDatabaseAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public long insertFavourite(String transportName, String source, String destination) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String sql = "SELECT * FROM FAVOURITES_TABLE WHERE " +   databaseHelper.TRANSPORT_NAME + " = '" + transportName
                + "'" + " AND " +  databaseHelper.SOURCE + " = '" + source + "'" + " AND destination = " + "'" + destination + "'";
        String sql_1 = "SELECT * FROM FAVOURITES_TABLE";
        Cursor check = db.rawQuery(sql, null);

        if (check.getCount() == 0) {
            check = db.rawQuery(sql_1, null);
            if (check.getCount() == TOTAL_FAV_ELEMENTS) {
                check.moveToFirst();
                deleteFavourite(check.getString(0));
            }
            db = databaseHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(databaseHelper.TRANSPORT_NAME, transportName);
            contentValues.put(databaseHelper.SOURCE, source);
            contentValues.put(databaseHelper.DESTINATION, destination);
            Toast.makeText(databaseHelper.context, "Saved!", Toast.LENGTH_SHORT).show();
            return db.insert("FAVOURITES_TABLE", null, contentValues);
        } else {
            Toast.makeText(databaseHelper.context, "Already Exists!", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    public long insertRecents(String transportName, String source, String destination, String date) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT * FROM RECENTS_TABLE WHERE transport_name = " + "'" + transportName
                + "'" + " AND source = " + "'" + source + "'" + " AND destination = " + "'" + destination + "'";
        String sql_1 = "SELECT * FROM RECENTS_TABLE";
        Cursor check = db.rawQuery(sql, null);

        if (check.getCount() == 0) {
            Cursor mCur = db.rawQuery(sql_1, null);
            if (mCur.getCount() == TOTAL_FAV_ELEMENTS) {
                Log.d("101010", "010101");
                mCur.moveToFirst();
                deleteRecent(mCur.getString(0));
            }
            mCur.close();
        } else {
            check.moveToFirst();
            deleteRecent(check.getString(0));
        }

        db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(databaseHelper.TRANSPORT_NAME, transportName);
        contentValues.put(databaseHelper.SOURCE, source);
        contentValues.put(databaseHelper.DESTINATION, destination);
        contentValues.put(databaseHelper.DATE, date);
        check.close();
        return db.insert("RECENTS_TABLE", null, contentValues);

    }

    public ArrayList<ArrayList<String>> showFavourites() {
        ArrayList<ArrayList<String>> sf = new ArrayList<ArrayList<String>>();
        ArrayList<String> temp;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql_1 = "SELECT * FROM FAVOURITES_TABLE ORDER BY _id DESC";
        Cursor mCur = db.rawQuery(sql_1, null);
        while (mCur.moveToNext()) {
            temp = new ArrayList<String>();
            temp.add(mCur.getString(0));
            temp.add(mCur.getString(1));
            temp.add(mCur.getString(2));
            temp.add(mCur.getString(3));
            sf.add(new ArrayList<String>(temp));
        }

        return sf;
    }

    public ArrayList<ArrayList<String>> showRecents() {
        ArrayList<ArrayList<String>> sf = new ArrayList<ArrayList<String>>();
        ArrayList<String> temp;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql_1 = "SELECT * FROM RECENTS_TABLE ORDER BY _id DESC";
        Cursor mCur = db.rawQuery(sql_1, null);
        while (mCur.moveToNext()) {
            temp = new ArrayList<String>();
            temp.add(mCur.getString(0));
            temp.add(mCur.getString(1));
            temp.add(mCur.getString(2));
            temp.add(mCur.getString(3));
            temp.add(mCur.getString(4));
            sf.add(new ArrayList<String>(temp));
        }
        mCur.close();
        return sf;
    }


    public boolean deleteFavourite(String id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete("FAVOURITES_TABLE", "_id = " + id, null) > 0;
    }

    public boolean deleteRecent(String id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.delete("RECENTS_TABLE", "_id = " + id, null) > 0;
    }



    static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "UserFavouriteRecent";
        private static final int DATABASE_VERSION = 1;
        private static final String UID = "_id";
        private static final String TRANSPORT_NAME = "transport_name";
        private static final String SOURCE = "source";
        private static final String DESTINATION = "destination";
        private static final String DATE = "date";
        private Context context;


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createFavouritesTable(db);
            createRecentsTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
        }

        public void createFavouritesTable(SQLiteDatabase db) {
            try {
                String createTable = "CREATE TABLE IF NOT EXISTS FAVOURITES_TABLE (" +
                        UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TRANSPORT_NAME + " VARCHAR(255), " +
                        SOURCE + " VARCHAR(255), " + DESTINATION + " VARCHAR(255));";
                db.execSQL(createTable);
                Log.d("TABLE_CREATED", "CREATED!");
            } catch (SQLException e) {
                Log.d("TABLE_CREATED", "FAILED!");
            }
        }

        public void createRecentsTable(SQLiteDatabase db) {
            try {
                String createTable = "CREATE TABLE IF NOT EXISTS RECENTS_TABLE (" +
                        UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TRANSPORT_NAME + " VARCHAR(255), " +
                        SOURCE + " VARCHAR(255), " + DESTINATION + " VARCHAR(255), " + DATE + " VARCHAR(255));";
                db.execSQL(createTable);
                Log.d("TABLE_CREATED", "RECENTS CREATED!");
            } catch (SQLException e) {
                Log.d("TABLE_CREATED", "FAILED!");
            }
        }
    }
}
