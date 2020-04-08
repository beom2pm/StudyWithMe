package com.example.studywithme.Profile

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.studywithme.MainActivity
import com.example.studywithme.R
import com.example.studywithme.data.UserPost
import com.example.studywithme.scheduling.Goal_list_data
import com.example.studywithme.scheduling.View_Todo_List
import kotlinx.android.synthetic.main.profile_post_item.view.*
import java.lang.invoke.ConstantCallSite


class ProfileGoalAdapter(val context: Context, val items: ArrayList<Goal_list_data>, val topFrag: Fragment): RecyclerView.Adapter<ProfileGoalAdapter.ProfileViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ProfileViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        items[position].let { item ->
            with(holder) {
                goalName.text = item.goalName
                goal_id.visibility=GONE
                dday.visibility=GONE
            }
        }

        val goal = holder.goallayout
        val activity = topFrag.activity as MainActivity

        var goalname = items[position].goalName
        var goaldday = items[position].dday
        var goalid = items[position].goal_id

        goal.setOnClickListener{
            var fragment : Fragment = View_Todo_List()
            var bundle: Bundle = Bundle(3)
            bundle.putString("goal_name",goalname)
            bundle.putString("d_day", goaldday)
            bundle.putString("goal_id",goalid.toString())

            fragment.arguments=bundle
            activity.fromAdaptertoFragment(fragment)
        }


    }

    inner class ProfileViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.goal_list_item, parent, false)) {
        val goalName = itemView.findViewById<Button>(R.id.goal_list_goalName)
        var dday = itemView.findViewById<TextView>(R.id.goal_list_dDay)
        val message = itemView.findViewById<TextView>(R.id.goal_list_dDay_message)
        var goal_id = itemView.findViewById<TextView>(R.id.goal_list_id)
        var goallayout = itemView.findViewById<ConstraintLayout>(R.id.goal_layout)
    }

}
