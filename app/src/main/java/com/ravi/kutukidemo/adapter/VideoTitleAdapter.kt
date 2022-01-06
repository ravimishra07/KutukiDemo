package com.ravi.kutukidemo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ravi.kutukidemo.R
import com.ravi.kutukidemo.VideoTabClickListner
import com.ravi.kutukidemo.model.Videos

class VideoTitleAdapter(
    private val categories: List<Videos>,
    private val context: Context,
    private val videoTabClickListener: VideoTabClickListner,
    vidIndex: Int
) : RecyclerView.Adapter<VideoTitleAdapter.ViewHolder>() {

    private var lastPosition = vidIndex

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.video_name_row, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.textView.text = categories[position].title

        holder.nameLayout.setOnClickListener {
            lastPosition = position
            notifyDataSetChanged()
            videoTabClickListener.getVideo(categories[position].videoURL)
        }

        if (lastPosition == position) {
            holder.nameLayout.background = ContextCompat.getDrawable(context, R.drawable.bg_line)
        } else {
            holder.nameLayout.background = ContextCompat.getDrawable(context, R.color.theme)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.tv_videoName)
        var nameLayout: LinearLayout = itemView.findViewById(R.id.nameLayout)
    }

}