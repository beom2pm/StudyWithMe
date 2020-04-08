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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.example.studywithme.R
import com.example.studywithme.data.App
import com.example.studywithme.data.Follow
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class Following : Fragment() {

    var userid=""
    var username=""
    var accoutid= App.prefs.myUserIdData
    val followList = mutableListOf<Follow>()
    var mContext: Context? = null
    var rv_follow_list: RecyclerView? = null
    var follow_user_list_has_no_item_msg: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            userid = bundle.getString("userid")
            username = bundle.getString("username")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.fragment_following, container, false)
        var itemcnt = 0

        mContext = view.context
        rv_follow_list = view.findViewById(R.id.rv_follow_list) as RecyclerView
        follow_user_list_has_no_item_msg = view.findViewById<TextView>(R.id.follow_user_list_has_no_item_msg)

        setHasOptionsMenu(true)
        var toolbarTitle: TextView = activity!!.findViewById(R.id.toolbar_title)
        toolbarTitle.text = username

        followList.removeAll(followList)

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

            val url = "http://203.245.10.33:8888/getFollowList.php"
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
                    var result_array: JSONArray = JSONArray(response.body!!.string())
                    var size = result_array.length() - 1
                    for (i in 0..size) {
                        var id = result_array.getJSONObject(i).getString("user_id").toString()
                        var name = result_array.getJSONObject(i).getString("user_name").toString()
                        var comment = result_array.getJSONObject(i).getString("user_comment").toString()
                        var newFollowItem = Follow("", id, name, comment)
                        followList.add(newFollowItem)
                    }

                    getActivity()?.runOnUiThread {
                        // 어댑터에 데이터변경사항 알리기
                        itemcnt = rv_follow_list!!.adapter!!.getItemCount()
                        rv_follow_list!!.adapter?.notifyDataSetChanged()

                        if(itemcnt <= 0){
                            follow_user_list_has_no_item_msg!!.text = "팔로우하고 있는 친구가 없어요."
                            follow_user_list_has_no_item_msg!!.visibility = VISIBLE
                        }
                        else {
                            follow_user_list_has_no_item_msg!!.visibility = GONE
                        }
                    }

                }
            })
            return result
        }

        override fun onPostExecute(result: String) {
            var adapter = FollowAdapter(
                mContext!!,
                followList,
                this@Following
            )
            rv_follow_list!!.adapter = adapter
            rv_follow_list!!.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        }
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
