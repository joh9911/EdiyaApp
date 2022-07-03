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
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isGone
import com.google.android.material.internal.ParcelableSparseArray
import com.google.gson.Gson
import org.w3c.dom.Text
import java.lang.NullPointerException

class ShoppingBasketPageActivity: AppCompatActivity() {
    lateinit var boundService: Intent
    lateinit var linearLayout: LinearLayout
    lateinit var customView: View

    val deleteIndex = arrayListOf<Int>()
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
        Log.d("shoppingoncreate", "여긴됨?")

        val deleteButton = findViewById<Button>(R.id.delete_button)
        val selectAllButton = findViewById<Button>(R.id.select_all_button)
        val deleteProcessConfirmButton = findViewById<Button>(R.id.delete_process_confirm_button)


        deleteButton.visibility = View.INVISIBLE
        selectAllButton.visibility = View.INVISIBLE
        deleteProcessConfirmButton.visibility = View.GONE

        linearLayout = findViewById(R.id.shopping_basket_linear_layout)

        serviceBind()
        backButtonEvent()
        wasteButtonEvent()
        deleteProcessConfirmButtonEvent()
        orderButtonEvent()

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

    override fun onDestroy() {
        serviceUnBind()
        super.onDestroy()
    }


    fun wasteButtonEvent() {
        val wasteButton = findViewById<ImageView>(R.id.waste_button)
        val orderConfirmButton = findViewById<Button>(R.id.order_confirm_button)
        val deleteButton = findViewById<Button>(R.id.delete_button)
        val selectAllButton = findViewById<Button>(R.id.select_all_button)
        val deleteProcessConfirmButton = findViewById<Button>(R.id.delete_process_confirm_button)

        wasteButton.setOnClickListener {
            wasteButton.visibility = View.GONE
            deleteButton.visibility = View.VISIBLE
            selectAllButton.visibility = View.VISIBLE
            deleteProcessConfirmButton.visibility = View.VISIBLE
            orderConfirmButton.visibility = View.INVISIBLE

            val count = linearLayout.childCount
            for (index in 0 until count) {
                val child = linearLayout.getChildAt(index)
                child.findViewById<CheckBox>(R.id.waste_check_button).visibility = View.VISIBLE
                child.invalidate()
            }
            var tag = 1
            selectAllButton.setOnClickListener {
                if (tag == 1) {
                    val count = linearLayout.childCount
                    for (index in 0 until count) {
                        val child = linearLayout.getChildAt(index)
                        val checkBox = child.findViewById<CheckBox>(R.id.waste_check_button)
                        checkBox.isChecked = true
                    }
                    tag = 0
                }
                else if (tag == 0){
                    val count = linearLayout.childCount
                    for (index in 0 until count) {
                        val child = linearLayout.getChildAt(index)
                        val checkBox = child.findViewById<CheckBox>(R.id.waste_check_button)
                        checkBox.isChecked = false
                    }
                    tag = 1
                }
            }

            deleteButton.setOnClickListener {
                val count = linearLayout.childCount
                for (index in 0 until count) {
                    val child = linearLayout.getChildAt(index)
                    val checkBox = child.findViewById<CheckBox>(R.id.waste_check_button)

                    if (checkBox.isChecked) { // 만약 체크가 됐다면,
                        val name = child.findViewById<TextView>(R.id.menu_name).text //체크가 된 인덱스의 child의 메뉴 이름

                        for (index1 in 0 until shoppingList.size){ //저 메뉴 이름과 저장된 shoppingList 안의 Gson 데이터의 메뉴 이름과 비교해서
                             // 일치하는 항목의 amount를 1 빼준다.
                            if (name == shoppingList[index1].menuName){
                                var amount = shoppingList[index1].amount?.toInt()!!
                                amount -= 1
                                shoppingList[index1].amount = amount.toString()
                            }

                        }
                        deleteIndex.add(index)  // 체크된 인덱스들 저장
                    } else {
                    }
                }
                deleteButton.visibility = View.INVISIBLE
                selectAllButton.visibility = View.INVISIBLE
                orderConfirmButton.visibility = View.VISIBLE
                deleteProcessConfirmButton.visibility = View.GONE
                wasteButton.visibility = View.VISIBLE
                linearLayout.removeAllViews()
                addShoppingListView(shoppingList)
                myService?.getFinalShoppingList(shoppingList)
            }
        }
    }

    fun deleteProcessConfirmButtonEvent() {
        val deleteProcessConfirmButton = findViewById<Button>(R.id.delete_process_confirm_button)
        deleteProcessConfirmButton.setOnClickListener {
            deleteProcessConfirmButton.visibility = View.GONE
            findViewById<Button>(R.id.delete_button).visibility = View.INVISIBLE
            findViewById<Button>(R.id.select_all_button).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.waste_button).visibility = View.VISIBLE
            findViewById<Button>(R.id.order_confirm_button).visibility = View.VISIBLE
            val count = linearLayout.childCount
            for (index in 0 until count) {
                val child = linearLayout.getChildAt(index)
                child.findViewById<CheckBox>(R.id.waste_check_button).isChecked = false
                child.findViewById<CheckBox>(R.id.waste_check_button).visibility = View.GONE
                child.invalidate()
            }
        }
    }

    fun backButtonEvent() {
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }

    fun addShoppingListView(shoppingList: ArrayList<MenuSelection>) {
        var count = 0
        var allAmount = 0
        var allPrice = 0
        for (index in 0 until shoppingList.size) {
            for (amount in 0 until shoppingList[index].amount?.toInt()!!) {

                    customView = layoutInflater.inflate(
                        R.layout.shopping_basket_custom_view,
                        linearLayout,
                        false
                    )
                    customView.findViewById<CheckBox>(R.id.waste_check_button).visibility =
                        View.GONE
                    customView.findViewById<TextView>(R.id.menu_name).text =
                        shoppingList[index].menuName
                    customView.findViewById<TextView>(R.id.menu_price).text =
                        shoppingList[index].menuPrice
                    customView.findViewById<TextView>(R.id.sub_option).text = shoppingList[index].size
                    customView.findViewById<TextView>(R.id.sub_price).text =
                        shoppingList[index].menuPrice
                    allAmount += 1
                    val a = shoppingList[index].menuPrice.substring(0 until 1)
                    val b = shoppingList[index].menuPrice.substring(2 until 5)
                    val c = a + b
                    allPrice += c.toInt()
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

    fun orderButtonEvent(){
        val orderButton = findViewById<Button>(R.id.order_confirm_button)
        orderButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("주문")
            dialog.setMessage("주문을 완료하였습니다.")

            dialog.apply{
                setPositiveButton("OK") {dialog, which ->
                    dialog.dismiss()
                }

            }
            dialog.create()
            dialog.show()
        }

    }
}






