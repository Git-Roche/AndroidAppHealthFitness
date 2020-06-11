package com.example.phitboi2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button dietButton = (Button) findViewById(R.id.bDiet);

        dietButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dietActivity = new Intent(getApplicationContext(),DietActivity.class);
                startActivity(dietActivity);
            }
        });

        Button exerciseButton = (Button) findViewById(R.id.bExercise);

        exerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent exerciseActivity = new Intent(getApplicationContext(),ExerciseActivity.class);
                startActivity(exerciseActivity);
            }
        });
    }

}
