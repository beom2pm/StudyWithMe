package com.example.studywithme.signup

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.util.AttributeSet
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.load.model.ByteBufferEncoder
import com.example.studywithme.R
import com.example.studywithme.R.id
import com.example.studywithme.ui.login.LoginActivity
import com.jakewharton.rxbinding2.widget.RxTextView
import com.jakewharton.rxbinding2.widget.textChanges
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.view.*
import okhttp3.*
import okio.Utf8
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.util.regex.Pattern


class SignUpActivity: AppCompatActivity() {

    private var RegisterURL="http://203.245.10.33:8888/signup/register.php"
    var id_sign_string: String = ""
    var password_sign_string: String = ""
    var name_sign_string: String = ""
    val loading: ProgressBar? = null
    var isUserIdDuplicate: Boolean = false
    var isUserNameDuplicate: Boolean = false
    var signUp_view: View? = null
    var signUp_dialogBuilder: AlertDialog.Builder? = null
    var signUp_authentication_dialogView: View? = null
    var login_view: View? = null
    var login_intent: Intent? = null

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val id_sign: EditText = findViewById(id.id_sign)
        val password_sign: EditText = findViewById(id.password_sign)
        val name_sign: EditText = findViewById(id.name_sign)
        val btn_signup: Button = findViewById(id.btn_signup)
        signUp_view = layoutInflater.inflate(R.layout.activity_sign_up, null)
        signUp_dialogBuilder = AlertDialog.Builder(signUp_view?.context)
        signUp_authentication_dialogView = layoutInflater.inflate(R.layout.signup_popup_for_authentication, null)
        login_view = layoutInflater.inflate(R.layout.activity_login, null)
        login_intent= Intent(this, LoginActivity::class.java)

        // 각 editText에 값 입력될 때마다 실행
        RxTextView.textChanges(id_sign)
            .subscribe{
                id_sign_string = id_sign.text.toString()
                btn_signup.isEnabled = isAllDataValid()
                if ( !isIdValid(id_sign_string) ){
                    id_sign.error = "올바른 이메일 형식이 아닙니다."
                } else {
                    Log.d("응답 테스트", id_sign_string)
                }
            }
        RxTextView.textChanges(password_sign)
            .subscribe{
                password_sign_string = password_sign.text.toString()
                btn_signup.isEnabled = isAllDataValid()
                if ( !isPassWordValid(password_sign_string) ) {
                    password_sign.error = "비밀번호는 알파벳과 숫자를 포함하고 7글자 이상이어야 합니다."
                } else {
                    Log.d("응답 테스트", password_sign_string)
                }
            }
        RxTextView.textChanges(name_sign)
            .subscribe{
                name_sign_string = name_sign.text.toString()
                btn_signup.isEnabled = isAllDataValid()
                if ( (name_sign_string == "") && (name_sign_string == null) ){
                    password_sign.error = "계정 이름을 입력해주세요."
                } else {
                    Log.d("응답 테스트", name_sign_string)
                }
            }


