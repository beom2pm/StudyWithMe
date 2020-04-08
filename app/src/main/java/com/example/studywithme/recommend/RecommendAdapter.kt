package com.example.studywithme.recommend

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.os.Bundle
import android.support.v4.app.FragmentStatePagerAdapter
import android.os.Parcelable

class RecommendAdapter(private val fm: FragmentManager, val goal: String) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                var fragment = InfoFrag()
                val bundle = Bundle(1) // 파라미터는 전달할 데이터 개수
                bundle.putString("chosenGoal", goal) // key , value
                fragment.arguments=bundle
                return fragment
            }
            else -> {
                var fragment = UserFrag()
                val bundle = Bundle(1) // 파라미터는 전달할 데이터 개수
                bundle.putString("chosenGoal", goal) // key , value
                fragment.arguments=bundle
                return fragment
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun restoreState(arg0: Parcelable?, arg1: ClassLoader?) {
        //do nothing here! no call to super.restoreState(arg0, arg1);
    }


}