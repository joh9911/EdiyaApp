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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class MenuSelectPageActivity : AppCompatActivity() {
    lateinit var linearLayout: LinearLayout
    lateinit var viewPagerAdapter: ViewPagerAdapter
    lateinit var tabLayout: TabLayout
    var getCategoryArray = arrayListOf<String>()
    lateinit var retrofit: Retrofit  //connect   걍 외우셈
    lateinit var retrofitHttp: RetrofitService  //cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_select_page)
        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        initRetrofit()
        getCategory()
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

    fun getCategory(){
        val lang = "kr"
        retrofitHttp.getCategory(
            lang
        )
        .enqueue(object: Callback<AccountCategoryData> {

            override fun onFailure(
                call: Call<AccountCategoryData>,
                t: Throwable
            ) {
                Log.d("result", "Request Fail: ${t}")
            }
            override fun onResponse(
                call: Call<AccountCategoryData>,
                response: Response<AccountCategoryData>
            ) {
                if (response.body()!!.success) {
                    Log.d("responresult", "${response.body()!!.data.size}")
                    for(index in 0 until response.body()!!.data.size){
                        getCategoryArray.add(response.body()!!.data[index].category_name)
                    }
                    initAdapter()
                }
                else{
                    Log.d("result","${response.body()!!.message}")
                }
            }
        })
    }

    fun initAdapter() {
        val fragmentList = listOf(MenuSelectCoffeeFragment(), MenuSelectBeverageFragment(), MenuSelectAdeFragment(), MenuSelectBakeryFragment(), MenuSelectGetCategoryFragment(), MenuSelectGetCategoryAnotherFragment())
        val adapterXml = findViewById<ViewPager2>(R.id.view_pager)
        val tabTitle = arrayOf("커피", "베버리지", "에이드", "베이커리",getCategoryArray[0], getCategoryArray[1] ) // 이거 카테고리 여러개가 추가되면 또 일일이 해야하나요?
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.fragments.addAll(fragmentList)
        adapterXml.adapter = viewPagerAdapter
        tabLayout = findViewById(R.id.tap_layout)
        TabLayoutMediator(tabLayout, adapterXml) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()
    }



}
