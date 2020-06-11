package com.example.phitboi2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class DoWorkoutActivity extends AppCompatActivity {

    private ExerciseDatabaseHelper exerciseDB;
    private TextView nameText;
    private Integer workoutID = -1;
    private LinearLayout llExercises;

    private Button finishedButton;
    private Button cancelButton;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_workout);

        exerciseDB = new ExerciseDatabaseHelper(this);
        nameText = (TextView) findViewById(R.id.tvDoWkName);
        llExercises = (LinearLayout) findViewById(R.id.llDoWkTable);
        finishedButton = (Button) findViewById(R.id.bDoWkFinish);
        cancelButton = (Button) findViewById(R.id.bDoWkCancel);

        setupNameText();
        setupExerciseList();
        setupFinishedButton();
        setupCancelButton();
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

    private void setupNameText(){
        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "select " + ExerciseDatabaseHelper.WORKOUT_NAME +
                ", " + ExerciseDatabaseHelper.WORKOUT_ID +
                " from " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                " where " + ExerciseDatabaseHelper.WORKOUT_ACTIVE +
                " = 1";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        String name = cursor.getString(0);
        workoutID = cursor.getInt(1);

        nameText.setText(nameText.getText().toString().replace("{0}",name));

        cursor.close();
        db.close();
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    private void setupExerciseList(){

        if (workoutID == -1){
            //missing workout id
            return;
        }

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "select " + ExerciseDatabaseHelper.EXERCISE_NAME +
                ", " + ExerciseDatabaseHelper.EXERCISE_DISTANCE +
                ", " + ExerciseDatabaseHelper.EXERCISE_WEIGHT +
                ", " + ExerciseDatabaseHelper.EXERCISE_REPS +
                ", " + ExerciseDatabaseHelper.EXERCISE_DURATION +
                ", " + ExerciseDatabaseHelper.EXERCISE_NOTE +
                ", " + ExerciseDatabaseHelper.EXERCISE_ID +
                " from " + ExerciseDatabaseHelper.EXERCISE_TABLE +
                " where " + ExerciseDatabaseHelper.EXERCISE_WORKOUT_ID +
                " = " + workoutID +
                " order by " + ExerciseDatabaseHelper.EXERCISE_ORDER;


        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()){

            String name = cursor.getString(0);
            String dist = cursor.getString(1);
            String weight = cursor.getString(2);
            String reps = cursor.getString(3);
            String dur = cursor.getString(4);
            String note = cursor.getString(5);
            Integer id = cursor.getInt(6);

            addExerciseListRow(name, dist, weight, reps, dur, note, id);
        }

        cursor.close();
        db.close();
    }

    private void addExerciseListRow(String name, String dist, String weight, String reps, String dur, String note, Integer id){


        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        CheckBox cb = new CheckBox(this);

        String txt = name;
        if(!dist.isEmpty()){
            txt += "\nDistance: " + dist;
        }
        if(!weight.isEmpty()){
            txt += "\nWeight: " + weight;
        }
        if(!reps.isEmpty()){
            txt += "\nReps: " + reps;
        }
        if(!dur.isEmpty()){
            txt += "\nDuration: " + dur;
        }
        if(!note.isEmpty()){
            txt += "\nNote: " + note;
        }

        cb.setText(txt);

        cb.setTextColor(this.getResources().getColor(R.color.colorBlack));


        row.setBackgroundColor(this.getResources().getColor(R.color.colorWhite));
        row.addView(cb);
        row.setId(id);

        llExercises.addView(row);

    }
    private void setupFinishedButton(){

        finishedButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {


                if (workoutID != -1){
                    //write to db
                    writeData();
                }

                Intent exerciseAct = new Intent(getApplicationContext(),ExerciseActivity.class);
                startActivity(exerciseAct);

            }
        });
    }

    private void writeData(){

        Long oldWID = exerciseDB.addOldWorkoutData((long) workoutID);

        for(int r_cnt = llExercises.getChildCount() - 1; r_cnt >= 0; r_cnt--) {
            View view = llExercises.getChildAt(r_cnt);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;

                //find checkbox in the row
                for(int r_itm = 0, r_itm_cnt = row.getChildCount(); r_itm < r_itm_cnt; r_itm++) {
                    View r_view = row.getChildAt(r_itm);
                    if (r_view instanceof CheckBox) {
                        CheckBox cb = (CheckBox) r_view;

                        if (cb.isChecked()){

                            exerciseDB.addOldExerciseData(oldWID, (long) row.getId());
                            break;
                        }


                    }
                }


            }
        }

    }
    private void setupCancelButton(){

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {


                SQLiteDatabase db = exerciseDB.getReadableDatabase();

                String queryResetActive = "update " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                        " set " + ExerciseDatabaseHelper.WORKOUT_ACTIVE +
                        " = 0";

                db.execSQL(queryResetActive);
                db.close();

                Intent exerciseAct = new Intent(getApplicationContext(),ExerciseActivity.class);
                startActivity(exerciseAct);

            }
        });
    }


}
