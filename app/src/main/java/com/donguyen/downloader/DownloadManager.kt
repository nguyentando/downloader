package com.donguyen.downloader

import android.content.Context
import android.net.Uri
import android.os.Environment
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

    fun createDownloadFunction(
        url: String,
        success: ((Request?) -> Unit)? = null,
        fail: ((Error?) -> Unit)? = null
    ) {
        val filePath = getFilePath(url)
        val downloadRequest = Request(url, filePath)

        fetch.enqueue(downloadRequest,
            { request: Request? ->
                success?.invoke(request)
            },
            { error: Error? ->
                fail?.invoke(error)
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