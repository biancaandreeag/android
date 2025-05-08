package com.example.likes2love.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.likes2love.models.SentimentAnalysisResult;

import java.util.ArrayList;
import java.util.List;

public class SentimentPieChart {
    private List<SentimentAnalysisResult.SentimentDistribution> distribution;
    private int width;
    private int height;
    private int[] colors = {
            Color.rgb(76, 175, 80),   // Very Positive - Green
            Color.rgb(139, 195, 74),   // Positive - Light Green
            Color.rgb(255, 235, 59),   // Neutral - Yellow
            Color.rgb(255, 152, 0),    // Negative - Orange
            Color.rgb(244, 67, 54)     // Very Negative - Red
    };

    public SentimentPieChart(List<SentimentAnalysisResult.SentimentDistribution> distribution) {
        this.distribution = distribution != null ? distribution : new ArrayList<>();
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
        if (distribution == null || distribution.isEmpty()) {
            return;
        }

        // Initialize paints
        Paint piePaint = new Paint();
        piePaint.setStyle(Paint.Style.FILL);
        piePaint.setAntiAlias(true);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setAntiAlias(true);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);

        // Calculate total value for percentages
        int total = 0;
        for (SentimentAnalysisResult.SentimentDistribution item : distribution) {
            total += item.getValue();
        }

        // Set up pie chart rectangle
        int pieSize = Math.min(width, height) / 2;
        int centerX = width / 3;
        int centerY = height / 2;
        RectF pieRect = new RectF(centerX - pieSize, centerY - pieSize,
                centerX + pieSize, centerY + pieSize);

        // Draw pie slices
        float startAngle = 0;
        for (int i = 0; i < distribution.size(); i++) {
            SentimentAnalysisResult.SentimentDistribution item = distribution.get(i);
            float sweepAngle = 360f * item.getValue() / total;

            // Set slice color
            piePaint.setColor(colors[i % colors.length]);

            // Draw slice
            canvas.drawArc(pieRect, startAngle, sweepAngle, true, piePaint);

            // Calculate angle for label line
            float midAngle = startAngle + sweepAngle / 2;
            float radians = (float) Math.toRadians(midAngle);
            float lineX = (float) (centerX + pieSize * 0.8 * Math.cos(radians));
            float lineY = (float) (centerY + pieSize * 0.8 * Math.sin(radians));
            float textX = (float) (centerX + pieSize * 1.2 * Math.cos(radians));
            float textY = (float) (centerY + pieSize * 1.2 * Math.sin(radians));

            // Draw line to label
            canvas.drawLine(centerX, centerY, lineX, lineY, linePaint);
            canvas.drawLine(lineX, lineY, textX, textY, linePaint);

            // Draw label text
            String label = item.getLabel() + ": " + item.getValue() + "%";
            float textOffset = (midAngle > 90 && midAngle < 270) ? -textPaint.measureText(label) : 0;
            canvas.drawText(label, textX + textOffset, textY, textPaint);

            // Update start angle for next slice
            startAngle += sweepAngle;
        }

        // Draw chart title
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Sentiment Distribution", width / 2, 30, textPaint);
    }
}
