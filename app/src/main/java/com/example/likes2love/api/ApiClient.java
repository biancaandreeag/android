package com.example.likes2love.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "https://api.example.com/"; // Înlocuiește cu URL-ul real al API-ului tău

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    /**
     * Execută o cerere GET către API
     */
    public static void get(String endpoint, final ApiCallback<String> callback) {
        new AsyncTask<String, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(String... params) {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(BASE_URL + params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(15000);

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

                        return new ApiResult(true, response.toString(), null);
                    } else {
                        // Eroare de la server
                        return new ApiResult(false, null, "Server error: " + responseCode);
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Error in API request", e);
                    return new ApiResult(false, null, "Network error: " + e.getMessage());
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
            protected void onPostExecute(ApiResult result) {
                if (result.isSuccess) {
                    callback.onSuccess(result.data);
                } else {
                    callback.onError(result.errorMessage);
                }
            }
        }.execute(endpoint);
    }

    /**
     * Execută o cerere POST către API
     */
    public static void post(String endpoint, JSONObject requestBody, final ApiCallback<String> callback) {
        new AsyncTask<Object, Void, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Object... params) {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    String endpointUrl = (String) params[0];
                    JSONObject body = (JSONObject) params[1];

                    URL url = new URL(BASE_URL + endpointUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(15000);

                    // Scrie body-ul cererii
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = body.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
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

                        return new ApiResult(true, response.toString(), null);
                    } else {
                        // Eroare de la server
                        return new ApiResult(false, null, "Server error: " + responseCode);
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Error in API request", e);
                    return new ApiResult(false, null, "Network error: " + e.getMessage());
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
            protected void onPostExecute(ApiResult result) {
                if (result.isSuccess) {
                    callback.onSuccess(result.data);
                } else {
                    callback.onError(result.errorMessage);
                }
            }
        }.execute(endpoint, requestBody);
    }

    /**
     * Metodă helper pentru a crea un JSONObject din parametri
     */
    public static JSONObject createJsonBody(Map<String, Object> params) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
        }
        return jsonObject;
    }

    /**
     * Clasă internă pentru a reprezenta rezultatul unei cereri API
     */
    private static class ApiResult {
        boolean isSuccess;
        String data;
        String errorMessage;

        ApiResult(boolean isSuccess, String data, String errorMessage) {
            this.isSuccess = isSuccess;
            this.data = data;
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Exemplu de utilizare:
     *
     * // Pentru GET
     * ApiClient.get("users/1", new ApiClient.ApiCallback<String>() {
     *     @Override
     *     public void onSuccess(String result) {
     *         // Procesează rezultatul
     *     }
     *
     *     @Override
     *     public void onError(String errorMessage) {
     *         // Gestionează eroarea
     *     }
     * });
     *
     * // Pentru POST
     * Map<String, Object> params = new HashMap<>();
     * params.put("name", "John");
     * params.put("age", 30);
     *
     * JSONObject requestBody = ApiClient.createJsonBody(params);
     *
     * ApiClient.post("users", requestBody, new ApiClient.ApiCallback<String>() {
     *     @Override
     *     public void onSuccess(String result) {
     *         // Procesează rezultatul
     *     }
     *
     *     @Override
     *     public void onError(String errorMessage) {
     *         // Gestionează eroarea
     *     }
     * });
     */
}