        // 회원 가입 버튼 클릭 이벤트
        btn_signup.setOnClickListener{
            // id나 user가 중복되지 않는지 체크
            checkDuplicateUser_and_showResult()
        }


    }


    // 각 editText 입력 값 체크 함수들
    private fun isIdValid(id: String): Boolean{
        return if ( (id.contains('@')) && (id.isNotBlank()) ) {
            Patterns.EMAIL_ADDRESS.matcher(id).matches()
        } else {
            false
        }
    }

    private fun isPassWordValid(password: String): Boolean {
        return (password.length > 6) && (password.contains(Regex("[0-9]"))) && Pattern.compile("^[a-zA-Z0-9]+$").matcher(password_sign_string).matches()
    }

    private fun isAllDataValid(): Boolean {
        Log.d("응답 테스트", id_sign_string + " " + password_sign_string + " "+ name_sign_string)
        return isIdValid(id_sign_string) && isPassWordValid(password_sign_string) && (name_sign_string!="" && name_sign_string!=null)

    }

    private fun checkDuplicateUser_and_showResult() {
        // url 만들기
        val url="http://203.245.10.33:8888/signup/checkDuplicateUser.php"
        // 데이터를 담아 보낼 바디 만들기
        val requestBody : RequestBody = FormBody.Builder()
            .add("user_id", id_sign_string)
            .add("user_name", name_sign_string)
            .build()
        // okhttp request를 만든다.
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        // 클라이언트 생성
        val client = OkHttpClient()
        // 요청 전송
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                var data = response?.body?.string().toString()
                var result_array = JSONObject(data)
                isUserIdDuplicate = result_array.getString("user_id").toBoolean()
                isUserNameDuplicate = result_array.getString("user_name").toBoolean()
                Log.d("응답", isUserIdDuplicate.toString() + " " + isUserNameDuplicate.toString())

                // show result
                if (isUserIdDuplicate || isUserNameDuplicate){
                    runOnUiThread{
                        showLoginFailedMsg()
                    }
                } else {
                    // smtp로 랜덤 숫자 사용자 이메일로 전송
                    send_authenticateNum(id_sign_string, name_sign_string)
                }
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.d("요청", "요청 실패")
            }
        })
    }

    private fun send_authenticateNum(user_emailAddress: String, user_name: String){
        // url 만들기
        val url="http://203.245.10.33:8888/signup/sendAuthenticateString.php"
        // 데이터를 담아 보낼 바디 만들기
        val requestBody : RequestBody = FormBody.Builder()
            .add("user_name", user_name)
            .add("user_emailAddress", user_emailAddress)
            .build()
        // okhttp request를 만든다.
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        // 클라이언트 생성
        val client = OkHttpClient()
        // 요청 전송
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string().toString()
                val stringForAuth = data.substring(data.length - 6, data.length)
                Log.d("응답", "인증번호 전송 완료 " + stringForAuth)
                show_authentication_dialog(stringForAuth)
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.d("요청", "요청 실패")
            }
        })
    }

    private fun show_authentication_dialog(string_for_auth: String){
        runOnUiThread {
            signUp_dialogBuilder
                ?.setView(signUp_authentication_dialogView)
                ?.setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                    var signUp_authentication_string = signUp_authentication_dialogView?.findViewById<EditText>(R.id.signup_authentication_number)?.text.toString()
                    if (string_for_auth == signUp_authentication_string){
                        // 데이터 db에 추가
                        insertUserDataToDB(id_sign_string, password_sign_string, name_sign_string)
                    }
                }
                ?.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->
                    Toast.makeText(signUp_view?.context, "회원가입이 취소되셨습니다.", Toast.LENGTH_LONG).show()
                }
                ?.show()
        }
    }

    private fun insertUserDataToDB(user_emailAddress: String, user_password: String, user_name: String){
        // url 만들기
        val url="http://203.245.10.33:8888/signup/insert_userData.php"

        // 데이터를 담아 보낼 바디 만들기
        val requestBody : RequestBody = FormBody.Builder()
            .add("user_password", user_password)
            .add("user_emailAddress", user_emailAddress)
            .add("user_name", user_name)
            .build()
        // okhttp request를 만든다.
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        // 클라이언트 생성
        val client = OkHttpClient()
        // 요청 전송
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("응답", "db추가완료")
                // 로그인 화면으로 이동하고 갸입 성공했다는 toast메시지 띄우기
                startActivity(login_intent)
            }
            override fun onFailure(call: Call, e: IOException) {
                Log.d("요청", "요청 실패")
            }
        })
    }

    private fun showLoginFailedMsg(){
        if (isUserIdDuplicate){
            id_sign.error = "이미 회원가입 완료된 이메일 계정입니다."
        }
        if (isUserNameDuplicate){
            name_sign.error = "이미 있는 사용자 이름입니다."
        }
    }

}