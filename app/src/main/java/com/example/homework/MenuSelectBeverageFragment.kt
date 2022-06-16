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

class MenuSelectBeverageFragment: Fragment() {

    val beverageData = arrayOf(
        arrayOf("chocolate", "초콜릿", "3,700"),
        arrayOf("double_topinut_latte", "더블 토피넛 라떼", "4,300"),
        arrayOf("white_chocolate", "화이트 초콜릿", "3,900"),
        arrayOf("mint_chocolate", "민트 초콜릿", "4,000"),
        arrayOf("black_latte", "흑당 라떼", "3,300"),
        arrayOf("peach_juice", "복숭아 주스", "3,900"),
        arrayOf("strawberry_juice", "딸기 주스", "3,900"),
        arrayOf("goldkiwi_juice", "골드 키위 주스", "3,900"),
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
        for (index in 0 until beverageData.size) {
            val customView = layoutInflater.inflate(R.layout.menu_custom_view, linearLayout, false)

            val id: Int =
                resources.getIdentifier(//배열에 R.mipmap.~~ 이런식으로 저장하고 불러와서 .toInt()로 변환해서 넣으면 안돌아감. 배열에 mipmap 이름만 저장하고 이런식으로 불러오기
                    beverageData[index][0],
                    "mipmap",
                    activity?.packageName
                )
            customView.findViewById<ImageView>(R.id.menu_image).setImageResource(id)
            customView.findViewById<TextView>(R.id.menu_name).text = beverageData[index][1]
            customView.findViewById<TextView>(R.id.menu_price).text = beverageData[index][2]

            customView.setOnClickListener {
                val intent = Intent(context,SelectOptionPageActivity::class.java)
                startActivity(intent)
            }

            linearLayout.addView(customView)
        }
    }
}
