package com.example.homework

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isGone
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.internal.ParcelableSparseArray
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.NullPointerException

class ShoppingBasketPageActivity: AppCompatActivity() {
    lateinit var boundService: Intent
    lateinit var linearLayout: LinearLayout
    lateinit var customView: View

    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    lateinit var sharedPreferences: SharedPreferences

    lateinit var retrofit: Retrofit  //connect   걍 외우셈
    lateinit var retrofitHttp: RetrofitService  //cursor

    var seletAllTag = 1

    lateinit var trashButton: MenuItem
    lateinit var menuButton: MenuItem
    lateinit var checkButton: MenuItem

    var shoppingList = arrayListOf<MenuSelection>()

    var myService: BoundService? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("ShoppingBaskeetPageActivity 내의 onServiceConneced", " 실행되나?")
            val b = p1 as BoundService.MyBoundService
            myService = b.getService()
            isConService = true
            myService?.getEatingWay()
            shoppingList = myService?.sendShoppingList()!!
            addShoppingListView(shoppingList)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_basket_page)

        sharedPreferences = getSharedPreferences("login_data", MODE_PRIVATE)

        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

    }

    override fun onResume() {
        super.onResume()
        linearLayout = findViewById(R.id.shopping_basket_linear_layout)
        initRetrofit()
        serviceBind()
        orderButtonEvent()
        deleteButtonEvent()
        selectAllButtonEvent()
    }

    fun buttonVisiblitySetting(setting: String){
        val orderConfirmButton = findViewById<Button>(R.id.order_confirm_button)
        val deleteButton = findViewById<Button>(R.id.delete_button)
        val selectAllButton = findViewById<Button>(R.id.select_all_button)

        if(setting == "trashButton pressed") {
            deleteButton.visibility = View.VISIBLE
            selectAllButton.visibility = View.VISIBLE
            orderConfirmButton.visibility = View.INVISIBLE
        }
        else if(setting == "checkButton pressed"){
            deleteButton.visibility = View.INVISIBLE
            selectAllButton.visibility = View.INVISIBLE
            orderConfirmButton.visibility = View.VISIBLE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar,menu)

        checkButton = menu?.findItem(R.id.check_button)!!
        menuButton = menu?.findItem(R.id.menu_button)!!
        trashButton = menu?.findItem(R.id.trash_button)!!

        menuButton?.setVisible(false)
        checkButton?.setVisible(false)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.trash_button -> {

                checkButton.setVisible(true)
                trashButton.setVisible(false)
                buttonVisiblitySetting("trashButton pressed")

                val count = linearLayout.childCount
                for (index in 0 until count) {
                    val child = linearLayout.getChildAt(index)
                    child.findViewById<CheckBox>(R.id.waste_check_button).visibility = View.VISIBLE
                    child.invalidate()
                }
                return true
            }
            R.id.check_button -> {
                trashButton.setVisible(true)
                checkButton.setVisible(false)

                buttonVisiblitySetting("checkButton pressed")

                val count = linearLayout.childCount
                for (index in 0 until count) {
                    val child = linearLayout.getChildAt(index)
                    child.findViewById<CheckBox>(R.id.waste_check_button).isChecked = false
                    child.findViewById<CheckBox>(R.id.waste_check_button).visibility = View.GONE
                    child.invalidate()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }



    fun gotoAnotherPage(intent: String){
        if (intent == "login") {
            val intent = Intent(this, LoginPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
        else if (intent == "shoppingList"){
            val intent = Intent(this, ShoppingBasketPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
        else if (intent == "signUP"){
            val intent = Intent(this, SignUpPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
    }

    fun selectAllButtonEvent(){
        val selectAllButton = findViewById<Button>(R.id.select_all_button)
        selectAllButton.setOnClickListener {
            if (seletAllTag == 1) {
                val count = linearLayout.childCount
                for (index in 0 until count) {
                    val child = linearLayout.getChildAt(index)
                    val checkBox = child.findViewById<CheckBox>(R.id.waste_check_button)
                    checkBox.isChecked = true
                }
                seletAllTag = 0
            } else if (seletAllTag == 0) {
                val count = linearLayout.childCount
                for (index in 0 until count) {
                    val child = linearLayout.getChildAt(index)
                    val checkBox = child.findViewById<CheckBox>(R.id.waste_check_button)
                    checkBox.isChecked = false
                }
                seletAllTag = 1
            }
        }
    }

    fun getOrderData(){
        val id = sharedPreferences.getString("id",null)!!
        retrofitHttp.getOrderData(
            id
        )
            .enqueue(object: Callback<orderRecord> {

                override fun onFailure(
                    call: Call<orderRecord>,
                    t: Throwable
                ) {
                    Log.d("result", "Request Fail: ${t}")
                }
                override fun onResponse(
                    call: Call<orderRecord>,
                    response: Response<orderRecord>
                ) {
                    if (response.body()!!.success) {
                        Log.d("responresult", "${response.body()!!}")

                    }
                    else{
                        Log.d("result","${response.body()!!.message}")
                    }
                }
            })
    }

    fun deleteButtonEvent(){
        val deleteButton = findViewById<Button>(R.id.delete_button)
        deleteButton.setOnClickListener {
            val count = linearLayout.childCount
            for (index in 0 until count) {
                val child = linearLayout.getChildAt(index)
                val checkBox = child.findViewById<CheckBox>(R.id.waste_check_button)

                if (checkBox.isChecked) { // 만약 체크가 됐다면,
                    val name =
                        child.findViewById<TextView>(R.id.menu_name).text //체크가 된 인덱스의 child의 메뉴 이름

                    for (index1 in 0 until shoppingList.size) { //저 메뉴 이름과 저장된 shoppingList 안의 Gson 데이터의 메뉴 이름과 비교해서
                        // 일치하는 항목의 amount를 1 빼준다.
                        if (name == shoppingList[index1].menuName) {
                            var amount = shoppingList[index1].amount?.toInt()!!
                            amount -= 1
                            shoppingList[index1].amount = amount.toString()
                            Log.d("난 이만큼을 지웠음","${shoppingList[index1].amount}")
                        }
                    }
                }
            }
            buttonVisiblitySetting("checkButton pressed")

            checkButton.setVisible(false)
            trashButton.setVisible((true))

            linearLayout.removeAllViews()

            addShoppingListView(shoppingList)
            myService?.getFinalShoppingList(shoppingList)
        }
    }

    fun initRetrofit() {
        retrofit = RetrofitClient.initRetrofit() // 걍 외우셈
        retrofitHttp = retrofit!!.create(RetrofitService::class.java)
    }

    fun addShoppingListView(shoppingList: ArrayList<MenuSelection>) {
        var allAmount = 0
        var allPrice = 0
        for (index in 0 until shoppingList.size) {
            for (amount in 0 until shoppingList[index].amount?.toInt()!!) {
                Log.d("쇼핑리스트 뷰 동적할당 수량들","${shoppingList[index].amount?.toInt()!!}")

                customView = layoutInflater.inflate(
                    R.layout.shopping_basket_custom_view,
                    linearLayout,
                    false
                )
                customView.findViewById<CheckBox>(R.id.waste_check_button).visibility = View.GONE
                customView.findViewById<TextView>(R.id.menu_name).text = shoppingList[index].menuName
                customView.findViewById<TextView>(R.id.menu_price).text = shoppingList[index].menuPrice
                customView.findViewById<TextView>(R.id.sub_option).text = shoppingList[index].size
                customView.findViewById<TextView>(R.id.sub_price).text = shoppingList[index].menuPrice

                allAmount += 1

                if (shoppingList[index].menuPrice.contains(",")){ // 내가 만든 메뉴들은 가격 표시에 ,가 들어감 레트로핏 과제와 구별하기 위함
                    val a = shoppingList[index].menuPrice.substring(0 until 1)
                    val b = shoppingList[index].menuPrice.substring(2 until 5)
                    val c = a + b
                    allPrice += c.toInt()
                }
                else{
                    allPrice += shoppingList[index].menuPrice.toInt()
                }
                linearLayout.addView(customView)
            }
        }
        itemSummary(allAmount, allPrice)
    }

    fun itemSummary(allAmount: Int, allPrice: Int) {
        val allAmountText = findViewById<TextView>(R.id.all_amount_text)
        val allPriceText = findViewById<TextView>(R.id.all_price_text)
        allAmountText.text = "총 ${allAmount}개"
        allPriceText.text = "${allPrice} 원"
    }

    fun orderButtonEvent() {
        val orderButton = findViewById<Button>(R.id.order_confirm_button)
        val idValue = sharedPreferences.getString("id", null)
        orderButton.setOnClickListener {

            if (idValue == null) { // 로그인을 해야지만 주문을 할 수가 있음
                val dialog = MessageDialog("yes_or_no_mode")
                dialog.setTextMessage("로그인이 필요한 서비스입니다.\n로그인하시겠습니까?")
                dialog.setButtonEvent(object: MessageDialog.OnButtonClickListener{
                    override fun yesButtonClickListener() {
                        val intent = Intent(this@ShoppingBasketPageActivity, LoginPageActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                        startActivity(intent)
                    }
                    override fun noButtonClickListener() {
                    }
                    override fun okButtonClickListener() {
                    }
                })
                dialog.show(supportFragmentManager,"Dialog")
            }
            else {
                for (index in 0 until shoppingList.size) {

                    var requestData: HashMap<String, Any> = HashMap()

                    var allPrice = 0
                    val a = shoppingList[index].menuPrice.substring(0 until 1)//sum_price 계산 저장이 3,400으로 되어있어서 , 빼줘야함
                    val b = shoppingList[index].menuPrice.substring(2 until 5)
                    val c = a + b
                    allPrice = c.toInt() * shoppingList[index].amount?.toInt()!!

                    var myOrderList = orderList(
                        name = shoppingList[index].menuName,
                        count = shoppingList[index].amount?.toInt()!!,
                        sum_price = allPrice
                    )
                    var shared = getSharedPreferences("login_data", MODE_PRIVATE)
                    var list = listOf(myOrderList)
                    requestData["id"] = shared.getString("id", null).toString()
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
                                Log.d("result", "Request success")
                                val dialog = MessageDialog("ok_mode")
                                dialog.setTextMessage("주문이 완료되었습니다")
                                dialog.setButtonEvent(object: MessageDialog.OnButtonClickListener{
                                    override fun noButtonClickListener() {
                                    }

                                    override fun okButtonClickListener(){
                                    }

                                    override fun yesButtonClickListener() {

                                    }
                                })
                                dialog.show(supportFragmentManager,"Dialog")
                            } else {
                                Log.d("result", "${response.body()!!.message}")
                            }
                        }
                    })
                }
            }
        }
    }

    override fun onStart() {
        Log.d("onStart", "adsf")
        val intent = Intent(this, BoundService::class.java)
        serviceUnBind()
        stopService(intent)
        super.onStart()
    }

    override fun onUserLeaveHint() {
        Log.d("onuserleave", "실행")
        val intent = Intent(this, BoundService::class.java)
        ContextCompat.startForegroundService(this, intent)
        super.onUserLeaveHint()
    }

    override fun onDestroy() {
        serviceUnBind()
        super.onDestroy()
    }

    fun serviceBind() {
        boundService = Intent(this, BoundService::class.java)
        bindService(boundService, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d("ShoppingBasket 내의 serviceBind", "난 서비스 실행했음")
    }

    fun serviceUnBind() {
        if (isConService) {
            unbindService(serviceConnection)
            isConService = false
            Log.d("ShoppingBasket 내의 serviceUnBind", "난 서비스 껏음")
        }
    }
}








