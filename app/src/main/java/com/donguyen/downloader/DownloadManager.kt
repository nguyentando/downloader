package com.donguyen.downloader

import android.content.Context
import android.os.Environment
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.Downloader
import com.tonyodev.fetch2okhttp.OkHttpDownloader

/**
 * Created by DoNguyen on 14/9/20.
 */
class DownloadManager(context: Context) {

    private lateinit var fetch: Fetch

    init {
        val fetchConfiguration = FetchConfiguration.Builder(context.applicationContext)
            .setDownloadConcurrentLimit(3)
            .enableRetryOnNetworkGain(true)
            .setHttpDownloader(OkHttpDownloader(Downloader.FileDownloaderType.PARALLEL))
            .setNotificationManager(object : DefaultFetchNotificationManager(context) {
                override fun getFetchInstanceForNamespace(namespace: String): Fetch {
                    return fetch
                }
            })
            .build()

        fetch = Fetch.getInstance(fetchConfiguration)
    }

    fun createDownloadRequest(
        url: String,
        success: ((Request?) -> Unit)? = null,
        fail: ((Error?) -> Unit)? = null
    ) {
        val filePath = getFilePath(url)
        val downloadRequest = Request(url, filePath).apply {
            priority = Priority.HIGH
        }

        fetch.enqueue(downloadRequest,
            { request: Request? ->
                success?.invoke(request)
            },
            { error: Error? ->
                fail?.invoke(error)
            })
    }

    fun getAllDownloads(callback: (List<Download>) -> Unit) {
        fetch.getDownloads(callback)
    }

    fun getDownloadsByRequestId(requestId: Long, callback: (List<Download>) -> Unit) {
        fetch.getDownloadsByRequestIdentifier(requestId, callback)
    }

    fun pause(id: Int) {
        fetch.pause(id)
    }

    fun resume(id: Int) {
        fetch.resume(id)
    }

    fun remove(id: Int) {
        fetch.remove(id)
    }

    fun retry(id: Int) {
        fetch.retry(id)
    }

    fun addListener(listener: FetchListener) {
        fetch.addListener(listener)
    }

    fun removeListener(listener: FetchListener) {
        fetch.removeListener(listener)
    }

    fun release() {
        fetch.close()
    }

    private fun getFilePath(url: String): String {
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/Downloader"

        val fileName = Utils.getFileName(url)

        return "$directory/$fileName"
    }
}