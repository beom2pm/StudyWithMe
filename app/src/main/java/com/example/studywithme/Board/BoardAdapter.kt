package com.example.studywithme.Board

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.studywithme.recommend.InfoFrag
import com.example.studywithme.recommend.UserFrag

class BoardAdapter(private val fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return WholeboardFrag()
            else -> return MyboardFrag()
        }
    }

    override fun getCount(): Int {
        return 2
    }

}