package com.example.homework

import android.content.*
import android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.System.load

class SelectOptionPageActivity : AppCompatActivity() {
    var amount = 1
    lateinit var boundService: Intent
    lateinit var sizeTextView: TextView
    lateinit var menuSelection: MenuSelection

    lateinit var retrofit: Retrofit  //connect   걍 외우셈
    lateinit var retrofitHttp: RetrofitService  //cursor



    var myService: BoundService? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("SelectOptionPageAcitivy 내의 onServiceConneced"," 실행되나?")
            val b = p1 as BoundService.MyBoundService
            myService = b.getService()
            isConService = true
            myService?.getEatingWay()
            menuSelection = myService?.sendSelectionData()!!
            settingMenu(menuSelection)
            sizeChangeButtonEvent()
            amountChangeButtonEvent()
            orderButtonEvent(menuSelection)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConService = false
        }
    }


    override fun onStart(){
        Log.d("onStart","adsf")
        val intent = Intent(this,BoundService::class.java)
        serviceUnBind()
        stopService(intent)
        super.onStart()
    }

    override fun onUserLeaveHint() {
        Log.d("onuserleave","실행")
        val intent = Intent(this,BoundService::class.java)
        ContextCompat.startForegroundService(this, intent)
        super.onUserLeaveHint()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_option_page)
        sizeTextView = findViewById<TextView>(R.id.size_text)
        serviceBind()
        initRetrofit()
        initEvent()
    }

    fun initRetrofit() {
        retrofit = RetrofitClient.initRetrofit() // 걍 외우셈
        retrofitHttp = retrofit!!.create(RetrofitService::class.java)
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
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }

        val basketButton = findViewById<Button>(R.id.basket_button)
        basketButton.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.message_dialog,null)
            val dialog = AlertDialog.Builder(this)
                .setView(view)
                .create()

            dialog.setCancelable(false)
            val okButton = view.findViewById<Button>(R.id.ok_button)

            okButton.setOnClickListener {
                dialog.dismiss()
                myService?.getAmountData(amount.toString())
                myService?.getSizeData(sizeTextView.text.toString())
                myService?.addShoppingList()
            }
            dialog.show()

        }
    }
    fun orderButtonEvent(receiveData: MenuSelection){
        val orderButton = findViewById<Button>(R.id.order_button)
        orderButton.setOnClickListener {
            if (myService?.getLoginStatus()!! == false){ // 로그인을 해야지만 주문을 할 수가 있음
                val view = layoutInflater.inflate(R.layout.message_yes_or_no_dialog,null)
                val dialog = AlertDialog.Builder(this)
                    .setView(view)
                    .create()
                view.findViewById<TextView>(R.id.message).textSize
                view.findViewById<TextView>(R.id.message).text = "로그인이 필요한 서비스입니다.\n로그인하시겠습니까?"
                dialog.setCancelable(false)
                val yesButton = view.findViewById<Button>(R.id.yes_button)
                val noButton = view.findViewById<Button>(R.id.no_button)
                yesButton.setOnClickListener {
                    dialog.dismiss()
                    val intent = Intent(this, LoginPageActivity::class.java)
                    intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
                    startActivity(intent)
                }
                noButton.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }

            else{

                var requestData: HashMap<String, Any> = HashMap()

                var allPrice = 0
                val a = receiveData.menuPrice.substring(0 until 1)//계산 과정
                val b = receiveData.menuPrice.substring(2 until 5)
                val c = a + b
                allPrice = c.toInt()*amount

                var myOrderList = orderList(
                    name = receiveData.menuName,
                    count = amount,
                    sum_price = allPrice
                )

                var list = listOf(myOrderList)
                requestData["id"] = myService!!.getMyId()
                requestData["order_list"] = list
                requestData["total_price"] = list[0].sum_price

                retrofitHttp.postOrderMenu(
                    requestData
                ).enqueue(object: Callback<AccountData> {

                    override fun onFailure(
                        call: Call<AccountData>,
                        t: Throwable
                    ) { // 통신 실패하면 이게 뜸
                        Log.d("result", "Request Fail: ${t}") // t는 통신 실패 이유
                    }

                    override fun onResponse(
                        call: Call<AccountData>,
                        response: Response<AccountData>
                    ) {
                        if (response.body()!!.success) {
                            val view = layoutInflater.inflate(R.layout.message_dialog,null)
                            findViewById<TextView>(R.id.message).text = "주문이 완료되었습니다"
                            val dialog = AlertDialog.Builder(this@SelectOptionPageActivity)
                                .setView(view)
                                .create()
                            dialog.setCancelable(false)
                            val okButton = view.findViewById<Button>(R.id.ok_button)

                            okButton.setOnClickListener {
                                dialog.dismiss()
                            }
                            dialog.show()

                        }
                        else{
                            Log.d("result","${response.body()!!.message}")
                        }
                    }

                })
            }
        }
    }

    fun settingMenu(receiveData: MenuSelection) {
        val menuImage = findViewById<ImageView>(R.id.menu_image)
        val menuName = findViewById<TextView>(R.id.menu_name)
        val menuPrice = findViewById<TextView>(R.id.menu_price)
        if (receiveData.menuImageSource.contains("image")){
            Glide
                .with(this)
                .load("http://3.39.66.6:3000"+receiveData.menuImageSource)
                .thumbnail()
                .into(menuImage)
        }
        else{
            menuImage.setImageResource(receiveData.menuImageSource.toInt())
        }
        menuName.text = receiveData.menuName
        menuPrice.text = receiveData.menuPrice
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
        var tag = 0
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
