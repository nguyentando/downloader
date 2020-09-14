package com.donguyen.downloader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.tonyodev.fetch2.AbstractFetchListener
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_create_download_dialog.view.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

    companion object {
        private const val STORAGE_PERMISSION_CODE = 200
    }

    private lateinit var downloadManager: DownloadManager

    private lateinit var downloadAdapter: DownloadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloadManager = DownloadManager(this)

        create_download_fab.setOnClickListener {
            checkStoragePermissions()
        }

        downloadAdapter = DownloadAdapter(actionListener)
        download_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = downloadAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        downloadManager.addListener(fetchListener)
        downloadManager.getAllDownloads { downloads ->
            downloads.forEach {
                downloadAdapter.addOrUpdateDownload(it)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        downloadManager.removeListener(fetchListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadManager.release()
    }

    private fun checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        } else {
            showCreateDownloadDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE
            && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            showCreateDownloadDialog()
        } else {
            showSnackbar(R.string.permission_not_enabled)
        }
    }

    private fun showCreateDownloadDialog() {
        val view = layoutInflater.inflate(R.layout.layout_create_download_dialog, null)
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.create_download))
            .setView(view)
            .setPositiveButton(resources.getString(R.string.download)) { _, _ ->
                val url = view.input_url.text.toString()
                createDownload(url)
            }
            .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                // No need to do anything.
            }
            .show()
    }

    private fun createDownload(url: String) {
        // Validate the URL
        if (!Utils.isValidURL(url)) {
            showSnackbar(R.string.invalid_url)
            return
        }

        // The URL is valid now, start the download
        downloadManager.createDownloadRequest(
            url,
            success = {
                showSnackbar(R.string.create_download_success)
                download_list.smoothScrollToPosition(0)
            },
            fail = {
                showSnackbar(R.string.create_download_fail)
            })
    }

    private fun showSnackbar(@StringRes resId: Int) {
        Snackbar.make(main_layout, resId, Snackbar.LENGTH_LONG).show()
    }

    private val fetchListener: FetchListener = object : AbstractFetchListener() {

        override fun onAdded(download: Download) {
            downloadAdapter.addOrUpdateDownload(download)
        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
            downloadAdapter.addOrUpdateDownload(download)
        }

        override fun onCompleted(download: Download) {
            downloadAdapter.addOrUpdateDownload(download)
        }

        override fun onError(download: Download, error: Error, throwable: Throwable?) {
            downloadAdapter.addOrUpdateDownload(download)
        }

        override fun onProgress(
            download: Download,
            etaInMilliseconds: Long,
            downloadedBytesPerSecond: Long
        ) {
            downloadAdapter.addOrUpdateDownload(download)
        }

        override fun onPaused(download: Download) {
            downloadAdapter.addOrUpdateDownload(download)
        }

        override fun onResumed(download: Download) {
            downloadAdapter.addOrUpdateDownload(download)
        }

        override fun onCancelled(download: Download) {
            downloadAdapter.addOrUpdateDownload(download)
        }

        override fun onRemoved(download: Download) {
            downloadAdapter.removeDownload(download)
        }

        override fun onDeleted(download: Download) {
            downloadAdapter.addOrUpdateDownload(download)
        }
    }

    private val actionListener = object : ActionListener {

        override fun onView(download: Download) {
            // TODO - view a file
        }

        override fun onPause(id: Int) {
            downloadManager.pause(id)
        }

        override fun onResume(id: Int) {
            downloadManager.resume(id)
        }

        override fun onRemove(id: Int) {
            downloadManager.remove(id)
        }

        override fun onRetry(id: Int) {
            downloadManager.retry(id)
        }
    }

}