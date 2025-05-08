package com.example.likes2love.api;

/**
 * Implementare simplificată a RequestQueue din Volley
 */
public class RequestQueue {
    private static RequestQueue instance;

    /**
     * Constructor privat pentru Singleton
     */
    private RequestQueue() {
    }

    /**
     * Obține instanța singleton
     */
    public static synchronized RequestQueue getInstance() {
        if (instance == null) {
            instance = new RequestQueue();
        }
        return instance;
    }

    /**
     * Adaugă o cerere la coadă
     */
    public void add(StringRequest request) {
        // Execută cererea direct
        request.execute();
    }
}
