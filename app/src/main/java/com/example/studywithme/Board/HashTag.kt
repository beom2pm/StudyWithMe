package com.example.studywithme.Board

import android.content.Context
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

class HashTag:ClickableSpan() {
    interface ClickEventListener{
       fun onClickEvent(data:String)

    }
    lateinit var mClickEventListener:ClickEventListener
   lateinit var context: Context
   lateinit var textPaint:TextPaint

    fun HashTag(ctx:Context){
        context=ctx
    }
    fun setOnclickEventListener(listener: Any){
        mClickEventListener= listener as ClickEventListener
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        textPaint=ds
        ds.color=ds.linkColor
        ds.setARGB(255, 88,130,250)
    }

    override fun onClick(widget: View) {
        val tv:TextView= widget as TextView
        val sp:Spanned= tv.text as Spanned

        val start:Int=sp.getSpanStart(this)
        val end:Int=sp.getSpanEnd(this)

        val word:String=sp.subSequence(start+1,end).toString()
        mClickEventListener.onClickEvent(word)

    }
}