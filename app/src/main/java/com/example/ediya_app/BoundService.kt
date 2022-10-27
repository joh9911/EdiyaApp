package com.example.ediya_app

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class BoundService : Service() {

    var shoppingList = arrayListOf<MenuSelection>()
    var wayOfEating= ""
    var menuAmount= ""
    var menuSize= ""
    var menuCategoryPosition = 0
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

    fun getSelectionData(data: MenuSelection) { //menuSelectionPage에서의 메뉴 정보 저장
        menuSelection = data
    }

    fun sendSelectionData(): MenuSelection{//menuSelectionPage에서 저장한 메뉴 정보 selectOptionPage로 보내기
        return menuSelection
    }

    fun getAmountData(amount: String) {//selectOptionPage에서 선택한 수량 저장
        Log.d("getAmountData함수첫","${shoppingList}")
            menuAmount = amount
            menuSelection.amount = menuAmount
        Log.d("getAmountData함수","${menuAmount}")
        Log.d("getAmountData함수","${menuSelection}")
        Log.d("getAmountData함수","${shoppingList}")
    }

    fun getSizeData(size: String) {//selectOptionPage에서 선택한 사이즈 저장
        menuSize = size
        menuSelection.size = menuSize
    }

    fun addShoppingList(){
        Log.d("addshoppinglist함수","실행됨")
        Log.d("addshoppinglist내에서 shoppingLIst첫조회","${shoppingList}")
        for (index in 0 until shoppingList.size){ // 만약 기존의 장바구니에 이름과 사이즈가 같은 품목이 있다면, add하지 말고 그 품목의 amount를 올려줌
            Log.d("addshoppinglist내에서  반복문속 menusleection menuname","${menuSelection.menuName}")
            if (shoppingList[index].menuName == menuSelection.menuName && shoppingList[index].size == menuSelection.size){
                Log.d("반복문 index","${index}")
                var shoppingListAmount = shoppingList[index].amount?.toInt()!!
                Log.d("반복문 속 amount1","${shoppingList[index].amount?.toInt()!!}")
                shoppingListAmount += menuSelection.amount?.toInt()!!
                Log.d("반복문 속 amount","${shoppingListAmount}")
                shoppingList[index].amount = shoppingListAmount.toString()
                Log.d("반복문 속 amount 설정","${shoppingList[index].amount}")
                break
            }
            else{
                shoppingList.add(menuSelection.copy())
            }
        }
        if (shoppingList.size == 0){
            Log.d("if문 실행됨","gkgkgkgkgk")
            shoppingList.add(menuSelection.copy())
        }
        Log.d("addshoppinglist내에서 shoppingLIst둘조회","${shoppingList}")
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