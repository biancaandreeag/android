package com.example.likes2love.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MetricsBarChart {
    private Map<String, Double> metrics;
    private List<String> labels;
    private List<Double> values;
    private int width;
    private int height;
    private int[] colors = {
            Color.rgb(76, 175, 80),   // Green
            Color.rgb(33, 150, 243),   // Blue
            Color.rgb(255, 193, 7),    // Amber
            Color.rgb(156, 39, 176)    // Purple
    };

    public MetricsBarChart(Map<String, Double> metrics) {
        this.metrics = metrics;

        // Extract labels and values from the map
        this.labels = new ArrayList<>();
        this.values = new ArrayList<>();

        if (metrics != null) {
            this.labels.addAll(metrics.keySet());
            for (String label : labels) {
                values.add(metrics.get(label));
            }
        }
    }

    public ImageView createChartImageView(Context context, int width, int height) {
        this.width = width;
        this.height = height;

        // Create bitmap and canvas
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw chart on canvas
        drawChart(canvas);

        // Create ImageView and set bitmap
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);

        return imageView;
    }

    private void drawChart(Canvas canvas) {
        // Skip drawing if no data
        if (metrics == null || metrics.isEmpty()) {
            return;
        }

        // Initialize paints
        Paint barPaint = new Paint();
        barPaint.setStyle(Paint.Style.FILL);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint axisPaint = new Paint();
        axisPaint.setColor(Color.WHITE);
        axisPaint.setStrokeWidth(2);

        // Draw axes
        canvas.drawLine(100, height - 100, width - 50, height - 100, axisPaint); // X-axis
        canvas.drawLine(100, 50, 100, height - 100, axisPaint); // Y-axis

        // Calculate bar width and spacing
        int numBars = values.size();
        float barWidth = (width - 150) / numBars * 0.6f;
        float spacing = (width - 150) / numBars * 0.4f;

        // Draw bars and labels
        for (int i = 0; i < numBars; i++) {
            // Set bar color
            barPaint.setColor(colors[i % colors.length]);

            // Calculate bar position and height
            float value = values.get(i).floatValue();
            float barHeight = (height - 150) * value;
            float left = 100 + i * (barWidth + spacing);
            float top = height - 100 - barHeight;
            float right = left + barWidth;
            float bottom = height - 100;

            // Draw bar
            canvas.drawRect(left, top, right, bottom, barPaint);

            // Draw value on top of bar
            canvas.drawText(String.format("%.2f", value),
                    left + barWidth / 2,
                    top - 10,
                    textPaint);

            // Draw label below bar
            canvas.drawText(labels.get(i),
                    left + barWidth / 2,
                    height - 60,
                    textPaint);
        }

        // Draw chart title
        textPaint.setTextSize(40);
        canvas.drawText("Metrics", width / 2, 30, textPaint);
    }
}
