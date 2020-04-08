package com.example.studywithme.scheduling

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import com.example.studywithme.R
import com.example.studywithme.fragment.Calendar
import com.github.mikephil.charting.charts.HorizontalBarChart
import kotlinx.android.synthetic.main.fragment_calendar_view_todo_acheivement.*

class View_Todo_Acheivement : Fragment() {


    lateinit var skillRatingChart : HorizontalBarChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        // 상단바 이름 바꾸기
        var toolbarTitle: TextView = activity!!.findViewById(R.id.toolbar_title)
        toolbarTitle.text = "Schedule"
        // 캘린더에서 골네임 받아오기
        val Goal_name = view?.findViewById<TextView>(R.id.Goal_name)
        val args = getArguments()
        if (args != null) {
            //val mArgs = arguments
            var goal_name = args?.getString("goal_name")
            Log.d("get", goal_name)
            Goal_name?.setText(goal_name)
            Log.d("Goal_name_today", Goal_name?.text.toString())
        }
        // 캘린더에서 디데이 받아오기
        val Goal_day=view?.findViewById<TextView>(R.id.D_day)
        val args1=getArguments()
        if (args1 != null) {
            //val mArgs = arguments
            var goal_day= args1?.getString("d_day")
            Log.d("get", goal_day)
            Goal_day?.setText(goal_day)
            Log.d("Goal_day_today", Goal_day?.text.toString())
        }
    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_calendar_view_todo_acheivement, container, false)

        //progressbar
        val progress1: RoundCornerProgressBar =view.findViewById(R.id.progress_1)
        progress1.progressColor = Color.parseColor("#83cac5")
        progress1.progressBackgroundColor= Color.parseColor("#e0e0e0")
        progress1.max= 70F
        progress1.progress=15F

        val progress2: RoundCornerProgressBar =view.findViewById(R.id.progress_2)
        progress2.progressColor = Color.parseColor("#83cac5")
        progress2.progressBackgroundColor= Color.parseColor("#e0e0e0")
        progress2.max= 70F
        progress2.progress=55F


        val Goal_name = view?.findViewById<TextView>(R.id.Goal_name)
        val Goal_day = view?.findViewById<TextView>(R.id.D_day)

        // 프래그먼트 버튼에서 오늘 달성률 프래그먼트로 연결하는 코드
        val view_today = view.findViewById<Button>(R.id.button_view_today_acheivement1)
        view_today.setOnClickListener {
            val fragment = View_Today_Acheivement() // Fragment 생성
            var bundle1: Bundle = Bundle(1)
            bundle1.putString("goal_name",Goal_name.text.toString())
            bundle1.putString("d_day",Goal_day.text.toString())
            fragment.arguments = bundle1
            val fm = fragmentManager
            val fmt = fm?.beginTransaction()
            fmt?.replace(R.id.content, fragment)?.addToBackStack(null)?.commit()
        }

        // 프래그먼트 버튼에서 월별별 달성률 프래그먼트로 연결하는 코드
        val view_month = view.findViewById<Button>(R.id. button_view_month_acheivement1)
        view_month.setOnClickListener {
            val fragment = View_Month_Acheivement() // Fragment 생성
            var bundle1: Bundle = Bundle(1)
            bundle1.putString("goal_name",Goal_name.text.toString())
            bundle1.putString("d_day",Goal_day.text.toString())
            Log.d("Goal_name_pass2", Goal_name.text.toString())
            fragment.arguments = bundle1
            val fm = fragmentManager
            val fmt = fm?.beginTransaction()
            fmt?.replace(R.id.content, fragment)?.addToBackStack(null)?.commit()
        }

        return view


    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            //뒤로가기 버튼 클릭 시
            android.R.id.home -> {
                fragmentManager!!.beginTransaction()
                    .replace(R.id.content, View_Todo_List())
                    .commitAllowingStateLoss()
            }
            R.id.search -> {

            }
            R.id.edit -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }


}