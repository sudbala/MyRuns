package com.example.myruns.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myruns.Activity.MainActivity;
import com.example.myruns.Activity.ManualActivity;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ExerciseDataSource {
    // database helper
    MySQLiteHelper databaseHelper;
    SQLiteDatabase database;

    public static final String TABLE_NAME = "exercises";
    public static final String KEY_COLUMN_ID = "_id";

    public ExerciseDataSource(Context context) {
        databaseHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public synchronized long insertExerciseEntry(ExerciseEntryStructure exerciseEntry) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MySQLiteHelper.KEY_INPUT_TYPE, exerciseEntry.getmInputType());        // 1
        contentValues.put(MySQLiteHelper.KEY_ACTIVITY_TYPE, exerciseEntry.getmActivityType());  // 2
        contentValues.put(MySQLiteHelper.KEY_DATE_TIME, exerciseEntry.getmDateTime());          // 3
        contentValues.put(MySQLiteHelper.KEY_DURATION, exerciseEntry.getmDuration());           // 4
        contentValues.put(MySQLiteHelper.KEY_DISTANCE, exerciseEntry.getmDistance());           // 5
        contentValues.put(MySQLiteHelper.KEY_AVG_PACE, exerciseEntry.getmAvgPace());            // 6
        contentValues.put(MySQLiteHelper.KEY_AVG_SPEED, exerciseEntry.getmAvgSpeed());          // 7
        contentValues.put(MySQLiteHelper.KEY_CALORIES, exerciseEntry.getmCalorie());            // 8
        contentValues.put(MySQLiteHelper.KEY_CLIMB, exerciseEntry.getmClimb());                 // 9
        contentValues.put(MySQLiteHelper.KEY_HEART_RATE, exerciseEntry.getmHeartRate());        // 10
        contentValues.put(MySQLiteHelper.KEY_COMMENT, exerciseEntry.getmComment());             // 11
        //contentValues.put(MySQLiteHelper.KEY_GPS, exerciseEntry.getmGPS());

        long insertId = database.insert(MySQLiteHelper.TABLE_NAME, null, contentValues);

        return insertId;
    }

    public synchronized void deleteExerciseEntry(ExerciseEntryStructure ExerciseEntry){
        long id = ExerciseEntry.getmID();
        Log.d("delete", "Entry deleted with ID: " + id);
        database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.KEY_COLUMN_ID
                + " = " + id, null);
    }

    public synchronized void deleteAllEntries(){
        Log.d("delete", "Literally e v e r y t h i n g was just deleted.");
        database.delete(MySQLiteHelper.TABLE_NAME, null, null);
    }

    public synchronized ExerciseEntryStructure fetchEntry(long entryID) {
        database = databaseHelper.getReadableDatabase();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
                null,
                KEY_COLUMN_ID + " = " + entryID,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        ExerciseEntryStructure exerciseEntry = cursorToExerciseEntry(cursor);
        cursor.close();
        return exerciseEntry;
    }

    public synchronized ArrayList<ExerciseEntryStructure> fetchAllEntries() {
        database = databaseHelper.getReadableDatabase();

        ArrayList<ExerciseEntryStructure> exerciseEntries = new ArrayList<ExerciseEntryStructure>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();

        // Fetch all now
        while (!cursor.isAfterLast()) {
            ExerciseEntryStructure exerciseEntry = cursorToExerciseEntry(cursor);
            exerciseEntries.add(exerciseEntry);
            cursor.moveToNext();
        }
        // Return our list
        return exerciseEntries;
    }

    public synchronized ExerciseEntryStructure cursorToExerciseEntry(Cursor cursor) {
        ExerciseEntryStructure exerciseEntry = new ExerciseEntryStructure(cursor.getInt(1), cursor.getInt(2));
        exerciseEntry.setmDateTime(cursor.getLong(3));
        exerciseEntry.setmDuration(cursor.getInt(4));
        // converts to miles if needed and round to nearest thousandth, seeing as
        // db stores all distance as km
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        if (MainActivity.units.equals("Miles")){
            exerciseEntry.setmDistance(Double.parseDouble(twoDForm.format(cursor.getDouble(5) * 0.621371)));
        } else {
            exerciseEntry.setmDistance(Double.parseDouble(twoDForm.format(cursor.getDouble(5))));
        }
        exerciseEntry.setmAvgPace(cursor.getDouble(6));
        exerciseEntry.setmAvgSpeed(cursor.getDouble(7));
        exerciseEntry.setmCalorie(cursor.getInt(8));
        exerciseEntry.setmClimb(cursor.getDouble(9));
        exerciseEntry.setmHeartRate(cursor.getInt(10));
        exerciseEntry.setmComment(cursor.getString(11));
        exerciseEntry.setmID(cursor.getLong(0));
        return exerciseEntry;
    }
}
