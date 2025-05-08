package com.example.likes2love;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AnalyseActivity extends AppCompatActivity {
    private static final String PREFS_NAME    = "likes2love_prefs";
    private static final String KEY_DARK_MODE = "pref_dark_mode";

    private SwitchCompat         swTheme;
    private Spinner              spModel;
    private EditText             etLinkInput;
    private Button               btnGoAnalyse;
    private FloatingActionButton fabAdd;

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
        setContentView(R.layout.activity_analyse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        // 3) Wire up Dark Mode switch
        swTheme = findViewById(R.id.switchTheme);
        swTheme.setChecked(darkMode);
        swTheme.setOnCheckedChangeListener((button, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // 4) Spinner setup
        spModel = findViewById(R.id.spModel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.model_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spModel.setAdapter(adapter);

        // 5) Other views
        etLinkInput   = findViewById(R.id.etLinkInput);
        btnGoAnalyse  = findViewById(R.id.btnGoAnalyse);
        fabAdd        = findViewById(R.id.fabAdd);

        // 6) Staggered fade-ins (exact same pattern as ServiceSelectionActivity)
        fadeInView(swTheme,       400,   0);
        fadeInView(spModel,       400, 200);
        fadeInView(etLinkInput,   400, 400);
        fadeInView(btnGoAnalyse,  400, 600);
        fadeInView(fabAdd,        400, 800);

        // 7) Button logic
        btnGoAnalyse.setOnClickListener(v -> {
            String selectedModel = spModel.getSelectedItem().toString();
            String url           = etLinkInput.getText().toString().trim();
            // TODO: dispatch to your analyzer with URL + selectedModel
        });

        fabAdd.setOnClickListener(v -> {
            // TODO: open your “add history” UI
        });
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
