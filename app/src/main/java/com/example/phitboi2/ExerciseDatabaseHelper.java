package com.example.phitboi2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

public class ExerciseDatabaseHelper extends SQLiteOpenHelper {


    public static final String TAG = "exdb";

    public static final Integer version = 2;

    public static final String EXERCISE_DATABASE = "exercise.db";

    public static final String WORKOUT_TABLE = "workout_table";
    public static final String WORKOUT_ID = "ID";
    public static final String WORKOUT_NAME = "NAME";
    public static final String WORKOUT_INCOMPLETE = "INCOMPLETE";
    public static final String WORKOUT_ACTIVE = "ACTIVE";

    public static final String EXERCISE_TABLE = "exercise_table";
    public static final String EXERCISE_ID = "ID";
    public static final String EXERCISE_WORKOUT_ID = "W_ID";
    public static final String EXERCISE_NAME = "NAME";
    public static final String EXERCISE_ORDER = "WORKOUT_ORDER";
    public static final String EXERCISE_REPS = "REPS";
    public static final String EXERCISE_DURATION = "DURATION";
    public static final String EXERCISE_DISTANCE = "DISTANCE";
    public static final String EXERCISE_WEIGHT = "WEIGHT";
    public static final String EXERCISE_NOTE = "NOTE";

    public static final String OLD_WORKOUT_TABLE = "old_workout_table";
    public static final String OLD_WORKOUT_ID = "ID";
    public static final String OLD_WORKOUT_WORKOUT_ID = "W_ID";
    public static final String OLD_WORKOUT_DATETIME = "DATETIME";

    public static final String OLD_EXERCISE_TABLE = "old_exercise_table";
    public static final String OLD_EXERCISE_ID = "ID";
    public static final String OLD_EXERCISE_OLD_WORKOUT_ID = "OW_ID";
    public static final String OLD_EXERCISE_EXERCISE_ID = "E_ID";
    public static final String OLD_EXERCISE_REPS = "REPS";
    public static final String OLD_EXERCISE_DURATION = "DURATION";
    public static final String OLD_EXERCISE_DISTANCE = "DISTANCE";
    public static final String OLD_EXERCISE_WEIGHT = "WEIGHT";


    @RequiresApi(api = Build.VERSION_CODES.P)
    public ExerciseDatabaseHelper(Context context) {
        super(context, EXERCISE_DATABASE, null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "creating WORKOUT_TABLE");
        String CreateWorkoutTable = "Create Table " + WORKOUT_TABLE
                + "("
                + WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WORKOUT_NAME + " TEXT,"
                + WORKOUT_INCOMPLETE + " INTEGER,"
                + WORKOUT_ACTIVE + " INTEGER"
                + ")";
        db.execSQL(CreateWorkoutTable);


        Log.d(TAG, "creating EXERCISE_TABLE");
        String CreateExerciseTable = "Create Table " + EXERCISE_TABLE
                + "("
                + EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXERCISE_WORKOUT_ID + " INTEGER, "
                + EXERCISE_ORDER + " INTEGER, "
                + EXERCISE_NAME + " TEXT, "
                + EXERCISE_REPS + " REAL, "
                + EXERCISE_DURATION + " REAL, "
                + EXERCISE_DISTANCE + " REAL, "
                + EXERCISE_WEIGHT + " REAL, "
                + EXERCISE_NOTE + " TEXT, "
                + "FOREIGN KEY (" + EXERCISE_WORKOUT_ID + ") REFERENCES "
                + WORKOUT_TABLE + "(" + WORKOUT_ID + ")"
                + ")";
        db.execSQL(CreateExerciseTable);


        Log.d(TAG, "creating OLD_WORKOUT_TABLE");
        String CreateOldWorkoutTable = "Create Table " + OLD_WORKOUT_TABLE
                + "("
                + OLD_WORKOUT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + OLD_WORKOUT_WORKOUT_ID + " INTEGER,"
                + OLD_WORKOUT_DATETIME + " INTEGER,"
                + "FOREIGN KEY (" + OLD_WORKOUT_WORKOUT_ID + ") REFERENCES "
                + WORKOUT_TABLE + "(" + WORKOUT_ID + ")"
                + ")";
        db.execSQL(CreateOldWorkoutTable);

        Log.d(TAG, "creating OLD_EXERCISE_TABLE");
        String CreateOldExerciseTable = "Create Table " + OLD_EXERCISE_TABLE
                + "("
                + OLD_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + OLD_EXERCISE_OLD_WORKOUT_ID + " INTEGER, "
                + OLD_EXERCISE_EXERCISE_ID + " INTEGER, "
                + OLD_EXERCISE_REPS + " REAL, "
                + OLD_EXERCISE_DURATION + " REAL, "
                + OLD_EXERCISE_DISTANCE + " REAL, "
                + OLD_EXERCISE_WEIGHT + " REAL, "
                + "FOREIGN KEY (" + OLD_EXERCISE_OLD_WORKOUT_ID + ") REFERENCES "
                + OLD_WORKOUT_TABLE + "(" + OLD_WORKOUT_ID + "),"
                + "FOREIGN KEY (" + OLD_EXERCISE_EXERCISE_ID + ") REFERENCES "
                + EXERCISE_TABLE + "(" + EXERCISE_ID + ")"
                + ")";
        db.execSQL(CreateOldExerciseTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"dropping WORKOUT_TABLE");
        String WorkoutTable = "DROP TABLE IF EXISTS " + WORKOUT_TABLE;
        db.execSQL(WorkoutTable);

        Log.d(TAG,"dropping EXERCISE_TABLE");
        String ExerciseTable = "DROP TABLE IF EXISTS " + EXERCISE_TABLE;
        db.execSQL(ExerciseTable);


        Log.d(TAG,"dropping OLD_WORKOUT_TABLE");
        String OldWorkoutTable = "DROP TABLE IF EXISTS " + OLD_WORKOUT_TABLE;
        db.execSQL(OldWorkoutTable);

        Log.d(TAG,"dropping OLD_EXERCISE_TABLE");
        String OldExerciseTable = "DROP TABLE IF EXISTS " + OLD_EXERCISE_TABLE;
        db.execSQL(OldExerciseTable);


        Log.d(TAG,"creating upgraded exercise db");
        onCreate(db);
    }

