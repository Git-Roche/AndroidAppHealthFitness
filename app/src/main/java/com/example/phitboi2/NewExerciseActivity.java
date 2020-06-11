package com.example.phitboi2;

import android.content.Intent;
import android.database.Cursor;
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

public class NewExerciseActivity extends AppCompatActivity {

    private ExerciseDatabaseHelper exerciseDB;
    private EditText nameText;
    private EditText repsText;
    private EditText durText;
    private EditText distText;
    private EditText weightText;
    private EditText noteText;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exercise);

        exerciseDB = new ExerciseDatabaseHelper(this);
        nameText = (EditText) findViewById(R.id.etExerciseName);
        repsText = (EditText) findViewById(R.id.etNumReps);
        durText = (EditText) findViewById(R.id.etDuration);
        distText = (EditText) findViewById(R.id.etDistance);
        weightText = (EditText) findViewById(R.id.etWeight);
        noteText = (EditText) findViewById(R.id.etExerciseNote);

        setupCancelButton();
        setupAddButton();
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

    private void setupAddButton(){


        Button finishButton = (Button) findViewById(R.id.bCreateNewExercise);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                if (addData()){

                    Intent exerciseIntent = new Intent(getApplicationContext(),NewExerciseListActivity.class);
                    startActivity(exerciseIntent);
                }
            }
        });
    }

    private boolean addData(){

        if (TextUtils.isEmpty(nameText.getText())){
            nameText.setError("Required.");
            return false;
        }

        Integer wID = getUnfinishedWorkoutID();

        Boolean result = exerciseDB.addExerciseData(
                wID,
                nameText.getText().toString(),
                repsText.getText().toString(),
                durText.getText().toString(),
                distText.getText().toString(),
                weightText.getText().toString(),
                noteText.getText().toString()
                );

        return result;

    }

    private Integer getUnfinishedWorkoutID(){

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "select " + ExerciseDatabaseHelper.WORKOUT_ID +
                " from " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                " where " + ExerciseDatabaseHelper.WORKOUT_INCOMPLETE +
                " = 1 ";

        Cursor cursor = db.rawQuery(query,null);

        cursor.moveToFirst();

        Integer wID = cursor.getInt(0);

        db.close();

        return wID;
    }

    private void setupCancelButton(){

        Button cancelButton = (Button) findViewById(R.id.bNoNewExercise);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                if (addData()){

                    Intent exerciseIntent = new Intent(getApplicationContext(),NewExerciseListActivity.class);
                    startActivity(exerciseIntent);
                }
            }
        });
    }

}
