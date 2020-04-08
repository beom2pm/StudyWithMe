package com.example.studywithme.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log

import com.example.studywithme.R
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import android.content.DialogInterface
import android.content.Intent
import android.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.example.studywithme.ui.login.LoginActivity
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import com.example.studywithme.Profile.Following
import com.example.studywithme.Profile.Goal
import com.example.studywithme.Profile.Posting
import com.example.studywithme.Profile.ProfilePostAdapter
import com.example.studywithme.data.App
import com.example.studywithme.data.UserPost
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject
import java.util.ArrayList


class Profile : Fragment() {

    var userid=""
    var accountid = App.prefs.myUserIdData
    val goalList = ArrayList<String>()
    var postList= arrayListOf<UserPost>()
    var check = "unfollow"
    var result = "unfollow"
    var goaladapter: ArrayAdapter<String>? = null
    var postadapter: ProfilePostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            userid = bundle.getString("userid")
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.fragment_profile, container, false)

        val listview = view.findViewById(R.id.profile_goal_list) as ListView
        val profile_post_list = view.findViewById(R.id.profile_post_list) as RecyclerView

        val profile_add_or_edit_btn: Button = view.findViewById(R.id.profile_add_or_edit_btn)
        val profile_username:TextView = view.findViewById(R.id.profile_username)
        val profile_comment:TextView = view.findViewById(R.id.profile_comment)
        val profile_goal_count:TextView = view.findViewById(R.id.profile_goal_count)
        val profile_post_count:TextView = view.findViewById(R.id.profile_post_count)
        val profile_follow_count:TextView = view.findViewById(R.id.profile_follow_count)

        val profile_layout_goal:LinearLayout = view.findViewById(R.id.profile_layout_goal)
        val profile_layout_post:LinearLayout = view.findViewById(R.id.profile_layout_post)
        val profile_layout_following:LinearLayout = view.findViewById(R.id.profile_layout_following)

        val profile_goal_list_has_no_item_msg: TextView = view.findViewById(R.id.profile_goal_list_has_no_item_msg)
        val profile_post_list_has_no_item_msg: TextView = view.findViewById(R.id.profile_post_list_has_no_item_msg)

        var name=""
        var comment=""
        var goalcount="0"
        var postcount="0"
        var followcount="0"


        val checkurl = "http://203.245.10.33:8888/checkFollow.php"
        val checkrequestBody: RequestBody = FormBody.Builder()
            .add("user_id", accountid) // accountId = userid
            .add("follow_id", userid) // userid = followid
            .build()

        val checkrequest = Request.Builder()
            .url(checkurl)
            .post(checkrequestBody)
            .build()

        val checkclient = OkHttpClient()
        checkclient.newCall(checkrequest).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                check = response.body!!.string()

                if (userid==accountid){
                    profile_add_or_edit_btn.text = "설정"
                    profile_add_or_edit_btn.setBackgroundResource(R.drawable.setting_btn)
                }
                else if (check.equals("follow")) {
                    profile_add_or_edit_btn.text = "팔로우 취소"
                    profile_add_or_edit_btn.setBackgroundResource(R.drawable.unfollow_btn_shape)
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.e("요청 ", e.toString())
            }
        })


        profile_add_or_edit_btn.setOnClickListener(){
            if(userid==accountid){
            }
            else {
                val url = "http://203.245.10.33:8888/recommend/UserFollow.php"
                val follow_id = userid
                val requestBody: RequestBody = FormBody.Builder()
                    .add("user_id", accountid) // 사용자 id
                    .add("follow_id", follow_id) // 추가할 id
                    .build()

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        result = response.body!!.string()
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("요청 ", e.toString())
                    }
                })

                if(profile_add_or_edit_btn.text=="팔로우"){
                    profile_add_or_edit_btn.text = "팔로우 취소"
                    profile_add_or_edit_btn.setBackgroundResource(R.drawable.unfollow_btn_shape)
                }
                else{
                    profile_add_or_edit_btn.text = "팔로우"
                    profile_add_or_edit_btn.setBackgroundResource(R.drawable.follow_btn_shape)
                }
            }
        }



        setHasOptionsMenu(true)
        var toolbarTitle: TextView = activity!!.findViewById(R.id.toolbar_title)

        goaladapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, goalList)
        listview!!.adapter = goaladapter

        postadapter =
            ProfilePostAdapter(context!!, postList, this@Profile)
        profile_post_list!!.adapter = postadapter
        profile_post_list!!.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)


        goaladapter!!.clear()
        postList.clear()

        val url = "http://203.245.10.33:8888/getProfile.php"
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
                    name = result_array.getJSONObject(i).getString("user_name").toString()
                    comment = result_array.getJSONObject(i).getString("user_comment").toString()
                    goalcount = result_array.getJSONObject(i).getString("goal_count").toString()
                    postcount = result_array.getJSONObject(i).getString("post_count").toString()
                    followcount = result_array.getJSONObject(i).getString("follow_count").toString()
                    val goal = result_array.getJSONObject(i).getString("goal_name").toString()
                    goalList.add(goal)
                }
                getActivity()?.runOnUiThread {
                    profile_username.text = name
                    if(comment==""){
                        profile_comment.visibility = GONE
                    }
                    else {
                        profile_comment.text = comment
                    }
                    toolbarTitle.text = name
                    profile_goal_count.text = goalcount
                    profile_post_count.text = postcount
                    profile_follow_count.text = followcount

                    goaladapter!!.notifyDataSetChanged()

                    val goalcnt = goalList.size

                    if(goalcnt <= 0){
                        profile_goal_list_has_no_item_msg.text="아직 목표가 없어요."
                        profile_goal_list_has_no_item_msg.visibility = VISIBLE
                    }
                    else {
                        profile_goal_list_has_no_item_msg.visibility = GONE
                    }

                }
            }

        })

        val post_url = "http://203.245.10.33:8888/getMyPost.php"
        val post_client = OkHttpClient()

        val post_requestBody: RequestBody = FormBody.Builder()
            .add("user_id", userid) // 사용자 id
            .build()

        val post_request = Request.Builder()
            .url(post_url)
            .post(post_requestBody)
            .build()

        post_client.newCall(post_request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
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
                    profile_post_list!!.adapter!!.notifyDataSetChanged()

                    var postcnt = profile_post_list!!.adapter!!.getItemCount()

                    if(postcnt <= 0){
                        profile_post_list_has_no_item_msg.text="작성한 게시물이 없어요."
                        profile_post_list_has_no_item_msg.visibility = VISIBLE
                    }
                    else{
                        profile_post_list_has_no_item_msg.visibility = GONE
                    }

                }
            }

        })



        profile_layout_goal.setOnClickListener(){
            val fragment = Goal()
            val bundle = Bundle(2)
            bundle.putString("userid", userid)
            bundle.putString("username", toolbarTitle.text.toString())
            fragment.arguments=bundle
            val fm = fragmentManager
            val fmt = fm?.beginTransaction()
            fmt?.replace(R.id.content, fragment)?.addToBackStack(null)?.commit()
        }
        profile_layout_post.setOnClickListener(){
            val fragment = Posting()
            val bundle = Bundle(2)
            bundle.putString("userid", userid)
            bundle.putString("username", toolbarTitle.text.toString())
            fragment.arguments=bundle
            val fm = fragmentManager
            val fmt = fm?.beginTransaction()
            fmt?.replace(R.id.content, fragment)?.addToBackStack(null)?.commit()
        }
        profile_layout_following.setOnClickListener(){
            val fragment = Following()
            val bundle = Bundle(2)
            bundle.putString("userid", userid)
            bundle.putString("username", toolbarTitle.text.toString())
            fragment.arguments=bundle
            val fm = fragmentManager
            val fmt = fm?.beginTransaction()
            fmt?.replace(R.id.content, fragment)?.addToBackStack(null)?.commit()
        }


        return view
    }



    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                fragmentManager!!.popBackStack()
                fragmentManager!!.beginTransaction().commit()
            }
            R.id.profile_logout -> {
                AlertDialog.Builder(activity/* 해당 액티비티를 가르킴 */)
                    .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("로그아웃", DialogInterface.OnClickListener { dialog, whichButton ->
                        // preference 데이터 삭제
                        var editor = App.prefs.editor
                        editor.clear()
                        editor.apply()
                        val i = Intent(activity, LoginActivity::class.java)
                        //i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    })
                    .setNegativeButton("취소",
                        DialogInterface.OnClickListener { dialog, whichButton -> })
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (userid==accountid){
            profile_add_or_edit_btn.text = "설정"
            profile_add_or_edit_btn.setBackgroundResource(R.drawable.setting_btn)
        }
        else if (check.equals("follow")) {
            profile_add_or_edit_btn.text = "팔로우 취소"
            profile_add_or_edit_btn.setBackgroundResource(R.drawable.unfollow_btn_shape)
        }

    }

    fun getTagList(post_id:Int):ArrayList<String>{
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
}
