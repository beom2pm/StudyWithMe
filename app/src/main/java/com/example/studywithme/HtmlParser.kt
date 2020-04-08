package com.example.studywithme

import android.os.AsyncTask
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException

/*Jsoup라이브러리와 AsyncTask를 이용해서 해당 링크에 대한 메타데이터 가져오기*/
class HtmlParser {

    fun get_bmLinkMetaData(url: String): String?{
        var bookmarkLinkURL = url
        var linkTitle = ""
        var linkImageURL = ""
        var linkDescription = ""
        var bookmarkLink_metaData: JSONObject = JSONObject()

        class getBmLinkMetaData: AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    val document = Jsoup.connect(bookmarkLinkURL).get()
                    val OG_tags = document.select("meta[property^=og:]")
                    if (OG_tags.size > 0) {
                        // 필요한 og태그 추려내기
                        for (i in 0 until OG_tags.size){
                            var tag: Element = OG_tags[i]
                            var text: String = tag.attr("property")
                            when (text) {
                                "og:title" -> linkTitle = tag.attr("content")
                                "og:image" -> linkImageURL = tag.attr("content")
                                "og:description" -> linkDescription = tag.attr("content")
                            }
                        }
                    }
                    bookmarkLink_metaData.put("title", linkTitle)
                    bookmarkLink_metaData.put("imageURL", linkImageURL)
                    bookmarkLink_metaData.put("description", linkDescription)
                } catch (e: IOException){
                    e.printStackTrace()
                    Log.d("북마크 크롤링 오류 ", e.localizedMessage)
                }
                Log.d("html parser 응답 ", bookmarkLink_metaData.toString())
                return bookmarkLink_metaData.toString()
            }
        } // 통신 클래스
        // 함수에서 통신 클래스 생성하고 실행해서 get 으로 값 리턴
        var startParser: getBmLinkMetaData = getBmLinkMetaData()
        return startParser.execute().get()
    }





}