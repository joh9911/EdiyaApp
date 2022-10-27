package com.example.ediya_app

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
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MenuSelectGetCategoryAnotherFragment: Fragment() {

    lateinit var boundService: Intent

    lateinit var retrofit: Retrofit  //connect   걍 외우셈
    lateinit var retrofitHttp: RetrofitService  //cursor

    var myService: BoundService? = null
    var isConService = false
    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val b = p1 as BoundService.MyBoundService
            myService = b.getService()
            isConService = true
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
        val view = layoutInflater.inflate(R.layout.menu_select_get_category_menu_fragment, container,false)
        serviceBind()
        initRetrofit()
        getMenu(view)
        return view
    }

    fun serviceBind(){
        boundService = Intent(context,BoundService::class.java)
        activity?.bindService(boundService, serviceConnection, Context.BIND_AUTO_CREATE)
        Log.d("MainSelectCoffeeFragment 내의 serviceBind","난 서비스 실행했음")
    }

    override fun onDestroy() {
        serviceUnBind()
        super.onDestroy()
    }

    fun serviceUnBind(){
        if (isConService) {
            activity?.unbindService(serviceConnection)
            isConService = false
            Log.d("MainSelectPageActivity내의 serviceUnBind","난 서비스 껏음")
        }
    }

    fun initRetrofit() {
        retrofit = RetrofitClient.initRetrofit() // 걍 외우셈
        retrofitHttp = retrofit!!.create(RetrofitService::class.java)
    }
    fun getMenu(view: View){
        var categoryName = "음료"
        var lang = "kr"
        retrofitHttp.getCategoryMenu(
            categoryName,
            lang
        ).enqueue(object: Callback<AccountCategoryMenuData> {

            override fun onFailure(
                call: Call<AccountCategoryMenuData>,
                t: Throwable
            ) {
                Log.d("result", "Request Fail: ${t}")
            }

            override fun onResponse(
                call: Call<AccountCategoryMenuData>,
                response: Response<AccountCategoryMenuData>
            ) {
                if (response.body()!!.success) {
                    Log.d("result", "Request success")
                    addView(view, response)
                }
                else{
                    Log.d("result","${response.body()!!.message}")
                }
            }

        })
    }

    fun addView(view: View, response: Response<AccountCategoryMenuData>) {

        val linearLayout = view.findViewById<LinearLayout>(R.id.menu_select_page_fragment_linear_layout)
        for (index in 0 until response.body()!!.data.size) {
            val customView = layoutInflater.inflate(R.layout.menu_custom_view, linearLayout, false)

//            val id: Int =
//                resources.getIdentifier(//배열에 R.mipmap.~~ 이런식으로 저장하고 불러와서 .toInt()로 변환해서 넣으면 안돌아감. 배열에 mipmap 이름만 저장하고 이런식으로 불러오기
//                    coffeeGsonArray[index].menuImageSource,
//                    "mipmap",
//                    activity?.packageName
//                )

            Glide
                .with(view)
                .load("http://3.39.66.6:3000"+response.body()!!.data[index].menu_image)
                .thumbnail()
                .into(customView.findViewById<ImageView>(R.id.menu_image))
            customView.findViewById<TextView>(R.id.menu_name).text = response.body()!!.data[index].menu_name
            customView.findViewById<TextView>(R.id.menu_price).text = response.body()!!.data[index].menu_price

            customView.setOnClickListener {
                val intent = Intent(context,SelectOptionPageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION)
                startActivity(intent)
                val menu = MenuSelection(response.body()!!.data[index].menu_image, response.body()!!.data[index].menu_name, response.body()!!.data[index].menu_price, null, null)
                myService?.getSelectionData(menu)
            }
            linearLayout.addView(customView)
        }
    }
}


