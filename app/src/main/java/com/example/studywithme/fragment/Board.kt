package com.example.studywithme.fragment

import android.app.ActionBar
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.TextView
import com.example.studywithme.Board.BoardAdapter
import com.example.studywithme.R
import android.view.Window
import android.view.WindowManager
import android.widget.Toolbar
import com.example.studywithme.MainActivity
import com.jakewharton.rxbinding2.view.visibility
import kotlinx.android.synthetic.main.activity_main.*

class Board : Fragment() {
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       // activity!!.swm_toolbar.hideOverflowMenu()
        val view: View = inflater.inflate(R.layout.fragment_board, container, false)

        // 상단바 이름 바꾸기
       var toolbarTitle: TextView = activity!!.findViewById(R.id.toolbar_title)
        toolbarTitle.text = "Board"

            //activity!!.swm_toolbar.visibility=View.GONE

        tabLayout = view.findViewById(R.id.board_tabs)
        viewPager = view.findViewById(R.id.board_viewpager)


        val adapter = BoardAdapter(childFragmentManager)
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

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}