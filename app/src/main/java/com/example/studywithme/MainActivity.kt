package com.example.studywithme

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.example.studywithme.Board.MyboardFrag
import com.example.studywithme.Board.Write_post
import com.example.studywithme.fragment.*
import com.example.studywithme.fragment.Calendar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.bottomNavi

class MainActivity : AppCompatActivity() {

    // 프래그먼트를 변수로 저장해서 mainactivity에서 사용.
    var calendarFrag = Calendar()
    var recommendFrag = Recommend()
    var homeFrag = Home()
    var boardFrag = Board()
    var bookmarkFrag = Bookmark()
    // 상단바 제목
    var toolbarTitle : TextView?= null
    // 마지막으로 뒤로가기 버튼이 터치된 시간
    private var lastTimeBackPressed:Long = 0L

    // 메인 액티비티 실행될 때 바로 실행되는 것들. 기본으로 선택되어있는 하단바 메뉴는 action_home
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavi.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottomNavi.selectedItemId = R.id.action_home

        /* 상단에 있는 메뉴바 */
        // 액션바 대신 툴바 사용
        setSupportActionBar(swm_toolbar)
        // 기본 타이틀 없애기
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        // 액션바에 홈버튼 추가 (<- 버튼임)
        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // 상단바 제목
        toolbarTitle = findViewById(R.id.toolbar_title)
        toolbarTitle?.text = "홈"

        // 다른 앱에서 url값 전달받을 때 (북마크용)
        if (intent?.type == "text/plain") {
            var sharedLinkURL = intent.getStringExtra(Intent.EXTRA_TEXT)
            var bmFrag_for_sharedLink = Bookmark()
            var args: Bundle = Bundle(1)
            args.putString("sharedLinkURL", sharedLinkURL)
            bmFrag_for_sharedLink.arguments = args
            replaceFragment(bmFrag_for_sharedLink)
        } else if (intent?.type?.startsWith("image/") == true) {
            // 이미지 공유
        }

        /*
        if (intent.action != null){
            Log.d("응답", "인텐트 널값 아님")
            var action: String = intent.action
            var type: String = intent.type
            // 인텐트 정보가 있을 경우 다음을 실행한다.
            if (Intent.ACTION_SEND == action && type != null){
                if ("text/plain" == type) {
                    var sharedLinkURL = intent.getStringExtra(Intent.EXTRA_TEXT)
                    Log.d("다른 앱에서 url 받아오기 응답", sharedLinkURL)
                }
            }
        } else {
            Log.d("응답", "인텐트 널값임")
        }*/

    }


    // 핸드폰 뒤로 가기 버튼 클릭 시 이벤트 처리. 근데 이건 메뉴에 있는 뒤로가기가 아님. 걍 핸드폰 백 버튼임.
    override fun onBackPressed() {
        /* 사용자가 뒤로가기 버튼을 터치할 때마다 현재 시간을 저장해놓는다.
        System.currentTimeMills는 1970년 1월 1일부터 지금까지 경과한 시간을 말함.
         */
        if (System.currentTimeMillis() - lastTimeBackPressed < 1500){
            this.finish()
            return
        }
        Toast.makeText(this, "'뒤로가기' 버튼을 한번 더 누르면 어플리케이션이 종료됩니다.", Toast.LENGTH_SHORT).show()
        lastTimeBackPressed = System.currentTimeMillis()
    }




    /* 하단바에서 메뉴 선택하면 해당 프래그먼트로 replace해주는 함수를 호출
    함수프래그먼트를 다른 것으로 교체하고 이전 상태를 백 스택에 보존합니다.
    그러면 각 프래그먼트에서 뒤로가기 버튼 누르면 홈 프래그먼트로 돌아온다.*/
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.action_schedule-> {
                replaceFragment(calendarFrag)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_recommend -> {
                replaceFragment(recommendFrag)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_home -> {
                replaceFragment(homeFrag)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_board -> {
                replaceFragment(boardFrag)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_bookmark -> {
                replaceFragment(bookmarkFrag)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    /* 프래그먼트 교체하는 함수
    supportFragmentManager는 액티비티 내에서 fagmentmanager에 접근하기 위해 사용.
    프래그먼트는 트랜잭션을 통해 add, remove, replace 등을 할 수 있기 때문에 beginTransaction 호출
    commit()을 호출해야만 트랜잭션 작업을 정상적으로 수행할 수 있음. */
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment)
            .commitAllowingStateLoss()
    }

    fun onFragmentChange(index:Int) {
        if(index==1) {

            supportFragmentManager.beginTransaction().replace(R.id.content, Write_post()).commit()
        }
        else if(index==2){
            supportFragmentManager.beginTransaction().replace(R.id.content, MyboardFrag()).commit()
            bottomNavi.setSelectedItemId(R.id.action_board)
        }

    }

    fun fromAdaptertoFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.content, fragment)?.addToBackStack(null)?.commit()
    }

}
