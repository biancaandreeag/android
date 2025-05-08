package com.example.likes2love.utils;

import android.content.Context;
import android.util.Log;

import com.example.likes2love.R;
import com.example.likes2love.models.SentimentAnalysisResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonDataParser {
    private static final String TAG = "JsonDataParser";
    
    public static SentimentAnalysisResult findAnalysisResult(Context context, String model, String url) {
        try {
            // Load JSON data from raw resource
            String jsonString = loadJSONFromRaw(context);
            JSONObject jsonData = new JSONObject(jsonString);
            
            // Parse models array
            JSONArray modelsArray = jsonData.getJSONArray("models");
            
            // Find the specified model
            for (int i = 0; i < modelsArray.length(); i++) {
                JSONObject modelObj = modelsArray.getJSONObject(i);
                String modelName = modelObj.getString("name");
                
                if (modelName.equals(model)) {
                    // Found the model, now find the analysis with matching URL
                    JSONArray analysesArray = modelObj.getJSONArray("analyses");
                    
                    for (int j = 0; j < analysesArray.length(); j++) {
                        JSONObject analysisObj = analysesArray.getJSONObject(j);
                        String analysisUrl = analysisObj.getString("url");
                        
                        // For demo purposes, we'll do a partial match on URL
                        if (url.contains(extractDomain(analysisUrl))) {
                            // Found matching analysis, parse it
                            return parseAnalysisResult(analysisObj);
                        }
                    }
                    
                    // If no exact match, return the first analysis for this model
                    if (analysesArray.length() > 0) {
                        return parseAnalysisResult(analysesArray.getJSONObject(0));
                    }
                }
            }
            
            // If no matching model found, return null
            return null;
            
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Error parsing JSON data", e);
            return null;
        }
    }
    
    private static String extractDomain(String url) {
        // Simple domain extraction for demo purposes
        if (url.contains("facebook")) {
            return "facebook";
        } else if (url.contains("tiktok")) {
            return "tiktok";
        }
        return url;
    }
    
    private static SentimentAnalysisResult parseAnalysisResult(JSONObject analysisObj) throws JSONException {
        String url = analysisObj.getString("url");
        String sentiment = analysisObj.getString("sentiment");
        double score = analysisObj.getDouble("score");
        
        // Parse metrics
        JSONObject metricsObj = analysisObj.getJSONObject("metrics");
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("accuracy", metricsObj.getDouble("accuracy"));
        metrics.put("precision", metricsObj.getDouble("precision"));
        metrics.put("recall", metricsObj.getDouble("recall"));
        metrics.put("f1_score", metricsObj.getDouble("f1_score"));
        
        // Parse top words
        JSONArray topWordsArray = analysisObj.getJSONArray("top_words");
        List<SentimentAnalysisResult.WordCount> topWords = new ArrayList<>();
        
        for (int i = 0; i < topWordsArray.length(); i++) {
            JSONObject wordObj = topWordsArray.getJSONObject(i);
            String word = wordObj.getString("word");
            int count = wordObj.getInt("count");
            topWords.add(new SentimentAnalysisResult.WordCount(word, count));
        }
        
        // Parse sentiment distribution
        JSONArray distributionArray = analysisObj.getJSONArray("sentiment_distribution");
        List<SentimentAnalysisResult.SentimentDistribution> distribution = new ArrayList<>();
        
        for (int i = 0; i < distributionArray.length(); i++) {
            JSONObject distObj = distributionArray.getJSONObject(i);
            String label = distObj.getString("label");
            int value = distObj.getInt("value");
            distribution.add(new SentimentAnalysisResult.SentimentDistribution(label, value));
        }
        
        return new SentimentAnalysisResult(url, sentiment, score, metrics, topWords, distribution);
    }
    
    private static String loadJSONFromRaw(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.sentiment_analysis_data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        reader.close();
        return sb.toString();
    }
}
