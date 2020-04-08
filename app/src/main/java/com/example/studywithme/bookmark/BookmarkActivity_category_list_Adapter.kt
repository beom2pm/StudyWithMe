package com.example.studywithme.bookmark

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.studywithme.MainActivity
import com.example.studywithme.R
import com.example.studywithme.data.App
import com.example.studywithme.data.category
import com.example.studywithme.data.categorylist
import com.example.studywithme.fragment.Bookmark
import com.example.studywithme.fragment.Bookmark_link_list
import kotlinx.android.synthetic.main.bookmark_category_list_item.view.*
import kotlinx.android.synthetic.main.bookmark_main.*

/* 어느 요소를 어느 View에 넣을 것인지 연결해주는 것이 Adapter의 역할이다.
recyclerview의 어댑터는 RecyclerView.Adapter를 extend해야 함.
근데 이 어댑터에서는 ViewHolder라는 것이 필요하다. */
class BookmarkActivity_category_list_Adapter (val context: Context, var categoryList: ArrayList<BookmarkActivity_category>, val topFragment: Fragment) :
    RecyclerView.Adapter<BookmarkActivity_category_list_Adapter.Holder>(), Filterable {

    var categoryHolder: Holder? = null
    val unFilteredList = categoryList
    var filteredList = categoryList

    /* 화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성한다.*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.bookmark_category_list_item, parent, false)
        if (categoryList.size > 0){
            LayoutInflater.from(context).inflate(R.layout.bookmark_main, parent, false).findViewById<TextView>(R.id.bookmark_category_list_has_no_item_msg).visibility = View.VISIBLE
        } else {
            LayoutInflater.from(context).inflate(R.layout.bookmark_main, parent, false).findViewById<TextView>(R.id.bookmark_category_list_has_no_item_msg).visibility = View.GONE
        }
        return Holder(view)
    }

    /* onCreateViewHolder에서 만든 View와 실제 입력되는 각각의 데이터를 연결한다.*/
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(categoryList[position], context)
        /*showLinksButton 클릭하면 해당 아이템의 카테고리 이름, 소목표 이름 출력 편집 메뉴버튼도 출력*/
        holder?.categoryItemLayout.setOnClickListener {
            // 해당 카테고리에 등록된 링크들이 띄워지는 프래그먼트가 띄워져야함. 백스택 사용해서 이전 상태 유지
            // 링크 프래그먼트에 user_id랑 category_name 전달
            var fragment: Fragment = Bookmark_link_list()
            var bundle: Bundle = Bundle()
            bundle.putString("category_name", holder?.categoryName.text.toString())
            if (topFragment.arguments == null){
                Log.d("카테고리 프래그먼트 응답", "번들 없음")
            } else {
                Log.d("카테고리 프래그먼트 응답", "번들 있음")
                var sharedLinkURL = topFragment.arguments!!.getString("sharedLinkURL")
                bundle.putString("sharedLinkURL", sharedLinkURL)
            }
            fragment.arguments = bundle
            // 링크 프래그먼트로 전환
            topFragment.fragmentManager!!.beginTransaction().replace(R.id.content, fragment).addToBackStack(null).commit()
        }
    }

    /* RecyclerView로 만들어지는 item의 총 개수를 반환한다.*/
    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                filteredList = if (charString.isEmpty()) {
                    unFilteredList
                } else {
                    var filteringList = ArrayList<BookmarkActivity_category>()
                    for (item in unFilteredList) {
                        if (item.categoryName.contains(charString) || item.goalName.contains(charString)) {
                            filteringList.add(item)
                        }
                    }
                    filteringList
                }
                Log.d("어댑터 응답1", filteredList.toString())
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results!!.values as ArrayList<BookmarkActivity_category>
                Log.d("어댑터 응답2", filteredList.toString())
                categoryList = filteredList
                notifyDataSetChanged()
            }
        }
    }


    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /* 각 View의 이름을 정하고, findViewById를 통해 ImageView인지 TextView인지 Button인지 등 종류를 정하고
        id를 통해 layout과 연결된다.*/
        val categoryItemLayout = itemView?.findViewById<LinearLayout>(R.id.bookmark_category_linearLayout)
        val categoryItemCheckBoxLayout = itemView?.findViewById<LinearLayout>(R.id.bookmark_category_checkbox_layout)
        val categoryItemCheckBox = itemView?.findViewById<CheckBox>(R.id.bookmark_category_checkbox)
        val categoryName = itemView?.findViewById<TextView>(R.id.bookmark_category_list_item_categoryName)
        val goalName = itemView?.findViewById<TextView>(R.id.bookmark_category_list_item_goalName)
        /* bind 함수는 ViewHolder와 클래스의 각 변수를 연동하는 역할을 한다. Overrdie할 함수에서 사용하게 된다.
        쉽게 말해 이쪽 TextView엔 이 String을 넣어라, 라고 지정하는 함수라고 보면 된다.*/
        fun bind(category: BookmarkActivity_category, context: Context) {
            /*TextView와 String 데이터 연결하기*/
            categoryName?.text = category.categoryName
            goalName?.text = category.goalName
        }
    }
}