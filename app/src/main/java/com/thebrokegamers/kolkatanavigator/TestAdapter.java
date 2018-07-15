package com.thebrokegamers.kolkatanavigator;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TestAdapter {
    protected static final String TAG = "DataAdapter";
    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public TestAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public TestAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public TestAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
            Log.d("DATABASE_OPENED", "createDatabase database opened");
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public TestAdapter openWritable() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getWritableDatabase();
            Log.d("DATABASE_OPENED", "WRITABLE DATABASE OPENED");
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor getTestData() {
        try {
            String sql = "SELECT * FROM bus_table";

            Cursor mCur = mDb.rawQuery(sql, null);
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getAToBBuses(String pointA, String pointB, Boolean AC, Boolean nonAC) {
        String sql;
        if (AC == nonAC) {
            sql = "SELECT " + "bus_number, ac, source, destination" + " FROM bus_table WHERE via1 like " + "'%;"
                    + pointA + ";%'" + " AND " + "  via1 like " + "'%;" + pointB + ";%'";
        } else if (!AC) {
            sql = "SELECT " + "bus_number, ac, source, destination" + " FROM bus_table WHERE via1 like " + "'%;"
                    + pointA + ";%'" + " AND " + "  via1 like " + "'%;" + pointB + ";%'" + " AND ac = 'n'";
        } else {
            sql = "SELECT " + "bus_number, ac, source, destination" + " FROM bus_table WHERE via1 like " + "'%;"
                    + pointA + ";%'" + " AND " + "  via1 like " + "'%;" + pointB + ";%'" + " AND ac = 'y'";
        }
        try {
            Cursor mCur = mDb.rawQuery(sql, null);
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public Cursor getBusInfo(String busNumber) {
        String sql = "SELECT bus_operator, via2 FROM bus_table WHERE bus_number = " + "'" + busNumber + "'";
        try {
            Cursor mCur = mDb.rawQuery(sql, null);
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public ArrayList<ArrayList<String>> getTrains(String stationA, String stationB,
                                                  ArrayList<String> trainLines,
                                                  ArrayList<Integer> trainTableIndex,
                                                  ArrayList<Boolean> trainTableReverse,
                                                  int dayOfWeek) {
        String sql = "";
        stationA = stationA.toLowerCase().replace("-", "11");
        stationB = stationB.toLowerCase().replace("-", "11");
        stationA = stationA.toLowerCase().replace(" ", "_");
        stationB = stationB.toLowerCase().replace(" ", "_");
        Log.d("BREVER", stationA);
        Log.d("BREVER", stationB);
        Cursor mCur;
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        ArrayList<String> t;
        //
        for (int i = 0; i < trainLines.size(); i++) {
            sql = "SELECT " + stationA + " , " + stationB + " , train_no , train_name" + " FROM " +
                    trainLines.get(i) + " WHERE " +
                    stationA + " != '' " + " AND " + stationB + " != '' ";
            if (dayOfWeek == 1) {
                sql = "SELECT " + stationA + " , " + stationB + " , train_no , train_name" + " FROM " +
                        trainLines.get(i) + " WHERE " +
                        stationA + " != '' " + " AND " + stationB + " != '' " + " AND sunday = 'y'";
            }

            try {
                mCur = mDb.rawQuery(sql, null);

            } catch (SQLException mSQLException) {
                Log.e(TAG, "getTestData >>" + mSQLException.toString());
                throw mSQLException;
            }
            Log.d("FUCKING HELL", "BUH" + mCur.getCount());
            if (mCur.getCount() > 0) {
                while (mCur.moveToNext()) {
                    t = new ArrayList<String>();
                    t.add(mCur.getString(0).replace('"', ':'));
                    t.add(mCur.getString(1).replace('"', ':'));
                    t.add(mCur.getString(2));
                    t.add(mCur.getString(3));
                    t.add(trainLines.get(i));
                    t.add("" + trainTableIndex.get(i));
                    t.add("" + trainTableReverse.get(i));
                    temp.add(new ArrayList<String>(t));
                }
            } else {
                Log.d("BREVER", "FUCK YOU MAN");
            }
            mCur.close();
        }
        Log.d("BREVER", String.valueOf(temp.size()));
        Collections.sort(temp, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                return o1.get(0).compareTo(o2.get(0));
            }
        });
        return temp;
    }


    public Cursor trainNumberInfo(String trainNo, String trainLine) {
        String sql = "SELECT * FROM " + trainLine + " WHERE train_no = " + "'" + trainNo + "'";
        try {
            Cursor mCur = mDb.rawQuery(sql, null);
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public ArrayList<ArrayList<String>> getMetro(String stationA, String stationB, int dayOfWeek, boolean upDown) {
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        String day;
        Cursor mCur_0;
        Cursor mCur_1;
        String km_0;
        String km_1;
        if (stationA.contains("-")) {
            stationA = stationA.substring(0, stationA.indexOf("-") - 1);
        }

        if (stationB.contains("-")) {
            stationB = stationB.substring(0, stationB.indexOf("-") - 1);
        }

        stationA = stationA.replace(" ", "_");
        stationB = stationB.replace(" ", "_");

        Log.d("STATIONS", stationA + "   " + stationB);

        if (dayOfWeek > 1 && dayOfWeek < 7) {
            day = "mtof";
        } else if (dayOfWeek == 7) {
            day = "sat";
        } else {
            day = "sun";
        }
        String sql_0 = "SELECT " + stationA + " , " + stationB + " FROM metro_distance" + " WHERE "
                + stationA + " != '' " + " AND " + stationB + " != '' ";
        try {
            mCur_0 = mDb.rawQuery(sql_0, null);
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
        if (mCur_0.getCount() > 0) {
            mCur_0.moveToFirst();
            km_0 = mCur_0.getString(0);
            km_1 = mCur_0.getString(1);
            int fare = CalculatedFare(km_0, km_1);
            Log.d("METROFARE", "" + fare);
            String sql_1;
            if (upDown) {
                sql_1 = "SELECT " + stationA + " , " + stationB + " , id" + " FROM metro" + " WHERE "
                        + stationA + " != '' " + " AND " + stationB + " != '' " + " AND " + "day_id = " +
                        "'" + day + "'" + " AND up_down = 'up'";
            } else {
                sql_1 = "SELECT " + stationA + " , " + stationB + " , id" + " FROM metro" + " WHERE "
                        + stationA + " != '' " + " AND " + stationB + " != '' " + " AND " + "day_id = " +
                        "'" + day + "'" + " AND up_down = 'down'";
            }
            try {
                mCur_1 = mDb.rawQuery(sql_1, null);
            } catch (SQLException mSQLException) {
                Log.e(TAG, "getTestData >>" + mSQLException.toString());
                throw mSQLException;
            }
            Log.d("METROCOUNT", "" + mCur_1.getCount());
            int i = 0;
            String checkTime;
            while (mCur_1.moveToNext()) {
                temp.add(new ArrayList<String>());
                checkTime = mCur_1.getString(0);
                if (checkTime.length() == 4) {
                    checkTime = "0" + checkTime;
                }
                temp.get(i).add(checkTime);
                checkTime = mCur_1.getString(1);
                if (checkTime.length() == 4) {
                    checkTime = "0" + checkTime;
                }
                temp.get(i).add(checkTime);
                temp.get(i).add(fare + "");
                temp.get(i).add(mCur_1.getString(2));
                i++;
            }
            mCur_0.close();
            mCur_1.close();
        }

        return temp;
    }

    private int CalculatedFare(String km_0, String km_1) {
        float absoluteDistance;
        absoluteDistance = Float.parseFloat(km_0) - Float.parseFloat(km_1);
        if (absoluteDistance < 0f) {
            absoluteDistance = -absoluteDistance;
        }
        if (absoluteDistance >= 25) {
            return 25;

        } else if (absoluteDistance >= 20) {
            return 20;

        } else if (absoluteDistance >= 10) {
            return 15;

        } else if (absoluteDistance >= 5) {
            return 10;

        } else if (absoluteDistance >= 0) {
            return 5;

        }

        return 0;
    }

    public Cursor metroInfo(String id) {
        String sql = "SELECT * FROM metro WHERE id = " + "'" + id + "'";
        try {
            Cursor mCur = mDb.rawQuery(sql, null);
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public ArrayList<ArrayList<String>> emergencyInfo(String em) {
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        String sql = "SELECT * FROM emergency_no WHERE ph_type = " + "'" + em + "'";
        Cursor mCur = null;
        try {
            mCur = mDb.rawQuery(sql, null);
        } catch (SQLException mSQLException) {
            //
        }
        if (mCur != null) {
            while (mCur.moveToNext()) {
                ArrayList<String> t = new ArrayList<String>();
                t.add(mCur.getString(2));
                t.add(mCur.getString(3));
                temp.add(new ArrayList<String>(t));
            }
        }
        return temp;
    }

    public ArrayList<ArrayList<String>> ferryInfo() {
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        String sql = "SELECT * FROM ferry";
        Cursor mCur = null;
        try {
            mCur = mDb.rawQuery(sql, null);
        } catch (SQLException mSQLException) {
            //
        }
        if (mCur != null) {
            while (mCur.moveToNext()) {
                ArrayList<String> t = new ArrayList<String>();
                t.add(mCur.getString(1).toUpperCase());
                t.add(mCur.getString(2).toUpperCase());
                if (mCur.getString(3).length() > 0) {
                    String[] a;
                    a = mCur.getString(3).split(";");
                    t.add(a[1].toUpperCase());
                    t.add(a[2].toUpperCase());
                }
                t.add(mCur.getString(4).toUpperCase());
                t.add(mCur.getString(5).toUpperCase());
                t.add(mCur.getString(6).toUpperCase());
                temp.add(new ArrayList<String>(t));
            }
        }
        return temp;
    }

    public ArrayList<ArrayList<String>> tramInfo(String A, String B) {
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        String source = A.replace(" ", "_").toLowerCase();
        String destination = B.replace(" ", "_").toLowerCase();
        String sql = "SELECT route_no, depot, way, first_car, last_car FROM tram_line WHERE "
                + source + " != 'NULL' AND " + destination + " != 'NULL'";
        Cursor mCur = null;
        try {
            mCur = mDb.rawQuery(sql, null);
        } catch (SQLException mSQLException) {
            //
        }
        if (mCur != null) {
            while (mCur.moveToNext()) {
                ArrayList<String> t = new ArrayList<String>();
                t.add(mCur.getString(0).toUpperCase());
                t.add(mCur.getString(1).toUpperCase());
                if (mCur.getString(2).equals("1")) {
                    t.add("Runs One Way.");
                }
                else {
                    t.add("Runs Both Ways.");
                }
                t.add(mCur.getString(3).toUpperCase());
                t.add(mCur.getString(4).toUpperCase());
                temp.add(new ArrayList<String>(t));
            }
            mCur.close();
        }
        return temp;
    }

    public ArrayList<ArrayList<String>> getAutoStand(String source)
    {
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        Cursor mCur = null;
        try
        {
            String sql ="SELECT s1, s2, s1_latlng, s2_latlng FROM auto_table "+  " WHERE via1 like "   + "'%;" +source+";%';" ;

            //query = "SELECT " + itemToSelect + " FROM " + tableName + " WHERE bus_number ="   +"'"+wCol+"'";

            mCur = mDb.rawQuery(sql, null);

        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
        if (mCur != null) {
            ArrayList<String> a;
            while (mCur.moveToNext()) {
                a = new ArrayList<String>();
                String[] tmpSrcLatLng = new String[4];
                String[] tmpDesLatLng = new String[4];
                a.add(mCur.getString(0));
                a.add(mCur.getString(1));
                tmpSrcLatLng = mCur.getString(2).split(";");
                a.add(tmpSrcLatLng[1]);
                a.add(tmpSrcLatLng[2]);
                tmpDesLatLng = mCur.getString(3).split(";");
                a.add(tmpDesLatLng[1]);
                a.add(tmpDesLatLng[2]);
                temp.add(a);
            }
        }
        return temp;
    }

    public ArrayList<ArrayList<String>> getAutos(String source, String destination)
    {
        ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
        Cursor mCur = null;
        try
        {
            String sql ="SELECT s1, s2, s1_latlng, s2_latlng FROM auto_table "+  " WHERE via1 like " +
                    "'%;" +source+";%' AND " + "via1 like " + "'%;" +destination+";%';";

            //query = "SELECT " + itemToSelect + " FROM " + tableName + " WHERE bus_number ="   +"'"+wCol+"'";

            mCur = mDb.rawQuery(sql, null);

        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
        if (mCur != null) {
            ArrayList<String> a;
            while (mCur.moveToNext()) {
                a = new ArrayList<String>();
                String[] tmpSrcLatLng = new String[4];
                String[] tmpDesLatLng = new String[4];
                a.add(mCur.getString(0));
                a.add(mCur.getString(1));
                tmpSrcLatLng = mCur.getString(2).split(";");
                a.add(tmpSrcLatLng[1]);
                a.add(tmpSrcLatLng[2]);
                tmpDesLatLng = mCur.getString(3).split(";");
                a.add(tmpDesLatLng[1]);
                a.add(tmpDesLatLng[2]);
                temp.add(a);
            }
        }
        return temp;
    }
}
