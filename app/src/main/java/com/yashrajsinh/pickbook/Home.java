package com.yashrajsinh.pickbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.yashrajsinh.pickbook.Fragments.AccountFragment;
import com.yashrajsinh.pickbook.Fragments.HomeFragment;
import com.yashrajsinh.pickbook.Fragments.UploadFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.sdsmdg.tastytoast.TastyToast;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    break;
                case R.id.nav_account:
                    selectedFragment = new AccountFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    break;
                case R.id.nav_upload:
                    selectedFragment = new UploadFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    break;
                case R.id.nav_logout:
                    firebaseAuth.signOut();
                    TastyToast.makeText(Home.this, "Logging out...", TastyToast.LENGTH_SHORT, TastyToast.CONFUSING).show();
                    startActivity(new Intent(Home.this, MainActivity.class));
                    finish();
                    break;
            }

            return true;
        }
    };

}
