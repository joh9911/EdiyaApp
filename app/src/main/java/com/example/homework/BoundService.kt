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
import android.widget.Toast
import androidx.core.app.NotificationCompat

class BoundService : Service() {
//    val NOTIFICATION_ID = 10
//    val CHANNEL_ID = "My Service Channel"
    var shoppingList = arrayListOf<ArrayList<String>>()
    var dataList = arrayListOf<String>()
    var wayOfEating = ""
    override fun onBind(p0: Intent?): IBinder? {
        Log.d("onBind", "service 시작")
        wayOfEating = p0?.getStringExtra("the way of eating").toString()

        return binder
    }

    fun getEatingWay() {
        Log.d("eatingWay", "${wayOfEating}")
    }

    fun sendEatingWay(): String {

        return wayOfEating
    }

    fun getData(data: ArrayList<String>) {
        dataList = data
        for (index in 0 until dataList.size) {
            Log.d("getData", "${dataList[index]}")
        }

    }

    fun addAmountData(amount: String) {
        dataList.add(amount)
    }

    fun addSizeData(size: String) {
        dataList.add(size)
    }

    fun sendData(): ArrayList<String> {
        return dataList
    }

    fun addShoppingList(data: ArrayList<String>) {
        Log.d("addshoppinglist", "실행됨")
        Log.d("adds", "${data[1]}")
        shoppingList.add(data)
        Log.d("pllus 한뒤", "${shoppingList.size}")
    }

    fun sendShoppingListData(): ArrayList<ArrayList<String>> {
        return shoppingList
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    inner class MyBoundService : Binder() {
        fun getService(): BoundService {
            return this@BoundService
        }
    }

//    fun createNotificationChannel() {
//        val notificationChannel = NotificationChannel(
//            CHANNEL_ID,
//            "MyApp notification",
//            NotificationManager.IMPORTANCE_HIGH
//        )
//        notificationChannel.enableLights(true)
//        notificationChannel.enableVibration(true)
//        notificationChannel.description = "AppApp Tests"
//
//        val notificationManager = applicationContext.getSystemService(
//            Context.NOTIFICATION_SERVICE
//        ) as NotificationManager
//        notificationManager.createNotificationChannel(
//            notificationChannel
//        )
//    }


    override fun onCreate() {
        Log.d("oncreate","service 실행됨")
        super.onCreate()
    }



    override fun onDestroy() {
        Log.d("destory","service 종료")
        super.onDestroy()
    }
    val binder = MyBoundService()


}