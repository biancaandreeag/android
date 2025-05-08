package com.example.likes2love.api;

import android.content.Context;

/**
 * Implementare simplificată a clasei Volley
 */
public class Volley {
    /**
     * Creează o nouă coadă de cereri
     */
    public static RequestQueue newRequestQueue(Context context) {
        return RequestQueue.getInstance();
    }
}
