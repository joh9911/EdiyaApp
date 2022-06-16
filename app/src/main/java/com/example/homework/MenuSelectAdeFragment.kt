package com.example.homework

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class MenuSelectAdeFragment: Fragment() {

    val adeData = arrayOf(

        arrayOf("lemon_ade", "레몬 에이드", "3,800"),
        arrayOf("grapefruit_ade", "자몽 에이드", "3,800"),
        arrayOf("grape_ade", "청포도 에이드", "3,900"),

        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.menu_select_coffee_fragment,container,false)

        addView(view)
        return view
    }

    fun addView(view: View) {
        val linearLayout = view.findViewById<LinearLayout>(R.id.menu_select_page_fragment_linear_layout)
        for (index in 0 until adeData.size) {
            val customView = layoutInflater.inflate(R.layout.menu_custom_view, linearLayout, false)

            val id: Int =
                resources.getIdentifier(//배열에 R.mipmap.~~ 이런식으로 저장하고 불러와서 .toInt()로 변환해서 넣으면 안돌아감. 배열에 mipmap 이름만 저장하고 이런식으로 불러오기
                    adeData[index][0],
                    "mipmap",
                    activity?.packageName
                )
            customView.findViewById<ImageView>(R.id.menu_image).setImageResource(id)
            customView.findViewById<TextView>(R.id.menu_name).text = adeData[index][1]
            customView.findViewById<TextView>(R.id.menu_price).text = adeData[index][2]

            customView.setOnClickListener {
                val intent = Intent(context,SelectOptionPageActivity::class.java)
                startActivity(intent)
            }
            
            linearLayout.addView(customView)
        }
    }
}

