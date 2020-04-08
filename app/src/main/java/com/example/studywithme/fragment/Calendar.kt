
package com.example.studywithme.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.databinding.adapters.DatePickerBindingAdapter
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import com.example.studywithme.R
import com.example.studywithme.data.App

import com.example.studywithme.scheduling.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.lang.String.format
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Calendar


class Calendar : Fragment() {

    /* 골 리스트 선언*/
    var goalList = arrayListOf<Goal_list_data>()
    var itemcnt = 0
    var goalListAdapter: Goal_list_adapter? = null
    // test용 사용자 아이디는 0으로 임시 설정했음
    var userID = App.prefs.myUserIdData


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // 상단바 메뉴 쓰는 것으로 설정
        setHasOptionsMenu(true)

        // 상단바 이름 바꾸기
        var toolbarTitle: TextView = activity!!.findViewById(R.id.toolbar_title)
        toolbarTitle.text = "Schedule"


        //  골네임 받아오기
        val Goal_name = view?.findViewById<TextView>(R.id.Goal_name)
        val args = getArguments()
        if (args != null) {
            //val mArgs = arguments
            var goal_name = args?.getString("goal_name")
            Log.d("get", goal_name)
            Goal_name?.setText(goal_name)
            Log.d("Goal_name", Goal_name?.text.toString())
        }
        // 홈에서 디데이 받아오기
        val Goal_day=view?.findViewById<TextView>(R.id.Goal_day)
        val args1=getArguments()
        if (args1 != null) {
            //val mArgs = arguments
            var goal_day= args1?.getString("d_day")
            Log.d("get", goal_day)
            Goal_day?.setText("D-" + goal_day)
            Log.d("Goal_day", Goal_day?.text.toString())
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_calendar, container, false)
        // RecyclerView 사용할 것. 어댑터를 생성
        goalListAdapter = Goal_list_adapter(view.context, goalList,this)

        /* 목표 추가 다이얼로그. */
        // 익명 객체 전달. 람다 형식.
        val home_add_goal_createBtn = view.findViewById<Button>(R.id.home_add_goal)
        home_add_goal_createBtn.setOnClickListener {

            /* 지윤이꺼 토대로 한 다이얼로그 빌더*/
            // 다이얼로그 빌더
            val add_goal_dialogBuilder = AlertDialog.Builder(view.context)

            //val dialog_detailedWork_spinner = add_goal_dialogView.findViewById<Spinner>(R.id.bookmark_detailedWorkList)

            // 레이아웃 인플레이터 선언
            //val bookmark_dialog : LayoutInflater = LayoutInflater.from(this)
            // 띄울 엑티비티 안의 View 컨트롤 하기 위해 인플레이트
            val add_goal_dialogView =
                layoutInflater.inflate(R.layout.fragment_add_goal_dialog, null)
            add_goal_dialogBuilder.setView(add_goal_dialogView)

                .setPositiveButton("확인") { dialogInterface, i ->

                    var dialog_goalName_string = add_goal_dialogView.findViewById<EditText>(R.id.write_name_goal).text.toString()
                    val dialog_datepick = add_goal_dialogView.findViewById<DatePicker>(R.id.write_goal_day_goal)
                    var dialog_dday_string = format("%d-%d-%d", dialog_datepick.year , dialog_datepick.month +1, dialog_datepick.dayOfMonth)


                    //var dialog_detailedWork_string = dialog_detailedWork_spinner.selectedItem.toString()
                    /* db에 데이터 추가 */
                    add_goalData_to_DB(userID,dialog_goalName_string,dialog_dday_string)

                    //추가하기전에
                    /*dday 계산해주기
                     dday바꿔주는 부분
                     받은 dday 형식은 string이므로 이걸 date 형식으로 바꿔줌*/
                    var datechange = LocalDate.parse(dialog_dday_string, DateTimeFormatter.ISO_DATE)
                    var localdate = LocalDate.now()
                    var period = datechange.toEpochDay() - localdate.toEpochDay()
                    var cal_dday = period.toString() //dday 계산*/
                    var goal_id= -1; //일단 -1로 넣어두기

                    // 카테고리 리스트에 아이템 추가
                    var newGoalItem =
                        Goal_list_data(
                            dialog_goalName_string,
                            cal_dday,
                            goal_id
                        )
                    goalList.add(newGoalItem)
                    // 어댑터에 데이터변경사항 알리기

                    //goal_list_recyclerview.adapter?.notifyDataSetChanged()
                    goal_list_recyclerview.adapter?.notifyDataSetChanged()

                }
                .setNegativeButton("취소") { dialogInterface, i ->
                    /*취소하면 실행해줄 거 없음*/
                }
            add_goal_dialogBuilder.show()
        }

