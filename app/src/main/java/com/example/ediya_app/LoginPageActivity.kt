package com.example.ediya_app

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginPageActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var sharedPreferences: SharedPreferences
    var idValue = "" //받아줄 변수
    var pwValue = ""

    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    lateinit var retrofit: Retrofit  //connect   걍 외우셈
    lateinit var retrofitHttp: RetrofitService  //cursor

    lateinit var boundService: Intent
    var myService: BoundService? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection {
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
        setContentView(R.layout.login_page)

        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        serviceBind()
        initNavigationMenu()
        initRetrofit()
        initEvent()
        super.onCreate(savedInstanceState)
    }

    fun initNavigationMenu(){
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)

        navView.setNavigationItemSelectedListener(this)
        val navMenu = navView.menu
        navMenu.findItem(R.id.login_button).setVisible(false) // 로그인 회원가입 페이지에서는 로그인 버튼 없애기
        var shared = getSharedPreferences("login_data", MODE_PRIVATE)
        if (shared.getString("id",null) != null){
            navMenu.findItem(R.id.login_button).setVisible(false)
            navMenu.findItem(R.id.order_record_button).setVisible(false)
            navView.getHeaderView(0).visibility = View.INVISIBLE
            navView.inflateHeaderView(R.layout.navigation_header).findViewById<TextView>(R.id.name).text = shared.getString("id",null)

        }
        else{
            navView.getHeaderView(0).visibility = View.GONE
            navView.inflateHeaderView(R.layout.navigation_header_logout)
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
                finish()
            }
            R.id.signup_button -> {
                gotoAnotherPage("signUp")
                drawerLayout.closeDrawers()
            }
        }
        return false
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
        else if (intent == "signUp"){
            val intent = Intent(this, SignUpPageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
            startActivity(intent)
        }
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

    override fun onStart() {
        Log.d("login","login")
        super.onStart()
    }

    override fun onDestroy() {
        serviceUnBind()
        super.onDestroy()
    }
    fun initRetrofit() {
        retrofit = RetrofitClient.initRetrofit() // 걍 외우셈
        retrofitHttp = retrofit!!.create(RetrofitService::class.java)
    }


    fun initEvent(){
        val signUpButton = findViewById<Button>(R.id.login_page_signup_button)
        val loginButton = findViewById<Button>(R.id.login_page_login_button)

        loginButton.setOnClickListener {
            idValue = findViewById<EditText>(R.id.id_edit_text)!!.text.toString()
            pwValue = findViewById<EditText>(R.id.pw_edit_text)!!.text.toString()

            retrofitHttp.getAccountLogin(
                idValue,
                pwValue
            ).enqueue(object: Callback<AccountData>{

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
                        sharedPreferences = getSharedPreferences("login_data", MODE_PRIVATE)
                        sharedPreferences.edit().putString("id",idValue).commit()
                        sharedPreferences.edit().putString("pw",pwValue).commit()

                        val messageDialog = MessageDialog("ok_mode")
                        messageDialog.setTextMessage("로그인에 성공하셨습니다")
                        messageDialog.setButtonEvent(object: MessageDialog.OnButtonClickListener{
                            override fun noButtonClickListener() {
                            }

                            override fun okButtonClickListener() {
                                finish()
                            }

                            override fun yesButtonClickListener() {
                            }
                        })
                        messageDialog.show(supportFragmentManager,"Dialog")

                    }
                    else{
                        Log.d("result","${response.body()!!.message}")

                        val messageDialog = MessageDialog("ok_mode")
                        messageDialog.setTextMessage("아이디 또는 비밀번호가 틀립니다")
                        messageDialog.setButtonEvent(object: MessageDialog.OnButtonClickListener{
                            override fun noButtonClickListener() {
                            }

                            override fun okButtonClickListener() {
                            }

                            override fun yesButtonClickListener() {
                            }
                        })
                        messageDialog.show(supportFragmentManager,"Dialog")

                    }
                }
            })
        }
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpPageActivity::class.java)
            startActivity(intent)
            findViewById<EditText>(R.id.id_edit_text).text.clear()
            findViewById<EditText>(R.id.pw_edit_text).text.clear()
        }
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
}