package br.com.targettrust.exercicio10;

/**
 * Created by instrutor on 22/10/2015.
 */

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.targettrust.exercicio10.model.Filme;

public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "DownloadService";

    public DownloadService() {
        super(DownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Service Started!");

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");

        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(url)) {
            /* Update UI: Download Service is Running */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {
                ArrayList<Filme> results = downloadData(url);

                /* Sending result back to activity */
                if (null != results && results.size() > 0) {
                    //bundle.putStringArray("results", results);
                    Log.v("Service","Size: " + results.size());
                    bundle.putSerializable("results", results);
                    receiver.send(STATUS_FINISHED, bundle);
                }
            } catch (Exception e) {

                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    private ArrayList<Filme> downloadData(String requestUrl) throws IOException, DownloadException {
        InputStream inputStream = null;

        HttpURLConnection urlConnection = null;

        /* forming th java.net.URL object */
        URL url = new URL(requestUrl);

        urlConnection = (HttpURLConnection) url.openConnection();

        /* optional request header */
        urlConnection.setRequestProperty("Content-Type", "application/json");

        /* optional request header */
        urlConnection.setRequestProperty("Accept", "application/json");

        /* for Get request */
        urlConnection.setRequestMethod("GET");

        int statusCode = urlConnection.getResponseCode();

        /* 200 represents HTTP OK */
        if (statusCode == 200) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());

            String response = convertInputStreamToString(inputStream);

            ArrayList<Filme> filmes = parseResult(response);

            return filmes;
        } else {
            throw new DownloadException("Failed to fetch data!!");
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

            /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }

        return result;
    }

    private ArrayList<Filme> parseResult(String result) {

        ArrayList<Filme> filmes = new ArrayList<Filme>();
        try {
            JSONObject response = new JSONObject(result);

            JSONArray posts = response.optJSONArray("results");

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                String title = post.optString("title");
                Filme film = new Filme(post.optString("id"),
                        post.optString("title"),
                        "https://image.tmdb.org/t/p/w370" + post.optString("poster_path"));
                filmes.add(film);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return filmes;
    }

    public class DownloadException extends Exception {

        public DownloadException(String message) {
            super(message);
        }

        public DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}