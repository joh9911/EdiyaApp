package com.example.homework

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MenuSelectPageActivity : AppCompatActivity() {
    var sequence = 0
    var trashArray = arrayListOf<String>("시발 쓰레기값") //아니 이거 초기화 어캐했엇지
    var shoppingBasket = arrayListOf(trashArray)
    lateinit var receiveShoppingBasketData: ArrayList<String>
    lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_select_page)
        receiveShoppingBasketData = arrayListOf("아니 초기화 어떻게 해")

        shoppingBasket.add(receiveShoppingBasketData) //이새기 첫번 째 값에 쓰레기값 들고 있음 초기화 땜에
        shoppingBasketButtoon()
        val fragment = MenuSelectCoffeeFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_box,fragment).commit()

//        addCustomView()
        categoriButtonEvent()
        backButtonEvent()
    }
    fun shoppingBasketButtoon() {
        val shoppingBasketButton = findViewById<ImageView>(R.id.shopping_basket_button)
        shoppingBasketButton.setOnClickListener {
            val intent = Intent(this, ShoppingBasketPageActivity::class.java)
            startActivity(intent)
        }
    }

//    fun addCustomView() {
//        for (index in 0 until dataList[sequence].size) {
//
//            val customView = layoutInflater.inflate(
//                R.layout.menu_custom_view,
//                linearLayout,
//                false
//            )
//            val id: Int =
//                resources.getIdentifier(//배열에 R.mipmap.~~ 이런식으로 저장하고 불러와서 .toInt()로 변환해서 넣으면 안돌아감. 배열에 mipmap 이름만 저장하고 이런식으로 불러오기
//                    dataList[sequence][index][0],
//                    "mipmap",
//                    packageName.toString()
//                )
//            customView.findViewById<ImageView>(R.id.menu_image).setImageResource(id)
//            customView.findViewById<TextView>(R.id.menu_name).text = dataList[sequence][index][1]
//            customView.findViewById<TextView>(R.id.menu_price).text = dataList[sequence][index][2]
//
//            customView.setTag(index)
//            linearLayout.addView(customView)
//
//
//            customView.setOnClickListener{
//                val intent = Intent(this, SelectOptionPageActivity::class.java)
//                val eachId = id.toString()
//                val eachName = dataList[sequence][index][1]
//                val eachPrice = dataList[sequence][index][2]
//                Log.d("보낼 데이터 목록","${eachId},${eachName},${eachPrice}")
//                val sendArray = arrayListOf(eachId, eachName, eachPrice, "cupSize")
//                intent.putExtra("sendData",sendArray)
//                startActivity(intent)
//            }
//        }

    fun backButtonEvent(){
        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener{
            finish()
        }
    }

    fun categoriButtonEvent() {
        val coffeeButton = findViewById<Button>(R.id.coffee_button)
        val beverageButton = findViewById<Button>(R.id.beverage_button)
        val adeButton = findViewById<Button>(R.id.ade_button)
        val bakeryButton = findViewById<Button>(R.id.bakery_button)

        val buttonArray = arrayOf(coffeeButton, beverageButton, adeButton, bakeryButton)

        val coffeeFragment = MenuSelectCoffeeFragment()
        val beverageFragment = MenuSelectBeverageFragment()
        val adeFragment = MenuSelectAdeFragment()
        val bakeryFragment = MenuSelectBakeryFragment()

        val fragmentArray = arrayOf(coffeeFragment, beverageFragment, adeFragment, bakeryFragment)

        buttonArray[0].setTextColor(ContextCompat.getColor(this,R.color.ediya_color))
        buttonArray[0].setTypeface(null, Typeface.BOLD)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_box, fragmentArray[0]).commit()

        for (index in 0 until buttonArray.size){
            buttonArray[index].setOnClickListener{
                for (restOfButton in 0 until buttonArray.size){
                    buttonArray[restOfButton].setTextColor(ContextCompat.getColor(this,R.color.black))
                    buttonArray[restOfButton].setTypeface(null, Typeface.NORMAL)
                }
                buttonArray[index].setTextColor(ContextCompat.getColor(this,R.color.ediya_color))
                buttonArray[index].setTypeface(null, Typeface.BOLD)
                supportFragmentManager.beginTransaction().replace(R.id.fragment_box, fragmentArray[index]).commit()


//                linearLayout.removeAllViews()
//                addCustomView()

            }
        }

    }
}
