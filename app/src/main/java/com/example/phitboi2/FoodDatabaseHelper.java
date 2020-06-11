
package com.example.phitboi2;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.w3c.dom.DOMStringList;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class FoodDatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "fdb";

    public static final Integer version = 3;

    public static final String DIET_DATABASE = "diet.db";

    public static final String FOOD_TABLE = "food_table";
    public static final String FOOD_ID = "ID";
    public static final String FOOD_NAME = "food_name";
    public static final String FOOD_PROTEIN = "protein";
    public static final String FOOD_CARB = "carbs";
    public static final String FOOD_FAT = "fat";
    public static final String FOOD_SERVING_SIZE = "serving_size";
    public static final String FOOD_DELETED = "deleted";

    public static final String MEAL_TABLE = "meal_table";
    public static final String MEAL_ID = "ID";
    public static final String MEAL_FOOD_ID = "FID";
    public static final String MEAL_SERVINGS = "servings";
    public static final String MEAL_DATETIME = "date_time";

    @RequiresApi(api = Build.VERSION_CODES.P)
    public FoodDatabaseHelper(Context context) {
        super(context, DIET_DATABASE, null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG,"creating food table");
        String CreateFoodTable = "Create Table " + FOOD_TABLE
                + "("
                + FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FOOD_NAME + " TEXT, "
                + FOOD_PROTEIN + " REAL, "
                + FOOD_CARB + " REAL, "
                + FOOD_FAT + " REAL, "
                + FOOD_SERVING_SIZE + " REAL,"
                + FOOD_DELETED + " INTEGER"
                + ")";
        db.execSQL(CreateFoodTable);


        Log.d(TAG,"creating meal table");
        String CreateMealTable = "Create Table " + MEAL_TABLE
                + "("
                + MEAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MEAL_FOOD_ID + " INTEGER, "
                + MEAL_SERVINGS + " REAL, "
                + MEAL_DATETIME + " INTEGER, "
                + "FOREIGN KEY (" + MEAL_FOOD_ID +") REFERENCES "
                + FOOD_TABLE + "(" + FOOD_ID + ")"
                + ")";
        db.execSQL(CreateMealTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG,"dropping food table");
        String DropFoodTable = "DROP TABLE IF EXISTS " + FOOD_TABLE;
        db.execSQL(DropFoodTable);

        Log.d(TAG,"dropping meal table");
        String DropMealTable = "DROP TABLE IF EXISTS " + MEAL_TABLE;
        db.execSQL(DropMealTable);

        Log.d(TAG,"creating upgraded db");
        onCreate(db);
    }

    public boolean addFoodData(String foodName, String protein, String carb, String fat, String servingSize, String servings) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_NAME,foodName);
        contentValues.put(FOOD_PROTEIN,protein);
        contentValues.put(FOOD_CARB,carb);
        contentValues.put(FOOD_FAT,fat);
        contentValues.put(FOOD_SERVING_SIZE,servingSize);
        contentValues.put(FOOD_DELETED,0);

        long result = db.insert(FOOD_TABLE,null,contentValues);

        if (result == -1) {
            return false;
        } else {
            //add to meal table
            if ((servings != null) && !(TextUtils.isEmpty(servings))){
                Calendar now = Calendar.getInstance();

                boolean success = addMealData(servings,result,now.getTimeInMillis());
                //if not success alert user ...
            }
            return true;
        }
    }

    public boolean addMealData(String servings, Long foodID, Long dateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MEAL_FOOD_ID,foodID);
        contentValues.put(MEAL_SERVINGS,servings);
        contentValues.put(MEAL_DATETIME,dateTime);

        long result = db.insert(MEAL_TABLE,null,contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


}
