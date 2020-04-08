package com.example.studywithme.Profile


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.example.studywithme.R
import com.example.studywithme.data.App
import com.example.studywithme.data.UserPost
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.ArrayList

class Posting : Fragment() {

    var userid=""
    var username=""
    var accoutid= App.prefs.myUserIdData
    val postList = arrayListOf<UserPost>()
    var mContext: Context? = null
    var rv_post_list: RecyclerView? = null
    var post_list_has_no_item_msg: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            userid = bundle.getString("userid")
            username = bundle.getString("username")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view: View =inflater.inflate(R.layout.fragment_posting, container, false)

        mContext = view.context
        rv_post_list = view.findViewById(R.id.rv_post_list) as RecyclerView
        post_list_has_no_item_msg = view.findViewById<TextView>(R.id.post_list_has_no_item_msg)

        setHasOptionsMenu(true)
        var toolbarTitle: TextView = activity!!.findViewById(R.id.toolbar_title)
        toolbarTitle.text = username

        postList.removeAll(postList)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        MyAsyncTask().execute()
    }

    inner class MyAsyncTask: AsyncTask<Void, String, String>(){
        private var result : String = "success"

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void): String {
            var itemcnt = 0

            val url = "http://203.245.10.33:8888/getMyPost.php"
            val client = OkHttpClient()

            val requestBody: RequestBody = FormBody.Builder()
                .add("user_id", userid) // 사용자 id
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("응답 fail", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    var result_array: JSONArray = JSONObject(response.body!!.string()).getJSONArray("post_list")
                    var size = result_array.length() - 1
                    for (i in 0..size) {
                        val goalid = result_array.getJSONObject(i).getString("goal_id").toInt()
                        val postid = result_array.getJSONObject(i).getString("post_id").toInt()
                        val content = result_array.getJSONObject(i).getString("post_contents").toString()
                        val date = result_array.getJSONObject(i).getString("post_date").toString()
                        val likecount = result_array.getJSONObject(i).getString("likes").toInt()
                        val goal = result_array.getJSONObject(i).getString("goal_name").toString()
                        val taglist = getTagList(postid)
                        val newPostItem = UserPost(userid, postid, goalid, goal, content, date, likecount, taglist)
                        postList.add(newPostItem)
                    }

                    getActivity()?.runOnUiThread {
                        // 어댑터에 데이터변경사항 알리기
                        itemcnt = rv_post_list!!.adapter!!.getItemCount()
                        rv_post_list!!.adapter?.notifyDataSetChanged()

                        if(itemcnt <= 0){
                            post_list_has_no_item_msg!!.text = "작성한 게시물이 없어요."
                            post_list_has_no_item_msg!!.visibility = View.VISIBLE
                        }
                        else {
                            post_list_has_no_item_msg!!.visibility = View.GONE
                        }
                    }

                }
            })
            return result
        }

        override fun onPostExecute(result: String) {
            var adapter = ProfilePostAdapter(
                mContext!!,
                postList,
                this@Posting
            )
            rv_post_list!!.adapter = adapter
            rv_post_list!!.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        }
    }

    fun getTagList(post_id:Int): ArrayList<String> {
        var tag_list = arrayListOf<String>()
        val url_hashtag = "http://203.245.10.33:8888/getHashtag.php"

        val requestBody: RequestBody = FormBody.Builder()
            .add("post_id", post_id.toString())
            .build()

        val request_board = Request.Builder()
            .url(url_hashtag)
            .post(requestBody)
            .build()

        val client_hashtag = OkHttpClient()

        client_hashtag.newCall(request_board).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                var result_array: JSONArray = JSONObject(response.body!!.string()).getJSONArray("tag_list")
                var size = result_array.length()-1
                for(i in 0..size){
                    var tagitem = result_array.getJSONObject(i).getString("tag_item")
                    tag_list.add(tagitem)
                }
            }
            override fun onFailure(call: Call, e: IOException) {
            }
        })
        return tag_list
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                fragmentManager!!.popBackStack()
                fragmentManager!!.beginTransaction().commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
