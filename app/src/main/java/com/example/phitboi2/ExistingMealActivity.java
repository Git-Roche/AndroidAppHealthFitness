package com.example.phitboi2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class ExistingMealActivity extends AppCompatActivity {
    private FoodDatabaseHelper dietDB;
    public static final String TAG = "EMA";
    private TableLayout mealTable;
    private Button removeButton;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_meals);

        dietDB = new FoodDatabaseHelper(this);
        mealTable = (TableLayout)findViewById(R.id.tlExistingMeals);
        removeButton = (Button)findViewById(R.id.bRemoveExistingMeal);

        dietDB = new FoodDatabaseHelper(this);

        setupSummary();

        initRemoveMealButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.nav_diet:

                Intent dietActivity = new Intent(getApplicationContext(),DietActivity.class);
                startActivity(dietActivity);
                return true;
            case R.id.nav_exercise:

                Intent exerciseActivity = new Intent(getApplicationContext(),ExerciseActivity.class);
                startActivity(exerciseActivity);
                return true;
            case R.id.nav_home:

                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void setupSummary(){

        Log.d(TAG,"creating food database");
        //FoodDatabaseHelper dietDB = new FoodDatabaseHelper(this);
        SQLiteDatabase db = dietDB.getReadableDatabase();

        Calendar now = Calendar.getInstance();

        now.set(Calendar.HOUR_OF_DAY,0);
        now.set(Calendar.MINUTE,0);
        now.set(Calendar.SECOND,0);
        now.set(Calendar.MILLISECOND,0);

        String startOfCurDay = String.valueOf(now.getTimeInMillis());

        String query = "Select " +

                FoodDatabaseHelper.FOOD_NAME + ", " +
                //convert food serving size to consumed serving size
                FoodDatabaseHelper.FOOD_PROTEIN +
                " * " + FoodDatabaseHelper.MEAL_SERVINGS +
                "/" + FoodDatabaseHelper.FOOD_SERVING_SIZE + ", " +

                FoodDatabaseHelper.FOOD_CARB +
                " * " + FoodDatabaseHelper.MEAL_SERVINGS +
                "/" + FoodDatabaseHelper.FOOD_SERVING_SIZE +", " +

                FoodDatabaseHelper.FOOD_FAT +
                " * " + FoodDatabaseHelper.MEAL_SERVINGS +
                "/" + FoodDatabaseHelper.FOOD_SERVING_SIZE +", " +

                FoodDatabaseHelper.MEAL_DATETIME + ", " +

                "m." + FoodDatabaseHelper.MEAL_ID +
                " from " + FoodDatabaseHelper.MEAL_TABLE + " as m " +
                " inner join " + FoodDatabaseHelper.FOOD_TABLE + " as f " +
                " on f." + FoodDatabaseHelper.FOOD_ID +
                " = m." + FoodDatabaseHelper.MEAL_FOOD_ID +
                " where " + FoodDatabaseHelper.MEAL_DATETIME +
                " >= " + startOfCurDay +
                " order by " + FoodDatabaseHelper.MEAL_DATETIME + " desc";

        Log.d(TAG,query);
        Log.d(TAG,"reading cursor");
        Cursor cursor = db.rawQuery(query, null);


        while (cursor.moveToNext()){
            String foodName = cursor.getString(0);
            String protein =  cursor.getString(1);
            String carbs = cursor.getString(2);
            String fat = cursor.getString(3);
            Date dateTime = new Date( cursor.getLong(4));
            String strDateTime = dateTime.toString();
            Integer foodID = cursor.getInt(5);

            addMealTableRow(foodID, foodName,protein,carbs,fat,strDateTime);
        }

        Log.d(TAG,"getting cursor values");

        db.close();
    }

    private void addMealTableRow(Integer fID, String foodName, String protein, String carbs, String fat, String dateTime){
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        CheckBox cb = new CheckBox(this);

        String txt = foodName + "\n" +
                "P: " + protein + ", " +
                "C: " + carbs + ", " +
                "F: " + fat + "\n" +
                dateTime;

        cb.setText(txt);
        cb.setTextColor(this.getResources().getColor(R.color.colorBlack));

        row.setBackgroundColor(this.getResources().getColor(R.color.colorWhite));
        row.addView(cb);

        row.setId(fID);

        mealTable.addView(row);

    }


    public void initRemoveMealButton() {
        removeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                searchTableRemoveChecked();

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void searchTableRemoveChecked(){
        for(int r_cnt = mealTable.getChildCount() - 1; r_cnt >= 0; r_cnt--) {
            View view = mealTable.getChildAt(r_cnt);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;

                //find checkbox in the row
                for(int r_itm = 0, r_itm_cnt = row.getChildCount(); r_itm < r_itm_cnt; r_itm++) {
                    View r_view = row.getChildAt(r_itm);
                    if (r_view instanceof CheckBox) {
                        CheckBox cb = (CheckBox) r_view;

                        if (cb.isChecked()){

                            removeMealFromDB(row.getId());
                            mealTable.removeView(row);
                            break;
                        }


                    }
                }


            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void removeMealFromDB(Integer id){
        //FoodDatabaseHelper dietDB = new FoodDatabaseHelper(this);
        SQLiteDatabase db = dietDB.getReadableDatabase();

        String query = "delete from " + FoodDatabaseHelper.MEAL_TABLE +
                " where " + FoodDatabaseHelper.MEAL_ID +
                " = " + id.toString();
        db.execSQL(query);
        db.close();
    }

}
