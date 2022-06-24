package com.example.homework

import android.content.*
import android.media.Image
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.isGone
import com.google.android.material.internal.ParcelableSparseArray
import org.w3c.dom.Text
import java.lang.NullPointerException

class ShoppingBasketPageActivity: AppCompatActivity() {
    lateinit var boundService: Intent
    lateinit var linearLayout: LinearLayout
    lateinit var customView: View
    var shoppingList =
        arrayListOf<ArrayList<String>>()//1.drawable id, 2.name ,3.price, 4.amount, 5.size
    var myService: BoundService? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("ShoppingBaskeetPageActivity 내의 onServiceConneced", " 실행되나?")
            val b = p1 as BoundService.MyBoundService
            myService = b.getService()
            isConService = true
            myService?.getEatingWay()
            shoppingList = myService?.sendShoppingListData()!!
            addShoppingListView(shoppingList)
            itemSummary(shoppingList)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_basket_page)

        findViewById<Button>(R.id.delete_button).visibility = View.INVISIBLE
        findViewById<Button>(R.id.select_all_button).visibility = View.INVISIBLE
        findViewById<Button>(R.id.delete_process_confirm_button).visibility = View.GONE
        linearLayout = findViewById(R.id.shopping_basket_linear_layout)
        serviceBind()
        backButtonEvent()
        wasteButtonEvent()
        deleteProcessConfirmButtonEvent()

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

    override fun onDestroy() {
        serviceUnBind()
        super.onDestroy()
    }

    fun wasteButtonEvent() {
        val wasteButton = findViewById<ImageView>(R.id.waste_button)
        wasteButton.setOnClickListener {
            wasteButton.visibility = View.GONE
            findViewById<Button>(R.id.delete_button).visibility = View.VISIBLE
            findViewById<Button>(R.id.select_all_button).visibility = View.VISIBLE
            findViewById<Button>(R.id.delete_process_confirm_button).visibility = View.VISIBLE
            findViewById<Button>(R.id.order_confirm_button).visibility = View.INVISIBLE
            val count = linearLayout.childCount
            for (index in 0 until count) {
                val child = linearLayout.getChildAt(index)
                child.findViewById<Button>(R.id.waste_check_button).visibility = View.VISIBLE
                child.invalidate()
            }
            deleteProcessConfirmButtonEvent()
        }
    }
    fun deleteProcessConfirmButtonEvent(){
        val deleteProcessConfirmButton = findViewById<Button>(R.id.delete_process_confirm_button)
        deleteProcessConfirmButton.setOnClickListener{
            deleteProcessConfirmButton.visibility = View.GONE
            findViewById<Button>(R.id.delete_button).visibility = View.INVISIBLE
            findViewById<Button>(R.id.select_all_button).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.waste_button).visibility = View.VISIBLE
            findViewById<Button>(R.id.order_confirm_button).visibility = View.VISIBLE
            val count = linearLayout.childCount
            for (index in 0 until count) {
                val child = linearLayout.getChildAt(index)
                child.findViewById<Button>(R.id.waste_check_button).visibility = View.GONE
                child.invalidate()
            }
            wasteButtonEvent()

        }

    }
    fun deleteConfirmButtonEvent(){
        val deleteConfirmButton = findViewById<Button>(R.id.delete_confirm_button)
    }

    fun backButtonEvent() {
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }


    fun addShoppingListView(shoppingList: ArrayList<ArrayList<String>>){

        for(index in 0 until shoppingList.size) {
            for(amount in 0 until shoppingList[index][3].toInt()) {
                    customView = layoutInflater.inflate(
                    R.layout.shopping_basket_custom_view,
                    linearLayout,
                    false
                )
                customView.findViewById<Button>(R.id.waste_check_button).visibility = View.GONE
                customView.findViewById<TextView>(R.id.menu_name).text = shoppingList[index][1]
                customView.findViewById<TextView>(R.id.menu_price).text = shoppingList[index][2]
                customView.findViewById<TextView>(R.id.sub_option).text = shoppingList[index][4]
                customView.findViewById<TextView>(R.id.sub_price).text = shoppingList[index][2]

                linearLayout.addView(customView)
            }
        }
    }
    fun itemSummary(shoppingList: ArrayList<ArrayList<String>>){
        var allAmount = 0
        var allPrice = 0
        val allAmountText = findViewById<TextView>(R.id.all_amount_text)
        val allPriceText = findViewById<TextView>(R.id.all_price_text)
        for (index in 0 until shoppingList.size){
            allAmount += shoppingList[index][3].toInt()

            val a = shoppingList[index][2].substring(0 until 1)
            val b = shoppingList[index][2].substring(2 until 5)
            val c = a+b
            val amount = shoppingList[index][3].toInt()
            allPrice += c.toInt() * amount
        }
        allAmountText.text = "총 ${allAmount}개"
        allPriceText.text = "${allPrice} 원"
    }
}





