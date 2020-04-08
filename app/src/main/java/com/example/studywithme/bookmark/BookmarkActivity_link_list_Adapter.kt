package com.example.studywithme.bookmark

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.studywithme.MainActivity
import com.example.studywithme.R
import org.w3c.dom.Text
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URI
import java.net.URL

class BookmarkActivity_link_list_Adapter(val context: Context, val linkURLlist: ArrayList<BookmarkActivity_link>, val topFragment: Fragment) :
    RecyclerView.Adapter<BookmarkActivity_link_list_Adapter.Holder>(){

    var mActivity: MainActivity? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.bookmark_link_list_item, parent, false)
        mActivity = topFragment.activity as MainActivity
        if (linkURLlist.size > 0){
            LayoutInflater.from(context).inflate(R.layout.fragment_link_list, parent, false).findViewById<TextView>(
                R.id.bookmark_link_list_has_no_item_msg).visibility = View.VISIBLE
        } else {
            LayoutInflater.from(context).inflate(R.layout.fragment_link_list, parent, false).findViewById<TextView>(
                R.id.bookmark_link_list_has_no_item_msg).visibility = View.GONE
        }

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return linkURLlist.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(linkURLlist[position], context)
        holder?.linkURL_string.setOnClickListener{
            var intent: Intent = Intent(Intent.ACTION_VIEW)
            var uri: Uri? = Uri.parse(holder?.linkURL_string.toString())
            intent.setData(uri)
            mActivity!!.startActivity(intent)
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linkTitle_string = itemView?.findViewById<TextView>(R.id.bookmark_link_title)
        val linkURL_string = itemView?.findViewById<TextView>(R.id.bookmark_link_url)
        val linkDescription_string = itemView?.findViewById<TextView>(R.id.bookmark_link_description)
        var bitmap: Bitmap? = null

        fun bind(link: BookmarkActivity_link, context: Context) {
            linkTitle_string.text = link.linkTitle
            linkURL_string.text = link.linkURL
            linkDescription_string.text = link.linkDescription
            // 이미지 url 에서 이미지 불러와서 비트맵으로 만들고 이미지뷰에 적용
            //getUserPostToImage(link.linkImage)
        }

        fun getUserPostToImage(imageURL: String) {
            val mThread = object : Thread() {
                override fun run() {
                    try {
                        val url = URL(imageURL)
                        /*web에서 이미지를 가져온 뒤 ImageView에 지정할 Bitmap을 만든다*/
                        val urlConnection = url.openConnection() as HttpURLConnection
                        urlConnection.setDoInput(true) // 서버로부터 응답 수신
                        urlConnection.connect()

                        val `is` = urlConnection.getInputStream() // InputStream 값 가져오기
                        var options: BitmapFactory.Options = BitmapFactory.Options()
                        options.inSampleSize = 2
                        bitmap = BitmapFactory.decodeStream(`is`, null, options)
//                        bitmap = BitmapFactory.decodeStream(`is`) // Bitmap으로 변환

                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            // Thread 실행
            mThread.start()
            try {
                // 메인 thread는 별도의 작업 thread가 작업을 완료할 때까지 대기해야한다.
                // join()를 호출하여 별도의 작업 thread가 종료될 때까지 메인 thread가 기다리게 한다.
                mThread.join()

                // 작업 thread에서 이미지를 불러오는 작업을 완료한 뒤 UI 작업을 할 수 있는 메인 Thread에서 ImageView에 이미지 지정
                //mActivity!!.runOnUiThread(Runnable { link_imageWindow.setImageBitmap(bitmap) })

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }
    }


}