package com.example.studywithme.recommend

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.example.studywithme.MainActivity
import com.example.studywithme.R
import com.example.studywithme.data.App
import com.example.studywithme.data.InfoRecommend
import com.example.studywithme.fragment.Recommend
import kotlinx.android.synthetic.main.fragment_info.*
import okhttp3.*
import org.json.JSONArray
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.w3c.dom.Text


class InfoFrag : Fragment() {

    val infoList = mutableListOf<InfoRecommend>()
    val userid:String = App.prefs.myUserIdData
    var chosenGoal:String = ""

    var query = ""
    var weburl = ""

    var mContext: Context? = null
    var rv_info_list: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_info, container, false)
        val recommend_info_list_has_no_item_msg:TextView = view.findViewById(R.id.recommend_info_list_has_no_item_msg)

        mContext=view.context
        rv_info_list = view.findViewById(R.id.rv_info_list)


        val bundle = arguments
        if (bundle != null) {
            chosenGoal = bundle.getString("chosenGoal")
        }
        Log.d("infofrag", chosenGoal)
        query = chosenGoal
        weburl="https://www.google.com/search?q=" + query + "&num=30"

        if(chosenGoal.equals("")){
            recommend_info_list_has_no_item_msg.text = "목표를 선택해 주세요."
            recommend_info_list_has_no_item_msg.visibility = VISIBLE
        }
        else {
            recommend_info_list_has_no_item_msg.visibility = GONE

        }
        Log.d("infofrag", chosenGoal)
        query = chosenGoal
        weburl="https://www.google.com/search?q=" + query + "&num=20"

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        MyAsyncTask().execute(weburl)
    }


    inner class MyAsyncTask: AsyncTask<String, String, String>(){ //input, progress update type, result type
        private var result : String = "success"

        override fun onPreExecute() {
            super.onPreExecute()
            //progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: String): String {
            try{
                val doc: Document = Jsoup.connect("$weburl").get()
                val link: Elements = doc.select("div.rc")

                link.forEachIndexed { index, elem ->
                    val header: String = elem.select("span.S3Uucc").text()
                    val content: String = elem.select("span.st").text()
                    val url: String = elem.select("a").attr("href")

                    var newInfoItem =
                        InfoRecommend(
                            header,
                            content,
                            url
                        )
                    infoList.add(newInfoItem)

                    getActivity()?.runOnUiThread {
                        // 어댑터에 데이터변경사항 알리기
                        rv_info_list!!.adapter?.notifyDataSetChanged()
                    }
                }
            }
            catch(e: HttpStatusException){
                result = "fail"
            }

            Log.d("result", result)
            return result
        }

        override fun onPostExecute(result: String) {
            //progressBar.visibility = View.GONE
            if(result=="fail"){
                Toast.makeText(mContext, "페이지를 불러올 수 없습니다", Toast.LENGTH_LONG)
            }
            else {
                var adapter = InfoAdapter(mContext!!, infoList)
                rv_info_list!!.setAdapter(adapter)
                rv_info_list!!.layoutManager =
                    LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
            }
        }
    }

}