package com.example.homework

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ParcelableSparseArray
import java.lang.NullPointerException

class ShoppingBasketPageActivity: AppCompatActivity() {

    lateinit var savedData: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_basket_page)

//            savedData =
//                intent.getStringArrayListExtra("send data to shoppingPage")!! //2차언 배열은 어떻게 받지?


        val linearLayout = findViewById<LinearLayout>(R.id.shopping_basket_linear_layout)
//        addCustomView(savedData, linearLayout)

    }


    fun addCustomView(savedData: ArrayList<String>, linearLayout: LinearLayout){
            val customView = layoutInflater.inflate(
                R.layout.shopping_basket_custom_view,
                linearLayout,
                false
            )
            customView.findViewById<TextView>(R.id.menu_name).text = savedData[1]
            customView.findViewById<TextView>(R.id.menu_price).text = savedData[2]
            customView.findViewById<TextView>(R.id.sub_option).text = savedData[3]
            customView.findViewById<TextView>(R.id.sub_price).text = savedData[2]

            linearLayout.addView(customView)
        }


    }



//        if (savedInstanceState != null){
//            savedData = arrayListOf(savedInstanceState.getStringArrayList("saved data")!!)
//        }
//override fun onSaveInstanceState(outState: Bundle) {
//    outState.putStringArrayList("saved data", receiveData)
//    super.onSaveInstanceState(outState)
//
//}
