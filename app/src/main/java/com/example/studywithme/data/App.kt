package com.example.studywithme.data

import android.app.Application

/* Application()을 상속받는 App 클래스를 생성, onCreate 보다 먼저 prefs 초기화를 해준다.
그리고 이 액티비티의 이름을 manifest 의 <application>에 등록해준다.
* */
class App: Application() {
    /* prefs라는 이름의 SharedPreferences 하나만 생성할 수 있도록 설정 */
    companion object {
        lateinit var prefs : SharedPreferences
    }

    override fun onCreate() {
        prefs = SharedPreferences(applicationContext)
        super.onCreate()
    }

}