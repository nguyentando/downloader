package com.donguyen.downloader

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.tonyodev.fetch2.AbstractFetchListener
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_create_download_dialog.view.*
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        private const val STORAGE_PERMISSION_CODE = 200
    }

    private lateinit var downloadManager: DownloadManager

    private lateinit var downloadAdapter: DownloadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init download manager
        downloadManager = DownloadManager(this)

        // init download list
        downloadAdapter = DownloadAdapter(actionListener)
        download_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = downloadAdapter
        }

        // init create download FAB
        create_download_fab.setOnClickListener {
            checkStoragePermissions()
        }
    }

    override fun onStart() {
        super.onStart()
        downloadManager.addListener(fetchListener)
        downloadManager.getAllDownloads { downloads ->
            val mutableList = mutableListOf<Download>().apply {
                addAll(downloads)
            }
            mutableList.sortWith { first: Download, second: Download ->
                // newest on the top
                first.created.compareTo(second.created)
            }
            mutableList.forEach {
                downloadAdapter.addOrUpdateDownload(it)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        downloadManager.removeListener(fetchListener)
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
            // permission granted
            showCreateDownloadDialog()
        } else {
            showSnackbar(R.string.permission_not_enabled)
        }
    }

    private fun showCreateDownloadDialog() {
        val view = layoutInflater.inflate(R.layout.layout_create_download_dialog, null)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.create_download)
            .setView(view)
            .setPositiveButton(R.string.download) { _, _ ->
                val url = view.input_url.text.toString()
                createDownload(url)
            }
            .setNeutralButton(R.string.cancel) { _, _ ->
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
                showSnackbar(getString(R.string.create_download_fail, it?.name))
            })
    }

    private fun showSnackbar(@StringRes resId: Int) {
        Snackbar.make(main_layout, resId, Snackbar.LENGTH_LONG).show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(main_layout, message, Snackbar.LENGTH_LONG).show()
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
            view(download)
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

    private fun view(download: Download) {
        val intent = Intent().apply {

            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            val file = File(download.file)
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                FileProvider.getUriForFile(
                    this@MainActivity,
                    this@MainActivity.applicationContext.packageName.toString() + ".provider",
                    file
                )
            } else {
                Uri.fromFile(file)
            }
            setDataAndType(uri, Utils.getMimeType(this@MainActivity, uri))
        }
        if (Utils.isIntentAvailable(this, intent)) {
            startActivity(intent)
        } else {
            showSnackbar(R.string.intent_not_available)
        }
    }
}