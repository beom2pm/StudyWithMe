package com.example.studywithme.scheduling

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import java.time.LocalDate

class JavaDatePickerDialog : DialogFragment() {
    private var onDateSetListener: DatePickerDialog.OnDateSetListener? = null

    lateinit var localDate: LocalDate
    var onOk: ((date: LocalDate) -> Unit)? = null
    var onCancel: (() -> Unit)? = null

    companion object {
        private const val TAG = "JavaDatePickerDialog"

        private const val EXTRA_DATE = "date"

        fun newInstance(date: LocalDate? = null): JavaDatePickerDialog {
            val dialog = JavaDatePickerDialog()
            val args = Bundle().apply {
                putSerializable(EXTRA_DATE, date)
                /*
                date?.also {
                    putLong(EXTRA_DATE, it.atStartOfDay(ZoneId.systemDefault()).toEpochSecond())
                }
                 */
            }
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        localDate = arguments?.getSerializable(EXTRA_DATE) as LocalDate? ?: LocalDate.now()

        val year = localDate.year
        val month = localDate.monthValue - 1
        val day = localDate.dayOfMonth

        val listener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            this.localDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            onOk?.invoke(localDate)
        }

        val dialog = DatePickerDialog(activity!!, listener, year, month, day)
        dialog.setOnCancelListener {
            onCancel?.invoke()
        }

        return dialog
    }
}