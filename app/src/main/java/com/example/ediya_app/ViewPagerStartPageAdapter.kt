package com.example.ediya_app

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ViewPagerStartPageAdapter(imageList: ArrayList<Int>) : RecyclerView.Adapter<ViewPagerStartPageAdapter.PagerViewHolder>(){
    var item = imageList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.idol.setImageResource(item[position])
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.start_page_ads_image, parent, false)){

        val idol = itemView.findViewById<ImageView>(R.id.ads_picture)
    }
}