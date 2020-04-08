package com.example.studywithme.Board

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.studywithme.MainActivity

import com.example.studywithme.R
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class TagSearchPost : Fragment(), AdapterView.OnItemSelectedListener{
    var activity: MainActivity? = null
    var list_tagpost= arrayListOf<BoardData>()
    private lateinit var adapter_s:BoardRecycle_Adapter
    internal var textlength=0
    private lateinit var _recyclerView: RecyclerView
    companion object{
        private const val FRAGMENT_TAG = "custom_view"
        fun newInstance()=MyboardFrag()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as MainActivity
    }

    override fun onDetach() {
        super.onDetach()

        activity = null
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_tag_search_post, container, false)
        val tag_str=arguments!!.getString("tag_name")
        fun get_tagPost_from_DB(tag_name:String) {

            val url_hashtag = "http://203.245.10.33:8888/getTagPost.php"
            val requestBody: RequestBody = FormBody.Builder()
                .add("tag_name", tag_name)
                .build()

            val request_board = Request.Builder()
                .url(url_hashtag)
                .post(requestBody)
                .build()
            val client_hashtag = OkHttpClient()
            var size_post=0

            client_hashtag.newCall(request_board).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    var str_response_post = response.body!!.string()
                    System.out.println("진짜 확인해야겠어"+str_response_post)
                    var taglist_now= arrayListOf<String>()
                    val jsonobj_post: JSONObject = JSONObject(str_response_post)
                    //create json array
                    var json_array_post: JSONArray = jsonobj_post.getJSONArray("tag_list")
                    size_post = json_array_post.length()

                    //System.out.println("이거확인하려"+size_post)

                    for (i in 0 until size_post) {
                        var json_objdetail_post: JSONObject = json_array_post.getJSONObject(i)
                        var userID_tag = json_objdetail_post.getString("user_id")
                        var goalID_tag=json_objdetail_post.getString("goal_id")
                        var postID_tag=json_objdetail_post.getString("post_id")
                        var contents_tag=json_objdetail_post.getString("post_contents")
                        var date_tag=json_objdetail_post.getString("post_date")
                        var goalName_tag=json_objdetail_post.getString("goal_name")

                        //int로 바꾸기
                        var goalID_int :Int=goalID_tag.toInt()
                        taglist_now=get_tagData_from_DB(postID_tag)

                        System.out.println("d유저 아이디는"+userID_tag)

                        var newPostItem=
                            BoardData(
                                postID_tag,
                                userID_tag,
                                goalID_int,
                                contents_tag,
                                date_tag,
                                taglist_now,
                                goalName_tag
                            )
                        list_tagpost.add(newPostItem)
                        System.out.println("이러닝러닝 여기는 함수안"+list_tagpost.size)

                    }
                    activity!!.runOnUiThread {
                        // 어댑터에 데이터변경사항 알리기
                       _recyclerView.adapter?.notifyDataSetChanged()
                    }

                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("요청", "요청 실패")
                    Log.d("why왜!!", e.toString())
                }
            })
            _recyclerView=view.findViewById(R.id.mrecyclerView_tag)
            _recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayout.VERTICAL, false)

            adapter_s = BoardRecycle_Adapter(view.context,list_tagpost,this@TagSearchPost)

            _recyclerView.adapter=adapter_s

        }


        get_tagPost_from_DB(tag_str)

        System.out.println("이러닝러닝ㄹ"+list_tagpost.size)

       /* val fragmentManager: FragmentManager =getActivity()!!.supportFragmentManager
        fragmentManager.beginTransaction().remove(Write_post()).commit()
        fragmentManager.popBackStack()*/


        return view
    }


    override fun onItemSelected(adapterview: AdapterView<*>?, view: View?, position: Int, id: Long) {

        // use position to know the selected item
        // goal_text.text="Selected:"+list_of_goal[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
    /*
    fun get_tagPost_from_DB(tag_name:String) {

        val url_hashtag = "http://203.245.10.33:8888/getTagPost.php"
        val requestBody: RequestBody = FormBody.Builder()
            .add("tag_name", tag_name)
            .build()

        val request_board = Request.Builder()
            .url(url_hashtag)
            .post(requestBody)
            .build()
        val client_hashtag = OkHttpClient()
        var size_post=0
        client_hashtag.newCall(request_board).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                var str_response_post = response.body!!.string()
                System.out.println("진짜 확인해야겠어"+str_response_post)
                var taglist_now= arrayListOf<String>()
                val jsonobj_post: JSONObject = JSONObject(str_response_post)
                //create json array
                var json_array_post: JSONArray = jsonobj_post.getJSONArray("tag_list")
                size_post = json_array_post.length()

                //System.out.println("이거확인하려"+size_post)

                for (i in 0 until size_post) {
                    var json_objdetail_post: JSONObject = json_array_post.getJSONObject(i)
                    var userID_tag = json_objdetail_post.getString("user_id")
                    var goalID_tag=json_objdetail_post.getString("goal_id")
                    var postID_tag=json_objdetail_post.getString("post_id")
                    var contents_tag=json_objdetail_post.getString("post_contents")
                    var date_tag=json_objdetail_post.getString("post_date")
                    //int로 바꾸기
                    var goalID_int :Int=goalID_tag.toInt()
                    taglist_now=get_tagData_from_DB(postID_tag)

                    System.out.println("d유저 아이디는"+userID_tag)

                    var newPostItem=
                        BoardData(
                            postID_tag,
                            userID_tag,
                            goalID_int,
                            contents_tag,
                            date_tag,
                            taglist_now
                        )
                    list_tagpost.add(newPostItem)
                    System.out.println("이러닝러닝 여기는 함수안"+list_tagpost.size)

                }

            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("요청", "요청 실패")
                Log.d("why왜!!", e.toString())
            }
        })

    }*/

    fun get_tagData_from_DB(post_id:String):ArrayList<String>{

        var tag_list = arrayListOf<String>()
        val url_hashtag = "http://203.245.10.33:8888/getHashtag.php"
        val requestBody: RequestBody = FormBody.Builder()
            .add("post_id", post_id)
            .build()

        val request_board = Request.Builder()
            .url(url_hashtag)
            .post(requestBody)
            .build()
        val client_hashtag = OkHttpClient()

        client_hashtag.newCall(request_board).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                // 확인메시지
                Log.d("요청", "요청 완료tagsearch get")

                //data 가져오기
                var str_response_tag = response.body!!.string()
                val jsonobj_tag: JSONObject = JSONObject(str_response_tag)
                //create json array
                var json_array_tag: JSONArray = jsonobj_tag.getJSONArray("tag_list")
                var size_taglist = json_array_tag.length()


                for (i in 0 until size_taglist) {
                    var json_objdetail_tag: JSONObject = json_array_tag.getJSONObject(i)
                    var tagitem=json_objdetail_tag.getString("tag_item")

                    tag_list.add(tagitem)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("요청", "요청 실패")
                Log.d("why왜!!", e.toString())
            }
        })
        return tag_list
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            //뒤로가기 버튼 클릭 시
            android.R.id.home -> {
                fragmentManager!!.popBackStack()
                fragmentManager!!.beginTransaction().commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }



}