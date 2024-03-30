package com.kquotes;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuoteFetcher extends AsyncTask<Void, Void, List<String>> {

    private static final String TAG = QuoteFetcher.class.getSimpleName();
    private static final String API_URL = "https://api.quotable.io/quotes/random?limit=10";

    // Define an interface for callbacks
    public interface QuoteListener {
        void onQuotesReceived(List<String> quotes);
        void onError(String errorMessage);
    }

    private final OkHttpClient client = new OkHttpClient();
    private final QuoteListener quoteListener;

    public QuoteFetcher(QuoteListener quoteListener) {
        this.quoteListener = quoteListener;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        try {
            // Create a request to the API URL
            Request request = new Request.Builder()
                    .url(API_URL)
                    .build();

            // Execute the request and get the response
            Response response = client.newCall(request).execute();

            // Check if the request was successful (HTTP code 200)
            if (response.isSuccessful()) {
                // Parse the JSON response and extract the "content" field from each quote
                String responseBody = response.body().string();
                return parseQuotes(responseBody);
            } else {
                // Handle the error case
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error making request", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<String> result) {
        super.onPostExecute(result);

        // Check if the result is not null and notify the listener
        if (result != null) {
            quoteListener.onQuotesReceived(result);
        } else {
            quoteListener.onError("Error fetching quotes, Check Internet Connection!");
        }
    }

    private List<String> parseQuotes(String responseBody) {
        List<String> quotes = new ArrayList<>();

        try {
            // Parse the JSON array
            JSONArray jsonArray = new JSONArray(responseBody);

            // Iterate through each quote and extract the "content" field
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject quoteObject = jsonArray.getJSONObject(i);
                String content = quoteObject.getString("content");
                quotes.add(content);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }

        return quotes;
    }
}