    public long addWorkoutData(String workoutName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(WORKOUT_NAME,workoutName);
        contentValues.put(WORKOUT_INCOMPLETE,1); //initialize as being built by user
        contentValues.put(WORKOUT_ACTIVE,0);

        long result = db.insert(WORKOUT_TABLE,null,contentValues);

        return result;
    }

    public boolean addExerciseData(Integer WID, String name, String reps, String dur, String dist, String weight, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(EXERCISE_WORKOUT_ID,WID);
        contentValues.put(EXERCISE_NAME,name);
        contentValues.put(EXERCISE_REPS,reps);
        contentValues.put(EXERCISE_DURATION,dur);
        contentValues.put(EXERCISE_DISTANCE,dist);
        contentValues.put(EXERCISE_WEIGHT,weight);
        contentValues.put(EXERCISE_NOTE,note);

        long result = db.insert(EXERCISE_TABLE,null,contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public long addOldWorkoutData(Long WID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Calendar now = Calendar.getInstance();

        contentValues.put(OLD_WORKOUT_WORKOUT_ID,WID);
        contentValues.put(OLD_WORKOUT_DATETIME,now.getTimeInMillis());

        long result = db.insert(OLD_WORKOUT_TABLE,null,contentValues);

        db.close();
        return result;
    }

    public boolean addOldExerciseData(Long OWID, Long EID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(OLD_EXERCISE_OLD_WORKOUT_ID,OWID);
        contentValues.put(OLD_EXERCISE_EXERCISE_ID,EID);

        String query = "select " + EXERCISE_REPS +
                ", " + EXERCISE_DURATION +
                ", " + EXERCISE_DISTANCE +
                ", " + EXERCISE_WEIGHT +
                " from " + EXERCISE_TABLE +
                " where " + EXERCISE_ID +
                " = " + EID;

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();


        contentValues.put(OLD_EXERCISE_REPS,cursor.getFloat(0));
        contentValues.put(OLD_EXERCISE_DURATION,cursor.getFloat(1));
        contentValues.put(OLD_EXERCISE_DISTANCE,cursor.getFloat(2));
        contentValues.put(OLD_EXERCISE_WEIGHT,cursor.getFloat(3));
        cursor.close();

        long result = db.insert(OLD_EXERCISE_TABLE,null,contentValues);

        db.close();
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

}
