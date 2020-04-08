package com.example.studywithme.scheduling

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.example.studywithme.HttpConnection
import com.example.studywithme.R
import com.example.studywithme.bookmark.BookmarkActivity_category
import com.example.studywithme.data.category
import com.example.studywithme.fragment.Bookmark_link_list
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue

/*@by jiyun: today 할일 목록 관련해서 만든 파일입니다*/
class Detail_list_adapter(val context: Context, val detailList: ArrayList<Detail_list_data>, val topFragment: Fragment) :
    RecyclerView.Adapter<Detail_list_adapter.Holder>() {

    var httpConn: HttpConnection = HttpConnection()

    /* 화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성한다.*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_today_detail_list_item, parent, false)
        if (detailList.size < 1){
            LayoutInflater.from(context).inflate(R.layout.fragment_home, parent, false).findViewById<TextView>(
                R.id.home_today_list_has_no_item_msg).visibility = View.VISIBLE
        } else {
            LayoutInflater.from(context).inflate(R.layout.fragment_home, parent, false).findViewById<TextView>(
                R.id.home_today_list_has_no_item_msg).visibility = View.GONE
        }

        return Holder(view)
    }

    /* onCreateViewHolder에서 만든 View와 실제 입력되는 각각의 데이터를 연결한다.*/
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(detailList[position], context)
        holder?.todayDetailItemLayout.setOnClickListener {
            // 꾹 눌렀을 때 편집 가능하도록 해야 함.
            Log.d("홈 오늘 할일 응답", "편집 기능 추가해야함")
        }
        holder?.todayDetailCheckButton.setOnClickListener {
            // 할일 체크 버튼. 할일 체크 될 때마다 today 달성률 계산해서 그래프 변경
            // db 데이터 변경
            httpConn.update_detailedWorkData(detailList[position].detailName, detailList[position].date)
            // 프래그먼트에 있는 그래프 갱신
            topFragment.onActivityCreated(null)
        }

        if (detailList[position].done == 1){
            holder?.todayDetailCheckButton.isChecked = true
        }
    }

    /* RecyclerView로 만들어지는 item의 총 개수를 반환한다.*/
    override fun getItemCount(): Int {
        return detailList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todayDetailItemLayout = itemView?.findViewById<LinearLayout>(R.id.home_detail_list_linearLayout)
        val todayDetailName = itemView?.findViewById<TextView>(R.id.home_detail_list_item_todayDetailedWork)
        val todayDetailCheckButton = itemView?.findViewById<CheckBox>(R.id.home_check_today_detailedWork_btn)
        fun bind(todayDetail: Detail_list_data, context: Context) {
            todayDetailName?.text = todayDetail.detailName
        }
    }


}
