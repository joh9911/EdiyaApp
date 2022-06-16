package com.example.homework


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_page)

        initEvent()

    }

    fun initEvent() {
        val eatThereButton = findViewById<Button>(R.id.eat_there_button)
        eatThereButton.setOnClickListener() {
            val intent = Intent(this, MenuSelectPageActivity::class.java)
            startActivity(intent)
        }
        val takeOutButton = findViewById<Button>(R.id.take_out_button)
        takeOutButton.setOnClickListener() {
            val intent = Intent(this, MenuSelectPageActivity::class.java)
            startActivity(intent)
        }
    }
}