        /* 카테고리 리스트 생성 */
        //어떤 데이터(ArrayList)와 어떤 RecylcerView를 쓸 것인지 설정한다.
        val goal_list_recyclerview = view.findViewById<RecyclerView>(R.id.goal_list_recyclerview)
        goal_list_recyclerview.adapter = goalListAdapter
        /* 추가로, ListViewAdapter와는 다르게, RecylcerView Adapter에서는 레이아웃 매니저를 설정해주어야 한다.
        LayoutManager는 RecyclerView의 각 item들을 배치하고, item이 더 이상 보이지 않을 때 재사용할 것인지
        결정하는 역할을 한다. item을 재사용할 때, LayoutManager 사용을 통해 불필요한 findViewById를 수행하지 않아도 되고, 앱 성능을 향상시킬 수 있다.
        기본적으로 안드로이드에서 3가지의 LayoutManager 라이브러리를 지원한다.
        - LinearLayoutManager
        - GridLayoutManager
        - StaggeredGridLayoutManager
        이 외에도 사용자가 추상 클래스를 확장하여 임의의 레이아웃을 지정할 수도 있다.
        LinearLayoutManager는 RecylcerView를 불러올 액티비티에 LayoutManager를 추가한다.*/
        val goal_list_linearLayoutManager = LinearLayoutManager(view.context)
        goal_list_recyclerview.layoutManager = goal_list_linearLayoutManager
        /* recyclerView에 setHasFixedSize 옵션에 true값을 준다.
        왜냐하면 item이 추가되거나 삭제될 때 RecylcerView의 크기가 변경될 수도 있고, 그렇게 되면 계층 구조의
        다른 View 크기가 변경될 가능성이 있기 때문. item이 자주 추가되거나 삭제되면 오류가 날 수도 있음. */
        //goal_list_recyclerview.setHasFixedSize(true)
        // db에서 카테고리 데이터 가져오기
        getGoalList_from_DB("search")
        // if(itemcnt > 0) goal_list_recyclerview.adapter?.notifyDataSetChanged()



        return view
    }

    // 카테고리 데이터 db 추가
    fun add_goalData_to_DB(user_id: String, goal_name: String, dday: String) {
        // url 만들기
        val url = "http://203.245.10.33:8888/scheduling/insert.php"
        // 데이터를 담아 보낼 바디 만들기
        val requestBody: RequestBody = FormBody.Builder()
            .add("user_id",user_id) // user_id 일단 임의로 1으로 저장
            .add("goal_name",goal_name)
            .add("dday",dday)
            .build()
        // okhttp request를 만든다.
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        // 클라이언트 생성
        val client = OkHttpClient()
        // 요청 전송
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("요청", "요청 하이")
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d("요청", "요청 그만")
            }
        })
    }




    // db에서 php로 데이터 가져오기 (+userID 체크 추가)
    fun getGoalList_from_DB(phpName: String) {
        // url 만들기
        val url = "http://203.245.10.33:8888/scheduling/" + phpName + ".php"
        // POST로 보낼 데이터 설정
        // 데이터를 담아 보낼 바디 만들기
        val requestBody: RequestBody = FormBody.Builder()
            .add("user_id", userID) // user_id 일단 임의로 1 저장
            .build()
        // okhttp request를 만든다.
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        // 클라이언트 생성
        val client = OkHttpClient()
        // 요청 전송
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("요청 ", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                var data = response?.body?.string().toString()
                var result_array: JSONArray = JSONArray(data)
                var jsonobjCnt = result_array.length() - 1
                if (jsonobjCnt >= 0){
                    goalList.removeAll(goalList)
                    for (i in 0..jsonobjCnt) {
                        // 목표 이름
                        var goalName = result_array.getJSONObject(i).getString("goal_name").toString()
                        var dday = result_array.getJSONObject(i).getString("dday").toString()
                        var goal_id = result_array.getJSONObject(i).getString("goal_id").toInt()
                        /*if (d_day == "" && goalName == ""){
                            d_day = "입력해야해요^^"
                            goalName = "목표가 없어요"
                        }
                        else if (d_day == "" && goalName != ""){
                            d_day = "dday가 비었어요"
                        }*/
                        //if (dday =="")
                        //dday = "2020-01-01"
                        /*dday 계산해주기
                        dday바꿔주는 부분
                        받은 dday 형식은 string이므로 이걸 date 형식으로 바꿔줌*/
                        var datechange = LocalDate.parse(dday, DateTimeFormatter.ISO_DATE)
                        var localdate = LocalDate.now()
                        var period = datechange.toEpochDay() - localdate.toEpochDay()
                        var cal_dday = period.toString() //dday 계산 */
                        /* 목표 리스트에 아이템 추가 */
                        var newGoalItem =
                            Goal_list_data(
                                goalName,
                                cal_dday,
                                goal_id
                            )
                        goalList.add(newGoalItem)
                    }
                    // 백그라운드에서 돌기 때문에 메인쓰레드로 ui에 접근할 수 있도록 해줘야 한다.
                    getActivity()!!.runOnUiThread {
                        // 어댑터에 데이터변경사항 알리기
                        itemcnt = goalListAdapter!!.getItemCount()
                        goal_list_recyclerview.adapter?.notifyDataSetChanged()
                        // goallist에 아이템이 하나도 없을 경우 안내메시지 view에 출력
                        Log.d("요청 ", itemcnt.toString())

                        if (itemcnt <= 0) {
                            scheduling_goal_list_has_no_item_msg.text = "아직 목표가 없습니다. \n목표를 추가해보시죠!"
                            scheduling_goal_list_has_no_item_msg.visibility = View.VISIBLE
                        } else {
                            scheduling_goal_list_has_no_item_msg.visibility = View.GONE
                        }
                    }
                }

            }
        })

    }




    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}