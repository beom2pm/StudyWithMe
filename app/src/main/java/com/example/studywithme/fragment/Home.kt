package com.example.studywithme.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.example.studywithme.DialogFragment.DialogAddTodayDetailedWork
import com.example.studywithme.HttpConnection
import com.example.studywithme.MainActivity
import com.example.studywithme.R
import com.example.studywithme.data.App
import com.example.studywithme.home.*
import com.jakewharton.rxbinding2.support.design.widget.select
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.view.PieChartView
import okhttp3.internal.notifyAll
import org.json.JSONArray


class Home : Fragment() {

    var activity: MainActivity? = null
    val userID = App.prefs.myUserIdData
    val userName = App.prefs.myUserNameData
    var todayDetailedWorkPieChart: PieChartView? = null
    var pieData = arrayListOf<SliceValue>()
    var pieChartData: PieChartData? = null
    var todayDetailCreateBtn: Button? = null
    var todayDetailList = arrayListOf<Detail_list_data>()
    var getTodayDetailResult: String? = null
    var httpConn: HttpConnection = HttpConnection()
    var homeViewPager: ViewPager? = null
    var home_tabLayout: TabLayout? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        // 상단바 메뉴 쓰는 것으로 설정
        setHasOptionsMenu(true)

        // 상단바 이름 바꾸기
        var toolbarTitle: TextView = activity!!.findViewById(R.id.toolbar_title)
        toolbarTitle.text = "StudyWithMe"

        todayDetailCreateBtn = view.findViewById<Button>(R.id.home_create_today_detailedWork_btn)
        todayDetailedWorkPieChart = view.findViewById(R.id.home_today_detailedWork_piechart)

        homeViewPager = view.findViewById(R.id.home_today_list_view_pager)
        home_tabLayout = view.findViewById(R.id.home_detail_list_tab)

        home_tabLayout!!.addTab(home_tabLayout!!.newTab().setText("Today"))
        home_tabLayout!!.addTab(home_tabLayout!!.newTab().setText("All"))
        Log.d("탬 응답1", home_tabLayout!!.selectedTabPosition.toString())
        home_tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        // today 또는 all 할일 리스트 뷰페이저
        val home_view_pager_adapter = Home_view_pager_adapter(childFragmentManager)
        homeViewPager!!.adapter = home_view_pager_adapter
        homeViewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(home_tabLayout))
        home_tabLayout!!.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab) {
                homeViewPager!!.adapter?.notifyDataSetChanged()
                homeViewPager!!.currentItem = p0.position
            }
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 클릭 이벤트 리스너 붙이기
        setOnClickedListener()

        getTodayDetailResult = httpConn.get_todayDetailData()
        if (getTodayDetailResult != null){
            update_today_pieChart(getTodayDetailResult)
        }

    }


    override fun onDetach() {
        super.onDetach()
        activity = null
    }


    private fun setOnClickedListener() {
        todayDetailCreateBtn!!.setOnClickListener {
            show_createTodayDetailDialog()
        }
    }

    // todo리스트 추가 다이얼로그 띄우는 함수
    private fun show_createTodayDetailDialog(){
        var dialog: DialogAddTodayDetailedWork = DialogAddTodayDetailedWork()
        this.fragmentManager!!.beginTransaction()
            .replace(R.id.content, dialog)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // menu에 있는 메뉴 클릭 시 이벤트 처리
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.home_profile -> {
                val fragment = Profile()
                val bundle = Bundle(1)
                bundle.putString("userid", userID)
                fragment.arguments=bundle
                val fm = fragmentManager
                val fmt = fm?.beginTransaction()
                fmt?.replace(R.id.content, fragment)?.addToBackStack(null)?.commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun update_today_pieChart(resultData: String?) {
        var doneWorkCount: Int = 0
        if (resultData == "실패"){
            // 일단 보류. 딱히 할게 없음
        } else {
            var result_array: JSONArray = JSONArray(resultData)
            var jsonobj_index = result_array.length() - 1
            // db에서 가져온 데이터가 0 이상일 때만 카테고리 리스트 한번 리셋하고 리스트 업데이트
            if (jsonobj_index >= 0){
                todayDetailList.removeAll(todayDetailList)
                for (i in 0..jsonobj_index){
                    var todayDetailDone = result_array.getJSONObject(i).getString("done").toInt()
                    if (todayDetailDone == 1){
                        doneWorkCount++
                        Log.d("응답", doneWorkCount.toString())
                    }
                }
            }
            // 파이 차트 갱신
            var done_rate = ( doneWorkCount.toFloat() / result_array.length() ) * 100
            Log.d("응답", done_rate.toString())
            changeTodayPieChart(done_rate)
        }

    }

    fun changeTodayPieChart(doneRate: Float){
        Log.d("파이 차트 갱신 응답", doneRate.toString())
        if (pieData.size > 0){
            pieData.removeAll(pieData)
        }
        // 오늘 할일 관련 달성률 데이터 (list에 들어가는 데이터 값은 두가지 달성한거, 달성하지 못한거)
        pieData.add(SliceValue(doneRate, Color.parseColor("#83cac8")))
        pieData.add(SliceValue(100 - doneRate,Color.parseColor("#e0e0e0") ))

        pieChartData = PieChartData(pieData)
        pieChartData!!.setHasCenterCircle(true).setCenterText1(doneRate.toInt().toString())
        todayDetailedWorkPieChart!!.pieChartData = pieChartData
    }


}