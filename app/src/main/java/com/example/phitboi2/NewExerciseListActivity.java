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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class NewExerciseListActivity extends AppCompatActivity {

    private TextView workoutNameText;
    private TableLayout exerciseList;
    private long workoutID = -1;

    private ExerciseDatabaseHelper exerciseDB;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exercise_list);

        exerciseDB = new ExerciseDatabaseHelper(this);
        workoutNameText = (TextView) findViewById(R.id.tvWorkingWorkoutName);
        exerciseList = (TableLayout) findViewById(R.id.tlWorkingExerciseList);

        setupWorkoutName();
        setupExerciseList();

        setupAddButton();
        setupRemoveButton();
        setupCancelButton();
        setupFinishButton();
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
    private void setupWorkoutName(){

        //ExerciseDatabaseHelper exerciseDB = new ExerciseDatabaseHelper(this);

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "select " + ExerciseDatabaseHelper.WORKOUT_NAME +
                ", " + ExerciseDatabaseHelper.WORKOUT_ID +
                " from " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                " where " + ExerciseDatabaseHelper.WORKOUT_INCOMPLETE +
                " = 1";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        String name = cursor.getString(0);
        workoutID = cursor.getLong(1);

        workoutNameText.setText(workoutNameText.getText().toString().replace("{0}",name));

        cursor.close();
        db.close();

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void setupExerciseList(){

        if (workoutID == -1){
            //missing workout id
            return;
        }

        //ExerciseDatabaseHelper exerciseDB = new ExerciseDatabaseHelper(this);

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
        //TextView tv = new TextView(this);

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

        //tv.setText(txt);
        cb.setTextColor(this.getResources().getColor(R.color.colorBlack));
        //tv.hei

        row.setBackgroundColor(this.getResources().getColor(R.color.colorWhite));
        row.addView(cb);
        //row.addView(tv);
        row.setId(id);

        exerciseList.addView(row);

    }


    private void setupAddButton(){
        //start add new exercise activity

        Button addButton = (Button) findViewById(R.id.bAddWorkingExercise);
        addButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                Intent exerciseIntent = new Intent(getApplicationContext(),NewExerciseActivity.class);
                startActivity(exerciseIntent);
            }
        });
    }
    private void setupRemoveButton(){
        //delete exercise from workout

        Button removeButton = (Button) findViewById(R.id.bRemoveWorkingExercise);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                for(int r_cnt = exerciseList.getChildCount() - 1; r_cnt >= 0; r_cnt--) {
                    View view = exerciseList.getChildAt(r_cnt);
                    if (view instanceof TableRow) {
                        TableRow row = (TableRow) view;

                        //find checkbox in the row
                        for(int r_itm = 0, r_itm_cnt = row.getChildCount(); r_itm < r_itm_cnt; r_itm++) {
                            View r_view = row.getChildAt(r_itm);
                            if (r_view instanceof CheckBox) {
                                CheckBox cb = (CheckBox) r_view;

                                if (cb.isChecked()){

                                    removeExerciseFromDB(row.getId());
                                    exerciseList.removeView(row);
                                    break;
                                }


                            }
                        }


                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void removeExerciseFromDB(Integer id){

        //ExerciseDatabaseHelper exerciseDB = new ExerciseDatabaseHelper(this);

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "delete from " + ExerciseDatabaseHelper.EXERCISE_TABLE +
                " where " + ExerciseDatabaseHelper.EXERCISE_ID +
                " = " + id;

        db.execSQL(query);

        db.close();
    }


    private void setupCancelButton(){
        //delete workout in progress

        Button cancelButton = (Button) findViewById(R.id.bCancelNewWorkout);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {

                deleteInProgressWorkout();

                Intent exerciseIntent = new Intent(getApplicationContext(),ExerciseActivity.class);
                startActivity(exerciseIntent);
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void deleteInProgressWorkout(){

        //ExerciseDatabaseHelper exerciseDB = new ExerciseDatabaseHelper(this);

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "select " + ExerciseDatabaseHelper.WORKOUT_ID +
                " from " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                " where " + ExerciseDatabaseHelper.WORKOUT_INCOMPLETE +
                " = 1 ";

        Cursor cursor = db.rawQuery(query,null);

        cursor.moveToFirst();

        Integer wID = cursor.getInt(0);

        db.close();

        deleteExercises(wID);

        deleteWorkout(wID);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void deleteWorkout(Integer id){

        //ExerciseDatabaseHelper exerciseDB = new ExerciseDatabaseHelper(this);

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "delete from " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                " where " + ExerciseDatabaseHelper.WORKOUT_ID +
                " = " + id;
        db.execSQL(query);
        db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void deleteExercises(Integer wID){

        //ExerciseDatabaseHelper exerciseDB = new ExerciseDatabaseHelper(this);

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "delete from " + ExerciseDatabaseHelper.EXERCISE_TABLE +
                " where " + ExerciseDatabaseHelper.EXERCISE_WORKOUT_ID +
                " = " + wID;
        db.execSQL(query);
        db.close();
    }


    private void setupFinishButton(){
        //mark workout as complete

        Button finishButton = (Button) findViewById(R.id.bFinishNewWorkout);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {


                SQLiteDatabase db = exerciseDB.getReadableDatabase();

                String query = "update " + ExerciseDatabaseHelper.WORKOUT_TABLE +
                        " set " + ExerciseDatabaseHelper.WORKOUT_INCOMPLETE +
                        " = 0 where " + ExerciseDatabaseHelper.WORKOUT_INCOMPLETE +
                        " = 1";

                db.execSQL(query);
                db.close();

                Intent exerciseIntent = new Intent(getApplicationContext(),ExerciseActivity.class);
                startActivity(exerciseIntent);
            }
        });
    }


}
