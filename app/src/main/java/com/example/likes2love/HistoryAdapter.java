package com.example.likes2love;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> historyItems;
    private OnItemClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(HistoryItem item);
    }

    public HistoryAdapter(List<HistoryItem> historyItems, OnItemClickListener listener) {
        this.historyItems = historyItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem item = historyItems.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvModel.setText(item.getModel());
        holder.tvDate.setText(dateFormat.format(item.getDate()));
        holder.tvSentiment.setText(item.getSentiment());

        // Set sentiment color based on sentiment value
        int color;
        switch (item.getSentiment().toLowerCase()) {
            case "positive":
            case "very positive":
                color = holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark);
                break;
            case "negative":
            case "very negative":
                color = holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark);
                break;
            case "neutral":
                color = holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark);
                break;
            default:
                color = holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray);
                break;
        }
        holder.tvSentiment.setTextColor(color);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvModel;
        TextView tvDate;
        TextView tvSentiment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvModel = itemView.findViewById(R.id.tvModel);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvSentiment = itemView.findViewById(R.id.tvSentiment);
        }
    }
}
