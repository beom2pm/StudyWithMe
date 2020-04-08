package com.example.studywithme.DialogFragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat.finishAffinity
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.studywithme.HtmlParser
import com.example.studywithme.HttpConnection
import com.example.studywithme.R
import org.json.JSONObject

class DialogAddBookMarkLink: DialogFragment() {
    private var parent_fragment: Fragment? = null
    var args: Bundle? = null
    var categoryName: String? = null
    var sharedLinkURL: String? = null
    var positiveButton: Button? = null
    var negativeButton: Button? = null
    var httpConn: HttpConnection = HttpConnection()
    var htmlParser: HtmlParser = HtmlParser()
    var existSharedLinkURL: Boolean = false

    fun addBookmarkLink() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null){
            args = arguments
            categoryName = args!!.getString("categoryName")
            if (args!!.getString("sharedLinkURL") != null){
                existSharedLinkURL = true
                sharedLinkURL = args!!.getString("sharedLinkURL")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.bookmark_popup_link, container, false)
        /* 종료 버튼 view 객체 할당*/
        positiveButton = view.findViewById(R.id.link_dialog_positive)
        negativeButton = view.findViewById(R.id.link_dialog_negative)
        // 부모 프래그먼트 받아오기 (이거 다이얼로그 show로 꺼낼 때 쓰던 거임)
        //parent_fragment = activity!!.supportFragmentManager.findFragmentByTag("addLinkDialog")
        if (existSharedLinkURL){
            view!!.findViewById<EditText>(R.id.link_url).setText(sharedLinkURL)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 버튼 클릭 이벤트
        setOnClickedListener()
    }

    private fun setOnClickedListener(){
        positiveButton!!.setOnClickListener {
            Log.d("링크 다이얼로그 응답", "확인")
            var linkURL_string = view!!.findViewById<EditText>(R.id.link_url).text.toString()
            var linkDescription_string = view!!.findViewById<EditText>(R.id.link_description).text.toString()
            Log.d("링크 다이얼로그 응답", linkURL_string + " " + linkDescription_string)
            var temp = addLink(linkURL_string, linkDescription_string)
            // http 통신이랑 크롤링도 하고 그래서 콜 안 맞아서 자꾸 실패 뜨는 거 같아서 조건문 추가해봤음
            if (temp != null || temp != ""){
                if (existSharedLinkURL){
                    Toast.makeText(this.context, "링크 추가되었습니다.", Toast.LENGTH_SHORT)
                    finishAffinity(activity!!)
                    System.runFinalization()
                    System.exit(0)
                } else {
                    fragmentManager!!.beginTransaction().remove(this).commitAllowingStateLoss()
                    fragmentManager!!.popBackStack()
                }
            }
        }
        negativeButton!!.setOnClickListener {
            Log.d("링크 다이얼로그 응답", "취소")
            if (existSharedLinkURL){
                activity!!.finish()
            } else {
                fragmentManager!!.beginTransaction().remove(this).commitAllowingStateLoss()
                fragmentManager!!.popBackStack()
            }
            /* 이거는 다이얼로그 프래그먼트가 show 로 나오는 경우에만 먹히는듯
            if (parent_fragment != null){
                var dialogFragment: DialogFragment = parent_fragment as DialogFragment
                dialogFragment.dismiss()
            }
             */
        }
    }

    fun addLink(linkURL: String, linkDescription: String): String?{
        /* db에 uri 데이터 추가하기 위해 크롤링 함수 호출 */
        var result_string = htmlParser.get_bmLinkMetaData(linkURL)
        var result_object = JSONObject(result_string)
        var title = result_object.getString("title")
        var imageURL = result_object.getString("imageURL")
        var description: String = ""
        if (linkDescription == null || linkDescription == ""){
            description = result_object.getString("description")
        } else {
            description = linkDescription
        }
        /* db에 아이템 추가*/
        var tempResult = httpConn.add_bookmarkLinkData(linkURL, title, imageURL, description, categoryName)
        return tempResult
    }
}