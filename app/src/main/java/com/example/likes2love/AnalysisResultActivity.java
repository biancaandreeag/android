package com.example.likes2love;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.likes2love.utils.MetricsBarChart;
import com.example.likes2love.utils.SentimentPieChart;
import com.example.likes2love.utils.TopWordsBarChart;
import com.example.likes2love.models.SentimentAnalysisResult;

public class AnalysisResultActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "likes2love_prefs";
    private static final String KEY_DARK_MODE = "pref_dark_mode";

    private TextView tvUrl;
    private TextView tvModel;
    private TextView tvSentiment;
    private TextView tvScore;
    private LinearLayout metricsContainer;
    private LinearLayout topWordsContainer;
    private LinearLayout distributionContainer;

    private SentimentAnalysisResult analysisResult;

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
        setContentView(R.layout.activity_analysis_result);

        // 3) Wire up Dark Mode switch
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

        // 4) Initialize views
        tvUrl = findViewById(R.id.tvUrl);
        tvModel = findViewById(R.id.tvModel);
        tvSentiment = findViewById(R.id.tvSentiment);
        tvScore = findViewById(R.id.tvScore);
        metricsContainer = findViewById(R.id.metricsContainer);
        topWordsContainer = findViewById(R.id.topWordsContainer);
        distributionContainer = findViewById(R.id.distributionContainer);

        // 5) Menu setup
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
            startActivity(new Intent(this, AboutActivity.class));
        });

        menuDimmer.setOnClickListener(v -> overlay.setVisibility(View.GONE));

        // 6) Get analysis result from intent
        if (getIntent().hasExtra("analysis_result")) {
            try {
                analysisResult = (SentimentAnalysisResult) getIntent().getSerializableExtra("analysis_result");
                displayAnalysisResult();
            } catch (Exception e) {
                Toast.makeText(this, "Error loading analysis data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            // Handle error - no data passed
            Toast.makeText(this, "No analysis data provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 7) Apply fade-in animations
        fadeInView(sw, 400, 0);
        fadeInView(btnMenu, 400, 100);
        fadeInView(findViewById(R.id.tvResultTitle), 400, 200);
    }

    private void displayAnalysisResult() {
        // Display basic info
        tvUrl.setText("URL: " + analysisResult.getUrl());
        tvModel.setText("Model: " + getIntent().getStringExtra("model_name"));
        tvSentiment.setText("Sentiment: " + analysisResult.getSentiment());
        tvScore.setText("Score: " + String.format("%.2f", analysisResult.getScore()));

        // Setup custom charts
        setupMetricsChart();
        setupTopWordsChart();
        setupSentimentDistributionChart();
    }

    private void setupMetricsChart() {
        // Create metrics chart
        MetricsBarChart metricsChart = new MetricsBarChart(analysisResult.getMetrics());

        // Create ImageView with chart and add to container
        ImageView chartImageView = metricsChart.createChartImageView(this,
                getResources().getDisplayMetrics().widthPixels - 64, // Full width minus padding
                500);

        chartImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                500)); // Height in pixels

        metricsContainer.addView(chartImageView);
    }

    private void setupTopWordsChart() {
        // Create top words chart
        TopWordsBarChart topWordsChart = new TopWordsBarChart(analysisResult.getTopWords());

        // Create ImageView with chart and add to container
        ImageView chartImageView = topWordsChart.createChartImageView(this,
                getResources().getDisplayMetrics().widthPixels - 64, // Full width minus padding
                500);

        chartImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                500)); // Height in pixels

        topWordsContainer.addView(chartImageView);
    }

    private void setupSentimentDistributionChart() {
        // Create sentiment distribution chart
        SentimentPieChart pieChart = new SentimentPieChart(analysisResult.getSentimentDistribution());

        // Create ImageView with chart and add to container
        ImageView chartImageView = pieChart.createChartImageView(this,
                getResources().getDisplayMetrics().widthPixels - 64, // Full width minus padding
                500);

        chartImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                500)); // Height in pixels

        distributionContainer.addView(chartImageView);
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
