package com.example.likes2love;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.likes2love.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private static final String PREFS_NAME    = "likes2love_prefs";
    private static final String KEY_DARK_MODE = "pref_dark_mode";

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyItems;
    private List<HistoryItem> filteredItems;

    private Spinner spinnerModelFilter;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private Button btnApplyFilters;
    private Button btnClearFilters;
    private TextView tvEmptyState;

    private Date startDate = null;
    private Date endDate = null;
    private String selectedModel = "All Models";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    private DatabaseHelper dbHelper;

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
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

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

        // 4) Menu setup
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

        // 5) Initialize database helper
        dbHelper = DatabaseHelper.getInstance(this);

        // 6) Initialize filter views
        spinnerModelFilter = findViewById(R.id.spinnerModelFilter);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        btnClearFilters = findViewById(R.id.btnClearFilters);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // 7) Setup model filter spinner
        setupModelSpinner();

        // 8) Setup date pickers
        tvStartDate.setOnClickListener(v -> showDatePicker(true));
        tvEndDate.setOnClickListener(v -> showDatePicker(false));

        // 9) Setup filter buttons
        btnApplyFilters.setOnClickListener(v -> applyFilters());
        btnClearFilters.setOnClickListener(v -> clearFilters());

        // 10) Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 11) Load data from database
        loadHistoryData();

        // 12) Initialize adapter
        adapter = new HistoryAdapter(filteredItems, this::onHistoryItemClick);
        recyclerView.setAdapter(adapter);

        // 13) Apply fade-in animations
        fadeInView(sw, 400, 0);
        fadeInView(btnMenu, 400, 200);
        fadeInView(findViewById(R.id.tvHistoryTitle), 400, 400);
        fadeInView(findViewById(R.id.cardFilters), 400, 600);
        fadeInView(findViewById(R.id.tableHeader), 400, 800);
        fadeInView(recyclerView, 400, 1000);

        // 14) Check if we have items to display
        updateEmptyState();

        // 15) Add sample data if database is empty (for testing)
        if (historyItems.isEmpty()) {
            addSampleData();
            loadHistoryData(); // Reload data after adding samples
            adapter.notifyDataSetChanged();
            updateEmptyState();
        }
    }

    private void setupModelSpinner() {
        // Create a list with "All Models" and the model options
        List<String> modelOptions = new ArrayList<>();
        modelOptions.add("All Models");

        // Add the models from the string array resource
        String[] models = getResources().getStringArray(R.array.model_options);
        for (String model : models) {
            modelOptions.add(model);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                modelOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelFilter.setAdapter(adapter);
    }

    private void showDatePicker(boolean isStartDate) {
        // Create a calendar instance for current date
        final Calendar calendar = Calendar.getInstance();

        // Set to the currently selected date if available
        if (isStartDate && startDate != null) {
            calendar.setTime(startDate);
        } else if (!isStartDate && endDate != null) {
            calendar.setTime(endDate);
        }

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, dayOfMonth);

                    if (isStartDate) {
                        startDate = selectedCalendar.getTime();
                        tvStartDate.setText(displayFormat.format(startDate));
                    } else {
                        endDate = selectedCalendar.getTime();
                        tvEndDate.setText(displayFormat.format(endDate));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void loadHistoryData() {
        // Load all history items from database
        historyItems = dbHelper.getAllHistoryItems();

        // Initialize filtered items with all items
        filteredItems = new ArrayList<>(historyItems);
    }

    private void applyFilters() {
        selectedModel = spinnerModelFilter.getSelectedItem().toString();

        // Get filtered items from database
        filteredItems = dbHelper.getFilteredHistoryItems(
                selectedModel.equals("All Models") ? null : selectedModel,
                startDate,
                endDate
        );

        // Update the adapter
        adapter = new HistoryAdapter(filteredItems, this::onHistoryItemClick);
        recyclerView.setAdapter(adapter);

        // Check if we have items to display
        updateEmptyState();
    }

    private void clearFilters() {
        // Reset filter values
        spinnerModelFilter.setSelection(0); // "All Models"
        startDate = null;
        endDate = null;
        tvStartDate.setText("Select start date");
        tvEndDate.setText("Select end date");

        // Reset to all items
        loadHistoryData();

        // Update the adapter
        adapter = new HistoryAdapter(filteredItems, this::onHistoryItemClick);
        recyclerView.setAdapter(adapter);

        // Check if we have items to display
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }
    }

    private void onHistoryItemClick(HistoryItem item) {
        // Navigate to analysis details
        Intent intent = new Intent(this, AnalyseActivity.class);
        intent.putExtra("history_id", item.getId());
        startActivity(intent);
    }

    private void addSampleData() {
        // Add some sample data to the database for testing
        Calendar cal = Calendar.getInstance();

        // Today
        dbHelper.addHistoryItem(
                "Facebook Post Analysis",
                "BERT",
                cal.getTime(),
                "https://facebook.com/example/post/123",
                "Positive",
                0.85
        );

        // Yesterday
        cal.add(Calendar.DAY_OF_MONTH, -1);
        dbHelper.addHistoryItem(
                "TikTok Video Analysis",
                "GPT",
                cal.getTime(),
                "https://tiktok.com/@user/video/123456",
                "Neutral",
                0.52
        );

        // 3 days ago
        cal.add(Calendar.DAY_OF_MONTH, -2);
        dbHelper.addHistoryItem(
                "Facebook Comment Analysis",
                "RandomForest",
                cal.getTime(),
                "https://facebook.com/example/comment/789",
                "Negative",
                0.25
        );

        // 1 week ago
        cal.add(Calendar.DAY_OF_MONTH, -4);
        dbHelper.addHistoryItem(
                "TikTok Trend Analysis",
                "RoBerta",
                cal.getTime(),
                "https://tiktok.com/tag/trending",
                "Very Positive",
                0.95
        );

        // 2 weeks ago
        cal.add(Calendar.DAY_OF_MONTH, -7);
        dbHelper.addHistoryItem(
                "Facebook Group Analysis",
                "BERT",
                cal.getTime(),
                "https://facebook.com/groups/example",
                "Slightly Negative",
                0.35
        );
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
