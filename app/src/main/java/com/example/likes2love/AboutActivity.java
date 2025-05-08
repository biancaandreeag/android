package com.example.likes2love;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import androidx.appcompat.widget.SwitchCompat;

public class AboutActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String PREFS_NAME    = "likes2love_prefs";
    private static final String KEY_DARK_MODE = "pref_dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                darkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up map fragment
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        View btnMenu     = findViewById(R.id.btnMenu);
        View overlay     = findViewById(R.id.menuOverlay);
        View menuHome    = findViewById(R.id.menuHome);
        View menuHistory = findViewById(R.id.menuHistory);
        View menuAnalyse = findViewById(R.id.menuAnalyse);
        View menuAbout   = findViewById(R.id.menuAbout);
        View menuDimmer  = findViewById(R.id.menuDimmer);

        btnMenu.setOnClickListener(v -> overlay.setVisibility(View.VISIBLE));

        menuHome.setOnClickListener(v -> {
            overlay.setVisibility(View.GONE);
            startActivity(new Intent(this, MainActivity.class));
        });

        menuHistory.setOnClickListener(v -> {
            overlay.setVisibility(View.GONE);
            startActivity(new Intent(this, HistoryActivity.class));
        });

        menuAnalyse.setOnClickListener(v -> {
            overlay.setVisibility(View.GONE);
            startActivity(new Intent(this, ServiceSelectionActivity.class));
        });

        menuAbout.setOnClickListener(v -> {
            overlay.setVisibility(View.GONE);
        });

        menuDimmer.setOnClickListener(v -> overlay.setVisibility(View.GONE));

        // Theme switch setup
        SwitchCompat sw = findViewById(R.id.switchTheme);
        sw.setChecked(darkMode);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();

            // Apply the theme change immediately
            AppCompatDelegate.setDefaultNightMode(
                    isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Plasează un marker (ex. în București)
        LatLng bucuresti = new LatLng(44.4268, 26.1025);
        mMap.addMarker(new MarkerOptions().position(bucuresti).title("Salut din București"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bucuresti, 12));
    }
}
