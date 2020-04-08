package com.example.studywithme.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.*
import android.widget.TextView
import com.example.studywithme.R
import com.example.studywithme.data.App
import com.example.studywithme.recommend.RecommendAdapter
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.lang.IndexOutOfBoundsException
import android.net.http.SslCertificate.restoreState
import android.os.Parcelable
import android.support.v4.app.FragmentManager
import com.example.studywithme.data.Goal


class Recommend : Fragment() {

    val goalList = ArrayList<String>()
    val userid:String = App.prefs.myUserIdData
    var chosenGoal:String = ""
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var goal=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* goal 중복방지 */
        goalList.clear()

        val goal_url = "http://203.245.10.33:8888/recommend/getGoalList.php"
        val goal_client = OkHttpClient()

        val goal_requestBody: RequestBody = FormBody.Builder()
            .add("user_id", userid) // 사용자 id
            .build()

        val goal_request = Request.Builder()
            .url(goal_url)
            .post(goal_requestBody)
            .build()

        goal_client.newCall(goal_request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("응답 fail", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                var goal_result_array: JSONArray = JSONArray(response.body!!.string())
                var size: Int = goal_result_array.length() - 1
                for (i in 0..size) {
                    var goal = goal_result_array.getJSONObject(i).getString("goal_name")
                    goalList.add(goal)
                }
            }
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_recommend, container, false)

        // 상단바 이름 바꾸기
        setHasOptionsMenu(true)

        tabLayout = view.findViewById(R.id.recommend_tabs)
        viewPager = view.findViewById(R.id.recommend_viewpager)


        try{
            chosenGoal = goalList[0]
        }
        catch(e: IndexOutOfBoundsException){
        }



        return view
    }


    private fun chooseGoal(goal: String){
        getActivity()?.runOnUiThread {
            viewPager!!.adapter?.notifyDataSetChanged()
        }


        val adapter = RecommendAdapter(childFragmentManager, goal)

        viewPager!!.adapter = adapter
        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var toolbarTitle: TextView = activity!!.findViewById(R.id.toolbar_title)
        toolbarTitle.text = "추천"
        chooseGoal(chosenGoal)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.search_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.select_goal -> {
                val dialog = AlertDialog.Builder(context)
                val array = arrayOfNulls<String>(goalList.size)
                dialog
                    .setTitle("목표를 선택하세요")
                    .setSingleChoiceItems(
                        goalList.toArray(array),
                        0,
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            chosenGoal = goalList[i]
                        })
                    .setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            chooseGoal(chosenGoal)
                        })
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }


}