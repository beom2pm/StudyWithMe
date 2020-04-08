package com.example.studywithme.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

/* 앱의 어디에서든 전역적으로 접근 가능하게 만드려면 새로 클래스를 생성해야함.
1. SharedPreferences Class 생성
2. App Class 생성해서 SharedPreferences 를 가장 먼저 쓸 수 있도록 설정
3. manifest에 App Class 이름 등록

!주의!
이 SharedPreferences 클래스는 앱에 있는 다른 액티비티보다 먼저 생성되어야 다른 곳에 데이터를 넘겨줄 수 있다.
이 설정을 해주기 위해서는 App에 해당되는 Class 파일 또한 생성해주어야 함.
* */

class SharedPreferences(context: Context) {

    /*파일 이름과 login정보를 저장할 key값을 만들고 prefs인스턴스 초기화*/
    val PREFS_FILENAME = "prefs"
    val PREF_KEY_MY_USERNAME = "myUserNameData"
    val PREF_KEY_MY_PASSWORD = "myPasswordData"
    val PREF_KEY_MY_USERID = "myUserIdData"

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, MODE_PRIVATE)
    val editor = prefs.edit()

    /* get/set 함수 임의 설정. get 실행 시 저장된 값을 반환하며 default 값은 ""
    * set(value) 실행 시 값을 대체한 후 저장 */
    var myUserNameData: String
        get() = prefs.getString(PREF_KEY_MY_USERNAME, "")
        set(value) = prefs.edit().putString(PREF_KEY_MY_USERNAME, value).apply()

    var myPasswordData: String
        get() = prefs.getString(PREF_KEY_MY_PASSWORD, "")
        set(value) = prefs.edit().putString(PREF_KEY_MY_PASSWORD, value).apply()

    var myUserIdData: String
        get() = prefs.getString(PREF_KEY_MY_USERID, "")
        set(value) = prefs.edit().putString(PREF_KEY_MY_USERID, value).apply()

}