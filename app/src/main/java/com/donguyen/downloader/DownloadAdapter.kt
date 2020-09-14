package com.donguyen.downloader

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tonyodev.fetch2.Download

/**
 * Created by DoNguyen on 14/9/20.
 */
class DownloadAdapter(private val actionListener: ActionListener) :
    RecyclerView.Adapter<DownloadViewHolder>() {

    private val downloads = mutableListOf<Download>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_download_item, parent, false)
        return DownloadViewHolder(view)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bind(downloads[position], actionListener)
    }

    override fun getItemCount(): Int {
        return downloads.size
    }

    fun addOrUpdateDownload(download: Download) {
        val position = downloads.indexOfFirst { it.id == download.id }
        if (position >= 0) {
            downloads[position] = download
            notifyItemChanged(position)
        } else {
            downloads.add(0, download)
            notifyItemInserted(0)
        }
    }

    fun removeDownload(download: Download) {
        val position = downloads.indexOfFirst { it.id == download.id }
        if (position >= 0) {
            downloads.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}