package com.example.photoslidingpuzzle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

//    Toolbar toolbar;

    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        if(item.getItemId() == R.id.action_info) {
            Toast.makeText(getApplicationContext(), "버튼 클릭", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
}