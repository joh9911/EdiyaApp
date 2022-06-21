package com.example.homework

import android.app.Dialog
import android.content.*
import android.media.Image
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

class SelectOptionPageActivity : AppCompatActivity() {
    var amount = 1
    lateinit var boundService: Intent
    lateinit var sizeTextView: TextView
    var dataList = arrayListOf<String>()
    var myService: BoundService? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("SelectOptionPageAcitivy 내의 onServiceConneced"," 실행되나?")
            val b = p1 as BoundService.MyBoundService
            myService = b.getService()
            isConService = true
            myService?.getEatingWay()
            dataList = myService?.sendData()!!


            settingMenu(dataList)
            sizeChangeButtonEvent()
            amountChangeButtonEvent()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_option_page)
        sizeTextView = findViewById<TextView>(R.id.size_text)
        serviceBind()
        initEvent()
    }
    fun serviceBind(){
        boundService = Intent(this,BoundService::class.java)
        bindService(boundService, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d("SelectOptionPageAcitivy 내의 serviceBind","난 서비스 실행했음")
    }
    override fun onDestroy() {
        Log.d("destory","이거 되잖아")

        super.onDestroy()
    }

    fun serviceUnBind(){
        if (isConService) {
            unbindService(serviceConnection)
            isConService = false
            Log.d("SelectOptionPageAcitivy내의 serviceUnBind","난 서비스 껏음")
        }
    }

    fun initEvent() {
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener{
            finish()
        }
        val shoppingBasketButton = findViewById<ImageView>(R.id.shopping_basket_button)
        shoppingBasketButton.setOnClickListener {
            val intent = Intent(this, ShoppingBasketPageActivity::class.java)
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
                myService?.addShoppingList(dataList)
                myService?.addAmountData(amount.toString())
                myService?.addSizeData(sizeTextView.text.toString())
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
        amount = 1
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
    fun sizeChangeButtonEvent() {

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
            dialog.dismiss()
        }
        val sizeLinearLayout = findViewById<LinearLayout>(R.id.size_linear)
        sizeLinearLayout.setOnClickListener {
            dialog.show()
        }
    }





}
