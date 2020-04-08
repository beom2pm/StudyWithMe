package com.example.studywithme.Profile

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.studywithme.MainActivity
import com.example.studywithme.R
import com.example.studywithme.data.App
import com.example.studywithme.data.Follow
import com.example.studywithme.fragment.Profile
import kotlinx.android.synthetic.main.profile_following_item.view.*
import okhttp3.*
import java.io.IOException

class FollowAdapter(val context: Context, val items: MutableList<Follow>, val topFrag: Fragment): RecyclerView.Adapter<FollowAdapter.FollowViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = FollowViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        var check = "follow"

        items[position].let { item ->
            with(holder) {
                if (item.img != "") {
                    val resourceId = context.resources.getIdentifier(
                        item.img,
                        "drawable",
                        context.packageName
                    )
                    Log.d("리소스", resourceId.toString())
                    tvImg?.setImageResource(resourceId)
                } else {
                    tvImg?.setImageResource(R.drawable.ic_account_circle_gray_24dp)
                }
                tvName.text = item.name
                tvComment.text = item.comment


                val url = "http://203.245.10.33:8888/checkFollow.php"
                val userid: String = App.prefs.myUserIdData
                val follow_id = items[position].id
                val requestBody: RequestBody = FormBody.Builder()
                    .add("user_id", userid) // 사용자 id
                    .add("follow_id", follow_id) // 추가할 id
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        check = response.body!!.string()

                        if (check.equals("follow")) {
                            follow_button.text = "팔로우 취소"
                            follow_button.setBackgroundResource(R.drawable.unfollow_btn_shape)
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("요청 ", e.toString())
                    }
                })

            }
        }


        val follow_item = holder.follow_item
        var id = items[position].id
        val activity = topFrag.activity as MainActivity
        follow_item.setOnClickListener(View.OnClickListener {
            val fragment = Profile()
            val bundle = Bundle(1)
            bundle.putString("userid", id)
            fragment.arguments=bundle
            activity.fromAdaptertoFragment(fragment)
        })



        val follow_button = holder.follow_button

        follow_button?.setOnClickListener(View.OnClickListener {
            val url = "http://203.245.10.33:8888/recommend/UserFollow.php"
            val userid:String = App.prefs.myUserIdData
            val follow_id = items[position].id
            val requestBody: RequestBody = FormBody.Builder()
                .add("user_id", userid) // 사용자 id
                .add("follow_id", follow_id) // 추가할 id
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                }
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("요청 ", e.toString())
                }
            })

            if(follow_button.text=="팔로우"){
                follow_button.text = "팔로우 취소"
                follow_button.setBackgroundResource(R.drawable.unfollow_btn_shape)
            }
            else{
                follow_button.text = "팔로우"
                follow_button.setBackgroundResource(R.drawable.follow_btn_shape)
            }
        })


    }

    inner class FollowViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.profile_following_item, parent, false)) {
        val tvImg: ImageView = itemView.profile_follow_img
        val tvName = itemView.profile_follow_username
        val tvComment = itemView.profile_follow_comment
        val follow_button = itemView.profile_follow_btn
        val follow_item = itemView.follow_item
    }

}
