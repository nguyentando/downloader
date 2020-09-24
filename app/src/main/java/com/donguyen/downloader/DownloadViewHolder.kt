package com.donguyen.downloader

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status
import kotlinx.android.synthetic.main.layout_download_item.view.*

/**
 * Created by DoNguyen on 14/9/20.
 */
class DownloadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(download: Download, actionListener: ActionListener) {
        val context = itemView.context

        // title
        itemView.download_title.text = download.file

        // progress
        var progress: Int = download.progress
        if (progress == -1) { // progress not determined yet
            progress = 0
        }
        itemView.download_progress.progress = progress
        itemView.download_progress_text.text = context.getString(
            R.string.progress_percent_format,
            progress
        )

        // download speed
        if (download.downloadedBytesPerSecond == 0L) {
            itemView.download_speed.text = ""
        } else {
            itemView.download_speed.text = Utils.getDownloadSpeedString(
                context,
                download.downloadedBytesPerSecond
            )
        }

        // remaining time
        if (download.etaInMilliSeconds == -1L) {
            itemView.remaining_time.text = ""
        } else {
            itemView.remaining_time.text = Utils.getETAString(
                context,
                download.etaInMilliSeconds
            )
        }

        // status
        itemView.download_status.setText(Utils.getStatusString(download.status))

        // action
        val actionButton = itemView.action_button
        actionButton.setOnClickListener(null)
        actionButton.isEnabled = true

        when (download.status) {
            Status.ADDED -> {
                actionButton.setText(R.string.resume)
                actionButton.setOnClickListener {
                    actionButton.isEnabled = false
                    actionListener.onResume(download.id)
                }
            }
            Status.DOWNLOADING, Status.QUEUED -> {
                actionButton.setText(R.string.pause)
                actionButton.setOnClickListener {
                    actionButton.isEnabled = false
                    actionListener.onPause(download.id)
                }
            }
            Status.PAUSED -> {
                actionButton.setText(R.string.resume)
                actionButton.setOnClickListener {
                    actionButton.isEnabled = false
                    actionListener.onResume(download.id)
                }
            }
            Status.FAILED -> {
                actionButton.setText(R.string.retry)
                actionButton.setOnClickListener {
                    actionButton.isEnabled = false
                    actionListener.onRetry(download.id)
                }
            }
            Status.COMPLETED -> {
                actionButton.setText(R.string.view)
                actionButton.setOnClickListener {
                    actionListener.onView(download)
                }
            }
        }

        // delete action
        itemView.setOnLongClickListener {
            val fileName = Utils.getFileName(download.url)
            AlertDialog.Builder(context)
                .setMessage(context.getString(R.string.delete_title, fileName))
                .setPositiveButton(R.string.delete) { _, _ ->
                    actionListener.onRemove(download.id)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
            true
        }
    }
}