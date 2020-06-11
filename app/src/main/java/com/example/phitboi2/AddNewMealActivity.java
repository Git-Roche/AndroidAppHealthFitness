package com.example.phitboi2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewMealActivity extends AppCompatActivity{

    private FoodDatabaseHelper dietDB;
    public EditText foodName;
    public EditText protein;
    public EditText carbs;
    public EditText fat;
    public EditText gramsInServing;
    private EditText gramsConsumed;
    private Button add;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_meal);
        dietDB = new FoodDatabaseHelper(this);
        foodName = (EditText)findViewById(R.id.etxtFoodName);
        protein = (EditText)findViewById(R.id.etxtProtein);
        carbs = (EditText)findViewById(R.id.etxtCarb);
        fat = (EditText)findViewById(R.id.etxtFat);
        gramsInServing = (EditText)findViewById(R.id.etxtServing);
        gramsConsumed = (EditText)findViewById(R.id.etxtServingsEaten);
        add = (Button) findViewById(R.id.bAddMeal);
        addData();


        Button lookupButton = (Button) findViewById(R.id.bLookupExistingFood);

        lookupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lookupActivity = new Intent(getApplicationContext(),LookupFoodActivity.class);
                startActivity(lookupActivity);
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

    public void addData(){
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(foodName.getText())){
                    foodName.setError("Required.");
                }
                if (TextUtils.isEmpty(protein.getText())){
                    protein.setError("Required.");
                }
                if (TextUtils.isEmpty(carbs.getText())){
                    carbs.setError("Required.");
                }
                if (TextUtils.isEmpty(fat.getText())){
                    fat.setError("Required.");
                }
                if (TextUtils.isEmpty(gramsInServing.getText())){
                    gramsInServing.setError("Required.");
                }
                if(
                        TextUtils.isEmpty(protein.getText()) ||
                        TextUtils.isEmpty(carbs.getText()) ||
                        TextUtils.isEmpty(fat.getText()) ||
                        TextUtils.isEmpty(gramsInServing.getText()) ||
                        TextUtils.isEmpty(foodName.getText())
                ){
                    Toast.makeText(getApplicationContext(),"missing data", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (
                        (foodName.getText().toString().length() > 0)
                        &&  (Float.parseFloat(protein.getText().toString()) >= 0)
                        &&  (Float.parseFloat(carbs.getText().toString()) >= 0)
                        &&  (Float.parseFloat(fat.getText().toString()) >= 0)
                        &&  (Float.parseFloat(gramsInServing.getText().toString()) >= 0)
                ){

                    dietDB.addFoodData(
                            foodName.getText().toString(),
                            protein.getText().toString(),
                            carbs.getText().toString(),
                            fat.getText().toString(),
                            gramsInServing.getText().toString(),
                            gramsConsumed.getText().toString()
                    );
                    startActivity(new Intent(AddNewMealActivity.this, DietActivity.class));

                } else {
                    Toast.makeText(getApplicationContext(),"bad data", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}
