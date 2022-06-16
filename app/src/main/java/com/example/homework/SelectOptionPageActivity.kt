package com.example.homework

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

class SelectOptionPageActivity : AppCompatActivity() {
//    var trashArray = arrayListOf<String>("시발 쓰레기값") //아니 이거 초기화 어캐했엇지
//    var shoppingBasket = arrayListOf(trashArray)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_option_page)
        val sizeTextView = findViewById<TextView>(R.id.size_text)
        var receiveData = intent.getStringArrayListExtra("sendData")!!

        receiveData.set(3, sizeTextView.text.toString())

        settingMenu(receiveData)
        amountChangeButtonEvent()
        sizeChangeButtonEvent(sizeTextView, receiveData)
        initEvent(receiveData)
    }

    fun initEvent(receiveData: ArrayList<String>) {
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener{
            val intent = Intent(this, MenuSelectPageActivity::class.java)
            startActivity(intent)
        }

        val basketButton = findViewById<Button>(R.id.basket_button)
        basketButton.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.select_option_page_basket_button_dialog,null)
            val dialog = AlertDialog.Builder(this)
                .setView(view)
                .create()
            dialog.setCancelable(false)
            val okButton = view.findViewById<Button>(R.id.ok_button)

            okButton.setOnClickListener {
                dialog.dismiss()
                val intent = Intent(this, MenuSelectPageActivity::class.java)
                intent.putExtra("shoppingBasketData send", receiveData)
                startActivity(intent)
            }
            dialog.show()

        }
    }

    fun settingMenu(receiveData: ArrayList<String>) {
        val menuImage = findViewById<ImageView>(R.id.menu_image)
        val menuName = findViewById<TextView>(R.id.menu_name)
        val menuPrice = findViewById<TextView>(R.id.menu_price)
        menuImage.setImageResource(receiveData[0].toInt())
        menuName.text = receiveData[1]
        menuPrice.text = receiveData[2]
    }

    fun amountChangeButtonEvent(){
        var amount = 1
        val removeOneAmount = findViewById<ImageView>(R.id.remove_one_amount)
        val addOneAmount = findViewById<ImageView>(R.id.add_one_amount)
        val textAmount = findViewById<TextView>(R.id.text_amount)
        textAmount.text = "${amount}"
        addOneAmount.setOnClickListener {
            textAmount.text = "${amount + 1}"
            amount += 1
        }
        removeOneAmount.setOnClickListener{
            if (amount > 1) {
                textAmount.text = "${amount - 1}"
                amount -= 1
            }
            else{
                amount = 1
            }
        }

    }
    fun sizeChangeButtonEvent(sizeTextView: TextView, receiveData: ArrayList<String>) {

        val view = layoutInflater.inflate(R.layout.select_size_option_dialog,null)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()
        dialog.setCancelable(false)
        var tag: Int = 0
        val regularLinear = view.findViewById<LinearLayout>(R.id.regular_linear)
        val extraLinear = view.findViewById<LinearLayout>(R.id.extra_linear)
        val cancelButton = view.findViewById<TextView>(R.id.cancel_button)
        val okButton = view.findViewById<TextView>(R.id.ok_button)

        regularLinear.setOnClickListener{
            view.findViewById<ImageView>(R.id.regular_circle_button).setImageResource(R.drawable.option_selected_circle_shape)
            view.findViewById<ImageView>(R.id.extra_circle_button).setImageResource(R.drawable.option_not_selected_circle_shape)
            tag = 0
        }

        extraLinear.setOnClickListener{
            view.findViewById<ImageView>(R.id.regular_circle_button).setImageResource(R.drawable.option_not_selected_circle_shape)
            view.findViewById<ImageView>(R.id.extra_circle_button).setImageResource(R.drawable.option_selected_circle_shape)
            tag = 1
        }

        cancelButton.setOnClickListener{
            dialog.dismiss()
        }
        okButton.setOnClickListener{
            if (tag == 0){
                sizeTextView.text = "Regular"
            }
            else{
                sizeTextView.text = "Extra"
            }
            receiveData.set(3, sizeTextView.text.toString())
            initEvent((receiveData))
            dialog.dismiss()
        }
        val sizeLinearLayout = findViewById<LinearLayout>(R.id.size_linear)
        sizeLinearLayout.setOnClickListener {
            dialog.show()
        }
    }





}
