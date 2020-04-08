package com.example.studywithme.Board

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.studywithme.MainActivity

import com.example.studywithme.R
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class WholeboardFrag : Fragment(), AdapterView.OnItemSelectedListener{

    var list_post= arrayListOf<BoardData>()
    private lateinit var adapter_s:BoardRecycle_Adapter
    internal var textlength=0
    private lateinit var _recyclerView: RecyclerView
    var boardSearchList= arrayListOf<BoardData>()
    var activity: MainActivity? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_wholeboard, container, false)

        //게시글 list

        _recyclerView=view.findViewById(R.id.mrecyclerView)
        _recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayout.VERTICAL, false)


        adapter_s = BoardRecycle_Adapter(view.context,list_post,this)
        _recyclerView.adapter=adapter_s

        boardSearchList.clear()

        var size_post=0
        val url_post = "http://203.245.10.33:8888/getWholePost.php"
        val client_post = OkHttpClient()
        val request_post = Request.Builder().url(url_post).build()
        client_post.newCall(request_post).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("응답 fail", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                var str_response_post = response.body!!.string()
                var taglist_now= arrayListOf<String>()
                val jsonobj_post: JSONObject = JSONObject(str_response_post)
                //create json array
                var json_array_post: JSONArray = jsonobj_post.getJSONArray("post_list")
                size_post = json_array_post.length()

                for (i in 0 until size_post) {
                    var json_objdetail_post: JSONObject = json_array_post.getJSONObject(i)
                    var userID_post = json_objdetail_post.getString("user_id")
                    var goalID_post=json_objdetail_post.getString("goal_id")
                    var postID_post=json_objdetail_post.getString("post_id")
                    var contents_post=json_objdetail_post.getString("post_contents")
                    var date_post=json_objdetail_post.getString("post_date")
                    var goalName_post="test"
                    //int로 바꾸기
                    var goalID_int :Int=goalID_post.toInt()
                    taglist_now=get_tagData_from_DB(postID_post)


                    var newPostItem=
                        BoardData(
                            postID_post,
                            userID_post,
                            goalID_int,
                            contents_post,
                            date_post,
                            taglist_now,
                            goalName_post
                        )
                    list_post.add(newPostItem)
                    System.out.println("리스트 사이즈는1"+list_post.size)
                    boardSearchList.add(newPostItem)

                }

            }
        })




        //custom 검색바 -edittext로 구현
        val search_edt=view.findViewById<EditText>(R.id.search_edt)
        search_edt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(edit: Editable?) {
            }
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                textlength=search_edt.text.length
                boardSearchList.clear()
                var str_sequence=charSequence.toString()
                for(i in list_post.indices){
                    if(list_post[i].goalID.toString().contains(str_sequence)||(list_post[i].postContent.contains(str_sequence)))
                        boardSearchList.add(list_post[i])
                }
                adapter_s= BoardRecycle_Adapter(view.context,boardSearchList, this@WholeboardFrag)
                _recyclerView.adapter=adapter_s
                _recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayout.VERTICAL, false)
            }


        })



        return view
    }


    override fun onItemSelected(adapterview: AdapterView<*>, view: View, position: Int, id: Long) {
        // use position to know the selected item
        // goal_text.text="Selected:"+list_of_goal[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
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
                Log.d("요청", "요청 완료")

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


}