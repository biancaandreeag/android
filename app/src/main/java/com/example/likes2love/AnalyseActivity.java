package com.example.likes2love;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.likes2love.api.ApiService;
import com.example.likes2love.api.ApiServiceImpl;
import com.example.likes2love.database.DatabaseHelper;
import com.example.likes2love.models.SentimentAnalysisResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnalyseActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "likes2love_prefs";
    private static final String KEY_DARK_MODE = "pref_dark_mode";

    private EditText etUrl;
    private RadioGroup rgModels;
    private Button btnAnalyse;
    private ProgressBar progressBar;
    private TextView tvError;

    private DatabaseHelper dbHelper;
    private ApiService apiService;

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
        setContentView(R.layout.activity_analyse);

        // 2) Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // 3) Initialize API service
        apiService = ApiServiceImpl.getInstance();

        // 4) Wire up Dark Mode switch
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

        // 5) Initialize views
        etUrl = findViewById(R.id.etUrl);
        rgModels = findViewById(R.id.rgModels);
        btnAnalyse = findViewById(R.id.btnAnalyse);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);

        // 6) Menu setup
        View btnMenu = findViewById(R.id.btnMenu);
        View overlay = findViewById(R.id.menuOverlay);
        View menuHome = findViewById(R.id.menuHome);
        View menuHistory = findViewById(R.id.menuHistory);
        View menuAnalyse = findViewById(R.id.menuAnalyse);
        View menuAbout = findViewById(R.id.menuAbout);
        View menuDimmer = findViewById(R.id.menuDimmer);

        btnMenu.setOnClickListener(v -> overlay.setVisibility(View.VISIBLE));

        menuHome.setOnClickListener(v -> {
            overlay.setVisibility(View.GONE);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        menuHistory.setOnClickListener(v -> {
            overlay.setVisibility(View.GONE);
            startActivity(new Intent(this, HistoryActivity.class));
            finish();
        });

        menuAnalyse.setOnClickListener(v -> {
            overlay.setVisibility(View.GONE);
            // Already on Analyse screen
        });

        menuAbout.setOnClickListener(v -> {
            overlay.setVisibility(View.GONE);
            startActivity(new Intent(this, AboutActivity.class));
            finish();
        });

        menuDimmer.setOnClickListener(v -> overlay.setVisibility(View.GONE));

        // 7) Set up Analyse button
        btnAnalyse.setOnClickListener(v -> performAnalysis());

        // 8) Apply fade-in animations
        fadeInView(sw, 400, 0);
        fadeInView(btnMenu, 400, 100);
        fadeInView(findViewById(R.id.tvTitle), 400, 200);
        fadeInView(findViewById(R.id.cardView), 400, 300);
    }

    private void performAnalysis() {
        // 1) Get URL and validate
        String url = etUrl.getText().toString().trim();
        if (url.isEmpty()) {
            tvError.setText(R.string.error_empty_url);
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        // 2) Get selected model
        int selectedId = rgModels.getCheckedRadioButtonId();
        if (selectedId == -1) {
            tvError.setText(R.string.error_select_model);
            tvError.setVisibility(View.VISIBLE);
            return;
        }
        RadioButton radioButton = findViewById(selectedId);
        String modelName = radioButton.getText().toString();

        // 3) Hide error, show progress
        tvError.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        btnAnalyse.setEnabled(false);

        // 4) Call API service
        apiService.getSentimentAnalysis(modelName, url, new ApiService.SentimentAnalysisCallback() {
            @Override
            public void onSuccess(SentimentAnalysisResult result) {
                // 5) Save to history
                saveToHistory(url, modelName, result);

                // 6) Navigate to results screen
                Intent intent = new Intent(AnalyseActivity.this, AnalysisResultActivity.class);
                intent.putExtra("analysis_result", result);
                intent.putExtra("model_name", modelName);
                startActivity(intent);

                // 7) Reset UI
                progressBar.setVisibility(View.GONE);
                btnAnalyse.setEnabled(true);
            }

            @Override
            public void onError(String errorMessage) {
                // 8) Show error
                tvError.setText(errorMessage);
                tvError.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                btnAnalyse.setEnabled(true);
            }
        });
    }

    private void saveToHistory(String url, String modelName, SentimentAnalysisResult result) {
        try {
            // Format current date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String timestamp = sdf.format(new Date());

            // Insert into database
            long id = dbHelper.insertHistory(
                    url,
                    modelName,
                    result.getSentiment(),
                    result.getScore(),
                    timestamp
            );

            if (id == -1) {
                Toast.makeText(this, "Failed to save to history", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving to history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
