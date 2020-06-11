package com.example.phitboi2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;

public class DietActivity extends AppCompatActivity{
    public static final String TAG = "dac";
    public static final Integer calPerGramProtein = 4;
    public static final Integer calPerGramCarb = 4;
    public static final Integer calPerGramFat = 9;

    private Button addMealButton;
    private Button existingMealButton;
    private Button setGoalsButton;

    private TextView proteinGram;
    private TextView carbGram;
    private TextView fatGram;

    private TextView proteinPct;
    private TextView carbPct;
    private TextView fatPct;

    private Float CurrentProteinGram = 0.0f;
    private Float CurrentCarbsGram = 0.0f;
    private Float CurrentFatGram = 0.0f;
    private Float CurrentCalories = 0.0f;

    private Float CurrentProteinPct = 0.0f;
    private Float CurrentCarbsPct = 0.0f;
    private Float CurrentFatPct = 0.0f;

    private Float GoalProteinGram = 0.0f;
    private Float GoalCarbsGram = 0.0f;
    private Float GoalFatGram = 0.0f;
    private Float GoalCalories = 0.0f;

    private Float GoalProteinPct = 0.0f;
    private Float GoalCarbsPct = 0.0f;
    private Float GoalFatPct = 0.0f;


    private TextView calories;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        proteinGram = (TextView) findViewById(R.id.textProteinGram);
        carbGram = (TextView) findViewById(R.id.textCarbGram);
        fatGram = (TextView) findViewById(R.id.textFatGram);
        proteinPct = (TextView) findViewById(R.id.textProteinPct);
        carbPct = (TextView) findViewById(R.id.textCarbPct);
        fatPct = (TextView) findViewById(R.id.textFatPct);
        calories = (TextView) findViewById(R.id.textCal);

        Log.d(TAG,"running setup summary");
        getCurrentGoals();
        getCurrentProgress();

        writeSummaryText();

        Log.d(TAG,"setupAddMealButton");
        setupAddMealButton();

