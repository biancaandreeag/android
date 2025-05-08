package com.example.likes2love.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.likes2love.R;
import com.example.likes2love.models.SentimentAnalysisResult;

import java.util.List;

public class AnalysisAdapter extends ArrayAdapter<SentimentAnalysisResult> {
    
    private final Context context;
    private final List<SentimentAnalysisResult> analysisList;
    private final OnAnalysisItemClickListener listener;
    
    public interface OnAnalysisItemClickListener {
        void onViewDetailsClick(SentimentAnalysisResult analysis);
    }
    
    public AnalysisAdapter(Context context, List<SentimentAnalysisResult> analysisList, OnAnalysisItemClickListener listener) {
        super(context, 0, analysisList);
        this.context = context;
        this.analysisList = analysisList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        
        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item_analysis, parent, false);
        }
        
        SentimentAnalysisResult currentAnalysis = analysisList.get(position);
        
        TextView tvTitle = listItem.findViewById(R.id.tvAnalysisTitle);
        TextView tvUrl = listItem.findViewById(R.id.tvAnalysisUrl);
        TextView tvSentiment = listItem.findViewById(R.id.tvAnalysisSentiment);
        TextView tvScore = listItem.findViewById(R.id.tvAnalysisScore);
        Button btnViewDetails = listItem.findViewById(R.id.btnViewDetails);
        
        // Set the title based on the URL
        String title = currentAnalysis.getUrl().contains("facebook") ? 
                "Facebook Analysis" : "TikTok Analysis";
        tvTitle.setText(title);
        
        tvUrl.setText("URL: " + currentAnalysis.getUrl());
        tvSentiment.setText("Sentiment: " + currentAnalysis.getSentiment());
        tvScore.setText("Score: " + String.format("%.2f", currentAnalysis.getScore()));
        
        btnViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetailsClick(currentAnalysis);
            }
        });
        
        return listItem;
    }
}
