package com.example.studywithme.Board

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.studywithme.MainActivity

import com.example.studywithme.R
import com.example.studywithme.data.App
import com.skyhope.materialtagview.TagView
import com.skyhope.materialtagview.enums.TagSeparator
import com.skyhope.materialtagview.interfaces.TagItemListener
import com.skyhope.materialtagview.model.TagModel
import kotlinx.android.synthetic.main.fragment_write_post.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class Write_post : Fragment(), AdapterView.OnItemSelectedListener  {
    var activity: MainActivity? = null
    var list_of_goal = ArrayList<String>()
    var spinner: Spinner? = null
    var taglist=ArrayList<String>()
    var stored_taglist=ArrayList<String>()
    var taglist_str:String=""
    val myname= App.prefs.myUserIdData
    var goalOfpost=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_write_post, container, false)

        // Inflate the layout for this fragment
        list_of_goal.add("기타")
        val url_spinner = "http://203.245.10.33:8888/getSpinnerList.php"
        val client_spinner = OkHttpClient()
        val requestBody: RequestBody = FormBody.Builder()
            .add("user_id", myname)
            .build()

        val request_spinner = Request.Builder()
            .url(url_spinner)
            .post(requestBody)
            .build()

        client_spinner.newCall(request_spinner).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("응답 fail", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                var str_response_spinner = response.body!!.string()
                val jsonobj_spinner: JSONObject = JSONObject(str_response_spinner)
                //create json array
                var json_array_spinner: JSONArray = jsonobj_spinner.getJSONArray("goal_list")
                var size_spinner: Int = json_array_spinner.length()


                for (i in 0 until size_spinner) {
                    var json_objdetail_spinner: JSONObject = json_array_spinner.getJSONObject(i)
                    var goal = json_objdetail_spinner.getString("goal_name")
                    list_of_goal.add(goal)
                }


            }
        })


        val spinner = view.findViewById<Spinner>(R.id.spinner_goal)
        spinner?.prompt = "목표를 선택해주세요"
        spinner?.onItemSelectedListener = this
        val items = ArrayAdapter(activity,android.R.layout.simple_spinner_item,list_of_goal)
        items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = items

        //태그 기능

        val tagView :TagView=view.findViewById(R.id.text_view_show_more)
        tagView.setHint("add text")
        tagView.addTagLimit(5)

        taglist.add("태그 입력후 클릭")
        tagView.setTagList(taglist)

        //태그 색상
        tagView.setTagBackgroundColor("#83cac5")

        //taglistener 설정
        tagView.initTagListener(object :TagItemListener{
            override fun onGetRemovedItem(model: TagModel?) {
                if (model != null) {
                    stored_taglist.remove(model.tagText)
                }

            }

            override fun onGetAddedItem(tagModel: TagModel?) {
                if (tagModel != null) {
                    stored_taglist.add(tagModel.tagText)
                    //System.out.println("이건진짜"+tagModel.tagText)
                }
            }

        })


        var board_contents= "no"
        /*완료 버튼 이벤트*/
        val register_button = view.findViewById<Button>(R.id.register_board_btn)
        register_button.setOnClickListener (object :View.OnClickListener{
            override fun onClick(v: View?) {
                board_contents = board_content.text.toString()
                println(board_contents)
                //taglist to ; string
                for(i in 0 until stored_taglist.size) {
                    if (i == stored_taglist.size-1)
                        taglist_str += stored_taglist[i]
                    else
                        taglist_str += stored_taglist[i] + ","

                }
                //goal id는 받아와서 보내야함 position으로
                add_postData_to_DB(myname, goalOfpost, board_contents, 0,taglist_str)
                activity?.onFragmentChange(2)

                /* val fragmentManager:FragmentManager=getActivity()!!.supportFragmentManager
                fragmentManager.beginTransaction().remove(Write_post()).commit()
                fragmentManager.popBackStack()*/

            }

        })


        return view
    }
    //게시글 작성
    fun add_postData_to_DB(user_id:String, goal_name:String,post_contents:String,likes:Int,taglist:String) {

        Log.d("뭐때문이지",taglist)
        val url_board = "http://203.245.10.33:8888/writePost.php"
        val requestBody: RequestBody = FormBody.Builder()
            .add("user_id", user_id)
            .add("goal_name", goal_name)
            .add("post_contents", post_contents)
            .add("likes", likes.toString())
            .add("tag_list",taglist)
            .build()

        val request_board = Request.Builder()
            .url(url_board)
            .post(requestBody)
            .build()
        val client_board = OkHttpClient()

        client_board.newCall(request_board).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("요청", "요청 완료_wirtepost")
                Log.d("등록 텍스트 확인",goal_name)
                //var temp=response.body?.string().toString()
               // Log.d("에러가뭐니",temp)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("writepost_error", e.toString())
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onAttach(context: Context) {
        activity = getActivity() as MainActivity
        super.onAttach(context)
    }

    override fun onDetach() {
        activity = null
        super.onDetach()
    }


    //adapter관련 function- 이 두개가 있어야함
    override fun onItemSelected(adapterview: AdapterView<*>, view: View, position: Int, id: Long) {
        // use position to know the selected item
        goalOfpost= list_of_goal[position]
    }
    override fun onNothingSelected(arg0: AdapterView<*>) {

    }



}
