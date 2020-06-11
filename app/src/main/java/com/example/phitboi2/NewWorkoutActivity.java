package com.example.phitboi2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class NewWorkoutActivity extends AppCompatActivity {
    private Button createWorkoutButton;
    private EditText workoutNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_workout);

        workoutNameText = (EditText) findViewById(R.id.etNewWorkoutName);

        setupNewWorkoutButton();

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
        createWorkoutButton = (Button) findViewById(R.id.bCreateNewWorkout);
        createWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                if (!createWorkoutInDB()){
                    return;
                }

                Intent exerciseIntent = new Intent(getApplicationContext(),NewExerciseListActivity.class);
                startActivity(exerciseIntent);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private boolean createWorkoutInDB(){

        if (TextUtils.isEmpty(workoutNameText.getText())){
            workoutNameText.setError("Required.");
            return false;
        }

        String name = workoutNameText.getText().toString();

        ExerciseDatabaseHelper exerciseDB = new ExerciseDatabaseHelper(this);

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String queryResetIncomplete = "update " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                " set " + ExerciseDatabaseHelper.WORKOUT_INCOMPLETE +
                " = 0";

        db.execSQL(queryResetIncomplete);
        db.close();

        if (exerciseDB.addWorkoutData(name) != -1){
            return true;
        } else {
            return false;
        }


    }



}
