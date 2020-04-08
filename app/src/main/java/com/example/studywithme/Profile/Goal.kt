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
import com.example.studywithme.scheduling.Goal_list_adapter
import com.example.studywithme.scheduling.Goal_list_data
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.ArrayList


class Goal : Fragment() {

    var userid=""
    var username=""
    var accoutid= App.prefs.myUserIdData
    val goalList = arrayListOf<Goal_list_data>()
    var mContext: Context? = null
    var rv_goal_list: RecyclerView? = null
    var goal_list_has_no_item_msg: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            userid = bundle.getString("userid")
            username = bundle.getString("username")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view: View =inflater.inflate(R.layout.fragment_goal, container, false)

        mContext = view.context
        rv_goal_list = view.findViewById(R.id.rv_goal_list) as RecyclerView
        goal_list_has_no_item_msg = view.findViewById<TextView>(R.id.goal_list_has_no_item_msg)

        setHasOptionsMenu(true)
        var toolbarTitle: TextView = activity!!.findViewById(R.id.toolbar_title)
        toolbarTitle.text = username

        goalList.removeAll(goalList)

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

            val url = "http://203.245.10.33:8888/scheduling/search.php"
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
                        var goalName = result_array.getJSONObject(i).getString("goal_name").toString()
                        var dday = result_array.getJSONObject(i).getString("dday").toString()
                        var goal_id = result_array.getJSONObject(i).getString("goal_id").toInt()
                        var datechange = LocalDate.parse(dday, DateTimeFormatter.ISO_DATE)
                        var localdate = LocalDate.now()
                        var period = datechange.toEpochDay() - localdate.toEpochDay()
                        var cal_dday = period.toString()
                        var newGoalItem =
                            Goal_list_data(
                                goalName,
                                cal_dday,
                                goal_id
                            )
                        goalList.add(newGoalItem)
                    }

                    getActivity()?.runOnUiThread {
                        // 어댑터에 데이터변경사항 알리기
                        itemcnt = rv_goal_list!!.adapter!!.getItemCount()
                        rv_goal_list!!.adapter?.notifyDataSetChanged()

                        if(itemcnt <= 0){
                            goal_list_has_no_item_msg!!.text = "아직 목표가 없어요."
                            goal_list_has_no_item_msg!!.visibility = View.VISIBLE
                        }
                        else {
                            goal_list_has_no_item_msg!!.visibility = View.GONE
                        }
                    }

                }
            })
            return result
        }

        override fun onPostExecute(result: String) {
            var adapter = Goal_list_adapter(
                mContext!!,
                goalList,
                this@Goal
            )
            rv_goal_list!!.adapter = adapter
            rv_goal_list!!.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
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

