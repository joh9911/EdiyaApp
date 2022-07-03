package com.example.homework

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION
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
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder

class MenuSelectBakeryFragment: Fragment() {
    lateinit var boundService: Intent

    var bakeryJsonFile = JsonFile.beverageJsonFile
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
//        bakeryArrayToJson()
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
//    fun bakeryArrayToJson() {
//        var temp = "["
//        for (index in 0 until bakeryData.size) {
//
//            var coffeeJsonData =
//                "{'menuImageSource': '${bakeryData[index][0]}', 'menuName': '${bakeryData[index][1]}', 'menuPrice': '${bakeryData[index][2]}'}"
//            temp += coffeeJsonData
//            if (index < bakeryData.size - 1) {
//                temp += ","
//            }
//        }
//        temp += "]"
//        bakeryJsonArray = temp
//        Log.d("1","${bakeryJsonArray}")
//    }
    fun addView(view: View) {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
        val bakeryGsonArray = gson.fromJson(bakeryJsonFile,Array<MenuSelection>::class.java)
        val linearLayout = view.findViewById<LinearLayout>(R.id.menu_select_page_fragment_linear_layout)
        for (index in 0 until bakeryGsonArray.size) {
            val customView = layoutInflater.inflate(R.layout.menu_custom_view, linearLayout, false)

            val id: Int =
                resources.getIdentifier(//배열에 R.mipmap.~~ 이런식으로 저장하고 불러와서 .toInt()로 변환해서 넣으면 안돌아감. 배열에 mipmap 이름만 저장하고 이런식으로 불러오기
                    bakeryGsonArray[index].menuImageSource,
                    "mipmap",
                    activity?.packageName
                )
            Glide
                .with(view)
                .load(id)
                .placeholder(R.mipmap.extrasize)
                .into(customView.findViewById<ImageView>(R.id.menu_image))
//            customView.findViewById<ImageView>(R.id.menu_image).setImageResource(id)
            customView.findViewById<TextView>(R.id.menu_name).text = bakeryGsonArray[index].menuName
            customView.findViewById<TextView>(R.id.menu_price).text = bakeryGsonArray[index].menuPrice

            customView.setOnClickListener {
                val intent = Intent(context,SelectOptionPageActivity::class.java)
                intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
                startActivity(intent)
                val menu = MenuSelection(id.toString(), bakeryGsonArray[index].menuName, bakeryGsonArray[index].menuPrice, null, null)
                myService?.getSelectionData(menu)
            }

            linearLayout.addView(customView)
        }
    }
}
