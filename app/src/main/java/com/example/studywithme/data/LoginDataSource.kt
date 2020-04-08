package com.example.studywithme.data

import android.os.AsyncTask
import android.util.Log
import com.example.studywithme.data.model.LoggedInUser
import okhttp3.*
import org.json.JSONObject
import java.lang.Exception

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */

class LoginDataSource {

    var dataFrom_name : String = ""
    var dataFrom_id : String = ""

    /* POST로 동기식 처리 */
    fun login(username: String, password: String): Result<LoggedInUser> {
        var userId: String
        class test : AsyncTask<Void, Void, String>(){
            val client: OkHttpClient = OkHttpClient()

            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String {
                // url 만들기
                val url="http://203.245.10.33:8888/loginAuthenticate.php"
                try {
                    // 데이터를 담아 보낼 바디 만들기
                    val requestBody : RequestBody = FormBody.Builder()
                        .add("user_name", username)
                        .add("user_password", password)
                        .build()
                    // okhttp request를 만든다.
                    val request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()
                    // 클라이언트 생성
                    val response : Response = client.newCall(request).execute()
                    // 응답 받음
                    val data = response.body?.string().toString()
                    var result_array = JSONObject(data)
                    dataFrom_name = result_array.getString("user_name")
                    dataFrom_id = result_array.getString("user_id")
                    Log.d("응답name", dataFrom_name)
                    Log.d("응답id", dataFrom_id)
                } catch (e: Exception) {

                }
                return dataFrom_name
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
            }
        }

        var StartTest : test = test()
        var result = StartTest.execute().get().toString()
        Log.d("응답 인증 결과", result)

        return if (result.isNotEmpty()){
            // 인증 성공
            var authenticatedUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
            App.prefs.myUserNameData = dataFrom_name
            App.prefs.myUserIdData = dataFrom_id
            App.prefs.myPasswordData = password

            Result.Success(authenticatedUser)
        } else {
            // 인증 실패
            Result.Error("Error logging")

        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}
