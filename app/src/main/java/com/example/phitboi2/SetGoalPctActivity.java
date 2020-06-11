package com.example.phitboi2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetGoalPctActivity extends AppCompatActivity  {

    private Button setButton;
    private EditText protein;
    private EditText carbs;
    private EditText fat;
    private EditText calories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goals_pct);


        setButton = (Button) findViewById(R.id.bSetGoalPct);
        protein = (EditText) findViewById(R.id.etSetProteinPct);
        carbs = (EditText) findViewById(R.id.etSetCarbs);
        fat = (EditText) findViewById(R.id.etSetFat);
        calories = (EditText) findViewById(R.id.etSetCalories);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (setGoals()){

                    Intent dietActivity = new Intent(getApplicationContext(),DietActivity.class);
                    startActivity(dietActivity);
                }

            }
        });
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
    private boolean setGoals(){

        if (TextUtils.isEmpty(protein.getText())){
            protein.setError("Required.");
        }
        if (TextUtils.isEmpty(carbs.getText())){
            carbs.setError("Required.");
        }
        if (TextUtils.isEmpty(fat.getText())){
            fat.setError("Required.");
        }
        if (TextUtils.isEmpty(calories.getText())){
            calories.setError("Required.");
        }

        if(
                TextUtils.isEmpty(protein.getText()) ||
                TextUtils.isEmpty(carbs.getText()) ||
                TextUtils.isEmpty(fat.getText()) ||
                TextUtils.isEmpty(calories.getText())
        ){
            Toast.makeText(getApplicationContext(),"missing numbers", Toast.LENGTH_SHORT).show();
            return false;
        }

        Float fProtein = Float.parseFloat(protein.getText().toString());
        Float fCarbs = Float.parseFloat(carbs.getText().toString());
        Float fFat = Float.parseFloat(fat.getText().toString());
        Float fCal = Float.parseFloat(calories.getText().toString());
        if( ((fFat <= 0) && (fCarbs <= 0) && (fProtein <= 0)) || (fCal <= 0)){
            Toast.makeText(getApplicationContext(),"bad numbers", Toast.LENGTH_SHORT).show();
            return false;
        }


        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.goalPreferenceFile),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Float normalizer = fFat + fCarbs + fProtein;
        fProtein = fProtein / normalizer;
        fCarbs = fCarbs / normalizer;
        fFat = fFat / normalizer;

        editor.putFloat(getString(R.string.PrefProteinPct), fProtein);
        editor.putFloat(getString(R.string.PrefCarbsPct), fCarbs);
        editor.putFloat(getString(R.string.PrefFatPct), fFat);
        editor.putFloat(getString(R.string.PrefCal), fCal);
        editor.commit();

        return true;
    }
}
