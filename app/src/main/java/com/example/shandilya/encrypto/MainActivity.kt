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

    private var encrypt: Button?=null
    private var decrypt: Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Requesting for user's camera permission
        if (ActivityCompat.checkSelfPermission(this.applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION)
        }

        //Request for User's Access to Write Storage
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        }

        //Request for User's Access to Read Storage
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
        }

        encrypt = findViewById<Button>(R.id.encrypt) as Button
        decrypt = findViewById<Button>(R.id.decrypt) as Button

        encrypt!!.setOnClickListener(){
            intent = Intent(this,EncryptionActivity::class.java)
            startActivity(intent)
        }

        decrypt!!.setOnClickListener(){
            intent = Intent(this,DecryptionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_PERMISSION){
            if(!(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(applicationContext,"This application needs some permission",Toast.LENGTH_SHORT).show()
                System.exit(0)
            }
        }
    }


    companion object {
        const val REQUEST_PERMISSION = 300

    }
}