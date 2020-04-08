package com.example.studywithme.home

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studywithme.HttpConnection
import com.example.studywithme.MainActivity
import com.example.studywithme.R
import org.json.JSONArray

class Home_all_detailed_work_list: Fragment() {

    var activity: MainActivity? = null
    var allDetailList = arrayListOf<Detail_list_data>()
    var allDetailListAdapter: Detail_list_adapter? = null
    var allDetailListRecyclerView: RecyclerView? = null
    var allDetailListLinearLayoutManager: LinearLayoutManager? = null
    var getAllDetailResult: String? = null
    var httpConn: HttpConnection = HttpConnection()

    fun Home_today_detailed_work_list(){

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("all 프래그먼트 응답", "onAttach")
        activity = getActivity() as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("all 프래그먼트 응답", "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView = inflater.inflate(R.layout.home_all_detailed_work_list, container, false)
        allDetailListAdapter =
            this.parentFragment?.let { Detail_list_adapter(rootView.context, allDetailList, it) }
        allDetailListRecyclerView = rootView.findViewById(R.id.home_all_list)
        allDetailListLinearLayoutManager = LinearLayoutManager(rootView.context)
        Log.d("all 프래그먼트 응답", "onCreateView")
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        allDetailListRecyclerView!!.adapter = allDetailListAdapter
        allDetailListRecyclerView!!.layoutManager = allDetailListLinearLayoutManager
        allDetailListRecyclerView!!.setHasFixedSize(true)

        getAllDetailResult = httpConn.get_allDetailData()
        if (getAllDetailResult != null){
            update_allDetailList(getAllDetailResult)
        }

        Log.d("all 프래그먼트 응답", "onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        Log.d("all 프래그먼트 응답", "onResume")
    }

    override fun onDetach() {
        super.onDetach()
        activity = null
        Log.d("all 프래그먼트 응답", "onDetach")
    }


    fun update_allDetailList(resultData: String?) {
        var doneWorkCount: Int = 0
        if (resultData == "실패"){
            // 일단 보류. 딱히 할게 없음
        } else {
            var result_array: JSONArray = JSONArray(resultData)
            var jsonobj_index = result_array.length() - 1
            // db에서 가져온 데이터가 0 이상일 때만 카테고리 리스트 한번 리셋하고 리스트 업데이트
            if (jsonobj_index >= 0){
                allDetailList.removeAll(allDetailList)
                for (i in 0..jsonobj_index){
                    var allDetailName = result_array.getJSONObject(i).getString("detail_name").toString()
                    var allDetailDone = result_array.getJSONObject(i).getString("done").toInt()
                    var allDetailDate = result_array.getJSONObject(i).getString("date").toString()
                    /* 카테고리 리스트에 아이템 추가 */
                    var newDetailItem =
                        Detail_list_data(
                            allDetailName,
                            allDetailDate,
                            allDetailDone
                        )
                    allDetailList.add(newDetailItem)
                }
                // 백그라운드에서 돌기 때문에 메인쓰레드로 ui에 접근할 수 있도록 해줘야 한다.
                activity!!.runOnUiThread {
                    // 어댑터에 데이터변경사항 알리기
                    allDetailListRecyclerView!!.adapter?.notifyDataSetChanged()
                }
            }
        }

    }
}