        setupExistingMealButton();
        setupSetGoalsButton();
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
    public void setupAddMealButton(){

        addMealButton = (Button) findViewById(R.id.bAddMeal);

        addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addMealActivity = new Intent(getApplicationContext(), AddNewMealActivity.class);
                startActivity(addMealActivity);
            }
        });
    }
    public void setupExistingMealButton(){

        existingMealButton = (Button) findViewById(R.id.bAddExistingMeal);

        existingMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent existingMealActivity = new Intent(getApplicationContext(), ExistingMealActivity.class);
                startActivity(existingMealActivity);
            }
        });
    }
    public void setupSetGoalsButton(){

        setGoalsButton = (Button) findViewById(R.id.bSetGoals);

        setGoalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setGoalslActivity = new Intent(getApplicationContext(), SetGoalPctActivity.class);
                startActivity(setGoalslActivity);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void getCurrentProgress(){


        Calendar now = Calendar.getInstance();

        now.set(Calendar.HOUR_OF_DAY,0);
        now.set(Calendar.MINUTE,0);
        now.set(Calendar.SECOND,0);
        now.set(Calendar.MILLISECOND,0);

        String startOfCurDay = String.valueOf(now.getTimeInMillis());


        Log.d(TAG,"creating food database");
        FoodDatabaseHelper dietDB = new FoodDatabaseHelper(this);
        SQLiteDatabase db = dietDB.getReadableDatabase();
        String query = "Select " +
                "sum(f." + FoodDatabaseHelper.FOOD_PROTEIN +
                " * " + FoodDatabaseHelper.MEAL_SERVINGS +
                "/" + FoodDatabaseHelper.FOOD_SERVING_SIZE +
                ")," +
                "sum(f." + FoodDatabaseHelper.FOOD_CARB +
                " * " + FoodDatabaseHelper.MEAL_SERVINGS +
                "/" + FoodDatabaseHelper.FOOD_SERVING_SIZE +
                ")," +
                "sum(f." + FoodDatabaseHelper.FOOD_FAT +
                " * " + FoodDatabaseHelper.MEAL_SERVINGS +
                "/" + FoodDatabaseHelper.FOOD_SERVING_SIZE +
                ") " +
                "from " + FoodDatabaseHelper.MEAL_TABLE + " as m " +
                " inner join " + FoodDatabaseHelper.FOOD_TABLE + " as f " +
                " on f." + FoodDatabaseHelper.FOOD_ID +
                " = m." + FoodDatabaseHelper.MEAL_FOOD_ID +
                " where " + FoodDatabaseHelper.MEAL_DATETIME +
                " >= " + startOfCurDay;
        Log.d(TAG,query);
        Log.d(TAG,"reading cursor");
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        Log.d(TAG,"getting cursor values");

        Log.d(TAG,"setting values");

        CurrentProteinGram = cursor.getFloat(0);;
        CurrentCarbsGram = cursor.getFloat(1);;
        CurrentFatGram = cursor.getFloat(2);;
        CurrentCalories = convertToCalories(CurrentProteinGram, CurrentCarbsGram, CurrentFatGram);;
        cursor.close();

        if (CurrentCalories != 0){

            CurrentProteinPct = CurrentProteinGram * calPerGramProtein / CurrentCalories;
            CurrentCarbsPct = CurrentCarbsGram * calPerGramCarb / CurrentCalories;
            CurrentFatPct = CurrentFatGram * calPerGramFat / CurrentCalories;

        }



        db.close();
    }

    public Float convertToCalories(Float protein, Float carbs, Float fat){
        return protein * calPerGramProtein + carbs * calPerGramCarb + fat * calPerGramFat;

    }


    private void getCurrentGoals(){
        SharedPreferences mPrefs = getSharedPreferences(getString(R.string.goalPreferenceFile), Context.MODE_PRIVATE);
        GoalProteinPct = mPrefs.getFloat(getString(R.string.PrefProteinPct), 0.0f);
        GoalCarbsPct = mPrefs.getFloat(getString(R.string.PrefCarbsPct), 0.0f);
        GoalFatPct = mPrefs.getFloat(getString(R.string.PrefFatPct), 0.0f);
        GoalCalories = mPrefs.getFloat(getString(R.string.PrefCal), 0.0f);

        GoalProteinGram = GoalCalories * GoalProteinPct / calPerGramProtein;
        GoalCarbsGram = GoalCalories * GoalCarbsPct / calPerGramCarb;
        GoalFatGram = GoalCalories * GoalFatPct / calPerGramFat;

    }

    private void writeSummaryText(){

        String ProteinGramText = "{0}/{1}g";
        String CarbGramText = "{0}/{1}g";
        String FatGramText = "{0}/{1}g";
        String ProteinPctText = "{0}/{1}%";
        String CarbPctText = "{0}/{1}%";
        String FatPctText = "{0}/{1}%";
        String CalText = "{0}/{1} Cal";


        DecimalFormat formatWhole = new DecimalFormat("#");
        String sCurProteinGram = formatWhole.format(CurrentProteinGram);
        String sCurCarbsGram = formatWhole.format(CurrentCarbsGram);
        String sCurFatGram = formatWhole.format(CurrentFatGram);

        String sGoalProteinGram = formatWhole.format(GoalProteinGram);
        String sGoalCarbsGram = formatWhole.format(GoalCarbsGram);
        String sGoalFatGram = formatWhole.format(GoalFatGram);

        String sCurCal = formatWhole.format(CurrentCalories);
        String sGoalCal = formatWhole.format(GoalCalories);


        String sCurProteinPct = formatWhole.format(CurrentProteinPct * 100.0f);
        String sCurCarbsPct = formatWhole.format(CurrentCarbsPct * 100.0f);
        String sCurFatPct = formatWhole.format(CurrentFatPct * 100.0f);

        String sGoalProteinPct = formatWhole.format(GoalProteinPct * 100.0f);
        String sGoalCarbsPct = formatWhole.format(GoalCarbsPct * 100.0f);
        String sGoalFatPct = formatWhole.format(GoalFatPct * 100.0f);

        ProteinGramText = ProteinGramText.replace("{0}",sCurProteinGram);
        ProteinGramText = ProteinGramText.replace("{1}",sGoalProteinGram);
        CarbGramText = CarbGramText.replace("{0}",sCurCarbsGram);
        CarbGramText = CarbGramText.replace("{1}",sGoalCarbsGram);
        FatGramText = FatGramText.replace("{0}",sCurFatGram);
        FatGramText = FatGramText.replace("{1}",sGoalFatGram);
        CalText = CalText.replace("{0}",sCurCal);
        CalText = CalText.replace("{1}",sGoalCal);

        ProteinPctText = ProteinPctText.replace("{0}",sCurProteinPct);
        ProteinPctText = ProteinPctText.replace("{1}",sGoalProteinPct);
        CarbPctText = CarbPctText.replace("{0}",sCurCarbsPct);
        CarbPctText = CarbPctText.replace("{1}",sGoalCarbsPct);
        FatPctText = FatPctText.replace("{0}",sCurFatPct);
        FatPctText = FatPctText.replace("{1}",sGoalFatPct);

        proteinGram.setText(ProteinGramText);
        carbGram.setText(CarbGramText);
        fatGram.setText(FatGramText);
        calories.setText(CalText);

        proteinPct.setText(ProteinPctText);
        carbPct.setText(CarbPctText);
        fatPct.setText(FatPctText);

    }
}
