package com.example.likes2love;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ServiceSelectionActivity extends AppCompatActivity {
    private static final String PREFS_NAME    = "likes2love_prefs";
    private static final String KEY_DARK_MODE = "pref_dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1) Load & apply saved theme BEFORE super.onCreate()
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                darkMode
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);

        // 2) Edge-to-edge + inflate
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        // 3) Theme switch wiring
        SwitchCompat sw = findViewById(R.id.switchTheme);
        sw.setChecked(darkMode);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // 4) Bind views
        View tvChoose    = findViewById(R.id.tvChooseService);
        View btnFacebook = findViewById(R.id.btnFacebook);
        View btnTikTok   = findViewById(R.id.btnTikTok);
        View btnMenu     = findViewById(R.id.btnMenu);
        View overlay     = findViewById(R.id.menuOverlay);
        View menuHome    = findViewById(R.id.menuHome);
        View menuHistory = findViewById(R.id.menuHistory);
        View menuAnalyse = findViewById(R.id.menuAnalyse);
        View menuAbout   = findViewById(R.id.menuAbout);
        View menuDimmer  = findViewById(R.id.menuDimmer);

        // 5) Initial fade-ins (staggered)
        fadeInView(sw,         400,   0);
        fadeInView(tvChoose,   400, 200);
        fadeInView(btnFacebook,400, 400);
        fadeInView(btnTikTok,  400, 600);
        fadeInView(btnMenu,    400, 800);

        // 6) FACEBOOK / TIKTOK → AnalyseActivity
        btnFacebook.setOnClickListener(v -> {
            Intent i = new Intent(this, AnalyseActivity.class);
            i.putExtra("service", "facebook");
            startActivity(i);
        });
        btnTikTok.setOnClickListener(v -> {
            Intent i = new Intent(this, AnalyseActivity.class);
            i.putExtra("service", "tiktok");
            startActivity(i);
        });

        // 7) Menu overlay logic & navigation
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
            startActivity(new Intent(this, AnalyseActivity.class));
        });

        menuAbout.setOnClickListener(v -> {
            overlay.setVisibility(View.GONE);
            startActivity(new Intent(this, AboutActivity.class));
        });

        menuDimmer.setOnClickListener(v -> overlay.setVisibility(View.GONE));
    }

    /** Helper to fade in any view */
    private void fadeInView(View view, int duration, int delay) {
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(duration);
        fadeIn.setStartOffset(delay);
        fadeIn.setFillAfter(true);
        view.startAnimation(fadeIn);
    }
}
