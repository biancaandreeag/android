package com.example.likes2love.api;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementare simplificată a StringRequest din Volley
 * Folosește HttpURLConnection în loc de Volley
 */
public class StringRequest {
    private static final String TAG = "StringRequest";

    public static final int METHOD_GET = 0;
    public static final int METHOD_POST = 1;

    private final int method;
    private final String url;
    private final Response.Listener<String> listener;
    private final Response.ErrorListener errorListener;
    private final Map<String, String> headers;
    private final Map<String, String> params;

    /**
     * Constructor pentru cereri GET
     */
    public StringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this(METHOD_GET, url, listener, errorListener);
    }

    /**
     * Constructor pentru cereri cu metoda specificată
     */
    public StringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        this.method = method;
        this.url = url;
        this.listener = listener;
        this.errorListener = errorListener;
        this.headers = new HashMap<>();
        this.params = new HashMap<>();
    }

    /**
     * Adaugă un header la cerere
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * Adaugă un parametru la cerere
     */
    public void addParam(String name, String value) {
        params.put(name, value);
    }

    /**
     * Execută cererea
     */
    public void execute() {
        new RequestTask().execute();
    }

    /**
     * AsyncTask pentru executarea cererii HTTP
     */
    private class RequestTask extends AsyncTask<Void, Void, Result> {
        @Override
        protected Result doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Construiește URL-ul pentru cereri GET cu parametri
                String requestUrl = url;
                if (method == METHOD_GET && !params.isEmpty()) {
                    StringBuilder urlBuilder = new StringBuilder(url);
                    urlBuilder.append(url.contains("?") ? "&" : "?");

                    boolean first = true;
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        if (!first) {
                            urlBuilder.append("&");
                        }
                        urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                        first = false;
                    }

                    requestUrl = urlBuilder.toString();
                }

                // Deschide conexiunea
                URL urlObj = new URL(requestUrl);
                connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod(method == METHOD_GET ? "GET" : "POST");

                // Adaugă headers
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }

                // Configurează conexiunea
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                // Pentru cereri POST, adaugă parametrii în body
                if (method == METHOD_POST && !params.isEmpty()) {
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    StringBuilder bodyBuilder = new StringBuilder();
                    boolean first = true;
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        if (!first) {
                            bodyBuilder.append("&");
                        }
                        bodyBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                        first = false;
                    }

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = bodyBuilder.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                }

                // Verifică codul de răspuns
                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    // Citește răspunsul
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    return new Result(true, response.toString(), null);
                } else {
                    // Eroare de la server
                    return new Result(false, null, "Server error: " + responseCode);
                }

            } catch (IOException e) {
                Log.e(TAG, "Error in network request", e);
                return new Result(false, null, "Network error: " + e.getMessage());
            } finally {
                // Închide conexiunea și reader-ul
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Error closing reader", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            if (result.isSuccess) {
                listener.onResponse(result.data);
            } else {
                errorListener.onErrorResponse(new VolleyError(result.errorMessage));
            }
        }
    }

    /**
     * Clasă internă pentru a reprezenta rezultatul unei cereri
     */
    private static class Result {
        boolean isSuccess;
        String data;
        String errorMessage;

        Result(boolean isSuccess, String data, String errorMessage) {
            this.isSuccess = isSuccess;
            this.data = data;
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Clasă pentru erori Volley
     */
    public static class VolleyError extends Exception {
        public VolleyError(String message) {
            super(message);
        }
    }

    /**
     * Interfețe pentru callback-uri
     */
    public static class Response {
        public interface Listener<T> {
            void onResponse(T response);
        }

        public interface ErrorListener {
            void onErrorResponse(VolleyError error);
        }
    }
}
