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

class MenuSelectCoffeeFragment: Fragment() {
    val coffeeData = arrayOf(
        arrayOf("americano", "카페 아메리카노", "3,200"),
        arrayOf("caffelatte", "카페 라떼", "3,700"),
        arrayOf("vanillalatte", "바닐라 라떼", "3,900"),
        arrayOf("capuchino", "카푸치노", "3,700"),
        arrayOf("cafemoca", "카페 모카", "3,900"),
        arrayOf("maggiyatto", "카라멜 마끼야또", "3,900"),
        arrayOf("whitechocolatemoca", "화이트 초콜렛 모카", "3,900"),
        arrayOf("mintmoca", "민트 모카", "4,200"),
        arrayOf("mixlatte", "믹스 라떼", "3,800"),
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
        for (index in 0 until coffeeData.size) {
            val customView = layoutInflater.inflate(R.layout.menu_custom_view, linearLayout, false)

            val id: Int =
                resources.getIdentifier(//배열에 R.mipmap.~~ 이런식으로 저장하고 불러와서 .toInt()로 변환해서 넣으면 안돌아감. 배열에 mipmap 이름만 저장하고 이런식으로 불러오기
                    coffeeData[index][0],
                    "mipmap",
                    activity?.packageName
                )
            customView.findViewById<ImageView>(R.id.menu_image).setImageResource(id)
            customView.findViewById<TextView>(R.id.menu_name).text = coffeeData[index][1]
            customView.findViewById<TextView>(R.id.menu_price).text = coffeeData[index][2]

            customView.setOnClickListener {
                val intent = Intent(context,SelectOptionPageActivity::class.java)
                startActivity(intent)
            }

            linearLayout.addView(customView)
        }
    }
}