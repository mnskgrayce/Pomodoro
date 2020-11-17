package com.example.pomodoro.editquotes;

import android.os.Bundle;
import android.widget.EditText;

public interface QuotesEditContract {
    void getCurrentQuotes();
    void handleEmptyData();
    EditText makeTextEdit();
    Bundle packNewQuotes();
    void goBackFromQuotesEdit();
}
