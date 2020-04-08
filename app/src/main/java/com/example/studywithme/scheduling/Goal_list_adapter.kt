package com.example.studywithme.scheduling


import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.studywithme.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/* 어느 요소를 어느 View에 넣을 것인지 연결해주는 것이 Adapter의 역할이다.
recyclerview의 어댑터는 RecyclerView.Adapter를 extend해야 함.
근데 이 어댑터에서는 ViewHolder라는 것이 필요하다. */
class Goal_list_adapter (val context: Context, val goalList: ArrayList<Goal_list_data>,val topFragment : Fragment) :
    RecyclerView.Adapter<Goal_list_adapter.Holder>() {

    /* 화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성한다.*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.goal_list_item, parent, false)
        return Holder(view)
    }
    /* onCreateViewHolder에서 만든 View와 실제 입력되는 각각의 데이터를 연결한다.*/
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(goalList[position], context)
        holder.dday.visibility=View.GONE
        holder.goal_id.visibility=View.GONE
        holder.goalName.setOnClickListener{
            var fragment : Fragment = com.example.studywithme.scheduling.View_Todo_List()
            var bundle: Bundle = Bundle(1)
            bundle.putString("goal_name",holder?.goalName.text.toString())
            bundle.putString("d_day",holder?.dday.text.toString())
            bundle.putString("goal_id",holder?.goal_id.text.toString())

            fragment.arguments = bundle
            //링크 프래그먼트로 전환
            topFragment.fragmentManager!!.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commitAllowingStateLoss()
        }
    }
    /* RecyclerView로 만들어지는 item의 총 개수를 반환한다.*/
    override fun getItemCount(): Int {
        return goalList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /* 각 View의 이름을 정하고, findViewById를 통해 ImageView인지 TextView인지 Button인지 등 종류를 정하고
        id를 통해 layout과 연결된다.*/

        //val goalItemLayout = itemView?.findViewById<LinearLayout>(R.id.goal_list_goalName)
        val goalName = itemView.findViewById<Button>(R.id.goal_list_goalName)
        var dday = itemView.findViewById<TextView>(R.id.goal_list_dDay)
        val message = itemView.findViewById<TextView>(R.id.goal_list_dDay_message)
        var goal_id = itemView.findViewById<TextView>(R.id.goal_list_id)


        /* bind 함수는 ViewHolder와 클래스의 각 변수를 연동하는 역할을 한다. Overrdie할 함수에서 사용하게 된다.
        쉽게 말해 이쪽 TextView엔 이 String을 넣어라, 라고 지정하는 함수라고 보면 된다.*/
        fun bind(category: Goal_list_data, context: Context) {
            /*TextView와 String 데이터 연결하기*/
            goalName?.text = category.goalName
            dday?.text = category.dday
            goal_id?.text = category.goal_id.toString()

            if(category.dday.toInt()<=5) {
                message?.text = "D-" + category.dday + "  끝까지 화이팅!"
            }
            else if(category.dday.toInt()<=30){
                message?.text= "D-" + category.dday +"  한달도 안남았어요♥"
            }
            else{
                message?.text= "D-" + category.dday +"  남은기간동안 아자아자!"
            }
        }
    }
}