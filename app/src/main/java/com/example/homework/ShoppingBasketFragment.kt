package com.example.homework

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.w3c.dom.Text

class ShoppingBasketFragment: Fragment() {
    lateinit var allAmountText: TextView
    lateinit var allPriceText: TextView
    lateinit var boundService : Intent
    lateinit var linearLayout: LinearLayout

    var shoppingList = arrayListOf<ArrayList<String>>()//1.drawable id, 2.name ,3.price, 4.amount, 5.size

    var myService: BoundService? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("ShoppingBasketFragment 내의 onServiceConneced", " 실행되나?")
            val b = p1 as BoundService.MyBoundService
            myService = b.getService()
            isConService = true
            myService?.getEatingWay()
            shoppingList = myService?.sendShoppingListData()!!
            addCustomView(shoppingList)
            itemSummary(shoppingList)

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConService = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.shopping_basket_fragment, container, false)
        serviceBind()
        linearLayout = view.findViewById(R.id.shopping_basket_linear_layout)
        allAmountText = view.findViewById<TextView>(R.id.all_amount_text)
        allPriceText = view.findViewById<TextView>(R.id.all_price_text)

        return view
    }

    fun serviceBind() {
        boundService = Intent(context, BoundService::class.java)
        activity?.bindService(boundService, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d("ShoppingBasket 내의 serviceBind", "난 서비스 실행했음")
    }

    fun serviceUnBind() {
        if (isConService) {
            activity?.unbindService(serviceConnection)
            isConService = false
            Log.d("ShoppingBasket 내의 serviceUnBind", "난 서비스 껏음")
        }

    }

    fun addCustomView(shoppingList: ArrayList<ArrayList<String>>){

        for(index in 0 until shoppingList.size) {
            val customView = layoutInflater.inflate(
                R.layout.shopping_basket_custom_view,
                linearLayout,
                false
            )

            customView.findViewById<TextView>(R.id.menu_name).text = shoppingList[index][1]
            customView.findViewById<TextView>(R.id.menu_price).text = shoppingList[index][2]
            customView.findViewById<TextView>(R.id.sub_option).text = shoppingList[index][4]
            customView.findViewById<TextView>(R.id.sub_price).text = shoppingList[index][2]
            for(amount in 0 until shoppingList[index][3].toInt()){
                val customView = layoutInflater.inflate(
                    R.layout.shopping_basket_custom_view,
                    linearLayout,
                    false
                )
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
