package com.example.studywithme.Profile

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studywithme.Board.SelectPostFrag
import com.example.studywithme.MainActivity
import com.example.studywithme.R
import com.example.studywithme.data.UserPost
import kotlinx.android.synthetic.main.profile_post_item.view.*


class ProfilePostAdapter(val context: Context, val items: ArrayList<UserPost>, val topFrag: Fragment): RecyclerView.Adapter<ProfilePostAdapter.ProfileViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ProfileViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        var tag = ""
        items[position].let { item ->
            with(holder) {
                tvName.text = "[" + item.goalname + "]"
                tvLike.text = item.like.toString()
                tvContent.text = item.content
                tvDate.text = item.date
                for (i in 0 until item.taglist.size) {
                    if(item.taglist.get(i).equals("")){
                        tag = ""
                    }
                    else{
                        tag += "#" + item.taglist.get(i) + " "
                    }
                }
                tvTag.text = tag
            }
            tag = ""
        }

        val layout = holder.layout
        val userid = items[position].userid
        val postid = items[position].postid
        val goalname = items[position].goalname
        val content = items[position].content
        val date = items[position].date
        val tagstr = items[position].taglist
        val activity = topFrag.activity as MainActivity
        layout.setOnClickListener(View.OnClickListener {
            val fragment = SelectPostFrag()
            val bundle = Bundle(6)
            bundle.putString("user_id", userid)
            bundle.putString("post_id", postid.toString())
            bundle.putString("goal_id", goalname)
            bundle.putString("content", content)
            bundle.putString("date", date)
            bundle.putString("tag", tagstr.toString())
            fragment.arguments=bundle
            activity.fromAdaptertoFragment(fragment)
        })

    }

    inner class ProfileViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.profile_post_item, parent, false)) {
        val tvName = itemView.profile_post_goal
        val tvLike = itemView.profile_post_like_count
        val tvContent = itemView.profile_post_content
        val tvTag = itemView.profile_post_tag
        val tvDate = itemView.profile_post_date
        val layout = itemView.profile_post_layout
    }

}
