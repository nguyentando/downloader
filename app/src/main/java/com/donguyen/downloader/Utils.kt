package com.donguyen.downloader

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Patterns
import android.webkit.MimeTypeMap
import com.tonyodev.fetch2.Status
import java.text.DecimalFormat


/**
 * Created by DoNguyen on 14/9/20.
 */
object Utils {

    fun isValidURL(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    fun getFileName(url: String): String {
        val uri = Uri.parse(url)
        return uri.lastPathSegment ?: ""
    }

    fun getDownloadSpeedString(context: Context, bytesPerSecond: Long): String {
        if (bytesPerSecond < 0) {
            return ""
        }

        val kb = bytesPerSecond / 1000F
        val mb = kb / 1000F
        val decimalFormat = DecimalFormat(".##")

        return when {
            mb >= 1 -> {
                context.getString(R.string.download_speed_mb, decimalFormat.format(mb))
            }
            kb >= 1 -> {
                context.getString(R.string.download_speed_kb, decimalFormat.format(kb))
            }
            else -> {
                context.getString(R.string.download_speed_bytes, bytesPerSecond)
            }
        }
    }

    fun getETAString(context: Context, etaInMilliSeconds: Long): String {
        if (etaInMilliSeconds < 0) {
            return ""
        }

        var seconds = etaInMilliSeconds / 1000

        val hours = seconds / 3600
        seconds -= hours * 3600

        val minutes = seconds / 60
        seconds -= minutes * 60

        return when {
            hours > 0 -> {
                context.getString(R.string.download_eta_hrs, hours, minutes, seconds)
            }
            minutes > 0 -> {
                context.getString(R.string.download_eta_min, minutes, seconds)
            }
            else -> {
                context.getString(R.string.download_eta_sec, seconds)
            }
        }
    }

    fun getStatusString(status: Status): Int {
        return when (status) {
            Status.COMPLETED -> R.string.done
            Status.DOWNLOADING -> R.string.downloading
            Status.FAILED -> R.string.failed
            Status.PAUSED -> R.string.paused
            Status.QUEUED -> R.string.queued
            Status.REMOVED -> R.string.removed
            Status.NONE -> R.string.none
            else -> R.string.unknown
        }
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        return if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            context.applicationContext.contentResolver.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase()
            )
        }
    }
}