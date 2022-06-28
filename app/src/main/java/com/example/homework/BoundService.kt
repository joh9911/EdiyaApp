package com.example.homework

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.gson.Gson

class BoundService : Service() {

    var shoppingList = arrayListOf<String>()
    var wayOfEating= ""
    lateinit var menuSelection: MenuSelection
    var menuAmount= ""
    var menuSize= ""
    val NOTIFICATION_ID = 10
    val CHANNEL_ID = "primary_notification_channel"



    data class MenuSelectionWithOption(
        var selection: MenuSelection,
        var amount: String,
        var size: String
    )

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("onBind", "service 시작")
        wayOfEating = p0?.getStringExtra("the way of eating").toString()

        return binder
    }

    fun getEatingWay() {
        Log.d("eatingWay", "${wayOfEating}")
    }

    fun getSelectionData(data: String) {
        val myData = Gson().fromJson(data,MenuSelection::class.java)
        menuSelection = myData
    }

    fun sendSelectionData(): String{
        val gsonData = Gson().toJson(menuSelection)
        return gsonData
    }

    fun getAmountData(amount: String) {
        menuAmount = amount
    }

    fun getSizeData(size: String) {
        menuSize = size
    }

    fun addShoppingList(){
        val myData = MenuSelectionWithOption(menuSelection, menuAmount, menuSize)
        val myMenuSelection = Gson().toJson(myData)
        shoppingList.add(myMenuSelection)
    }

    fun sendShoppingList(): ArrayList<String>{
        return shoppingList
    }

    fun getFinalShoppingList(data: ArrayList<String>){
        shoppingList = data
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            builder.setContentTitle("EdiyaApp")
            builder.setContentText("이디야 앱이 실행중입니다.")
            builder.build()
            val notification = builder.build()
            Log.d("Test", "start foreground")
            startForeground(NOTIFICATION_ID, notification)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    inner class MyBoundService : Binder() {
        fun getService(): BoundService {
            return this@BoundService
        }
    }

    fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "EdiyaApp",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.description = "AppApp Tests"

        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            notificationChannel)
    }

    override fun onDestroy() {
        Log.d("destory","service 종료")
        super.onDestroy()
    }
    val binder = MyBoundService()



}