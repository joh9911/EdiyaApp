package com.example.homework


import android.content.*
import android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var theWayOfEating: String
    lateinit var boundService: Intent

    var myService: BoundService? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d("MainActivity 내의 onServiceConneced"," 실행되나?")
            val b = p1 as BoundService.MyBoundService
            myService = b.getService()
            isConService = true
            initNavigationMenu()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_page)
        serviceBind()
        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        initEvent()
    }

    fun gotoAnotherPage(intent: String){
        if (intent == "login") {
            val intent = Intent(this, LoginPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
        else if (intent == "shoppingList"){
            val intent = Intent(this, ShoppingBasketPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
        else if (intent == "signUP"){
            val intent = Intent(this, SignUpPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
    }

    fun initNavigationMenu(){
        val navView = findViewById<NavigationView>(R.id.navigation_view)
        navView.setNavigationItemSelectedListener(this)
        val navMenu = navView.menu
        if (myService?.getLoginStatus()!!){
            navMenu.findItem(R.id.login_button).setVisible(false)
            navMenu.findItem(R.id.order_record_button).setVisible(false)
        }
        Log.d("initNav","${myService?.getLoginStatus()!!}")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.login_button -> gotoAnotherPage("login")
            R.id.basket_button -> gotoAnotherPage("shoppingList")
            R.id.signup_button -> gotoAnotherPage("signUp")
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
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
                drawerLayout.openDrawer(GravityCompat.END)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onStart(){
        Log.d("onStart","adsf")
        val intent = Intent(this,BoundService::class.java)
        serviceUnBind()
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
            val intent = Intent(this, MenuSelectPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
            theWayOfEating = "포장해서 먹을래요"

        }
        val takeOutButton = findViewById<Button>(R.id.take_out_button)
        takeOutButton.setOnClickListener() {
            val intent = Intent(this, MenuSelectPageActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
            theWayOfEating = "매장에서 먹을래요"

        }
    }
}