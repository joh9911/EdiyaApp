package com.example.homework


import android.content.*
import android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var sharedPreferences: SharedPreferences

    lateinit var theWayOfEating: String
    lateinit var boundService: Intent

    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    val handler = Handler(Looper.getMainLooper()){
        setPage()
        true
    }

    var currentPosition = 0
    lateinit var thread: Thread
    var threadIsStop = false

    var myService: BoundService? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("MainActivity 내의 onServiceConneced"," 실행되나?")
            val b = p1 as BoundService.MyBoundService
            myService = b.getService()
            isConService = true

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_page)

    }

    override fun onResume() {
        super.onResume()
        serviceBind()
        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        initNavigationMenu()
        initAdapter()
        initEvent()
    }

    fun initAdapter(){
        val adapterXml = findViewById<ViewPager2>(R.id.ads_picture)
        val dotIndicator = findViewById<DotsIndicator>(R.id.spring_dots_indicator)
        adapterXml.adapter = ViewPagerStartPageAdapter(getImageList())
        adapterXml.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        dotIndicator.setViewPager2(adapterXml)
    }
    fun getImageList(): ArrayList<Int> {
        return arrayListOf(R.mipmap.ediya_ads_1, R.mipmap.ediya_ads_2, R.mipmap.ediya_ads_3)
    }

    fun gotoAnotherPage(intent: String){
        if (intent == "login") {
            threadIsStop = true
            val intent = Intent(this, LoginPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
        else if (intent == "shoppingList"){
            threadIsStop = true
            val intent = Intent(this, ShoppingBasketPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
        else if (intent == "signUp"){
            threadIsStop = true
            val intent = Intent(this, SignUpPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
    }

    fun initNavigationMenu(){
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)

        navView.setNavigationItemSelectedListener(this)
        val navMenu = navView.menu
        var shared = getSharedPreferences("login_data", MODE_PRIVATE)
        Log.d("shared값","${shared.getString("id",null)}")
        if (shared.getString("id",null) != null){
            navMenu.findItem(R.id.login_button).setVisible(false)
        }
        else{
            navMenu.findItem(R.id.logout_button).setVisible(false)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.login_button -> {
                gotoAnotherPage("login")
                drawerLayout.closeDrawers()
            }
            R.id.basket_button -> {
                gotoAnotherPage("shoppingList")
                drawerLayout.closeDrawers()
            }
            R.id.signup_button -> {
                gotoAnotherPage("signUp")
                drawerLayout.closeDrawers()
            }
            R.id.logout_button -> {
                var shared = getSharedPreferences("login_data", MODE_PRIVATE)
                shared.edit().clear().apply()
                Log.d("logount0","sdf")
                Log.d("ha","${shared.getString("id",null)}")
            }

        }
        return false
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
                drawerLayout.openDrawer(GravityCompat.END)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun setPage(){
        if(currentPosition==2) currentPosition=0
        else currentPosition+=1
        val adapterXml = findViewById<ViewPager2>(R.id.ads_picture)
        adapterXml.setCurrentItem(currentPosition,true)
    }

    inner class PageRunner: Runnable{
        override fun run() {
            while(!threadIsStop){
                Thread.sleep(2000)
                handler.sendEmptyMessage(0)
            }
        }
    }

    override fun onStart(){
        Log.d("onStart","adsf")
        threadIsStop = false
        thread=Thread(PageRunner())
        thread.start()
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
        boundService = Intent(this,BoundService::class.java)
        super.onDestroy()
    }

    fun serviceBind(){
        boundService = Intent(this,BoundService::class.java)
        bindService(boundService, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d("MainActivity 내의 serviceBind","난 서비스 실행했음")
    }

    fun serviceUnBind(){
        if (isConService) {
            unbindService(serviceConnection)
            isConService = false
            Log.d("MainActivity 내의 serviceUnBind","난 서비스 껏음")
        }

    }

    fun initEvent() {
        val eatThereButton = findViewById<Button>(R.id.eat_there_button)
        eatThereButton.setOnClickListener() {
            threadIsStop = true
            val intent = Intent(this, MenuSelectPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
            theWayOfEating = "포장해서 먹을래요"
        }
        val takeOutButton = findViewById<Button>(R.id.take_out_button)
        takeOutButton.setOnClickListener() {
            threadIsStop = true
            val intent = Intent(this, MenuSelectPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
            theWayOfEating = "매장에서 먹을래요"
        }
    }
}