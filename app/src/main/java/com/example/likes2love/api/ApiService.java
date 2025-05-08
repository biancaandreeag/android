package com.example.likes2love.api;

import com.example.likes2love.models.SentimentAnalysisResult;

/**
 * Interfață pentru serviciul API de analiză de sentiment
 * Implementare simplificată fără Retrofit
 */
public interface ApiService {
    /**
     * Interfață de callback pentru rezultatele analizei de sentiment
     */
    interface SentimentAnalysisCallback {
        void onSuccess(SentimentAnalysisResult result);
        void onError(String errorMessage);
    }

    /**
     * Obține analiza de sentiment pentru un URL dat
     *
     * @param model Modelul de analiză de folosit
     * @param url URL-ul de analizat
     * @param callback Callback pentru rezultat
     */
    void getSentimentAnalysis(String model, String url, SentimentAnalysisCallback callback);
}
