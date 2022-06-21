package com.example.homework

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class MenuSelectBakeryFragment: Fragment() {
    lateinit var boundService: Intent
    var sendData = arrayListOf<String>()
    val bakeryData = arrayOf(
        arrayOf("square_pizza", "하와이완 스퀘어 피자", "4,200"),
        arrayOf("sweetpotato_square_pizza", "불닭 고구마 스퀘어 피자", "4,200"),
        arrayOf("chocolate_cookie", "초콜릿 청크 쿠키", "1,900"),
        arrayOf("strawberry_croffle", "생딸기 크로플", "3,000"),
        arrayOf("strawberry_bread", "생딸기 연유 브레드", "4,500"),
        arrayOf("strawberry_waffle", "생딸기 와플", "3,500"),
        arrayOf("sandwitch", "햄앤치즈 샌드 위치", "1,900"),
        arrayOf("mini_scon", "미니 스콘", "2,900"),
    )
    var myService: BoundService? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.menu_select_coffee_fragment,container,false)
        serviceBind()
        addView(view)
        return view

    }
    fun serviceBind(){
        boundService = Intent(context,BoundService::class.java)
        activity?.bindService(boundService, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d("MainSelectCoffeeFragment 내의 serviceBind","난 서비스 실행했음")
    }
    fun serviceUnBind(){
        if (isConService) {
            activity?.unbindService(serviceConnection)
            isConService = false
            Log.d("MainSelectPageActivity내의 serviceUnBind","난 서비스 껏음")
        }
    }
    override fun onDestroy() {
        serviceUnBind()
        super.onDestroy()
    }
    fun addView(view: View) {
        val linearLayout = view.findViewById<LinearLayout>(R.id.menu_select_page_fragment_linear_layout)
        for (index in 0 until bakeryData.size) {
            val customView = layoutInflater.inflate(R.layout.menu_custom_view, linearLayout, false)

            val id: Int =
                resources.getIdentifier(//배열에 R.mipmap.~~ 이런식으로 저장하고 불러와서 .toInt()로 변환해서 넣으면 안돌아감. 배열에 mipmap 이름만 저장하고 이런식으로 불러오기
                    bakeryData[index][0],
                    "mipmap",
                    activity?.packageName
                )
            customView.findViewById<ImageView>(R.id.menu_image).setImageResource(id)
            customView.findViewById<TextView>(R.id.menu_name).text = bakeryData[index][1]
            customView.findViewById<TextView>(R.id.menu_price).text = bakeryData[index][2]

            customView.setOnClickListener {
                val intent = Intent(context,SelectOptionPageActivity::class.java)
                startActivity(intent)
                sendData = arrayListOf(id.toString(), bakeryData[index][1], bakeryData[index][2])
                myService?.getData(sendData)
            }

            linearLayout.addView(customView)
        }
    }
}
