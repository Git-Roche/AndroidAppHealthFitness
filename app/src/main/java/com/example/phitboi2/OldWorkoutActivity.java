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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableRow;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class OldWorkoutActivity extends AppCompatActivity {

    private LinearLayout table;
    private ExerciseDatabaseHelper exerciseDB;
    private Button removeButton;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_workouts);

        table = (LinearLayout) findViewById(R.id.llOldWkTable);
        exerciseDB = new ExerciseDatabaseHelper(this);
        removeButton = (Button) findViewById(R.id.bOldWkDelete);

        setupFoodList();
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


    @RequiresApi(api = Build.VERSION_CODES.P)
    public void setupFoodList(){

        SQLiteDatabase db = exerciseDB.getReadableDatabase();

        String query = "Select w." + ExerciseDatabaseHelper.WORKOUT_NAME +
                ", o." + ExerciseDatabaseHelper.OLD_WORKOUT_DATETIME +
                ", o." + ExerciseDatabaseHelper.OLD_WORKOUT_ID +

                ", sum(oe." + ExerciseDatabaseHelper.OLD_EXERCISE_DISTANCE +
                " + oe." + ExerciseDatabaseHelper.OLD_EXERCISE_DURATION +
                " + oe." + ExerciseDatabaseHelper.OLD_EXERCISE_REPS +
                " + oe." + ExerciseDatabaseHelper.OLD_EXERCISE_WEIGHT +
                ") / nullif(sum(e." +ExerciseDatabaseHelper.EXERCISE_DISTANCE +
                " + e." + ExerciseDatabaseHelper.EXERCISE_DURATION +
                " + e." + ExerciseDatabaseHelper.EXERCISE_REPS +
                " + e." + ExerciseDatabaseHelper.EXERCISE_WEIGHT +
                "),0) * 100.0 as progress" +

                " from " + ExerciseDatabaseHelper.OLD_WORKOUT_TABLE + " o " +
                " inner join " + ExerciseDatabaseHelper.WORKOUT_TABLE + " w " +
                " on o." + ExerciseDatabaseHelper.OLD_WORKOUT_WORKOUT_ID +
                " = w." + ExerciseDatabaseHelper.WORKOUT_ID +
                " left join " + ExerciseDatabaseHelper.EXERCISE_TABLE + " e " +
                " on e." + ExerciseDatabaseHelper.EXERCISE_WORKOUT_ID +
                " = w." + ExerciseDatabaseHelper.WORKOUT_ID +
                " left join " + ExerciseDatabaseHelper.OLD_EXERCISE_TABLE + " oe " +
                " on oe." + ExerciseDatabaseHelper.OLD_EXERCISE_EXERCISE_ID +
                " = e." + ExerciseDatabaseHelper.EXERCISE_ID +
                " and oe." + ExerciseDatabaseHelper.OLD_EXERCISE_OLD_WORKOUT_ID +
                " = o." + ExerciseDatabaseHelper.OLD_WORKOUT_ID +

                " group by w." + ExerciseDatabaseHelper.WORKOUT_NAME +
                ", o." + ExerciseDatabaseHelper.OLD_WORKOUT_DATETIME +
                ", o." + ExerciseDatabaseHelper.OLD_WORKOUT_ID +
                " order by " + ExerciseDatabaseHelper.OLD_WORKOUT_DATETIME +
                " desc";

        Cursor cursor = db.rawQuery(query, null);


        while (cursor.moveToNext()){
            String name = cursor.getString(0);
            Date dateTime = new Date(cursor.getInt(1));
            Integer id = cursor.getInt(2);
            Integer progress = cursor.getInt(3);

            addWorkoutTableRow(name, dateTime, id, progress);
        }

        cursor.close();
        db.close();
    }

    private void addWorkoutTableRow(String name, Date dateTime, Integer id, Integer progress){
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);

        CheckBox cb = new CheckBox(this);

        if (progress == null) {
            progress = 0;
        }

        String txt = name +" (" + progress + "%)" +"\n" + dateTime;

        cb.setText(txt);
        cb.setTextColor(this.getResources().getColor(R.color.colorBlack));

        row.setBackgroundColor(this.getResources().getColor(R.color.colorWhite));
        row.addView(cb);

        row.setId(id);

        table.addView(row);
    }

    public void setupRemoveButton() {
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
        for(int r_cnt = table.getChildCount() - 1; r_cnt >= 0; r_cnt--) {
            View view = table.getChildAt(r_cnt);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;

                //find checkbox in the row
                for(int r_itm = 0, r_itm_cnt = row.getChildCount(); r_itm < r_itm_cnt; r_itm++) {
                    View r_view = row.getChildAt(r_itm);
                    if (r_view instanceof CheckBox) {
                        CheckBox cb = (CheckBox) r_view;

                        if (cb.isChecked()){

                            removeFromDB(row.getId());
                            table.removeView(row);
                            break;
                        }


                    }
                }


            }
        }
    }

    private void removeFromDB(Integer wid){
        SQLiteDatabase db = exerciseDB.getReadableDatabase();
        String queryExercise = "delete from " + ExerciseDatabaseHelper.OLD_EXERCISE_TABLE +
                " where " + ExerciseDatabaseHelper.OLD_EXERCISE_OLD_WORKOUT_ID +
                " = " + wid;

        String queryWorkout = "delete from " + ExerciseDatabaseHelper.OLD_WORKOUT_TABLE +
                " where " + ExerciseDatabaseHelper.OLD_WORKOUT_ID +
                " = " + wid;

        db.execSQL(queryExercise);
        db.execSQL(queryWorkout);

        db.close();
    }


}
