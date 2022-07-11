package com.example.homework

import android.content.*
import android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

    lateinit var sharedPreferences: SharedPreferences

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
        sharedPreferences = getSharedPreferences("login_data", MODE_PRIVATE)
        setContentView(R.layout.select_option_page)
        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        sizeTextView = findViewById(R.id.size_text)
        serviceBind()
        initRetrofit()
        basketButtonEvent()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar,menu)
        val trashButton = menu?.findItem(R.id.trash_button)
        val checkButton = menu?.findItem(R.id.check_button)
        trashButton?.setVisible(false)
        checkButton?.setVisible(false)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            android.R.id.home ->{
                Log.d("ToolBar_item: ", "뒤로가기 버튼 클릭")
                finish()
                return true
            }
            R.id.menu_button ->{
                Log.d("menu_button","메뉴 버튼 클릭")
                val intent = Intent(this, ShoppingBasketPageActivity::class.java)
                intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
                startActivity(intent)

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
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

    fun basketButtonEvent() {

        val basketButton = findViewById<Button>(R.id.basket_button)
        basketButton.setOnClickListener {
            val dialog = MessageDialog("ok_mode")
            dialog.setTextMessage("선택하신 상품을 장바구니에 담았습니다")
            dialog.setButtonEvent(object: MessageDialog.OnButtonClickListener{
                override fun yesButtonClickListener() {
                }
                override fun noButtonClickListener() {
                }
                override fun okButtonClickListener() {
                    myService?.getAmountData(amount.toString())
                    myService?.getSizeData(sizeTextView.text.toString())
                    myService?.addShoppingList()
                }
            })
            dialog.show(supportFragmentManager,"Dialog")
        }
    }

    fun orderButtonEvent(receiveData: MenuSelection) {

        val orderButton = findViewById<Button>(R.id.order_button)
        val idValue = sharedPreferences.getString("id", null)
        orderButton.setOnClickListener {
            Log.d("orderbutton","누름")
            if (idValue == null) { // 로그인을 해야지만 주문을 할 수가 있음

                val dialog = MessageDialog("yes_or_no_mode")
                dialog.setTextMessage("로그인이 필요한 서비스입니다.\n로그인하시겠습니까?")
                dialog.setButtonEvent(object: MessageDialog.OnButtonClickListener{
                    override fun yesButtonClickListener() {
                        val intent = Intent(this@SelectOptionPageActivity, LoginPageActivity::class.java)
                        intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
                        startActivity(intent)
                    }

                    override fun noButtonClickListener() {
                    }

                    override fun okButtonClickListener() {
                    }
                })
                dialog.show(supportFragmentManager,"Dialog")

            } else {
                var allPrice = 0
                var requestData: HashMap<String, Any> = HashMap()
                if (receiveData.menuPrice.contains(",")) {
                    val a = receiveData.menuPrice.substring(0 until 1)//계산 과정
                    val b = receiveData.menuPrice.substring(2 until 5)
                    val c = a + b
                    allPrice = c.toInt() * amount
                } else {
                    allPrice = receiveData.menuPrice.toInt() * amount
                }

                var myOrderList = orderList(
                    name = receiveData.menuName,
                    count = amount,
                    sum_price = allPrice
                )

                var list = listOf(myOrderList)
                requestData["id"] = sharedPreferences.getString("id", null).toString()
                requestData["order_list"] = list
                requestData["total_price"] = list[0].sum_price

                retrofitHttp.postOrderMenu(
                    requestData
                ).enqueue(object : Callback<AccountData> {

                    override fun onFailure(
                        call: Call<AccountData>,
                        t: Throwable
                    ) {
                        Log.d("result", "Request Fail: ${t}")
                    }

                    override fun onResponse(
                        call: Call<AccountData>,
                        response: Response<AccountData>
                    ) {
                        if (response.body()!!.success) {
                            val dialog = MessageDialog("ok_mode")
                            dialog.setTextMessage("주문이 완료되었습니다")
                            dialog.show(supportFragmentManager,"Dialog")
                        } else {
                            Log.d("result", "${response.body()!!.message}")
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
