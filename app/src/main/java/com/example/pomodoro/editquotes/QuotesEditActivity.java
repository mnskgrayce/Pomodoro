package com.example.pomodoro.editquotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pomodoro.MainActivity;
import com.example.pomodoro.R;

import java.util.ArrayList;

public class QuotesEditActivity extends AppCompatActivity implements QuotesRecyclerViewAdapter.Listener, QuotesEditContract {

    private QuotesRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private ArrayList<String> quotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes_edit);

        // Get data to populate recycler view
        getCurrentQuotes();

        // Set up recycler view
        recyclerView = findViewById(R.id.quotesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuotesRecyclerViewAdapter(this, quotes);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        // Set up other view components
        emptyView = findViewById(R.id.emptyView);
        ImageButton buttonAddQuote = findViewById(R.id.buttonAddQuote);
        buttonAddQuote.setOnClickListener(v -> onAddQuoteClick());

        ImageButton buttonBackFromQuotes = findViewById(R.id.buttonBackFromQuotes);
        buttonBackFromQuotes.setOnClickListener(v -> goBackFromQuotesEdit());

        // Call helper
        handleEmptyData();
    }

    @Override
    public void getCurrentQuotes() {
        Intent intent = getIntent();
        quotes = intent.getExtras().getStringArrayList("current_quotes");
    }

    // In case list is empty
    @Override
    public void handleEmptyData() {
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    // Dynamically set a TextEdit
    @Override
    public EditText makeTextEdit() {
        EditText input = new EditText(QuotesEditActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        return input;
    }

    @Override
    public void onAddQuoteClick() {
        // Initiate edit text dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(QuotesEditActivity.this);
        builder.setTitle(R.string.title_quote_editor);
        builder.setMessage("Write your own quote:");
        EditText input = makeTextEdit();
        builder.setView(input);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            // User clicked OK button
            String newQuote = input.getText().toString();
            // Change item in internal data
            quotes.add(newQuote);
            // Tell adapter (quite costly but safe)
            adapter.notifyDataSetChanged();
            // Call helper
            handleEmptyData();
            // Tell user
            Toast.makeText(getApplicationContext(), "Quote added!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // User cancelled the dialog
            dialog.cancel();
            Toast.makeText(getApplicationContext(), "You cancelled!", Toast.LENGTH_SHORT).show();
        });

        // Show the dialog
        builder.show();
    }

    @Override
    public void onEditQuoteClick(View view, int position) {
        String quote = adapter.getItem(position);

        // Initiate edit text dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(QuotesEditActivity.this);
        builder.setTitle(R.string.title_quote_editor);
        builder.setMessage("Change this how you like:");
        EditText input = makeTextEdit();
        input.setText(quote);
        builder.setView(input);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            // User clicked OK button
            String newQuote = input.getText().toString();
            // Change item in internal data
            quotes.set(position, newQuote);
            // Tell adapter
            adapter.notifyItemChanged(position);
            // Tell user
            Toast.makeText(getApplicationContext(), "Quote edited!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // User cancelled the dialog
            dialog.cancel();
            Toast.makeText(getApplicationContext(), "You cancelled!", Toast.LENGTH_SHORT).show();
        });

        // Show the dialog
        builder.show();
    }

    @Override
    public void onDeleteQuoteClick(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuotesEditActivity.this);
        builder.setMessage("Delete this quote?")
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    // Delete the quote from internal data
                    quotes.remove(position);
                    // Tell adapter (costly version)
                    adapter.notifyDataSetChanged();
                    // Call helper
                    handleEmptyData();
                    // Tell user
                    Toast.makeText(getApplicationContext(), "Quote deleted!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                    dialog.cancel();
                    Toast.makeText(getApplicationContext(), "You cancelled!", Toast.LENGTH_SHORT).show();
                });

        // Show the dialog
        builder.show();
    }

    @Override
    public Bundle packNewQuotes() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("new_quotes", quotes);
        return bundle;
    }

    @Override
    public void goBackFromQuotesEdit() {
        Intent intent = new Intent(QuotesEditActivity.this, MainActivity.class);
        intent.putExtras(packNewQuotes());
        setResult(RESULT_OK, intent);
        finish();
    }

    // Handle device back button
    @Override
    public void onBackPressed() {
        goBackFromQuotesEdit();
    }
}

