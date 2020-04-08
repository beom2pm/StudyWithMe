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
import com.example.studywithme.bookmark.BookmarkActivity_category
import org.json.JSONArray

class DialogAddCategory : DialogFragment(){

    private var parent_fragment: Fragment? = null
    var fragmentContext: Context? = null
    var args: Bundle? = null
    var positiveButton: Button? = null
    var negativeButton: Button? = null
    var dialogBookmarkGoalSpinner: Spinner? = null
    var goalList_array = arrayListOf<String>()
    var getGoalResult: String? = null
    var goalList_spinner: Spinner? = null
    var httpConn: HttpConnection = HttpConnection()

    fun DialogAddCategory() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.bookmark_popup_createcategory, container, false)
        fragmentContext = container!!.context
        positiveButton = view.findViewById(R.id.category_dialog_positive)
        negativeButton = view.findViewById(R.id.category_dialog_negative)
        // 레이아웃 인플레이터 선언
        // 띄울 엑티비티 안의 View 컨트롤 하기 위해 인플레이트
        dialogBookmarkGoalSpinner = view.findViewById(R.id.bookmark_bookmarkGoalList)
        goalList_spinner = view.findViewById(R.id.bookmark_bookmarkGoalList)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setOnClickedListener()
        getGoalResult = httpConn.get_bookmarkGoalData()
        if (getGoalResult != null){
            update_goalDataList(getGoalResult)
        }
    }

    private fun setOnClickedListener(){
        positiveButton!!.setOnClickListener {
            Log.d("카테고리 다이얼로그 응답", "확인")
            var categoryName_string = view!!.findViewById<EditText>(R.id.bookmark_categoryName).text.toString()
            var bookmarkGoal_string = dialogBookmarkGoalSpinner!!.selectedItem.toString()
            Log.d("카테고리 다이얼로그 응답", bookmarkGoal_string)
            /* db에 데이터 추가 */
            var result_temp = httpConn.add_categoryData(categoryName_string, bookmarkGoal_string).toString()
            Log.d("카테고리 다이얼로그 응답",result_temp)
            if (result_temp.contains("0")){
                Toast.makeText(fragmentContext, "이미 있는 카테고리 이름입니다!", Toast.LENGTH_LONG).show()
            }
            fragmentManager!!.beginTransaction().remove(this).commitAllowingStateLoss()
            fragmentManager!!.popBackStack()
        }
        negativeButton!!.setOnClickListener {
            Log.d("카테고리 다이얼로그 응답", "취소")
            fragmentManager!!.beginTransaction().remove(this).commitAllowingStateLoss()
            fragmentManager!!.popBackStack()
        }
    }

    fun update_goalDataList(resultData: String?) {
        if (resultData == "실패") {
            return
        } else {
            var result_array: JSONArray = JSONArray(resultData)
            var jsonobj_index = result_array.length() - 1
            if (jsonobj_index >= 0) {
                goalList_array.removeAll(goalList_array)
                goalList_array.add("연결된 세부 목표 없음")
                for (i in 0..jsonobj_index){
                    var goal_name = result_array.getJSONObject(i).getString("goal_name")
                    goalList_array.add(goal_name)
                }
            }
            set_goalSpinner(goalList_array)
        }

    }

    fun set_goalSpinner(itmeArray: ArrayList<String>){
        // spinner에 필요한 arrayadapter 객체 생성
        var spinnerAdapter = ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, itmeArray)
        var spinner = view!!.findViewById<Spinner>(R.id.bookmark_bookmarkGoalList)
        // spinner 객체에 arrayadapter 세팅
        spinner.adapter = spinnerAdapter
        // spinner에 이벤트 리스너 등록
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

        }
    }

}

