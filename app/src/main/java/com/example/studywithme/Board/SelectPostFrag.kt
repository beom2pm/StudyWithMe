package com.example.studywithme.Board

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.studywithme.MainActivity

import com.example.studywithme.R
import com.example.studywithme.fragment.Home
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern



class SelectPostFrag : Fragment(){
    var taglist = arrayListOf<String>()
    private lateinit var tag_txt:TextView
    var activity: MainActivity? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_select_post, container, false)
        setHasOptionsMenu(true)

        val goal_id_str=arguments!!.getString("goal_id")
        val user_id_str=arguments!!.getString("user_id")
        val content_str=arguments!!.getString("content")
        val date_str=arguments!!.getString("date")
        val post_str=arguments!!.getString("post_id")
        val tag_str=arguments!!.getString("tag")

        val user_txt=view.findViewById<TextView>(R.id.id_user)
        val goal_txt=view.findViewById<TextView>(R.id.id_goal)
        val content_txt=view.findViewById<TextView>(R.id.id_content)
        val date_txt=view.findViewById<TextView>(R.id.date_txt)
        tag_txt=view.findViewById<TextView>(R.id.tag_txt)

        //db에서 tag 받아오기
        get_tagData_from_DB(post_str)


        //text에 받아온 데이터 붙이기
        user_txt.setText(user_id_str)
        goal_txt.setText(goal_id_str)
        user_txt.setText(user_id_str)
        content_txt.setText(content_str)
        date_txt.setText(date_str)
        tag_txt.setText(tag_str)


        return view
    }

    fun getSpans(body:String, prefix:Char): ArrayList<IntArray> {
        System.out.println("getspan들어감")
        val spans = ArrayList<IntArray>()
        val pattern:Pattern=Pattern.compile(prefix+"\\w+")
        val matcher:Matcher=pattern.matcher(body)
        while (matcher.find()) {
            val currentSpan = IntArray(2)
            currentSpan[0] = matcher.start()
            currentSpan[1] = matcher.end()
            spans.add(currentSpan)
        }

        return spans
    }


    fun get_tagData_from_DB(post_id:String) {

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
                System.out.println("나는 왜리"+size_taglist)


                    for (i in 0 until size_taglist) {
                        var json_objdetail_tag: JSONObject = json_array_tag.getJSONObject(i)
                        var tagitem=json_objdetail_tag.getString("tag_item")

                        taglist.add(tagitem)
                    }

            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("요청", "요청 실패")
                Log.d("why왜!!", e.toString())
            }
        })
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



