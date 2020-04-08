package com.example.studywithme.scheduling

import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.studywithme.R
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import kotlinx.android.synthetic.main.fragment_calendar_view_today_acheivement.*


 class View_Today_Acheivement : Fragment() {



//     override fun onViewStateRestored(savedInstanceState: Bundle?) {
//         super.onViewStateRestored(savedInstanceState)
//         Goal_name.text = savedInstanceState?.getString("Goal_name")//미리 저장해둔 text를 복구
//     }


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

    fun timeGenerator() :String {

        //오늘 날짜 받아오려고 Calendar 클래스의 객체 생성
        val instance = Calendar.getInstance()
        //Calendar 객체의 일자 상수 얻어서 변수에 넣기
        var date = instance.get(Calendar.DATE).toString()
        return date
    }


     var today_date=timeGenerator()
     var yesterday_date_cal : Int = today_date.toInt()-1
     var yesterday_date : String = yesterday_date_cal.toString()
     var tomorrow_date_cal : Int = today_date.toInt()+1
     var tomorrow_date : String = tomorrow_date_cal.toString()





     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         val view: View = inflater.inflate(R.layout.fragment_calendar_view_today_acheivement,container, false)

         val date_view1 = view.findViewById<TextView>(R.id.date_view1)
         val date_view2 = view.findViewById<TextView>(R.id.date_view2)
         val date_view3 = view.findViewById<TextView>(R.id.date_view3)

         //progressbar
         val progress1:RoundCornerProgressBar=view.findViewById(R.id.progress_1)
         progress1.progressColor = Color.parseColor("#83cac5")
         progress1.progressBackgroundColor=Color.parseColor("#e0e0e0")
         progress1.max= 70F
         progress1.progress=15F

         val progress2:RoundCornerProgressBar=view.findViewById(R.id.progress_2)
         progress2.progressColor = Color.parseColor("#83cac5")
         progress2.progressBackgroundColor=Color.parseColor("#e0e0e0")
         progress2.max= 70F
         progress2.progress=55F


        date_view1.setText(yesterday_date)
        date_view2.setText(today_date)
        date_view3.setText(tomorrow_date)

         val Goal_name = view?.findViewById<TextView>(R.id.Goal_name)
         val Goal_day = view?.findViewById<TextView>(R.id.D_day)


         // 프래그먼트 버튼에서 할일별 달성률 프래그먼트로 연결하는 코드
         val view_todo = view.findViewById<Button>(R.id.button_view_todo_acheivement2)
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

         // 프래그먼트 버튼에서 월별별 달성률 프래그먼트로 연결하는 코드
         val view_month = view.findViewById<Button>(R.id. button_view_month_acheivement2)
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

//     override fun onSaveInstanceState(outState: Bundle) {
//         super.onSaveInstanceState(outState)
//
//         val Goal_name = view?.findViewById<TextView>(R.id.Goal_name)
//         outState.putString("Goal_name",Goal_name?.text.toString())
//         Log.d("바뀌는지", Goal_name?.text.toString())
//
//     }





}

