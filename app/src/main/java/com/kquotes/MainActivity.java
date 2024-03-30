package com.kquotes;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.kquotes.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements QuoteFetcher.QuoteListener {

    ActivityMainBinding binding;
    List<String> allQuotes = new ArrayList<>();
    int currentQuoteIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        QuoteFetcher quoteFetcher = new QuoteFetcher(this);
        quoteFetcher.execute();

        binding.loadingProgress.setVisibility(View.VISIBLE);

        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextQuote();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousQuote();
            }
        });

        binding.generateNewQuotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fetch new quotes when the button is clicked
                fetchNewQuotes();
            }
        });

    }

    private void showNextQuote() {
        if (!allQuotes.isEmpty()) {
            currentQuoteIndex = (currentQuoteIndex + 1) % allQuotes.size();
            updateCurrentQuote();
        }
    }

    private void showPreviousQuote() {
        if (!allQuotes.isEmpty()) {
            currentQuoteIndex = (currentQuoteIndex - 1 + allQuotes.size()) % allQuotes.size();
            updateCurrentQuote();
        }
    }

    private void updateCurrentQuote() {
        if (currentQuoteIndex >= 0 && currentQuoteIndex < allQuotes.size()) {
            String currentQuote = allQuotes.get(currentQuoteIndex);
            binding.quoteTextView.setText(currentQuote);
        }
    }

    private void fetchNewQuotes() {
        QuoteFetcher quoteFetcher = new QuoteFetcher(this);
        quoteFetcher.execute();
        binding.loadingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onQuotesReceived(List<String> quotes) {
        binding.loadingProgress.setVisibility(View.GONE);
        if (!quotes.isEmpty()) {
            allQuotes.clear();
            allQuotes.addAll(quotes);
            currentQuoteIndex = 0;
            updateCurrentQuote();
        }
    }

    @Override
    public void onError(String errorMessage) {
        binding.loadingProgress.setVisibility(View.GONE);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}