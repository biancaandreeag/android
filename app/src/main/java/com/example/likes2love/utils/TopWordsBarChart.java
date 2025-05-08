package com.example.likes2love.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.likes2love.models.SentimentAnalysisResult;

import java.util.ArrayList;
import java.util.List;

public class TopWordsBarChart {
    private List<SentimentAnalysisResult.WordCount> topWords;
    private int width;
    private int height;
    private int[] colors = {
            Color.rgb(255, 87, 34),   // Deep Orange
            Color.rgb(0, 188, 212),    // Cyan
            Color.rgb(233, 30, 99),    // Pink
            Color.rgb(255, 235, 59),   // Yellow
            Color.rgb(63, 81, 181)     // Indigo
    };

    public TopWordsBarChart(List<SentimentAnalysisResult.WordCount> topWords) {
        this.topWords = topWords != null ? topWords : new ArrayList<>();
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
        if (topWords == null || topWords.isEmpty()) {
            return;
        }

        // Initialize paints
        Paint barPaint = new Paint();
        barPaint.setStyle(Paint.Style.FILL);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.LEFT);

        Paint axisPaint = new Paint();
        axisPaint.setColor(Color.WHITE);
        axisPaint.setStrokeWidth(2);

        // Draw axes
        canvas.drawLine(150, height - 50, width - 50, height - 50, axisPaint); // X-axis
        canvas.drawLine(150, 50, 150, height - 50, axisPaint); // Y-axis

        // Find maximum count for scaling
        int maxCount = 0;
        for (SentimentAnalysisResult.WordCount wordCount : topWords) {
            maxCount = Math.max(maxCount, wordCount.getCount());
        }

        // Calculate bar height and spacing
        int numBars = topWords.size();
        float barHeight = (height - 100) / numBars * 0.7f;
        float spacing = (height - 100) / numBars * 0.3f;

        // Draw bars and labels
        for (int i = 0; i < numBars; i++) {
            // Set bar color
            barPaint.setColor(colors[i % colors.length]);

            // Calculate bar position and width
            SentimentAnalysisResult.WordCount wordCount = topWords.get(i);
            float barWidth = (width - 200) * ((float) wordCount.getCount() / maxCount);
            float left = 150;
            float top = 50 + i * (barHeight + spacing);
            float right = left + barWidth;
            float bottom = top + barHeight;

            // Draw bar
            canvas.drawRect(left, top, right, bottom, barPaint);

            // Draw word label inside bar
            textPaint.setColor(Color.WHITE);
            canvas.drawText(wordCount.getWord(),
                    left + 10,
                    top + barHeight / 2 + 10,
                    textPaint);

            // Draw count at end of bar
            canvas.drawText(String.valueOf(wordCount.getCount()),
                    right + 10,
                    top + barHeight / 2 + 10,
                    textPaint);
        }

        // Draw chart title
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Top Words", width / 2, 30, textPaint);
    }
}
