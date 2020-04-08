package com.example.studywithme.DialogFragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.studywithme.HttpConnection
import com.example.studywithme.R
import com.jakewharton.rxbinding2.widget.RxTextView
import org.json.JSONArray
import java.lang.String.format
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DialogAddTodayDetailedWork: DialogFragment() {

    private var parent_fragment: Fragment? = null
    var fragmentContext: Context? = null
    var args: Bundle? = null
    var todayDate: String = ""
    var detailedWorkName: EditText? = null
    var detailedWorkYear: EditText? = null
    var detailedWorkMonth: EditText? = null
    var detailedWorkDay: EditText? = null
    var detailedWorkYear_string: String = ""
    var detailedWorkMonth_string: String = ""
    var detailedWorkDay_string: String = ""
    var detailedWorkDate_string: String = ""
    var positiveButton: Button? = null
    var negativeButton: Button? = null
    var httpConn: HttpConnection = HttpConnection()

    fun DialogAddTodqyDetailedWork() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var currentTime = LocalDate.now()
        todayDate = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.home_popup_create_detailed_work, container, false)
        fragmentContext = container!!.context
        detailedWorkName = view.findViewById(R.id.detailed_work)
        detailedWorkYear = view.findViewById(R.id.detailed_work_date_year)
        detailedWorkMonth = view.findViewById(R.id.detailed_work_date_month)
        detailedWorkDay = view.findViewById(R.id.detailed_work_date_day)
        positiveButton = view.findViewById(R.id.detailed_work_dialog_positive)
        negativeButton = view.findViewById(R.id.detailed_work_dialog_negative)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setOnClickedListener()
        // date 유효성 검사 때문에 positiveButton은 일단 비활성하시켜놓기
        positiveButton!!.isEnabled = false
        // date 유효성 검사를 위한 바인딩 이벤트 붙이기
        checkDateIsAvailable()
    }

    private fun setOnClickedListener(){
        positiveButton!!.setOnClickListener {
            detailedWorkDate_string = detailedWorkYear_string + "-" + detailedWorkMonth_string + "-" + detailedWorkDay_string
            Log.d("오늘할일 추가 다이얼로그 응답", detailedWorkName!!.text.toString())
            Log.d("오늘할일 추가 다이얼로그 응답", detailedWorkDate_string)
            /* db에 데이터 추가 */
            var result_temp = httpConn.add_detailedWorkData(detailedWorkName!!.text.toString(), detailedWorkDate_string).toString()
            if (result_temp.contains("0")){
                Toast.makeText(fragmentContext, "이미 오늘 등록된 할일입니다!", Toast.LENGTH_LONG).show()
            }
            fragmentManager!!.beginTransaction().remove(this).commitAllowingStateLoss()
            fragmentManager!!.popBackStack()
        }
        negativeButton!!.setOnClickListener {
            Log.d("오늘할일 추가 다이얼로그 응답", "취소")
            fragmentManager!!.beginTransaction().remove(this).commitAllowingStateLoss()
            fragmentManager!!.popBackStack()
        }
    }

    // 오늘 날짜를 기준으로 이전 날짜를 입력하면 오류 메시지 출력
    fun checkDateIsAvailable() {
        detailedWorkYear?.let {
            RxTextView.textChanges(it)
                .subscribe{
                    detailedWorkYear_string = detailedWorkYear!!.text.toString()
                    positiveButton!!.isEnabled = isAllDataVaild()
                    if (!isYearVaild(detailedWorkYear_string)){
                        detailedWorkYear!!.error = "날짜는 오늘을 기준으로 이후 날짜만 입력 가능합니다."
                    }
                }
        }

        detailedWorkMonth?.let {
            RxTextView.textChanges(it)
                .subscribe{
                    detailedWorkMonth_string = detailedWorkMonth!!.text.toString()
                    positiveButton!!.isEnabled = isAllDataVaild()
                    if (!isMonthVaild(detailedWorkMonth_string)){
                        detailedWorkMonth!!.error = "입력 형식이 올바르지 않습니다."
                    }
                }
        }

        detailedWorkDay?.let {
            RxTextView.textChanges(it)
                .subscribe{
                    detailedWorkDay_string = detailedWorkDay!!.text.toString()
                    positiveButton!!.isEnabled = isAllDataVaild()
                    if (!isDayVaild(detailedWorkDay_string)){
                        detailedWorkDay!!.error = "입력 형식이 올바르지 않습니다."
                    }
                }
        }

        detailedWorkName?.let {
            RxTextView.textChanges(it)
                .subscribe{
                    positiveButton!!.isEnabled = isAllDataVaild()
                }
        }
    }

    fun isAllDataVaild(): Boolean {
        var tempDateString = detailedWorkYear_string + "-" + detailedWorkMonth_string + "-" + detailedWorkDay_string
        return (isYearVaild(detailedWorkYear_string) &&
                isMonthVaild(detailedWorkMonth_string) &&
                isDayVaild(detailedWorkDay_string) &&
                (todayDate <= tempDateString) &&
                (detailedWorkName!!.text.isNotBlank())
                )
    }

    fun isYearVaild(year: String): Boolean {
        var temp = LocalDate.now()
        var temp_year = temp.format(DateTimeFormatter.ofPattern("yyyy"))
        return year != "" && year.length == 4 && year >= temp_year
    }

    fun isMonthVaild(month: String): Boolean{
        var temp_result = false
        var month_string = "0"
        var temp = LocalDate.now()
        var temp_year = temp.format(DateTimeFormatter.ofPattern("yyyy"))
        var temp_month = temp.format(DateTimeFormatter.ofPattern("MM"))
        if (month != "") month_string = month
        if (temp_year == detailedWorkYear_string){
            if (temp_month.toInt() <= month_string.toInt() ){
                temp_result = true
            }
        } else {
            temp_result = true
        }
        return month != "" && (month_string.toInt() in 1..12) && temp_result
    }

    fun isDayVaild(day: String): Boolean {
        var temp_result = false
        var day_string = "0"
        var temp = LocalDate.now()
        var temp_year = temp.format(DateTimeFormatter.ofPattern("yyyy"))
        var temp_month = temp.format(DateTimeFormatter.ofPattern("MM"))
        var temp_day = temp.format(DateTimeFormatter.ofPattern("dd"))
        if (day != "") day_string = day
        if (temp_year == detailedWorkYear_string && temp_month == detailedWorkMonth_string){
            if (temp_day.toInt() <= day_string.toInt()){
                temp_result = true
            }
        } else {
            temp_result = true
        }
        return if (day != "" && temp_result){
            var temp_date = day_string.toInt()
            when(detailedWorkMonth_string){
                "2" -> temp_date in 1..28
                "4", "6", "9", "11" -> temp_date in 1..30
                else -> {
                    temp_date in 1..31
                }
            }
        } else {
            false
        }
    }



}