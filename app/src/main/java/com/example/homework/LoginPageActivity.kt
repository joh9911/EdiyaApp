package com.example.homework

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginPageActivity: AppCompatActivity() {

    var idValue = "" //받아줄 변수
    var pwValue = ""

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

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.login_page)
        serviceBind()
        initRetrofit()
        initEvent()
        super.onCreate(savedInstanceState)
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

    fun messageDialog(message: String){
        val view = layoutInflater.inflate(R.layout.message_dialog,null)
        view.findViewById<TextView>(R.id.message).text = message
        val dialog = AlertDialog.Builder(this@LoginPageActivity)
            .setView(view)
            .create()

        dialog.setCancelable(false)
        val okButton = view.findViewById<Button>(R.id.ok_button)

        okButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()

    }

    fun initEvent(){
        val backButton = findViewById<ImageView>(R.id.back_button)
        val signUpButton = findViewById<Button>(R.id.login_page_signup_button)
        val loginButton = findViewById<Button>(R.id.login_page_login_button)

        backButton.setOnClickListener{
            finish()
        }

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
                ) { // 통신 실패하면 이게 뜸
                    Log.d("result", "Request Fail: ${t}") // t는 통신 실패 이유
                }

                override fun onResponse(
                    call: Call<AccountData>,
                    response: Response<AccountData>
                ) {
                    if (response.body()!!.success) {
                        Log.d("result", "Request success")
                        myService?.login(idValue)
                        val message = "로그인에 성공하셨습니다"
                        messageDialog(message)

                    }
                    else{
                        Log.d("result","${response.body()!!.message}")
                        val message = "아이디 또는 비밀번호가 틀립니다"
                        messageDialog(message)
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
}