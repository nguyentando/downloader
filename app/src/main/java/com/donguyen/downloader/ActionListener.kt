package com.donguyen.downloader

import com.tonyodev.fetch2.Download

/**
 * Created by DoNguyen on 14/9/20.
 */
interface ActionListener {
    fun onView(download: Download)
    fun onPause(id: Int)
    fun onResume(id: Int)
    fun onRemove(id: Int)
    fun onRetry(id: Int)
}