package com.example.homework

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION
import android.content.ServiceConnection
import android.graphics.Typeface
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MenuSelectPageActivity : AppCompatActivity() {
    lateinit var linearLayout: LinearLayout
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_select_page)
        shoppingBasketButtoon()
        backButtonEvent()
        initAdapter()

    }
    override fun onStart(){
        Log.d("onStart","adsf")
        val intent = Intent(this,BoundService::class.java)
        stopService(intent)
        super.onStart()
    }

    override fun onUserLeaveHint() {
        Log.d("onuserleave","실행")
        val intent = Intent(this,BoundService::class.java)
        ContextCompat.startForegroundService(this, intent)
        super.onUserLeaveHint()
    }

    fun initAdapter() {
        val fragmentList = listOf(MenuSelectCoffeeFragment(), MenuSelectBeverageFragment(), MenuSelectAdeFragment(), MenuSelectBakeryFragment())
        val adapterXml = findViewById<ViewPager2>(R.id.view_pager)
        val tabTitle = arrayOf("커피", "베버리지", "에이드", "베이커리")
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.fragments.addAll(fragmentList)
        adapterXml.adapter = viewPagerAdapter
        tabLayout = findViewById(R.id.tap_layout)
        TabLayoutMediator(tabLayout, adapterXml) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()
    }

    fun shoppingBasketButtoon() {
        val shoppingBasketButton = findViewById<ImageView>(R.id.shopping_basket_button)
        shoppingBasketButton.setOnClickListener {
            val intent = Intent(this, ShoppingBasketPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
    }

    fun backButtonEvent(){
        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener{
//            serviceUnBind()
            finish()
        }
    }

}
