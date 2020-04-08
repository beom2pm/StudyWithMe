package com.example.studywithme.scheduling



import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import com.example.studywithme.R

/* 어느 요소를 어느 View에 넣을 것인지 연결해주는 것이 Adapter의 역할이다.
recyclerview의 어댑터는 RecyclerView.Adapter를 extend해야 함.
근데 이 어댑터에서는 ViewHolder라는 것이 필요하다. */
class Todo_list_adapter (val context: Context, val todoList: ArrayList<Todo_list_data>) :
    RecyclerView.Adapter<Todo_list_adapter.Holder>() {

    /* 화면을 최초 로딩하여 만들어진 View가 없는 경우, xml파일을 inflate하여 ViewHolder를 생성한다.*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.todo_list_item, parent, false)
        return Holder(view)
    }

    /* onCreateViewHolder에서 만든 View와 실제 입력되는 각각의 데이터를 연결한다.*/
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(todoList[position], context)
    }
    /* RecyclerView로 만들어지는 item의 총 개수를 반환한다.*/
    override fun getItemCount(): Int {
        return todoList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /* 각 View의 이름을 정하고, findViewById를 통해 ImageView인지 TextView인지 Button인지 등 종류를 정하고
        id를 통해 layout과 연결된다.*/
        val todoName = itemView?.findViewById<TextView>(R.id.todo_list_todoName)
        val dday = itemView?.findViewById<TextView>(R.id.todo_list_dDay)
        val checkbox = itemView?.findViewById<CheckBox>(R.id.todo_list_checkbox)
        var done = 0

        fun getcheckbox_is_checked(){
            checkbox.setChecked(false)
            if (checkbox.isChecked) {
                done = 1
            }
        }

        /* bind 함수는 ViewHolder와 클래스의 각 변수를 연동하는 역할을 한다. Overrdie할 함수에서 사용하게 된다.
        쉽게 말해 이쪽 TextView엔 이 String을 넣어라, 라고 지정하는 함수라고 보면 된다.*/
        fun bind(category: Todo_list_data, context: Context) {
            /*TextView와 String 데이터 연결하기*/
            todoName?.text = category.todoName
            dday?.text = "D-" + category.dday
        }
    }
}