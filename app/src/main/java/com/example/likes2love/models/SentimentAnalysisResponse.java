package com.example.likes2love.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model pentru răspunsul analizei de sentiment
 * Implementare simplificată fără dependențe de Gson
 */
public class SentimentAnalysisResponse implements Serializable {
    private String url;
    private String sentiment;
    private double score;
    private Map<String, Double> metrics;
    private List<WordCount> topWords;
    private List<SentimentDistribution> sentimentDistribution;

    /**
     * Constructor gol
     */
    public SentimentAnalysisResponse() {
        this.metrics = new HashMap<>();
        this.topWords = new ArrayList<>();
        this.sentimentDistribution = new ArrayList<>();
    }

    /**
     * Clasă pentru cuvinte frecvente
     */
    public static class WordCount implements Serializable {
        private String word;
        private int count;

        public WordCount(String word, int count) {
            this.word = word;
            this.count = count;
        }

        public String getWord() {
            return word;
        }

        public int getCount() {
            return count;
        }
    }

    /**
     * Clasă pentru distribuția sentimentelor
     */
    public static class SentimentDistribution implements Serializable {
        private String label;
        private int value;

        public SentimentDistribution(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Convertește răspunsul API în modelul nostru SentimentAnalysisResult
     */
    public SentimentAnalysisResult toSentimentAnalysisResult() {
        SentimentAnalysisResult result = new SentimentAnalysisResult();
        result.setUrl(this.url);
        result.setSentiment(this.sentiment);
        result.setScore(this.score);

        // Convertește metricile
        result.setMetrics(new HashMap<>(this.metrics));

        // Convertește cuvintele frecvente
        List<SentimentAnalysisResult.WordCount> resultTopWords = new ArrayList<>();
        for (WordCount wc : this.topWords) {
            resultTopWords.add(new SentimentAnalysisResult.WordCount(wc.getWord(), wc.getCount()));
        }
        result.setTopWords(resultTopWords);

        // Convertește distribuția sentimentelor
        List<SentimentAnalysisResult.SentimentDistribution> resultDistribution = new ArrayList<>();
        for (SentimentDistribution sd : this.sentimentDistribution) {
            resultDistribution.add(new SentimentAnalysisResult.SentimentDistribution(sd.getLabel(), sd.getValue()));
        }
        result.setSentimentDistribution(resultDistribution);

        return result;
    }

    // Getters și Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Double> metrics) {
        this.metrics = metrics;
    }

    public List<WordCount> getTopWords() {
        return topWords;
    }

    public void setTopWords(List<WordCount> topWords) {
        this.topWords = topWords;
    }

    public List<SentimentDistribution> getSentimentDistribution() {
        return sentimentDistribution;
    }

    public void setSentimentDistribution(List<SentimentDistribution> sentimentDistribution) {
        this.sentimentDistribution = sentimentDistribution;
    }
}
