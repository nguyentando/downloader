package com.donguyen.downloader

import android.util.Patterns

/**
 * Created by DoNguyen on 14/9/20.
 */
object Utils {
    fun isValidURL(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches();
    }
}