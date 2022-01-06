package com.ravi.kutukidemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ravi.kutukidemo.R
import com.ravi.kutukidemo.VideoClickListener
import com.ravi.kutukidemo.model.VideoCategories

class CategoriesAdapter(
    private val categories: List<VideoCategories>,
    private val videoClickListener: VideoClickListener
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View = layoutInflater.inflate(R.layout.video_row, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = ""
        var catName = ""
        var catNo = ""
        if (position >= 9) {
            catName = categories[position].name.dropLast(2)
            catNo = categories[position].name.takeLast(2)
        } else {
            catName = categories[position].name.dropLast(1)
            catNo = categories[position].name.last().toString()
        }

        holder.textView.text = "$catName\n$catNo"
        holder.videoLayout.setOnClickListener {
            videoClickListener.getVideo(position)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.tv_videoName)
        var videoLayout: LinearLayout = itemView.findViewById(R.id.videoLayout)
    }
}