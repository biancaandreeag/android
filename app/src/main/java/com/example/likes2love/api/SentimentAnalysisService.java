package com.example.likes2love.api;

import android.util.Log;

import com.example.likes2love.models.SentimentAnalysisResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SentimentAnalysisService {
    private static final String TAG = "SentimentAnalysisService";

    /**
     * Analizează sentimentul pentru un URL dat
     */
    public static void analyzeSentiment(String url, String modelName, final ApiClient.ApiCallback<SentimentAnalysisResult> callback) {
        // Creează parametrii pentru cerere
        Map<String, Object> params = new HashMap<>();
        params.put("url", url);
        params.put("model", modelName);

        JSONObject requestBody = ApiClient.createJsonBody(params);

        // În mod normal, am face o cerere API reală aici
        // Dar pentru demonstrație, vom simula un răspuns
        simulateApiResponse(url, modelName, callback);
    }

    /**
     * Simulează un răspuns API pentru analiza de sentiment
     */
    private static void simulateApiResponse(String url, String modelName, final ApiClient.ApiCallback<SentimentAnalysisResult> callback) {
        // Simulăm o întârziere de rețea
        new android.os.Handler().postDelayed(() -> {
            try {
                // Creează un rezultat simulat
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

                // Returnează rezultatul simulat
                callback.onSuccess(result);

            } catch (Exception e) {
                Log.e(TAG, "Error simulating API response", e);
                callback.onError("Error processing data: " + e.getMessage());
            }
        }, 1500); // Simulăm o întârziere de 1.5 secunde
    }

    /**
     * Parsează un răspuns JSON în obiectul SentimentAnalysisResult
     */
    private static SentimentAnalysisResult parseResponse(String jsonResponse) throws JSONException {
        JSONObject json = new JSONObject(jsonResponse);

        SentimentAnalysisResult result = new SentimentAnalysisResult();
        result.setUrl(json.getString("url"));
        result.setSentiment(json.getString("sentiment"));
        result.setScore(json.getDouble("score"));

        // Parsează metricile
        JSONObject metricsJson = json.getJSONObject("metrics");
        Map<String, Double> metrics = new HashMap<>();
        Iterator<String> keys = metricsJson.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            metrics.put(key, metricsJson.getDouble(key));
        }
        result.setMetrics(metrics);

        // Parsează cuvintele frecvente
        JSONArray topWordsJson = json.getJSONArray("top_words");
        List<SentimentAnalysisResult.WordCount> topWords = new ArrayList<>();
        for (int i = 0; i < topWordsJson.length(); i++) {
            JSONObject wordJson = topWordsJson.getJSONObject(i);
            topWords.add(new SentimentAnalysisResult.WordCount(
                    wordJson.getString("word"),
                    wordJson.getInt("count")
            ));
        }
        result.setTopWords(topWords);

        // Parsează distribuția sentimentelor
        JSONArray distributionJson = json.getJSONArray("sentiment_distribution");
        List<SentimentAnalysisResult.SentimentDistribution> distribution = new ArrayList<>();
        for (int i = 0; i < distributionJson.length(); i++) {
            JSONObject itemJson = distributionJson.getJSONObject(i);
            distribution.add(new SentimentAnalysisResult.SentimentDistribution(
                    itemJson.getString("label"),
                    itemJson.getInt("value")
            ));
        }
        result.setSentimentDistribution(distribution);

        return result;
    }
}
