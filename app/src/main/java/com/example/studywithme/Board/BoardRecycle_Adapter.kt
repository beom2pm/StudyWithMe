package com.example.studywithme.Board

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.method.LinkMovementMethod

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import com.example.studywithme.MainActivity
import com.example.studywithme.R
import java.util.regex.Matcher
import java.util.regex.Pattern


class BoardRecycle_Adapter(val context: Context,val boardList: ArrayList<BoardData>,val fragment_s:Fragment) : RecyclerView.Adapter<BoardRecycle_Adapter.Holder>() {

    //데이터를 저장할 아이템리스트
    val items = ArrayList<BoardData>()
    private var activity: MainActivity? = null
    //hashtag
    var tag_str:String=""
    var tagsContent: SpannableString? =null

    override fun getItemCount(): Int {
        return boardList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardRecycle_Adapter.Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.board_items, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val activity = fragment_s.activity as MainActivity

        holder.bind(boardList[position],context)
        holder.itemView.setOnClickListener{
            var fragment:Fragment=SelectPostFrag()
            var bundle: Bundle= Bundle(6)

            bundle.putString("user_id",holder.userID.text.toString())
            bundle.putString("goal_id",holder.goalName.text.toString())
            bundle.putString("content",holder.content_txt.text.toString())
            bundle.putString("date",holder.date_txt.text.toString())
            bundle.putString("post_id", boardList[position].postID)
            bundle.putString("tag",holder.tag_txt.text.toString())

            fragment.arguments=bundle
            activity.fromAdaptertoFragment(fragment)
           /* val fragmentManager:FragmentManager=activity!!.supportFragmentManager
            val fragmentTransaction:FragmentTransaction=fragmentManager.beginTransaction()
            fragmentTransaction.replace()

            fragmentManager.popBackStack()*/
            System.out.println("여기는 뷰홀더다")

        }
        val like_btn:ToggleButton=holder.itemView.findViewById(R.id.button_favorite)
        like_btn.setOnClickListener{
            holder.like_txt.text = "1"
        }
        //hastag event
        val hashtagSpans = getSpans(tag_str, '#')
        tagsContent = SpannableString(tag_str)
        for(i in 0 until hashtagSpans.size){

            val span = hashtagSpans[i]
            var hash_start=span[0]
            var hash_end=span[1]

            var hashtag=HashTag()
            hashtag.setOnclickEventListener(object :HashTag.ClickEventListener{
                override fun onClickEvent(data: String) {
                    var fragment:Fragment=TagSearchPost()
                    var bundle: Bundle= Bundle(1)
                    bundle.putString("tag_name",data)
                    fragment.arguments=bundle
                    activity.fromAdaptertoFragment(fragment)

                }

            })
            tagsContent?.setSpan(hashtag,hash_start,hash_end,0)
            holder.hash_bind(boardList[position],context)
        }
        //tag_str을 전역으로 써야하는데 자꾸 덧붙여지니까 초기화 (holder갔다가 여기오니까)
        tag_str=""
    }

    inner class Holder(ItemView:View):RecyclerView.ViewHolder(ItemView){
        val userID=ItemView.findViewById<TextView>(R.id.id_user_item)
        val goalName=ItemView.findViewById<TextView>(R.id.id_goal_item)
        val date_txt=ItemView.findViewById<TextView>(R.id.date_txt_item)
        val content_txt=ItemView.findViewById<TextView>(R.id.id_content_item)
        val like_txt=ItemView.findViewById<TextView>(R.id.likes)
        val tag_txt=ItemView.findViewById<TextView>(R.id.tag_view)

        fun bind(data: BoardData,context: Context){
            System.out.println("여기에 들어오긴하는건지")
            userID.text = data.userID
            goalName.text = data.goalName
            content_txt.text=data.postContent
            date_txt.text=data.date
            for(i in 0 until data.taglist.size){
               tag_str+="#"+data.taglist.get(i)+" "
            }
            tag_txt.text=tag_str
        }
        //holder에서 실행되고 해야하는거라 함수로 만들어줌
        fun hash_bind(data:BoardData, context: Context){
            tag_txt.movementMethod= LinkMovementMethod.getInstance()
            tag_txt.text = tagsContent
        }

    }

    //hashtag span
    fun getSpans(body:String, prefix:Char): ArrayList<IntArray> {
        System.out.println("getspan들어감")
        val spans = ArrayList<IntArray>()
        val pattern: Pattern = Pattern.compile(prefix+"\\w+")
        val matcher: Matcher =pattern.matcher(body)
        while (matcher.find()) {
            val currentSpan = IntArray(2)
            currentSpan[0] = matcher.start()
            currentSpan[1] = matcher.end()
            spans.add(currentSpan)
        }

        return spans
    }

   }




