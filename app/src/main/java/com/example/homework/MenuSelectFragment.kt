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
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class MenuSelectFragment(position: Int): Fragment() {
    lateinit var boundService: Intent
    var fragmentPosition = position
    var beverageJsonFile = JsonFile.jsonFile

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.menu_select_coffee_fragment,container,false)
        addView(view)
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    fun addView(view: View) {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val gsonArray = gson.fromJson(beverageJsonFile,Array<Array<MenuSelection>>::class.java)
        val linearLayout = view.findViewById<LinearLayout>(R.id.menu_select_page_fragment_linear_layout)
        for (index in 0 until gsonArray[fragmentPosition].size) {
            val customView = layoutInflater.inflate(R.layout.menu_custom_view, linearLayout, false)

            val id: Int =
                resources.getIdentifier(//배열에 R.mipmap.~~ 이런식으로 저장하고 불러와서 .toInt()로 변환해서 넣으면 안돌아감. 배열에 mipmap 이름만 저장하고 이런식으로 불러오기
                    gsonArray[fragmentPosition][index].menuImageSource,
                    "mipmap",
                    activity?.packageName
                )
            Glide
                .with(view)
                .load(id)
                .thumbnail()
                .into(customView.findViewById<ImageView>(R.id.menu_image))
//            customView.findViewById<ImageView>(R.id.menu_image).setImageResource(id)
            customView.findViewById<TextView>(R.id.menu_name).text = gsonArray[fragmentPosition][index].menuName
            customView.findViewById<TextView>(R.id.menu_price).text = gsonArray[fragmentPosition][index].menuPrice

            customView.setOnClickListener {
                val intent = Intent(context,SelectOptionPageActivity::class.java)
                intent.addFlags(FLAG_ACTIVITY_NO_USER_ACTION)
                startActivity(intent)
                val menu = MenuSelection(id.toString(), gsonArray[fragmentPosition][index].menuName, gsonArray[fragmentPosition][index].menuPrice, null, null)
                val dataInterface = context as SaveMenuSelectionData
                dataInterface.saveMenuSelectionData(menu)
            }

            linearLayout.addView(customView)
        }
    }
}
