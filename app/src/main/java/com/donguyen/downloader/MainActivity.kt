package com.donguyen.downloader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_create_download_dialog.view.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

    private val STORAGE_PERMISSION_CODE = 200

    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloadManager = DownloadManager(this)

        create_download_fab.setOnClickListener {
            checkStoragePermissions()
        }
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

                // Validate the URL
                val url = view.input_url.text.toString()
                if (!Utils.isValidURL(url)) {
                    showSnackbar(R.string.invalid_url)
                    return@setPositiveButton
                }

                // The URL is valid now, start the download
                downloadManager.createDownloadFunction(
                    url,
                    success = {
                        Log.d(TAG, "Create download success, request = $it")
                        showSnackbar(R.string.create_download_success)
                    },
                    fail = {
                        Log.d(TAG, "Create download fail, error = $it")
                        showSnackbar(R.string.create_download_fail)
                    })
            }
            .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                // No need to do anything.
            }
            .show()
    }

    private fun showSnackbar(@StringRes resId: Int) {
        Snackbar.make(main_layout, resId, Snackbar.LENGTH_LONG).show()
    }

}