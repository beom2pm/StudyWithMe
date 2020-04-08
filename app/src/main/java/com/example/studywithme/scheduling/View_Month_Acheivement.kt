package com.example.studywithme.scheduling

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.example.studywithme.R
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_calendar_view_month_acheivement.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class View_Month_Acheivement : Fragment() {


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
            Log.d("Goal_name_month", Goal_name?.text.toString())
        }
        // 캘린더에서 디데이 받아오기
        val Goal_day=view?.findViewById<TextView>(R.id.D_day_month)
        val args1=getArguments()
        if (args1 != null) {
            //val mArgs = arguments
            var goal_day= args1?.getString("d_day")
            Log.d("get", goal_day)
            D_day_month?.setText(goal_day)
            Log.d("Goal_day_month", Goal_day?.text.toString())
        }
    }


    //년도 계산 함수
    fun yearGenerator() :String {

        //오늘 년도 받아오려고 Calendar 클래스의 객체 생성
        val instance = Calendar.getInstance()
        //Calendar 객체의 년도 상수 얻어서 변수에 넣기
        var year = instance.get(Calendar.YEAR).toString()
        return year
    }

    //월 계산 함수
    fun monthGenerator() :String {

        //오늘 년도 받아오려고 Calendar 클래스의 객체 생성
        val instance = Calendar.getInstance()
        //Calendar 객체의 월 상수 얻어서 변수에 넣기
        var month = instance.get(Calendar.MONTH).toString()
        return month
    }


    var today_year=yearGenerator()
    var today_month_to_cal=monthGenerator()
    var today_month_cal : Int = today_month_to_cal.toInt()+1
    var today_month : String = today_month_cal.toString()
    var last_month_cal : Int = today_month.toInt()-1
    var last_month : String = last_month_cal.toString()
    var next_month_cal : Int = today_month.toInt()+1
    var next_month : String = next_month_cal.toString()


    //dday 계산 연습
    var practice_data ="2019-11-08"
    var change_data = LocalDate.parse(practice_data, DateTimeFormatter.ISO_DATE)
    var localdate = LocalDate.now()
    var period = change_data.toEpochDay() - localdate.toEpochDay()





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_calendar_view_month_acheivement, container, false)


        val monthandyear_view_now = view.findViewById<TextView>(R.id.monthandyear_view_now)
        val month_view_last = view.findViewById<TextView>(R.id. month_view_last)
        val month_view_next = view.findViewById<TextView>(R.id.month_view_next)
        val no_meaning = view.findViewById<TextView>(R.id.no_meaning)

        monthandyear_view_now.setText(today_year+"년 "+today_month+"월")
        month_view_last.setText("◀◀"+last_month+"월")
        month_view_next.setText(next_month+"월"+"▶▶")
        no_meaning.setText(period.toString())

        val Goal_name = view?.findViewById<TextView>(R.id.Goal_name)
        val Goal_day = view?.findViewById<TextView>(R.id.D_day_month)

        // 프래그먼트 버튼에서 할일별 달성률 프래그먼트로 연결하는 코드
        val view_todo = view.findViewById<Button>(R.id.button_view_todo_acheivement3)
        view_todo.setOnClickListener {
            val fragment1 = View_Todo_Acheivement() // Fragment 생성
            var bundle1: Bundle = Bundle(1)
            bundle1.putString("goal_name",Goal_name.text.toString())
            bundle1.putString("d_day",Goal_day.text.toString())
            Log.d("Goal_name_pass1", Goal_name.text.toString())
            fragment1.arguments = bundle1
            val fm = fragmentManager
            val fmt = fm?.beginTransaction()
            fmt?.replace(R.id.content, fragment1)?.addToBackStack(null)?.commit()
        }

        // 프래그먼트 버튼에서 오늘 달성률 프래그먼트로 연결하는 코드
        val view_today = view.findViewById<Button>(R.id.button_view_today_acheivement3)
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

