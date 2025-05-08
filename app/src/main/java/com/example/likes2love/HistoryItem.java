package com.example.likes2love;

import java.io.Serializable;
import java.util.Date;

/**
 * Model pentru elementele din istoricul analizelor
 */
public class HistoryItem implements Serializable {
    private long id;
    private String title;
    private String model;
    private Date date;
    private String url;
    private String sentiment;
    private double score;

    /**
     * Constructor gol
     */
    public HistoryItem() {
    }

    /**
     * Constructor complet
     */
    public HistoryItem(long id, String title, String model, Date date, String url, String sentiment, double score) {
        this.id = id;
        this.title = title;
        this.model = model;
        this.date = date;
        this.url = url;
        this.sentiment = sentiment;
        this.score = score;
    }

    // Getters și Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

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

    @Override
    public String toString() {
        return "HistoryItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", model='" + model + '\'' +
                ", date=" + date +
                ", url='" + url + '\'' +
                ", sentiment='" + sentiment + '\'' +
                ", score=" + score +
                '}';
    }
}
