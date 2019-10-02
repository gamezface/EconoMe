package br.com.coutinhoanderson.econome.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import br.com.coutinhoanderson.econome.R;
import br.com.coutinhoanderson.econome.utils.NavigationManager;

public class HomescreenActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                NavigationManager.openFragment(getSupportFragmentManager(), new BillFragment(), "Home", R.id.fragment_container);
                return true;
            case R.id.navigation_funds:
                return true;
            case R.id.navigation_group:
                return true;
        }
        return false;
    };

    public void addWorkout(View view) {
        Intent intent = new Intent(HomescreenActivity.this, AddExpenseActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

}