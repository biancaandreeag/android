package com.example.likes2love.database;

import java.io.Serializable;

/**
 * Model pentru elementele din istoricul analizelor
 */
public class HistoryItem implements Serializable {
    private long id;
    private String url;
    private String model;
    private String sentiment;
    private double score;
    private String timestamp;

    /**
     * Constructor gol
     */
    public HistoryItem() {
    }

    /**
     * Constructor complet
     */
    public HistoryItem(long id, String url, String model, String sentiment, double score, String timestamp) {
        this.id = id;
        this.url = url;
        this.model = model;
        this.sentiment = sentiment;
        this.score = score;
        this.timestamp = timestamp;
    }

    // Getters și Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HistoryItem{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", model='" + model + '\'' +
                ", sentiment='" + sentiment + '\'' +
                ", score=" + score +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
