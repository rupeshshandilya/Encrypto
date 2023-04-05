package com.example.shandilya.encrypto

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var encrypt = findViewById<Button>(R.id.encrypt)
        var decrypt = findViewById<Button>(R.id.decrypt)

        encrypt.setOnClickListener(){
            intent = Intent(this,EncryptionActivity::class.java)
            startActivity(intent)
        }

        decrypt.setOnClickListener(){
            intent = Intent(this,DecryptionActivity::class.java)
            startActivity(intent)
        }
    }

}