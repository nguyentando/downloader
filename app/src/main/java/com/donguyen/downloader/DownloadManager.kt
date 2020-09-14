package com.donguyen.downloader

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.tonyodev.fetch2.*

/**
 * Created by DoNguyen on 14/9/20.
 */
class DownloadManager(context: Context) {

    private lateinit var fetch: Fetch

    init {
        val fetchConfiguration = FetchConfiguration.Builder(context.applicationContext)
            .setDownloadConcurrentLimit(3)
            .setNotificationManager(object : DefaultFetchNotificationManager(context) {
                override fun getFetchInstanceForNamespace(namespace: String): Fetch {
                    return fetch
                }
            })
            .build()

        fetch = Fetch.getInstance(fetchConfiguration)
    }

    fun createDownloadFunction(url: String) {
        val filePath = getFilePath(url)
        val request = Request(url, filePath)

        fetch.enqueue(request,
            { updatedRequest: Request? ->
                run {
                    Log.d("DownloadManager", "success")
                }
            }, { error: Error? ->
                run {
                    Log.d("DownloadManager", error?.name.toString())
                }
            })
    }

    private fun getFilePath(url: String): String {
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/Downloader"

        val uri = Uri.parse(url)
        val fileName = uri.lastPathSegment

        return "$directory/$fileName"
    }
}