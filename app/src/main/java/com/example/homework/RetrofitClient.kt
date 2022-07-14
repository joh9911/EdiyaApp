package com.example.homework

import android.accounts.Account
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

object RetrofitClient {
    var instance: Retrofit? = null
    //기본적인 레트로핏 설정 함수 (connect의 역할)
    fun initRetrofit(): Retrofit{

        if(instance == null) { // 이 객체를 또 다른 클래스에서 쓰면 안되기 때문에 이렇게 narrowing을 해줌줌
            instance = Retrofit
                .Builder()
                .baseUrl("http://3.39.66.6:3000")//ip나 도메인: 포트번호
                .addConverterFactory(GsonConverterFactory.create()) //Gson을 쓰겠다.
                .build()
        }
        return instance!!
    }
}
data class AccountData(
    var message: String,
    var success: Boolean
)

data class AccountCategoryData(
    var message: String,
    var success: Boolean,
    var data: List<AccountCategoryDataList>
)

data class AccountCategoryDataList(
    var category_name: String
)

data class AccountCategoryMenuData(
    var message: String,
    var success: Boolean,
    var data: List<AccountCategoryMenuList>
)

data class AccountCategoryMenuList(
    var menu_name: String,
    var menu_price: String,
    var menu_image: String
)

data class orderList(
    val name: String,
    val count: Int,
    val sum_price: Int
)

data class orderRecordData(
    var order_list: List<orderList>,
    var total_list: Int
)

data class orderRecord(
    var message: String,
    var success: Boolean,
    var data: List<orderRecordData>
)




interface RetrofitService {   // 걍 외우셈

    @GET("/account/login")
    fun getAccountLogin(
        @Query("id") id: String,
        @Query("pw") pw: String
    ): Call<AccountData> // 결과값을 저 데이터 클래스 형식으로 받겟다 이소리임

    @POST("/account")
    fun postAccount( // post는 하나밖에 못보냄 따라서 hashmap을 사용
        @Body body: HashMap<String, String>
    ): Call<AccountData>

    @GET("/account/overlap")
    fun getAccountOverlap(
        @Query("id") id: String
    ): Call<AccountData>

    @GET("/category")
    fun getCategory(
        @Query("lang") lang: String
    ): Call<AccountCategoryData>

    @GET("/category/menu")
    fun getCategoryMenu(
        @Query("category_name") category_name: String,
        @Query("lang") lang: String
    ): Call<AccountCategoryMenuData>

    @POST("/order")
    fun postOrderMenu(
        @Body body: HashMap<String, Any>
    ): Call<AccountData>

    @GET("/order")
    fun getOrderData(
        @Query("id") id: String
    ): Call<orderRecord>

}