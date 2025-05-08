package com.example.likes2love.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.likes2love.models.SentimentAnalysisResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clasa pentru parsarea datelor de la distanță (JSON și XML)
 * Implementare simplificată fără Volley sau Retrofit
 */
public class RemoteDataParser {
    private static final String TAG = "RemoteDataParser";

    /**
     * Interfață de callback pentru rezultatele parsării
     */
    public interface ParseCallback {
        void onSuccess(SentimentAnalysisResult result);
        void onError(String errorMessage);
    }

    /**
     * Parsează date JSON de la un URL
     */
    public static void parseJsonFromUrl(Context context, String url, final ParseCallback callback) {
        new JsonParseTask(callback).execute(url);
    }

    /**
     * Parsează date XML de la un URL
     */
    public static void parseXmlFromUrl(Context context, String url, final ParseCallback callback) {
        new XmlParseTask(callback).execute(url);
    }

    /**
     * AsyncTask pentru parsarea JSON
     */
    private static class JsonParseTask extends AsyncTask<String, Void, Result> {
        private ParseCallback callback;

        public JsonParseTask(ParseCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Result doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Conectare la URL
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.connect();

                // Verifică codul de răspuns
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return new Result(false, null, "Server returned code: " + responseCode);
                }

                // Citește răspunsul
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Parsează JSON
                SentimentAnalysisResult result = parseJsonResponse(response.toString());
                return new Result(true, result, null);

            } catch (IOException e) {
                Log.e(TAG, "Error in network request", e);
                return new Result(false, null, "Network error: " + e.getMessage());
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON", e);
                return new Result(false, null, "JSON parsing error: " + e.getMessage());
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
                callback.onSuccess(result.data);
            } else {
                callback.onError(result.errorMessage);
            }
        }
    }

    /**
     * AsyncTask pentru parsarea XML
     */
    private static class XmlParseTask extends AsyncTask<String, Void, Result> {
        private ParseCallback callback;

        public XmlParseTask(ParseCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Result doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Conectare la URL
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.connect();

                // Verifică codul de răspuns
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return new Result(false, null, "Server returned code: " + responseCode);
                }

                // Citește răspunsul
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // Parsează XML
                SentimentAnalysisResult result = parseXmlResponse(response.toString());
                return new Result(true, result, null);

            } catch (IOException e) {
                Log.e(TAG, "Error in network request", e);
                return new Result(false, null, "Network error: " + e.getMessage());
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Error parsing XML", e);
                return new Result(false, null, "XML parsing error: " + e.getMessage());
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
                callback.onSuccess(result.data);
            } else {
                callback.onError(result.errorMessage);
            }
        }
    }

    /**
     * Parsează un răspuns JSON în obiectul SentimentAnalysisResult
     */
    private static SentimentAnalysisResult parseJsonResponse(String jsonResponse) throws JSONException {
        JSONObject json = new JSONObject(jsonResponse);

        SentimentAnalysisResult result = new SentimentAnalysisResult();
        result.setUrl(json.getString("url"));
        result.setSentiment(json.getString("sentiment"));
        result.setScore(json.getDouble("score"));

        // Parsează metricile
        JSONObject metricsJson = json.getJSONObject("metrics");
        Map<String, Double> metrics = new HashMap<>();
        JSONArray metricsNames = metricsJson.names();
        if (metricsNames != null) {
            for (int i = 0; i < metricsNames.length(); i++) {
                String key = metricsNames.getString(i);
                metrics.put(key, metricsJson.getDouble(key));
            }
        }
        result.setMetrics(metrics);

        // Parsează cuvintele frecvente
        JSONArray topWordsJson = json.getJSONArray("top_words");
        List<SentimentAnalysisResult.WordCount> topWords = new ArrayList<>();
        for (int i = 0; i < topWordsJson.length(); i++) {
            JSONObject wordJson = topWordsJson.getJSONObject(i);
            topWords.add(new SentimentAnalysisResult.WordCount(
                    wordJson.getString("word"),
                    wordJson.getInt("count")
            ));
        }
        result.setTopWords(topWords);

        // Parsează distribuția sentimentelor
        JSONArray distributionJson = json.getJSONArray("sentiment_distribution");
        List<SentimentAnalysisResult.SentimentDistribution> distribution = new ArrayList<>();
        for (int i = 0; i < distributionJson.length(); i++) {
            JSONObject itemJson = distributionJson.getJSONObject(i);
            distribution.add(new SentimentAnalysisResult.SentimentDistribution(
                    itemJson.getString("label"),
                    itemJson.getInt("value")
            ));
        }
        result.setSentimentDistribution(distribution);

        return result;
    }

    /**
     * Parsează un răspuns XML în obiectul SentimentAnalysisResult
     */
    private static SentimentAnalysisResult parseXmlResponse(String xmlResponse) throws XmlPullParserException, IOException {
        SentimentAnalysisResult result = new SentimentAnalysisResult();
        Map<String, Double> metrics = new HashMap<>();
        List<SentimentAnalysisResult.WordCount> topWords = new ArrayList<>();
        List<SentimentAnalysisResult.SentimentDistribution> distribution = new ArrayList<>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xmlResponse));

        int eventType = parser.getEventType();
        String currentTag = null;
        String currentWord = null;
        String currentLabel = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                currentTag = parser.getName();

                if ("url".equals(currentTag)) {
                    result.setUrl(parser.nextText());
                } else if ("sentiment".equals(currentTag)) {
                    result.setSentiment(parser.nextText());
                } else if ("score".equals(currentTag)) {
                    result.setScore(Double.parseDouble(parser.nextText()));
                } else if ("metric".equals(currentTag)) {
                    String name = parser.getAttributeValue(null, "name");
                    String value = parser.nextText();
                    metrics.put(name, Double.parseDouble(value));
                } else if ("word".equals(currentTag)) {
                    currentWord = parser.getAttributeValue(null, "text");
                } else if ("count".equals(currentTag) && currentWord != null) {
                    int count = Integer.parseInt(parser.nextText());
                    topWords.add(new SentimentAnalysisResult.WordCount(currentWord, count));
                    currentWord = null;
                } else if ("distribution".equals(currentTag)) {
                    currentLabel = parser.getAttributeValue(null, "label");
                } else if ("value".equals(currentTag) && currentLabel != null) {
                    int value = Integer.parseInt(parser.nextText());
                    distribution.add(new SentimentAnalysisResult.SentimentDistribution(currentLabel, value));
                    currentLabel = null;
                }
            }
            eventType = parser.next();
        }

        result.setMetrics(metrics);
        result.setTopWords(topWords);
        result.setSentimentDistribution(distribution);

        return result;
    }

    /**
     * Clasă internă pentru a reprezenta rezultatul unei operațiuni de parsare
     */
    private static class Result {
        boolean isSuccess;
        SentimentAnalysisResult data;
        String errorMessage;

        Result(boolean isSuccess, SentimentAnalysisResult data, String errorMessage) {
            this.isSuccess = isSuccess;
            this.data = data;
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Simulează un răspuns JSON pentru testare
     */
    public static String getMockJsonResponse() {
        return "{\n" +
                "  \"url\": \"https://example.com/sample\",\n" +
                "  \"sentiment\": \"Positive\",\n" +
                "  \"score\": 0.85,\n" +
                "  \"metrics\": {\n" +
                "    \"Confidence\": 0.92,\n" +
                "    \"Relevance\": 0.78,\n" +
                "    \"Accuracy\": 0.85,\n" +
                "    \"Precision\": 0.81\n" +
                "  },\n" +
                "  \"top_words\": [\n" +
                "    { \"word\": \"excellent\", \"count\": 12 },\n" +
                "    { \"word\": \"good\", \"count\": 10 },\n" +
                "    { \"word\": \"service\", \"count\": 8 },\n" +
                "    { \"word\": \"product\", \"count\": 7 },\n" +
                "    { \"word\": \"recommend\", \"count\": 5 }\n" +
                "  ],\n" +
                "  \"sentiment_distribution\": [\n" +
                "    { \"label\": \"Very Positive\", \"value\": 35 },\n" +
                "    { \"label\": \"Positive\", \"value\": 25 },\n" +
                "    { \"label\": \"Neutral\", \"value\": 20 },\n" +
                "    { \"label\": \"Negative\", \"value\": 15 },\n" +
                "    { \"label\": \"Very Negative\", \"value\": 5 }\n" +
                "  ]\n" +
                "}";
    }

    /**
     * Simulează un răspuns XML pentru testare
     */
    public static String getMockXmlResponse() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<analysis>\n" +
                "  <url>https://example.com/sample</url>\n" +
                "  <sentiment>Positive</sentiment>\n" +
                "  <score>0.85</score>\n" +
                "  <metrics>\n" +
                "    <metric name=\"Confidence\">0.92</metric>\n" +
                "    <metric name=\"Relevance\">0.78</metric>\n" +
                "    <metric name=\"Accuracy\">0.85</metric>\n" +
                "    <metric name=\"Precision\">0.81</metric>\n" +
                "  </metrics>\n" +
                "  <top_words>\n" +
                "    <word text=\"excellent\"><count>12</count></word>\n" +
                "    <word text=\"good\"><count>10</count></word>\n" +
                "    <word text=\"service\"><count>8</count></word>\n" +
                "    <word text=\"product\"><count>7</count></word>\n" +
                "    <word text=\"recommend\"><count>5</count></word>\n" +
                "  </top_words>\n" +
                "  <sentiment_distribution>\n" +
                "    <distribution label=\"Very Positive\"><value>35</value></distribution>\n" +
                "    <distribution label=\"Positive\"><value>25</value></distribution>\n" +
                "    <distribution label=\"Neutral\"><value>20</value></distribution>\n" +
                "    <distribution label=\"Negative\"><value>15</value></distribution>\n" +
                "    <distribution label=\"Very Negative\"><value>5</value></distribution>\n" +
                "  </sentiment_distribution>\n" +
                "</analysis>";
    }
}

