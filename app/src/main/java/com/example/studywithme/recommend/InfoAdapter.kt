package com.example.studywithme.recommend

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.studywithme.R
import com.example.studywithme.data.InfoRecommend
import kotlinx.android.synthetic.main.recommend_info_item.view.*

class InfoAdapter(val context: Context, val items: MutableList<InfoRecommend>): RecyclerView.Adapter<InfoAdapter.InfoViewHolder> (){

    var tvUrl:String = ""

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = InfoViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        items[position].let { item ->
            with(holder) {
                tvHeader.text = item.header
                tvContent.text = item.content
                tvUrl =item.url
            }
        }

        holder?.itemView?.setOnClickListener {
            val intent = Intent(ACTION_VIEW, Uri.parse(items[position].url))

            context.startActivity(intent)
            //Toast.makeText(context, position.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    inner class InfoViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recommend_info_item, parent, false)) {
        val tvHeader = itemView.recommend_info_header
        val tvContent = itemView.recommend_info_content
    }

}
