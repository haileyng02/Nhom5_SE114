package com.example.koffi.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.koffi.R;
import com.example.koffi.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StaffActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_staff);
        BottomNavigationView bottomNavigationView = findViewById(R.id.staff_bottom_nav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getLabel().equals("fragment_staff_order"))
                    bottomNavigationView.setVisibility(View.VISIBLE);
                else
                    bottomNavigationView.setVisibility(View.GONE);
            }
        });
    }
}