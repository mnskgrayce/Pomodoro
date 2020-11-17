package com.example.pomodoro.editquotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pomodoro.R;

import java.util.ArrayList;

public class QuotesRecyclerViewAdapter extends RecyclerView.Adapter<QuotesRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<String> data;
    private final LayoutInflater inflater;
    private Listener listener;

    // Data is passed into the constructor
    QuotesRecyclerViewAdapter(Context context, ArrayList<String> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_quote_row, parent, false);
        return new ViewHolder(view);
    }

    // Bind the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String quote = data.get(position);
        holder.quoteRowView.setText(quote);
    }

    // Total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }

    // Store and recycle views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView quoteRowView;
        private final ImageButton buttonEditQuote;
        private final ImageButton buttonDeleteQuote;

        ViewHolder(View itemView) {
            super(itemView);
            quoteRowView = itemView.findViewById(R.id.textQuoteRow);
            buttonEditQuote = itemView.findViewById(R.id.buttonEditQuote);
            buttonDeleteQuote = itemView.findViewById(R.id.buttonDeleteQuote);

            buttonEditQuote.setOnClickListener(this);
            buttonDeleteQuote.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                // If edit button clicked
                if (view.getId() == buttonEditQuote.getId()) {
                    listener.onEditQuoteClick(view, getAdapterPosition());
                }
                // If delete button clicked
                else if (view.getId() == buttonDeleteQuote.getId()) {
                    listener.onDeleteQuoteClick(view, getAdapterPosition());
                }
            }
        }
    }

    // Convenience method for getting data at click position
    String getItem(int id) {
        return data.get(id);
    }

    // Allows clicks events to be caught
    void setListener(Listener listener) {
        this.listener = listener;
    }

    // Parent activity will implement these methods to respond to click events
    public interface Listener {
        void onEditQuoteClick(View view, int position);
        void onDeleteQuoteClick(View view, int position);
        void onAddQuoteClick();
    }
}
