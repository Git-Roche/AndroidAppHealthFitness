package com.example.phitboi2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ExerciseActivity extends AppCompatActivity {

    private Button newWorkoutButton;
    private Button removeButton;
    private Button doWorkoutButton;
    private Button oldWorkoutsButton;
    private EditText searchText;
    private ExerciseDatabaseHelper exerciseDB;
    private RadioGroup workoutRG;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        exerciseDB = new ExerciseDatabaseHelper(this);
        searchText = (EditText) findViewById(R.id.etSearchWorkouts);
        workoutRG = (RadioGroup) findViewById(R.id.rgWorkouts);
        removeButton = (Button) findViewById(R.id.bDeleteExistingWorkout);
        doWorkoutButton = (Button) findViewById(R.id.bDoWorkout);
        oldWorkoutsButton = (Button) findViewById(R.id.bPastWorkouts);

        setupNewWorkoutButton();
        setupWorkoutList();
        setupSearch();
        setupRemoveButton();
        setupDoWorkoutButton();
        setupOldWorkoutsButton();

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
    private void setupNewWorkoutButton(){
        newWorkoutButton = (Button) findViewById(R.id.bAddWorkout);
        newWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newWorkout = new Intent(getApplicationContext(),NewWorkoutActivity.class);
                startActivity(newWorkout);
            }
        });
    }

    public void setupDoWorkoutButton(){

        doWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                if (workoutRG.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getApplicationContext(),"nothing selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase db = exerciseDB.getReadableDatabase();

                String queryResetActive = "update " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                        " set " + ExerciseDatabaseHelper.WORKOUT_ACTIVE +
                        " = 0";

                String querySetActive = "update " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                        " set " + ExerciseDatabaseHelper.WORKOUT_ACTIVE +
                        " = 1 " +
                        " where " + ExerciseDatabaseHelper.WORKOUT_ID +
                        " = " + Integer.toUnsignedLong(workoutRG.getCheckedRadioButtonId());

                db.execSQL(queryResetActive);
                db.execSQL(querySetActive);
                db.close();

                Intent doWorkout = new Intent(getApplicationContext(),DoWorkoutActivity.class);
                startActivity(doWorkout);

            }
        });

    }

    public void setupOldWorkoutsButton(){

        oldWorkoutsButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                Intent oldWorkout = new Intent(getApplicationContext(),OldWorkoutActivity.class);
                startActivity(oldWorkout);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void setupWorkoutList(){

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "Select " +
                ExerciseDatabaseHelper.WORKOUT_NAME +
                ", " + ExerciseDatabaseHelper.WORKOUT_ID +
                " from " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                " order by " + ExerciseDatabaseHelper.WORKOUT_NAME;

        Cursor cursor = db.rawQuery(query, null);


        while (cursor.moveToNext()){
            String wName = cursor.getString(0);
            Integer wID = cursor.getInt(1);

            addWorkoutTableRow(wName, wID);
        }

        db.close();
    }


    private void addWorkoutTableRow(String name, Integer wID){

        RadioButton rb = new RadioButton(this);

        String txt = name;

        rb.setText(txt);
        rb.setBackgroundColor(this.getResources().getColor(R.color.colorWhite));
        rb.setTextColor(this.getResources().getColor(R.color.colorBlack));
        rb.setId(wID);

        workoutRG.addView(rb);

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

        workoutRG.removeAllViews();

        String sText = searchText.getText().toString();

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "Select " +
                ExerciseDatabaseHelper.WORKOUT_NAME +
                ", " + ExerciseDatabaseHelper.WORKOUT_ID +
                " from " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                " where " + ExerciseDatabaseHelper.WORKOUT_NAME +
                " like '%" + sText + "%' " +
                " order by " + ExerciseDatabaseHelper.WORKOUT_NAME;

        Cursor cursor = db.rawQuery(query, null);


        while (cursor.moveToNext()){
            String wName = cursor.getString(0);
            Integer wID = cursor.getInt(1);

            addWorkoutTableRow(wName, wID);
        }

        db.close();

    }


    public void setupRemoveButton(){

        removeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {


                if (workoutRG.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getApplicationContext(),"nothing selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                SQLiteDatabase db = exerciseDB.getReadableDatabase();

                String queryWorkout = "delete from " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                        " where " + ExerciseDatabaseHelper.WORKOUT_ID +
                        " = " + Integer.toUnsignedLong(workoutRG.getCheckedRadioButtonId());


                String queryExercise = "delete from " + ExerciseDatabaseHelper.EXERCISE_TABLE +
                        " where " + ExerciseDatabaseHelper.EXERCISE_WORKOUT_ID +
                        " = " + Integer.toUnsignedLong(workoutRG.getCheckedRadioButtonId());

                workoutRG.removeView(workoutRG.findViewById(workoutRG.getCheckedRadioButtonId()));

                db.execSQL(queryExercise);
                db.execSQL(queryWorkout);
                db.close();
            }
        });
    }

}
