package com.example.likes2love.api;

import android.os.Handler;
import android.util.Log;

import com.example.likes2love.models.SentimentAnalysisResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementare a ApiService care simulează răspunsuri API
 */
public class ApiServiceImpl implements ApiService {
    private static final String TAG = "ApiServiceImpl";

    @Override
    public void getSentimentAnalysis(String model, String url, SentimentAnalysisCallback callback) {
        // Simulăm o întârziere de rețea
        new Handler().postDelayed(() -> {
            try {
                // Creează un rezultat simulat
                SentimentAnalysisResult result = createMockResult(model, url);
                callback.onSuccess(result);
            } catch (Exception e) {
                Log.e(TAG, "Error simulating API response", e);
                callback.onError("Error processing data: " + e.getMessage());
            }
        }, 1500); // Simulăm o întârziere de 1.5 secunde
    }

    /**
     * Creează un rezultat simulat pentru analiza de sentiment
     */
    private SentimentAnalysisResult createMockResult(String model, String url) {
        SentimentAnalysisResult result = new SentimentAnalysisResult();
        result.setUrl(url);

        // Setează sentimentul și scorul în funcție de URL
        if (url.contains("positive")) {
            result.setSentiment("Positive");
            result.setScore(0.85);
        } else if (url.contains("negative")) {
            result.setSentiment("Negative");
            result.setScore(0.25);
        } else {
            result.setSentiment("Neutral");
            result.setScore(0.50);
        }

        // Adaugă metrici
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("Confidence", 0.92);
        metrics.put("Relevance", 0.78);
        metrics.put("Accuracy", 0.85);
        metrics.put("Precision", 0.81);
        result.setMetrics(metrics);

        // Adaugă cuvinte frecvente
        List<SentimentAnalysisResult.WordCount> topWords = new ArrayList<>();
        topWords.add(new SentimentAnalysisResult.WordCount("excellent", 12));
        topWords.add(new SentimentAnalysisResult.WordCount("good", 10));
        topWords.add(new SentimentAnalysisResult.WordCount("service", 8));
        topWords.add(new SentimentAnalysisResult.WordCount("product", 7));
        topWords.add(new SentimentAnalysisResult.WordCount("recommend", 5));
        result.setTopWords(topWords);

        // Adaugă distribuția sentimentelor
        List<SentimentAnalysisResult.SentimentDistribution> distribution = new ArrayList<>();
        distribution.add(new SentimentAnalysisResult.SentimentDistribution("Very Positive", 35));
        distribution.add(new SentimentAnalysisResult.SentimentDistribution("Positive", 25));
        distribution.add(new SentimentAnalysisResult.SentimentDistribution("Neutral", 20));
        distribution.add(new SentimentAnalysisResult.SentimentDistribution("Negative", 15));
        distribution.add(new SentimentAnalysisResult.SentimentDistribution("Very Negative", 5));
        result.setSentimentDistribution(distribution);

        return result;
    }

    /**
     * Singleton pentru a obține instanța serviciului
     */
    private static ApiServiceImpl instance;

    public static ApiServiceImpl getInstance() {
        if (instance == null) {
            instance = new ApiServiceImpl();
        }
        return instance;
    }
}
