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

    var shoppingList = arrayListOf<MenuSelection>()
    var wayOfEating= ""
    var menuAmount= ""
    var menuSize= ""
    lateinit var menuSelection: MenuSelection
    val NOTIFICATION_ID = 10
    val CHANNEL_ID = "primary_notification_channel"


    override fun onBind(p0: Intent?): IBinder? {
        Log.d("onBind", "service 시작")
        wayOfEating = p0?.getStringExtra("the way of eating").toString()

        return binder
    }

    fun getEatingWay() {
        Log.d("eatingWay", "${wayOfEating}")
    }

    fun getSelectionData(data: MenuSelection) {
        menuSelection = data
    }

    fun sendSelectionData(): MenuSelection{
        return menuSelection
    }

    fun getAmountData(amount: String) {
        menuAmount = amount
        menuSelection.amount = menuAmount
    }

    fun getSizeData(size: String) {
        menuSize = size
        menuSelection.size = menuSize
    }

    fun addShoppingList(){
        shoppingList.add(menuSelection)
    }

    fun sendShoppingList(): ArrayList<MenuSelection>{
        return shoppingList
    }

    fun getFinalShoppingList(data: ArrayList<MenuSelection>){
        shoppingList = data
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("EdiyaApp")
            .setContentText("이디야 앱이 실행중입니다.")
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
            NotificationManager.IMPORTANCE_DEFAULT
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