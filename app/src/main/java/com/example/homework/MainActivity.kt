package com.example.homework


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
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
            myService?.getEatingWay()

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConService = false
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_page)

        initEvent()

    }

    override fun onDestroy() {
        serviceUnBind()
        boundService = Intent(this,BoundService::class.java)
        super.onDestroy()
    }
    fun serviceBind(theWayOfEating: String){
        boundService = Intent(this,BoundService::class.java)
        boundService.putExtra("the way of eating",theWayOfEating)
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
//    override fun onUserLeaveHint() {
//        val intent = Intent(this, BoundService::class.java)
//        ContextCompat.startForegroundService(this, intent)
//        startForegroundService(intent)
//        super.onUserLeaveHint()
//    }


    fun initEvent() {
        val eatThereButton = findViewById<Button>(R.id.eat_there_button)
        eatThereButton.setOnClickListener() {
            val intent = Intent(this, MenuSelectPageActivity::class.java)
            startActivity(intent)

            theWayOfEating = "포장해서 먹을래요"
            serviceBind(theWayOfEating)

        }
        val takeOutButton = findViewById<Button>(R.id.take_out_button)
        takeOutButton.setOnClickListener() {
            val intent = Intent(this, MenuSelectPageActivity::class.java)
            startActivity(intent)
            theWayOfEating = "매장에서 먹을래요"
            serviceBind(theWayOfEating)

        }
    }
}