package com.example.homework

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SignUpPageActivity: AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    lateinit var retrofit: Retrofit
    lateinit var retrofitHttp: RetrofitService

    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    var isIdOverlapChecked = false
    var isPwConfirmSame = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.signup_page)
        setSupportActionBar(findViewById(R.id.tool_bar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        initNavigationMenu()
        initRetrofit()
        signUpEvent()
        super.onCreate(savedInstanceState)
    }

    fun initNavigationMenu(){
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)

        navView.setNavigationItemSelectedListener(this@SignUpPageActivity)
        val navMenu = navView.menu
        navMenu.findItem(R.id.login_button).setVisible(false) // 로그인 회원가입 페이지에서는 로그인 버튼 없애기
        navMenu.findItem(R.id.signup_button).setVisible(false)
        var shared = getSharedPreferences("login_data", MODE_PRIVATE)
        if (shared.getString("id",null) != null){
            navMenu.findItem(R.id.order_record_button).setVisible(false)
            navView.getHeaderView(0).visibility = View.GONE
            navView.inflateHeaderView(R.layout.navigation_header_logout)
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
        else if (intent == "signUP"){
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

    fun initRetrofit() {
        retrofit = RetrofitClient.initRetrofit() // 걍 외우셈
        retrofitHttp = retrofit!!.create(RetrofitService::class.java)
    }

    fun signUpEvent() {
        var idEditText = findViewById<EditText>(R.id.signup_id_edittext)
        var pwEditText = findViewById<EditText>(R.id.signup_pw_edittext)
        var pwConfirmEditText = findViewById<EditText>(R.id.signup_pw_confirm_edittext)
        var nameEditText = findViewById<EditText>(R.id.signup_name_edittext)
        var contactEditText = findViewById<EditText>(R.id.signup_contact_edittext)

        val signUpButton = findViewById<Button>(R.id.signup_page_signup_button)
        val idOverlapCheckButton = findViewById<TextView>(R.id.signup_id_check)

        idOverlapCheckButton.setOnClickListener {
            val idValue = idEditText?.text.toString()
            retrofitHttp.getAccountOverlap(idValue)
                .enqueue(object : Callback<AccountData> {
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
                            findViewById<TextView>(R.id.signup_id_alert_message).setTextColor(
                                resources.getColor(R.color.ediya_color, null)
                            )
                            findViewById<TextView>(R.id.signup_id_alert_message).text =
                                "사용 가능한 아이디 입니다."
                            idOverlapCheckButton.setTextColor(
                                resources.getColor(
                                    R.color.ediya_color,
                                    null
                                )
                            )
                            idOverlapCheckButton.isClickable = false
                            isIdOverlapChecked = true
                        } else {
                            findViewById<TextView>(R.id.signup_id_alert_message).setTextColor(
                                resources.getColor(R.color.red, null)
                            )
                            findViewById<TextView>(R.id.signup_id_alert_message).text =
                                "중복된 아이디 입니다."
                            Log.d("result", "${response.body()!!.message}")
                        }
                    }
                })
        }

        idEditText.setOnFocusChangeListener { view, hasFocused ->
            if (hasFocused) {
            } else {
                if (isIdOverlapChecked) {
                    findViewById<TextView>(R.id.signup_id_alert_message).text = ""
                } else {
                    findViewById<TextView>(R.id.signup_id_alert_message).text = "아이디 중복체크를 해주세요"
                }
            }
        }

        pwConfirmEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if (pwConfirmEditText.text.toString() != pwEditText.text.toString()) {
                    findViewById<TextView>(R.id.signup_pw_confirm_alert_message).setTextColor(resources.getColor(R.color.red, null))
                    findViewById<TextView>(R.id.signup_pw_confirm_alert_message).text = "비밀번호가 일치하지 않습니다."
                    isPwConfirmSame = false
                }
                else{
                    findViewById<TextView>(R.id.signup_pw_confirm_alert_message).setTextColor(resources.getColor(R.color.ediya_color, null))
                    findViewById<TextView>(R.id.signup_pw_confirm_alert_message).text = "비밀번호가 일치합니다."
                    isPwConfirmSame = true
                }
            }
        })
        signUpButton.setOnClickListener {
            if (isIdOverlapChecked and isPwConfirmSame) {
                var requestData: HashMap<String, String> = HashMap()
                requestData["id"] = idEditText?.text.toString()
                requestData["pw"] = pwEditText?.text.toString()
                requestData["name"] = nameEditText?.text.toString()
                requestData["contact"] = contactEditText?.text.toString()

                retrofitHttp.postAccount(requestData)
                    .enqueue(object :
                        Callback<AccountData> {
                        override fun onFailure(
                            call: Call<AccountData>,
                            t: Throwable
                        ) { // 통신 실패하면 이게 뜸
                            Log.d("result", "Request Fail: ${t}")
                        }

                        override fun onResponse(
                            call: Call<AccountData>,
                            response: Response<AccountData>
                        ) {
                            if (response.body()!!.success) {
                                Log.d("result", "Request success")

                                val messageDialog = MessageDialog("ok_mode")
                                messageDialog.setTextMessage("회원가입을 완료했습니다")
                                messageDialog.show(supportFragmentManager,"Dialog")


                            } else {
                                Log.d("result", "${response.body()!!.message}")

                            }
                        }

                    })
            } else if (isIdOverlapChecked == false and isPwConfirmSame == true) {
                idEditText.requestFocus()
            } else if (isIdOverlapChecked == true and isPwConfirmSame == false) {
                pwConfirmEditText.requestFocus()
            } else {
                idEditText.requestFocus()
            }

        }
    }
}