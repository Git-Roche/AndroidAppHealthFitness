package com.example.phitboi2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class LookupFoodActivity extends AppCompatActivity {

    private Button addFood;
    private Button removeFood;
    private EditText searchText;
    private EditText gramsText;
    private RadioGroup foodRadioGroup;
    private FoodDatabaseHelper dietDB;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_food);

        dietDB = new FoodDatabaseHelper(this);
        addFood = (Button)findViewById(R.id.bAddExistingFood);
        removeFood = (Button)findViewById(R.id.bRemoveExistingFood);
        searchText = (EditText)findViewById(R.id.etSearch);
        gramsText = (EditText)findViewById(R.id.etExistingFoodGrams);
        foodRadioGroup = (RadioGroup)findViewById(R.id.rgExistingFoods);
        setupFoodList();
        setupSearch();
        setupAddButton();
        setupRemoveButton();
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

    public void setupAddButton(){

        addFood.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(gramsText.getText())){
                    gramsText.setError("Required.");
                    return;
                }
                if (foodRadioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getApplicationContext(),"nothing selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar now = Calendar.getInstance();

                dietDB.addMealData(
                        gramsText.getText().toString(),
                        Integer.toUnsignedLong(foodRadioGroup.getCheckedRadioButtonId()),
                        now.getTimeInMillis()
                );

                Intent dietActivity = new Intent(getApplicationContext(),DietActivity.class);
                startActivity(dietActivity);
            }
        });
    }


    public void setupRemoveButton(){

        removeFood.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {


                if (foodRadioGroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getApplicationContext(),"nothing selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase db = dietDB.getReadableDatabase();

                String queryFood = "delete from " + FoodDatabaseHelper.FOOD_TABLE +
                        " where " + FoodDatabaseHelper.FOOD_ID +
                        " = " + Integer.toUnsignedLong(foodRadioGroup.getCheckedRadioButtonId());


                String queryMeal = "delete from " + FoodDatabaseHelper.MEAL_TABLE +
                        " where " + FoodDatabaseHelper.MEAL_FOOD_ID +
                        " = " + Integer.toUnsignedLong(foodRadioGroup.getCheckedRadioButtonId());

                foodRadioGroup.removeView(foodRadioGroup.findViewById(foodRadioGroup.getCheckedRadioButtonId()));

                db.execSQL(queryMeal);
                db.execSQL(queryFood);
                db.close();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public void setupFoodList(){

        SQLiteDatabase db = dietDB.getReadableDatabase();

        String query = "Select " +
                FoodDatabaseHelper.FOOD_NAME + ", " +
                FoodDatabaseHelper.FOOD_PROTEIN + ", " +
                FoodDatabaseHelper.FOOD_CARB + ", " +
                FoodDatabaseHelper.FOOD_FAT + ", " +
                FoodDatabaseHelper.FOOD_SERVING_SIZE + ", " +
                FoodDatabaseHelper.FOOD_ID +
                " from " + FoodDatabaseHelper.FOOD_TABLE +
                " order by " + FoodDatabaseHelper.FOOD_NAME;

        Cursor cursor = db.rawQuery(query, null);


        while (cursor.moveToNext()){
            String foodName = cursor.getString(0);
            String protein =  cursor.getString(1);
            String carbs = cursor.getString(2);
            String fat = cursor.getString(3);
            String servingSize = cursor.getString(4);
            Integer foodID = cursor.getInt(5);

            addFoodTableRow(foodID, foodName,protein,carbs,fat, servingSize);
        }

        db.close();
    }


    private void addFoodTableRow(Integer fID, String foodName, String protein, String carbs, String fat, String servingSize){

        RadioButton rb = new RadioButton(this);

        String txt = foodName + "\n" +
                "P: " + protein + ", " +
                "C: " + carbs + ", " +
                "F: " + fat + ", " +
                "SS: " + servingSize
                + "\n";

        rb.setText(txt);
        rb.setBackgroundColor(this.getResources().getColor(R.color.colorWhite));
        rb.setTextColor(this.getResources().getColor(R.color.colorBlack));
        rb.setId(fID);

        foodRadioGroup.addView(rb);

    }


    public void setupSearch(){

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void performSearch(){

        foodRadioGroup.removeAllViews();

        String sText = searchText.getText().toString();

        dietDB = new FoodDatabaseHelper(this);
        SQLiteDatabase db = dietDB.getReadableDatabase();

        String query = "Select " +
                FoodDatabaseHelper.FOOD_NAME + ", " +
                FoodDatabaseHelper.FOOD_PROTEIN + ", " +
                FoodDatabaseHelper.FOOD_CARB + ", " +
                FoodDatabaseHelper.FOOD_FAT + ", " +
                FoodDatabaseHelper.FOOD_SERVING_SIZE + ", " +
                FoodDatabaseHelper.FOOD_ID +
                " from " + FoodDatabaseHelper.FOOD_TABLE +
                " where " + FoodDatabaseHelper.FOOD_NAME +
                " like '%" + sText + "%'" +
                " order by " + FoodDatabaseHelper.FOOD_NAME;

        Cursor cursor = db.rawQuery(query, null);


        while (cursor.moveToNext()){
            String foodName = cursor.getString(0);
            String protein =  cursor.getString(1);
            String carbs = cursor.getString(2);
            String fat = cursor.getString(3);
            String sevingSize = cursor.getString(4);
            Integer foodID = cursor.getInt(5);

            addFoodTableRow(foodID, foodName,protein,carbs,fat, sevingSize);
        }

        db.close();

    }

}
