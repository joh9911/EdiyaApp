package com.example.homework

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.w3c.dom.Text

class MessageDialog(modeSetting: String): DialogFragment() {
    var messageText = ""
    var mode = modeSetting
    lateinit var buttonClickListener: OnButtonClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.message_dialog,container,false)
        if (mode == "yes_or_no_mode"){
            view.findViewById<Button>(R.id.ok_button).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.yes_no_button_linear_layout).visibility = View.VISIBLE
        }
        else if(mode == "ok_mode"){
            view.findViewById<LinearLayout>(R.id.yes_no_button_linear_layout).visibility = View.GONE
            view.findViewById<Button>(R.id.ok_button).visibility = View.VISIBLE
        }
        view.findViewById<TextView>(R.id.message).text = messageText
        view.findViewById<Button>(R.id.yes_button).setOnClickListener {
            buttonClickListener.yesButtonClickListener()
            dismiss()
        }
        view.findViewById<Button>(R.id.no_button).setOnClickListener{
            buttonClickListener.noButtonClickListener()
            dismiss()
        }
        view.findViewById<Button>(R.id.ok_button).setOnClickListener{
            buttonClickListener.okButtonClickListener()
            dismiss()
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        context?.dialogFragmentResize(this, 0.9f, 0.3f )
    }

    interface OnButtonClickListener {
        fun yesButtonClickListener()
        fun noButtonClickListener()
        fun okButtonClickListener()
    }
    fun setTextMessage(message: String){
        messageText = message
    }
    fun setButtonEvent(listener: OnButtonClickListener){
        buttonClickListener = listener
    }
    fun Context.dialogFragmentResize(dialogFragment: DialogFragment, width: Float, height: Float) {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT < 30) {

            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialogFragment.dialog?.window

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)

        } else {

            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialogFragment.dialog?.window

            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }
}


