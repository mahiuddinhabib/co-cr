package com.habib.cocr;

import static com.habib.cocr.utils.GlobalState.getCurrentUserClassId;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habib.cocr.databinding.ActivityMainBinding;
import com.habib.cocr.ui.more.MoreOptionsBottomSheet;
import com.habib.cocr.utils.GlobalState;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static TextView actionBarTitle;
    ImageView accountIcon;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalState.getInstance(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        actionBarTitle = findViewById(R.id.fragment_title);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        accountIcon = findViewById(R.id.account_icon);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_schedule, R.id.navigation_events, R.id.navigation_notices, R.id.navigation_more)
//                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_more) {
                MoreOptionsBottomSheet moreOptionsBottomSheet = new MoreOptionsBottomSheet();
                moreOptionsBottomSheet.show(getSupportFragmentManager(), "moreOptionsBottomSheet");
                return true;
            } else {
                NavigationUI.onNavDestinationSelected(item, navController);
                return true;
            }
        });

        accountIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountDetailActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (getCurrentUserClassId() == null) {
            Intent intent = new Intent(MainActivity.this, CreateOrJoinClassActivity.class);
            startActivity(intent);
            finish();
        }
    }

}