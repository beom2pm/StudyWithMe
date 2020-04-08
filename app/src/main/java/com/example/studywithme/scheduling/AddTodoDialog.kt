package com.example.studywithme.scheduling


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

import com.example.studywithme.R
import com.example.studywithme.fragment.Calendar
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddTodoDialog : DialogFragment() {

    companion object {
        private const val FRAGMENT_TAG = "custom_dialog"

        fun newInstance() = AddTodoDialog()

        fun show(fragmentManager: FragmentManager): AddTodoDialog {
            val dialog = newInstance()
            // dialog.isCancelable = false
            dialog.show(fragmentManager, FRAGMENT_TAG)
            return dialog
        }
    }

    private lateinit var customView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return customView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // return super.onCreateDialog(savedInstanceState)

        val view = activity!!.layoutInflater.inflate(R.layout.fragment_add_todo_dialog, null)
        customView = view

        val set_todo_name=view.findViewById<EditText>(R.id.write_name_todo)
        val set_todo_day=view.findViewById<EditText>(R.id.write_dday_todo)



        val builder = AlertDialog.Builder(context!!)
            .setTitle("Add Todo")
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->

                val fragment = Calendar() // Fragment 생성
                val bundle = Bundle() // 파라미터는 전달할 데이터 개수
                bundle.putString("todo_name", set_todo_name.text.toString()) // key , value

                //dday바꿔주는 부분
                var datechange = LocalDate.parse(set_todo_day.text.toString(), DateTimeFormatter.ISO_DATE)
                //dday계산하는 알고리즘
                //goal_day입력받은 것 (string)이고 이걸 date 형식으로 바꿔줌
                var localdate = LocalDate.now()
                var period = datechange.toEpochDay() - localdate.toEpochDay()
                bundle.putString("todo_dday","D-"+ period.toString())







                val fm = fragmentManager
                val fmt = fm?.beginTransaction()
                fragment.setArguments(bundle)
                fmt?.replace(R.id.content, fragment)?.addToBackStack(null)?.commit()



                // do something
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                // do something
            }

        val dialog = builder.create()

        // optional
        dialog.setOnShowListener {
            // do something
        }

        return dialog
    }

}