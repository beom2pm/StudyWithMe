package com.example.studywithme.recommend

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studywithme.R
import com.example.studywithme.data.UserRecommend
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import com.example.studywithme.data.App
import kotlinx.android.synthetic.main.fragment_user.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class UserFrag : Fragment(){

    val userList = mutableListOf<UserRecommend>()
    val userid:String = App.prefs.myUserIdData
    var chosenGoal:String = ""

    var mContext: Context? = null
    var rv_user_list: RecyclerView? = null

    var itemcnt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_user, container, false)

        mContext=view.context
        rv_user_list = view.findViewById(R.id.rv_user_list) as RecyclerView

        //if (chosenGoal == "전체 보기") chosenGoal = "%"

        val bundle = arguments
        if (bundle != null) {
            chosenGoal = bundle.getString("chosenGoal")
        }

        userList.removeAll(userList)
        Log.d("userfag", chosenGoal)


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        MyAsyncTask().execute()
    }

    inner class MyAsyncTask: AsyncTask<Void, String, String>(){ //input, progress update type, result type
        private var result : String = "success"

        override fun onPreExecute() {
            super.onPreExecute()
            //progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Void): String {
            val user_url = "http://203.245.10.33:8888/recommend/getUserList.php"
            val user_requestBody: RequestBody = FormBody.Builder()
                .add("user_id", userid) // 사용자 id
                .add("goal_name", chosenGoal) // 선택한 목표
                .build()

            val user_request = Request.Builder()
                .url(user_url)
                .post(user_requestBody)
                .build()

            val user_client = OkHttpClient()

            user_client.newCall(user_request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("요청 ", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    var result_array: JSONArray = JSONArray(response.body!!.string())
                    var size = result_array.length() - 1


                    for (i in 0..size) {
                        var UserId =
                            result_array.getJSONObject(i).getString("user_id").toString()
                        var UserName =
                            result_array.getJSONObject(i).getString("user_name").toString()

                        var GoalName =
                            result_array.getJSONObject(i).getString("goal_name").toString()

                        var newUserItem =
                            UserRecommend(
                                "",
                                UserId,
                                UserName,
                                GoalName
                            )

                        Log.d("userid",UserId)
                        Log.d("username", UserName)
                        Log.d("GoalName", GoalName)
                        userList.add(newUserItem)
                    }

                    getActivity()?.runOnUiThread {
                        // 어댑터에 데이터변경사항 알리기
                        itemcnt = rv_user_list!!.adapter!!.getItemCount()
                        rv_user_list!!.adapter?.notifyDataSetChanged()

                        if(chosenGoal.equals("")){
                            recommend_user_list_has_no_item_msg.text = "목표를 선택해 주세요."
                            recommend_user_list_has_no_item_msg.visibility = VISIBLE
                        }
                        else if(itemcnt <= 0){
                            recommend_user_list_has_no_item_msg.text = "같은 목표를 가진 친구가 없어요."

                            recommend_user_list_has_no_item_msg.visibility = VISIBLE
                        }
                        else {
                            recommend_user_list_has_no_item_msg.visibility = GONE
                        }
                    }

                }
            })
            return result
        }

        override fun onPostExecute(result: String) {
            var adapter = UserAdapter(mContext!!, userList, this@UserFrag)
            rv_user_list!!.adapter = adapter
            rv_user_list!!.layoutManager = GridLayoutManager(activity, 2)

        }
    }


}