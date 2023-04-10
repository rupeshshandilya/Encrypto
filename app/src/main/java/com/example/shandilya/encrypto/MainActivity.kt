package com.example.shandilya.encrypto


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val encrypt = findViewById<Button>(R.id.encrypt)
        val decrypt = findViewById<Button>(R.id.decrypt)

        encrypt.setOnClickListener{
            intent = Intent(this,EncryptionActivity::class.java)
            startActivity(intent)
        }

        decrypt.setOnClickListener{
            intent = Intent(this,DecryptionActivity::class.java)
            startActivity(intent)
        }
    }

}