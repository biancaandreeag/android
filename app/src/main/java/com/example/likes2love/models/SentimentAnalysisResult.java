package com.example.likes2love.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model pentru rezultatul analizei de sentiment
 */
public class SentimentAnalysisResult implements Serializable {
    private String url;
    private String sentiment;
    private double score;
    private Map<String, Double> metrics;
    private List<WordCount> topWords;
    private List<SentimentDistribution> sentimentDistribution;

    /**
     * Constructor gol
     */
    public SentimentAnalysisResult() {
        this.metrics = new HashMap<>();
        this.topWords = new ArrayList<>();
        this.sentimentDistribution = new ArrayList<>();
    }

    /**
     * Constructor complet
     */
    public SentimentAnalysisResult(String url, String sentiment, double score,
                                   Map<String, Double> metrics,
                                   List<WordCount> topWords,
                                   List<SentimentDistribution> sentimentDistribution) {
        this.url = url;
        this.sentiment = sentiment;
        this.score = score;
        this.metrics = metrics != null ? metrics : new HashMap<>();
        this.topWords = topWords != null ? topWords : new ArrayList<>();
        this.sentimentDistribution = sentimentDistribution != null ? sentimentDistribution : new ArrayList<>();
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

        public void setWord(String word) {
            this.word = word;
        }

        public void setCount(int count) {
            this.count = count;
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

        public void setLabel(String label) {
            this.label = label;
        }

        public void setValue(int value) {
            this.value = value;
        }
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
